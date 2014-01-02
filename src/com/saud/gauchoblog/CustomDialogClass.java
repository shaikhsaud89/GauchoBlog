package com.saud.gauchoblog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {
	
	Activity a;
	String title, message; 
	Button OK;
	TextView t, m;
	
	public CustomDialogClass(Activity a) {
		super(a);
		this.a = a;
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}
	
	public void setVariables(String title, String message) {	
		this.title = title;
		this.message = message;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog);
		
        t = (TextView) findViewById(R.id.title);
        m = (TextView) findViewById(R.id.message);
        
        t.setText(title);
        m.setText(message);
		
		OK = (Button) findViewById(R.id.btn_ok);
		OK.setOnClickListener(this);

    }
	
	@Override
	public void onClick(View v) {	
		dismiss();
	}
	
}