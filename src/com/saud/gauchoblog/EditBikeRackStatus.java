package com.saud.gauchoblog;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.parse.ParseObject;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class EditBikeRackStatus extends Activity {

    private Button button_update;
	private CongestionLevel status;
	private String parseId;
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("status", status.toString());
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_edit_bike_rack_status);
		
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);		
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		status = CongestionLevel.OPEN;
		if(savedInstanceState != null) {
			String stringStatus = savedInstanceState.getString("status");
			if(stringStatus != null) {
				status = CongestionLevel.valueOf(stringStatus);

			}
		}
		
		RadioButton b = null;
		
		switch(status) {
		case HEAVY:
			b = (RadioButton) findViewById(R.id.radio_heavyCongestion);
			b.setBackgroundColor(Color.RED);
			break;
		case MILD:
			b = (RadioButton) findViewById(R.id.radio_mildCongestion);
			b.setBackgroundColor(0xffff8800);
			break;
		case OPEN:
			b = (RadioButton) findViewById(R.id.radio_open);
			b.setBackgroundColor(Color.GREEN);
			break;
		default:
			break;
		}
		
		b.setChecked(true);
		
		parseId = getIntent().getExtras().getString("parseId");
		long date = getIntent().getExtras().getLong("date");
		String status_string = getIntent().getExtras().getString("status");
		
		Date d = new Date(date);
		
		SimpleDateFormat format = new SimpleDateFormat("MMM d h:mm a");
		
		TextView lastUpdated = (TextView) findViewById(R.id.edit_bike_last_updated_time);
		
		if (CongestionLevel.NONE.toString().equalsIgnoreCase(status_string)) {
			lastUpdated.setText("Last Updated At: -- ");
		} else {
			lastUpdated.setText("Last Updated At: " + format.format(d).toString());
		}
		
		button_update = (Button) findViewById(R.id.button_update_bike_rack_status);
	    View.OnClickListener myListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				ParseObject o = ParseObject.createWithoutData("Rack", parseId); 
				o.put("status", status.toString());
				o.saveInBackground();
				Intent returnIntent = new Intent();
				returnIntent.putExtra("status", status.toString());
				returnIntent.putExtra("parseId", parseId);
				setResult(RESULT_OK,returnIntent); 
				finish();
				
			}
	    };
	    button_update.setOnClickListener(myListener);
	}
	
	public void onRadioButtonClicked(View view) {
	    
		RadioButton button = (RadioButton) view;
	    
	    RadioButton open = (RadioButton) findViewById(R.id.radio_open);
	    RadioButton mildcongestion = (RadioButton) findViewById(R.id.radio_mildCongestion);
	    RadioButton heavycongestion = (RadioButton) findViewById(R.id.radio_heavyCongestion);
	    
	    open.setBackgroundColor(Color.TRANSPARENT);
	    mildcongestion.setBackgroundColor(Color.TRANSPARENT);
	    heavycongestion.setBackgroundColor(Color.TRANSPARENT);
	    
	    switch (view.getId()) {
		case R.id.radio_open:
			button.setBackgroundColor(Color.GREEN);
			status = CongestionLevel.OPEN;
			break;
		case R.id.radio_mildCongestion:
			button.setBackgroundColor(0xffff8800);
			status = CongestionLevel.MILD;
			break;
		case R.id.radio_heavyCongestion:
			button.setBackgroundColor(Color.RED);
			status = CongestionLevel.HEAVY;
			break;
		}
	    
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    finish();
		overridePendingTransition(R.anim.blog_slide_in_left,R.anim.blog_slide_out_left);
	}

}