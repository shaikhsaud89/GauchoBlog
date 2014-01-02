package com.saud.gauchoblog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class WelcomeDialogClass extends Dialog {

	Activity a;
	String welcome_message; 
	TextView w_m;
	
	public WelcomeDialogClass(Activity a) {
		super(a);
		this.a = a;
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}
	
	public void setVariables(String welcome_message) {
		this.welcome_message = welcome_message;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_dialog);
		
        w_m = (TextView) findViewById(R.id.welcome_message);
        w_m.setText(welcome_message);

    }

}
