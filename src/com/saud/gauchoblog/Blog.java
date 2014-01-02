package com.saud.gauchoblog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class Blog extends Activity implements ClickListener, MarkAsSpamListener {
	
	private ListView mainListView;
	private ViewSwitcher switcher;
	private TextView nothing_posted, blogfeed_buildingname;
	private ProgressBar refresh_progressbar;
	private BlogAdapter blogAdapter;
	private Button postButton;
	private ImageView BlogFeed_Refresh;
	private CustomDialogClass custom_dialog;
	private MyProgressDialog progress_dialog;
    private byte[] imageBytes = null;
	private static int CAMERA_REQUEST = 0;
    private static int RESULT_LOAD_IMAGE = 1;
	private static String parseId = null, buildingname = null;
    private ImageSelectDialog image_select_dialog;
    private static String ALBUM_NAME = "gauchoblogimages";
    private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private String mCurrentPhotoPath;

    private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = AlbumDirFactory.getAlbumStorageDir(ALBUM_NAME);

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
    
    private void setPic() {

		int targetW = 400;
		int targetH = 400;

		ExifInterface exif;
		int orientation = 0;
		int rotation = 0;
		try {
			exif = new ExifInterface(mCurrentPhotoPath);
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(orientation == 6){
			rotation = 90;
        } else if(orientation == 8){
            rotation = 270;
        } else if(orientation == 3){
            rotation = 180;
        } else if(orientation == 9){
            rotation = 90;
        }
		
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		Matrix matrix = new Matrix();
		matrix.postRotate(rotation);
		if(rotation != 0)
		{
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		imageBytes = baos.toByteArray();
		
	}
    
    private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
    
    private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}
	
	private class LoadUsername extends AsyncTask<Void, Integer, Void> {
		
		private String userId;
		
		public LoadUsername(String userId) {
			this.userId = userId;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			ParseQuery<ParseUser> query_username = ParseUser.getQuery();
			
			ParseUser user;
			try {
				user = query_username.get(userId);
				ParseFile profileImage = user.getParseFile("profileImage");
				byte[] data = profileImage.getData();
				Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				
				for(int i=0; i<blogAdapter.getCount(); i++) {
					BlogEntry item = blogAdapter.getItem(i);
					if(item != null) {
						if(userId.equals(item.getUserId())) {
							blogAdapter.getItem(i).setUserName(user.getUsername());
							item.setProfileImage(bmp);
						}
					}
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
				
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			blogAdapter.notifyDataSetChanged();
		}
		
	}
	
	private class LoadObjectId extends AsyncTask<Void, Integer, Void> {
		
		private String uuid;
		private ParseObject object;
		
		public LoadObjectId(String uuid, ParseObject object) {
			this.uuid = uuid;
			this.object = object;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				object.save();
				for(int i = 0; i< blogAdapter.getCount(); i++) {
			    	BlogEntry item = blogAdapter.getItem(i);
			    	if(item != null) {
				   		if(item.getObjectId().equals(uuid)) {
				   			blogAdapter.getItem(i).setObjectId(object.getObjectId());
				   			break;
						}
			    	}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			blogAdapter.notifyDataSetChanged();
		}
	}
	
	private class LoadImage extends AsyncTask<Void, Integer, Void> {
		
		private ParseFile blogImage;
		private String entryId;
		
		LoadImage(String entryId, ParseFile blogImage ) {
			this.entryId = entryId;
			this.blogImage = blogImage;
		}
		
		@Override
		protected Void doInBackground(Void... params) {				
			for(int i = 0; i< blogAdapter.getCount(); i++) {
		    	BlogEntry item = blogAdapter.getItem(i);
		    	if(item != null) {
			   		if(item.getObjectId().equals(entryId)) {
			   			byte[] data = null;
						try {
							data = blogImage.getData();
						} catch (ParseException e) {
							e.printStackTrace();
						}
						if(data != null) {
							BitmapFactory.Options opts = new BitmapFactory.Options();	            
							Bitmap blogImage = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
							item.setBlogImage(blogImage);
						}
					}
		    	}
			}
						
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			blogAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog);

		mainListView = (ListView) findViewById(R.id.mblog_list);
		switcher = (ViewSwitcher) findViewById(R.id.blog_view_switcher);
		nothing_posted = (TextView) findViewById(R.id.no_blog_posted);
		BlogFeed_Refresh = (ImageView) findViewById(R.id.blogfeed_refresh);
	    refresh_progressbar = (ProgressBar) findViewById(R.id.refresh_progresscircle);
	    blogfeed_buildingname = (TextView) findViewById(R.id.blogfeed_buildingname);

		parseId = getIntent().getExtras().getString("parseId");
		buildingname = getIntent().getExtras().getString("name");

	    blogfeed_buildingname.setText(buildingname);
		
		View.OnClickListener RefreshListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				new RefreshBlog().execute();

			}
		};
		BlogFeed_Refresh.setOnClickListener(RefreshListener);
		
		ImageView picButton = (ImageView)findViewById(R.id.microblog_picture);
		picButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
            	
            	image_select_dialog = new ImageSelectDialog(Blog.this);
				image_select_dialog.show();
            }
            
        });

		progress_dialog = new MyProgressDialog(Blog.this);
		progress_dialog.setVariables("Loading Data..");
		progress_dialog.show();
		
		blogAdapter = new BlogAdapter(this, R.layout.micro_blog, new ArrayList<BlogEntry>());
		mainListView.setAdapter(blogAdapter);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("BlogEntry");
		query.orderByDescending("createdAt");
		query.whereEqualTo("buildingId", parseId);
		query.whereNotContainedIn("spam", Arrays.asList(ParseUser.getCurrentUser().getObjectId()));
		query.whereLessThanOrEqualTo("spamCount", 4);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if(progress_dialog != null) {
					progress_dialog.dismiss();
				}
				if (e == null) {
					if(objects.size() == 0) {
						nothing_posted.setText("No blog has been posted yet. Be the first!");
					} else {
						switcher.showNext();
						
						Set<String> uniqueUserIds = new HashSet<String>();
						Map<String, ParseFile> blogImages = new HashMap<String, ParseFile>();
						
						for (ParseObject o : objects) {
							String userId = o.getString("userId"); 
							
								BlogEntry b = new BlogEntry();
								b.setUserId(userId);
								b.setText(o.getString("text"));
								b.setUserName("Loading...");
								b.setObjectId(o.getObjectId());
								b.setDate(o.getCreatedAt());
								ParseFile imageFile = o.getParseFile("image");
								blogAdapter.add(b);
								
								uniqueUserIds.add(userId);
								if(imageFile != null) {
									blogImages.put(o.getObjectId(), imageFile);
								}
							
							
						}
												
						for(String s : uniqueUserIds) {
							new LoadUsername(s).execute();
						}
						for(Map.Entry<String, ParseFile> entry : blogImages.entrySet()) {
							new LoadImage(entry.getKey(), entry.getValue()).execute();
						}
						
					}
				} else {

				}
			}
		});

		postButton = (Button) findViewById(R.id.microblog_post);
		postButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on
				EditText blogText = (EditText) findViewById(R.id.microblog_message);

				if (blogText.getText().toString().equals("")) {
					custom_dialog = new CustomDialogClass(Blog.this);
					custom_dialog.setVariables("Please Enter you blog", "Here");
				} else {

					String parseId = getIntent().getExtras().getString("parseId");
					ParseUser user = ParseUser.getCurrentUser();
					
					ParseObject blogEntry = new ParseObject("BlogEntry");
					blogEntry.put("buildingId", parseId);
					blogEntry.put("text", blogText.getText().toString());
					blogEntry.put("userId", ParseUser.getCurrentUser().getObjectId());
					blogEntry.put("spamCount", 0);
					String uuid = UUID.randomUUID().toString();
					if(imageBytes != null) {
						final ParseFile imageFile = new ParseFile("image.jpeg", imageBytes);
						imageFile.saveInBackground();
						blogEntry.put("image", imageFile);
					}
					
					BlogEntry b = new BlogEntry();
					b.setText(blogText.getText().toString());
					b.setUserName(user.getUsername());
					b.setDate(new Date());
					b.setObjectId(uuid);
					ParseFile profileImage = user.getParseFile("profileImage");
					try {
						byte[] data = profileImage.getData();
						Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
						b.setProfileImage(bmp);
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					if(imageBytes != null) {
						BitmapFactory.Options opts = new BitmapFactory.Options();
			            Bitmap blogImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);
			            b.setBlogImage(blogImage);
					}
					
					if(0 == blogAdapter.getCount()) {
						switcher.showNext();
					}
							
					blogAdapter.insert(b, 0);
					blogAdapter.notifyDataSetChanged();
					mainListView.setSelection(0);
					
					new LoadObjectId(uuid, blogEntry).execute();
										
					ParseQuery pushQuery = ParseInstallation.getQuery();
					pushQuery.whereEqualTo("channels", "gauchoblog");
					pushQuery.whereNotEqualTo("installationId", ParseInstallation.getCurrentInstallation().getInstallationId());
					ParsePush push = new ParsePush();
					push.setQuery(pushQuery);
					push.setMessage(user.getUsername() + " has posted a new microblog at " + buildingname + ".");
					push.setExpirationTimeInterval(60*60);
					push.sendInBackground();
					
					imageBytes = null;
					blogText.setText("");
					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(blogText.getWindowToken(), 0);
					Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
	
	@Override
	public void onPositiveClick(int pos) {
		
		if (pos == 0) {
	    	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
	
	    	File f = null;
	    	try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}	    	
	        startActivityForResult(cameraIntent, CAMERA_REQUEST); 
    	}
    					
    	if (pos == 1) {
    	Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    	}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

			Uri uri = data.getData();

			String path;
			Cursor cursor = null;

			try {
				cursor = getContentResolver()
						.query(uri, null, null, null, null);
				cursor.moveToFirst();
				int idx = cursor
						.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				path = cursor.getString(idx);
			}

			finally {
				if (cursor != null) {
					cursor.close();
				}
			}

			File file = new File(path);
			imageBytes = new byte[(int) file.length()];
			try {
				BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
				buf.read(imageBytes, 0, imageBytes.length);
				buf.close();

				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);
				int sampleSize = BitmapHelper.calculateInSampleSize(opts, 512, 512); // should be 256
				opts.inSampleSize = sampleSize;
				opts.inJustDecodeBounds = false;

				Bitmap blogImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);
				blogImage = Bitmap.createScaledBitmap(blogImage, opts.outWidth, opts.outHeight, true);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				blogImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				imageBytes = baos.toByteArray();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && mCurrentPhotoPath != null) {

			setPic();
			
		}

	}
	
	public void refreshBlog() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("BlogEntry");
		query.whereEqualTo("buildingId", parseId);
		if(blogAdapter.getCount() > 0)
		{
			Date latest_time = blogAdapter.getItem(0).getDate();
			query.whereGreaterThan("createdAt", latest_time);
		}
		query.whereNotContainedIn("spam", Arrays.asList(ParseUser.getCurrentUser().getObjectId()));
		query.whereNotEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
		query.whereLessThanOrEqualTo("spamCount", 4);
		query.orderByAscending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				Set<String> uniqueUserIds = new HashSet<String>();
				Map<String, ParseFile> blogImages = new HashMap<String, ParseFile>();
				if(objects.size() > 0)
				{
					if(0 == blogAdapter.getCount()) {
						switcher.showNext();
					}
				}
				for (ParseObject o : objects) {
						String userId = o.getString("userId"); 
						
						BlogEntry b = new BlogEntry();
						b.setUserId(userId);
						b.setText(o.getString("text"));
						b.setUserName("Loading...");
						b.setObjectId(o.getObjectId());
						b.setDate(o.getCreatedAt());
						ParseFile imageFile = o.getParseFile("image");
						
						blogAdapter.insert(b, 0);
											
						uniqueUserIds.add(userId);
						if(imageFile != null) {
							blogImages.put(o.getObjectId(), imageFile);
						}
					
					
				}
				
				blogAdapter.notifyDataSetChanged();
				mainListView.setSelection(0);
				
				for(String s : uniqueUserIds) {
					new LoadUsername(s).execute();
				}
				for(Map.Entry<String, ParseFile> entry : blogImages.entrySet()) {
					new LoadImage(entry.getKey(), entry.getValue()).execute();
				}
				Toast.makeText(getApplicationContext(),
						"Done", Toast.LENGTH_SHORT)
						.show();
			}
		});

	}
	
	class RefreshBlog extends AsyncTask<Void, Void, Void> {
	    @Override
	    protected void onPreExecute() {    	
	    	BlogFeed_Refresh.setVisibility(View.INVISIBLE);
	    	refresh_progressbar.setVisibility(View.VISIBLE);
	    }

	    @Override
	    protected Void doInBackground(Void... params) {
	    	refreshBlog();
	    	return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	    	refresh_progressbar.setVisibility(View.INVISIBLE);
	    	BlogFeed_Refresh.setVisibility(View.VISIBLE);    	
	    }
	}

	@Override
	public void onMarkedAsSpam(int pos) {
		blogAdapter.removePosition(pos);
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.blog_slide_in_left,R.anim.blog_slide_out_left);  
	}

}