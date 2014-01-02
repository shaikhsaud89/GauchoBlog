package com.saud.gauchoblog;

import android.os.Bundle;
import android.app.Activity;

public class About extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}
	
	@Override
	public void onBackPressed() {    
		finish();
		overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);  
	}

}
