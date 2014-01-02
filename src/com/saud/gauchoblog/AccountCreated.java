package com.saud.gauchoblog;

import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class AccountCreated extends Activity {
	
	private TextView message_to_display;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_created);
		
		Intent intent = getIntent();
		String email = intent.getExtras().getString("Email");
		
	    message_to_display = (TextView) findViewById(R.id.thank_you_note);
	    
	    message_to_display.setText("THANK YOU for signing up with GauchoBlog. An email has been sent to " + email + " for verification. Please very your email address before signing in.");
		
	}
	
	@Override
	public void onBackPressed() {   
		finish();
	}

}
