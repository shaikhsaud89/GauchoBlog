package com.saud.gauchoblog;

import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class EmailSent extends Activity {

	private TextView message_to_display;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_sent);
		
		Intent intent = getIntent();
		String email = intent.getExtras().getString("Email");
		
	    message_to_display = (TextView) findViewById(R.id.message_to_display);
	    
	    message_to_display.setText("An email was successfully sent to " + email + " with password reset instructions.");
		
	}
	
	@Override
	public void onBackPressed() {   
		finish();
	}

}
