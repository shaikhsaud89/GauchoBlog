package com.saud.gauchoblog;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ForgotPassword extends Activity {
	
	private CustomDialogClass custom_dialog;
    final Context mContext = this;
    private MyProgressDialog progress_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);			    
		
		Button continue_button =(Button) findViewById(R.id.continue_button);
		
		View.OnClickListener myListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final EditText email_for_reset = (EditText) findViewById(R.id.email_for_reset);
				
				if (email_for_reset.getText().toString().equals("")) {
					email_for_reset.setError("Please enter your email address");
				} else if (isEmailValid(email_for_reset.getText().toString()) == false) {
					email_for_reset.setError("Please enter a valid email address");
				} else {

					progress_dialog = new MyProgressDialog(ForgotPassword.this);
					progress_dialog.setVariables("Please wait..");
					progress_dialog.show();
					
					ParseUser.requestPasswordResetInBackground(email_for_reset.getText().toString(), new RequestPasswordResetCallback() {
						public void done(ParseException e) {
							
							if(progress_dialog != null) {
								progress_dialog.dismiss();
							}
							
							if (e == null) {
								finish();
								Intent i = new Intent(ForgotPassword.this, EmailSent.class);
								i.putExtra("Email", email_for_reset.getText().toString());
								startActivity(i);
							} else {									
									custom_dialog = new CustomDialogClass(ForgotPassword.this);
									custom_dialog.setVariables("Reset Failed", e.getMessage());
									custom_dialog.show();
							}
						}
					});
				}
			}
		};
		continue_button.setOnClickListener(myListener);
		
	}

	private boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	@Override
	public void onBackPressed() {	    
		finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);  
	}

}