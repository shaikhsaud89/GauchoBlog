package com.saud.gauchoblog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class MyProgressDialog extends ProgressDialog {
	
	String message;
	TextView m;

	public MyProgressDialog(Context context) {
        super(context);
    }
	
    public void setVariables(String message) {
		this.message = message;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_dialog);
		
		setCancelable(false);
		setIndeterminate(true);
		
        m = (TextView) findViewById(R.id.progress_message);
        
        m.setText(message);
    }

}