package com.saud.gauchoblog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

public class MapActivity extends ActionBarActivity implements
		ActionBar.OnNavigationListener {

	private GoogleMap ucsbMap;
	private Button Normal, Hybrid, Satellite, MyLocation;
	private View myContentsView;
	private CustomDialogClass custom_dialog;
	private WelcomeDialogClass welcome_dialog;
	private DatabaseHandler database;
	private ActionBar actionbar;
	private static final int EDIT_BIKE_RACK_STATUS = 1;
	private static String NAVIGATION_ITEM_SELECTION = "navigationItemSelection";
	private int mapType = GoogleMap.MAP_TYPE_NORMAL;
	private int navigationItemSelection = 0;
	private ArrayList<SpinnerNavItem> navSpinner;
	private TitleNavigationAdapter adapter;
	private MenuItem refreshMenuItem;
	private boolean my_location;
	private Location location;
	private LatLng currentLocation;
	private List<BuildingDTO> buildingList;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		if (ucsbMap != null) {
			savedInstanceState.putInt("mapType", ucsbMap.getMapType());
			savedInstanceState.putInt(NAVIGATION_ITEM_SELECTION,
					navigationItemSelection);
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_map);

		actionbar = getSupportActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		navSpinner = new ArrayList<SpinnerNavItem>();
		navSpinner.add(new SpinnerNavItem("Home", R.drawable.ic_home));
		navSpinner.add(new SpinnerNavItem("Blogs", R.drawable.ic_blog));
		navSpinner.add(new SpinnerNavItem("Bike Racks", R.drawable.ic_bike));
		adapter = new TitleNavigationAdapter(getApplicationContext(),
				navSpinner);
		actionbar.setListNavigationCallbacks(adapter, this);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#DCFFFFFF")));
		actionbar.show();

		if (savedInstanceState != null) {
			mapType = savedInstanceState.getInt("mapType");
			navigationItemSelection = savedInstanceState
					.getInt(NAVIGATION_ITEM_SELECTION);
			actionbar.setSelectedNavigationItem(navigationItemSelection);

		}

		Normal = (Button) findViewById(R.id.Normal);
		Hybrid = (Button) findViewById(R.id.Hybrid);
		Satellite = (Button) findViewById(R.id.Satellite);
		MyLocation = (Button) findViewById(R.id.My_Location);

		database = new DatabaseHandler(getApplicationContext());
		
/*		if (database.getNumBuildings() == 0) {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Building");
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> objects, ParseException e) {
					if (e == null) {
						List<BuildingDTO> list = new ArrayList<BuildingDTO>();
						for (ParseObject o : objects) {
							list.add(ParseProtocolModule
									.convertBuildingParseToLocal(o));
						}
						database.addBuildingList(list);
						setBuildingMarkers();
					} else {
						custom_dialog = new CustomDialogClass(MapActivity.this);
						custom_dialog.setVariables("Error", e.getMessage());
						custom_dialog.show();
					}
				}
			});
		}

		if (database.getNumBikeRacks() == 0) {
			ParseQuery<ParseObject> queryRacks = ParseQuery.getQuery("Rack");
			queryRacks.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> objects, ParseException e) {
					if (e == null) {
						List<BikeRack> list = new ArrayList<BikeRack>();
						for (ParseObject o : objects) {
							list.add(ParseProtocolModule
									.convertBikeRackParseToLocal(o));
						}
						database.addBikeRackList(list);
						setBikeRackMarkers();
					} else {
						custom_dialog = new CustomDialogClass(MapActivity.this);
						custom_dialog.setVariables("Error", e.getMessage());
						custom_dialog.show();
					}
				}
			});
		}
*/
		setUpMapIfNeeded();

		View.OnClickListener normalListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mapType = GoogleMap.MAP_TYPE_NORMAL;
				ucsbMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			}
		};
		Normal.setOnClickListener(normalListener);

		View.OnClickListener hybridListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mapType = GoogleMap.MAP_TYPE_HYBRID;
				ucsbMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

			}
		};
		Hybrid.setOnClickListener(hybridListener);

		View.OnClickListener satelliteListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mapType = GoogleMap.MAP_TYPE_SATELLITE;
				ucsbMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

			}
		};
		Satellite.setOnClickListener(satelliteListener);

		View.OnClickListener MyLocationListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ucsbMap.setMyLocationEnabled(true);

				if (my_location == true) {

					MyLocation.setBackgroundResource(R.drawable.ic_location_on);

					location = ucsbMap.getMyLocation();

					if (location != null) {
						currentLocation = new LatLng(location.getLatitude(),
								location.getLongitude());
						ucsbMap.animateCamera(CameraUpdateFactory
								.newLatLngZoom(currentLocation, 18));
					}

					my_location = false;
				} else {
					MyLocation
							.setBackgroundResource(R.drawable.ic_location_off);
					ucsbMap.setMyLocationEnabled(false);
					my_location = true;
				}

			}
		};
		MyLocation.setOnClickListener(MyLocationListener);

		ucsbMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker marker) {

				if (marker.getTitle().equalsIgnoreCase("Bike Rack")) {

					myContentsView = getLayoutInflater().inflate(
							R.layout.custom_bike_rack_info_window, null);
					TextView Title = (TextView) myContentsView
							.findViewById(R.id.bike_rack_title);
					Title.setText(marker.getTitle());

				} else {

					myContentsView = getLayoutInflater().inflate(
							R.layout.custom_building_info_window, null);
					ImageView Window_Icon = (ImageView) myContentsView
							.findViewById(R.id.info_window_icon);
					TextView Title = (TextView) myContentsView
							.findViewById(R.id.title);
					Title.setText(marker.getTitle());

					String imageId = null;

					try {
						JSONObject o = new JSONObject(marker.getSnippet());
						if (!o.isNull("imageId")) {
							imageId = o.getString("imageId");
							int i = getResources().getIdentifier(imageId,
									"drawable", "com.saud.gauchoblog");
							Window_Icon.setImageResource(i);
						} else {
							System.out.println("Inside the loop");
							String objectId = o.getString("parseId");
							System.out.println(objectId);
							ParseQuery<ParseObject> query = ParseQuery
									.getQuery("Building");
							try {
								ParseObject newblog = query.get(objectId);
								ParseFile profileImage = newblog
										.getParseFile("Image");
								byte[] data = profileImage.getData();
								BitmapFactory.Options opts = new BitmapFactory.Options();
								Bitmap bmp = BitmapFactory.decodeByteArray(
										data, 0, data.length, opts);
								Window_Icon.setImageBitmap(bmp);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}

				}

				return myContentsView;
			}

			@Override
			public View getInfoContents(Marker marker) {
				return null;
			}
		});

		ucsbMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {

				if (marker.getTitle().equalsIgnoreCase("Bike Rack")) {

					Intent intent = new Intent(MapActivity.this,
							EditBikeRackStatus.class);
					try {
						JSONObject json = new JSONObject(marker.getSnippet());
						intent.putExtra("parseId", json.getString("parseId"));
						intent.putExtra("date", json.getLong("date"));
						intent.putExtra("status", json.getString("status"));
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
					startActivityForResult(intent, EDIT_BIKE_RACK_STATUS);
					overridePendingTransition(R.anim.blog_slide_in_right,
							R.anim.blog_slide_out_right);

				} else {

					Intent i = new Intent(MapActivity.this, Blog.class);
					try {
						JSONObject json = new JSONObject(marker.getSnippet());
						i.putExtra("parseId", json.getString("parseId"));
						i.putExtra("name", json.getString("name"));
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
					startActivity(i);
					overridePendingTransition(R.anim.blog_slide_in_right,
							R.anim.blog_slide_out_right);

				}

			}
		});

		if (savedInstanceState == null) {
			centerMapDefaultLocation(ucsbMap);

			welcome_dialog = new WelcomeDialogClass(MapActivity.this);
			welcome_dialog.setVariables("Welcome "
					+ ParseUser.getCurrentUser().getUsername());
			welcome_dialog.show();

			final Timer t2 = new Timer();
			t2.schedule(new TimerTask() {
				public void run() {
					if (welcome_dialog != null) {
						welcome_dialog.dismiss();
					}
					t2.cancel();
				}
			}, 3000);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (EDIT_BIKE_RACK_STATUS == requestCode) {
			if (RESULT_OK == resultCode) {
				database.updateBikeRackStatus(data.getStringExtra("parseId"),
						data.getStringExtra("status"), new Date().getTime());
				setUpMapIfNeeded();
			}
		}
	}

	private void centerMapDefaultLocation(GoogleMap map) {
		LatLng UCSantaBarbara = new LatLng(34.4125, -119.8481);
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(UCSantaBarbara).zoom(17).build();
		ucsbMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	private void setBikeRackMarkers() {
		List<BikeRack> bikeRacks = database.getAllBikeRacks();
		for (BikeRack b : bikeRacks) {
			int res = R.drawable.bike_rack_gray;
			if (CongestionLevel.HEAVY.toString()
					.equalsIgnoreCase(b.getStatus())) {
				res = R.drawable.bike_rack_red;
			} else if (CongestionLevel.MILD.toString().equalsIgnoreCase(
					b.getStatus())) {
				res = R.drawable.bike_rack_orange;
			} else if (CongestionLevel.OPEN.toString().equalsIgnoreCase(
					b.getStatus())) {
				res = R.drawable.bike_rack_green;
			}

			Date now = new Date();
			long timeout = 7200000; // 2 hours in ms

			if (now.getTime() - timeout > b.getDate()) {
				res = R.drawable.bike_rack_gray;
			}

			JSONObject json = new JSONObject();
			try {
				json.put("parseId", b.getParseId());
				json.put("date", b.getDate());
				json.put("status", b.getStatus());
				if (null == b.getStatus()) {
					json.put("status", CongestionLevel.NONE.toString());
				}

			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			ucsbMap.addMarker(new MarkerOptions().title("Bike Rack")
					.position(new LatLng(b.getLatitude(), b.getLongitude()))
					.snippet(json.toString()).alpha((float) .8)
					.anchor((float) .5, (float) .5)
					.icon(BitmapDescriptorFactory.fromResource(res)));
		}
	}

	private void setBuildingMarkers() {
		buildingList = database.getAllBuildings();
		for (BuildingDTO b : buildingList) {

			JSONObject jsonBuilding = new JSONObject();
			try {
				jsonBuilding.put("parseId", b.getParseId());
				jsonBuilding.put("imageId", b.getImageId());
				jsonBuilding.put("name", b.getName());
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			ucsbMap.addMarker(new MarkerOptions()
					.title(b.getName())
					.position(new LatLng(b.getLatitude(), b.getLongitude()))
					.snippet(jsonBuilding.toString())
					.alpha((float) .9)
					.anchor((float) .5, (float) .5)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.blogicon)));
		}
	}

	private void setUpMapIfNeeded() {

		if (ucsbMap == null) {
			ucsbMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			ucsbMap.setMapType(mapType);
		}

		if (ucsbMap != null) {
			ucsbMap.clear();

			ucsbMap.getUiSettings().setZoomControlsEnabled(false);
			ucsbMap.getUiSettings().setCompassEnabled(false);
			ucsbMap.getUiSettings().setMyLocationButtonEnabled(false);
			ucsbMap.setOnMapLongClickListener(null);
			ucsbMap.setMyLocationEnabled(true);

			switch (navigationItemSelection) {
			case 0:
				setBuildingMarkers();
				setBikeRackMarkers();
				break;
			case 1:
				setBuildingMarkers();
				break;
			case 2:
				setBikeRackMarkers();
				break;
			}

		}

	}
	
	private void populateBuildingsFromServer() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Building");
		try {
			List<ParseObject> objects = query.find();
			List<BuildingDTO> list = new ArrayList<BuildingDTO>();
			for (ParseObject o : objects) {
				list.add(ParseProtocolModule.convertBuildingParseToLocal(o));
			}
			database.addBuildingList(list);
		} catch (ParseException e) {
			custom_dialog = new CustomDialogClass(MapActivity.this);
			custom_dialog.setVariables("Error", e.getMessage());
			custom_dialog.show();
			e.printStackTrace();
		}
	}

	private void populateBikeRacksFromServer() {
		ParseQuery<ParseObject> queryRacks = ParseQuery.getQuery("Rack");
		try {
			List<ParseObject> objects = queryRacks.find();
			List<BikeRack> list = new ArrayList<BikeRack>();
			for (ParseObject o : objects) {
				list.add(ParseProtocolModule.convertBikeRackParseToLocal(o));
			}
			database.addBikeRackList(list);
		} catch (ParseException e) {
			custom_dialog = new CustomDialogClass(MapActivity.this);
			custom_dialog.setVariables("Error", e.getMessage());
			custom_dialog.show();
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_new_blog:
			
			custom_dialog = new CustomDialogClass(MapActivity.this);
			custom_dialog.setVariables("Create Blog", "Long click on any part of the map to choose a location for the new blog.");
			custom_dialog.show();
			
			ucsbMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

				@Override
				public void onMapLongClick(final LatLng arg0) {

					ucsbMap.addMarker(new MarkerOptions()
							.title("Ani")
							.position(arg0)
							.alpha((float) .9)
							.anchor((float) .5, (float) .5)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.blogicon)));

					Intent intent = new Intent(MapActivity.this, NewBlog.class);

					Bundle args = new Bundle();
					args.putParcelable("LatLong", arg0);

					intent.putExtra("bundle", args);

					startActivity(intent);
					overridePendingTransition(R.anim.blog_slide_in_right,
							R.anim.blog_slide_out_right);

				}
			});
			return true;
		case R.id.action_refresh:
			refreshMenuItem = item;
			new SyncData().execute();
			return true;
		case R.id.action_overflow:
			PopupMenu popup = new PopupMenu(MapActivity.this,
					findViewById(R.id.action_overflow));
			popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {

					switch (item.getItemId()) {
					case R.id.action_profile:
						Intent wordIntent = new Intent(getApplicationContext(),
								UserProfile.class);
						startActivity(wordIntent);
						overridePendingTransition(R.anim.blog_slide_in_right,
								R.anim.blog_slide_out_right);
						return true;
					case R.id.action_signout:
						ParseUser.logOut();
						Intent intent = new Intent(MapActivity.this,
								SignIn.class);
						startActivity(intent);
						overridePendingTransition(R.anim.logout_in_up,
								R.anim.logout_out_up);
						finish();
						return true;
					}

					return true;
				}
			});
			popup.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		navigationItemSelection = arg0;
		setUpMapIfNeeded();
		return false;
	}
	
	class LoadallData extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			populateBikeRacksFromServer();
			populateBuildingsFromServer();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			setUpMapIfNeeded();
		}

	}
	
	class SyncData extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			refreshMenuItem.setActionView(R.layout.action_progressbar);
			refreshMenuItem.expandActionView();
		}

		@Override
		protected String doInBackground(String... params) {
			populateBikeRacksFromServer();
			populateBuildingsFromServer();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			setUpMapIfNeeded();
			refreshMenuItem.collapseActionView();
			refreshMenuItem.setActionView(null);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		new LoadallData().execute();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (database != null) {
			database.close();
		}
		if (welcome_dialog != null) {
			if (welcome_dialog.isShowing()) {
				welcome_dialog.cancel();
			}
		}
	}

}