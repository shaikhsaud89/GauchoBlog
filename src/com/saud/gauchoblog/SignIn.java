package com.saud.gauchoblog;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.ParseException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SignIn extends Activity {
	
	private CustomDialogClass custom_dialog;
    private MyProgressDialog progress_dialog;
	private int[] image_list = {R.drawable.image1, R.drawable.image2, R.drawable.image3};
	private int flipper = 0;
    private Button about_button, sign_up_button, forgot_password_button, sign_in_button;
    private EditText username, password;
				
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
						
        sign_in_button = (Button) findViewById(R.id.sign_in);
		about_button = (Button) findViewById(R.id.about);
		sign_up_button = (Button) findViewById(R.id.sign_up);
		forgot_password_button = (Button) findViewById(R.id.forgotpassword);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		
		loadSavedPreferences();
		
		View.OnClickListener myListener1 = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(SignIn.this, About.class);
				startActivity(intent);
				overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );

			}
		};
		about_button.setOnClickListener(myListener1);
		
		View.OnClickListener myListener2 = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(SignIn.this, SignUp.class);
				startActivity(intent);
				overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );

			}
		};
		sign_up_button.setOnClickListener(myListener2);	
		
		View.OnClickListener myListener3 = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(SignIn.this, ForgotPassword.class);
				startActivity(intent);
				overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );

			}
		};
		forgot_password_button.setOnClickListener(myListener3);
				
		View.OnClickListener myListener4 = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (username.getText().toString().equals("") && (password.getText().toString().equals(""))) {
					custom_dialog = new CustomDialogClass(SignIn.this);
					custom_dialog.setVariables("Sign In Failed", "Oops, please check your username and password.");
					custom_dialog.show();
				} else if (password.getText().toString().equals("")) {
					custom_dialog = new CustomDialogClass(SignIn.this);
					custom_dialog.setVariables("Sign In Failed", "Oops, please check your password.");
					custom_dialog.show();
				} else if (username.getText().toString().equals("")) {
					custom_dialog = new CustomDialogClass(SignIn.this);
					custom_dialog.setVariables("Sign In Failed", "Oops, please check your username.");
					custom_dialog.show(); 
				} else {
	
					saveSignInPreferences();
					
					progress_dialog = new MyProgressDialog(SignIn.this);
					progress_dialog.setVariables("Signing In..");
					progress_dialog.show();
					
					ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
						
						  public void done(ParseUser user, ParseException e) {
							  
							if(progress_dialog != null) {
								progress_dialog.dismiss();
							}
							
						    if(user != null) {
						    	
						    	boolean verified = user.getBoolean("emailVerified");
						    	
						    	if(verified) {
						    		finish();
						    		Intent intent = new Intent(SignIn.this, MapActivity.class);
									startActivity(intent);
									overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);  
						    		
						    	} else {
						    		custom_dialog = new CustomDialogClass(SignIn.this);
									custom_dialog.setVariables("Sign In Failed", "Unable to Sign In. Please verify your email address.");
									custom_dialog.show();
						    	}
						    	
						    } else {
						    	custom_dialog = new CustomDialogClass(SignIn.this);
								custom_dialog.setVariables("Sign In Failed", e.getMessage());
								custom_dialog.show();
						    }
						    
						  }
						  
					});
					
				}
				
			}
		};
		sign_in_button.setOnClickListener(myListener4);
		
		final AlphaAnimation animstay1 = new AlphaAnimation(1.0f, 1.0f);
		animstay1.setDuration(4000);
		
		final AlphaAnimation animfadeout1 = new AlphaAnimation(1.0f, 0.0f);
		animfadeout1.setDuration(2000);
		
        final AlphaAnimation animfadein1 = new AlphaAnimation(0.0f, 1.0f);
		animfadein1.setDuration(500);
		
		layout.startAnimation(animstay1);
		
		final AlphaAnimation animstay2 = new AlphaAnimation(1.0f, 1.0f);
		animstay2.setDuration(4000);
		
		final AlphaAnimation animfadeout2 = new AlphaAnimation(1.0f, 0.1f);
		animfadeout2.setDuration(2000);
		
        final AlphaAnimation animfadein2 = new AlphaAnimation(0.0f, 1.0f);
		animfadein2.setDuration(1000);
		
		layout.startAnimation(animstay1);
		
        animstay1.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationEnd(android.view.animation.Animation animation) {

				layout.startAnimation(animfadeout1);
				
			}
			@Override
			public void onAnimationRepeat(android.view.animation.Animation animation) {
					
			}
			@Override
			public void onAnimationStart(android.view.animation.Animation animation) {
					
			}
	    });
		
        animfadeout1.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationEnd(android.view.animation.Animation animation) {
				
				if(flipper == 0) {
					flipper = 1;
				} else if(flipper == 1) { 
					flipper = 2;
				} else if(flipper == 2) {
					flipper = 0;
				}
				layout.setVisibility(View.GONE);
			    layout.setBackgroundResource(image_list[flipper]);
				layout.startAnimation(animfadein1);
					
			}
			@Override
			public void onAnimationRepeat(android.view.animation.Animation animation) {
					
			}
			@Override
			public void onAnimationStart(android.view.animation.Animation animation) {
					
			}
	    });
		
		animfadein1.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationEnd(android.view.animation.Animation animation) {

				layout.setVisibility(View.VISIBLE);
				layout.startAnimation(animstay1);
					
			}
			@Override
			public void onAnimationRepeat(android.view.animation.Animation animation) {
					
			}
			@Override
			public void onAnimationStart(android.view.animation.Animation animation) {
					
			}
	    });
				      		
	}
	
	private void loadSavedPreferences() {
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		String saved_email = sharedPref.getString("Email", "");
		String saved_password = sharedPref.getString("Password", "");
		
	    if (saved_email.length() > 0) {
	    	username.setText(saved_email);
	    }
	     
		if (saved_password.length() > 0) {
			password.setText(saved_password);
		}	
		
	}
	
	private void saveSignInPreferences() {
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("Email", username.getText().toString());
		editor.putString("Password", password.getText().toString());
		editor.apply();
		
	}
	
	@Override
    protected void onDestroy() {
    	
        super.onDestroy();
        saveSignInPreferences();
        
    }

}