package com.saud.gauchoblog;

import java.io.ByteArrayOutputStream;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class SignUp extends Activity implements ClickListener {
	
	private CustomDialogClass custom_dialog;
    private MyProgressDialog progress_dialog;
    private ImageSelectDialog image_select_dialog;
    private static int CAMERA_REQUEST = 0;
    private static int RESULT_LOAD_IMAGE = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_sign_up);
	    
	    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    	    	    
	    final ImageView profile_picture = (ImageView) findViewById(R.id.profile_picture);
		Button create_account_button = (Button) findViewById(R.id.create_account);
	    
	    View.OnClickListener myListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
								
			    EditText username = (EditText) findViewById(R.id.enter_username);
			    EditText password = (EditText) findViewById(R.id.enter_password);
			    EditText retype_password = (EditText) findViewById(R.id.retype_password);
			    final EditText email = (EditText) findViewById(R.id.enter_email);
			    
			    if (username.getText().toString().equals("") || email.getText().toString().equals("") || password.getText().toString().equals("") || retype_password.getText().toString().equals("")) {
			    	custom_dialog = new CustomDialogClass(SignUp.this);
					custom_dialog.setVariables("Sign Up Failed", "Missing information. All fields are required.");
					custom_dialog.show();
				} else if (!password.getText().toString().equals(retype_password.getText().toString())) {
					retype_password.setError("Passwords do not match.");
				} else {
					
					final ParseUser user = new ParseUser();
					user.setUsername(username.getText().toString());
					user.setPassword(password.getText().toString());
					user.setEmail(email.getText().toString());
					
					progress_dialog = new MyProgressDialog(SignUp.this);
					progress_dialog.setVariables("Creating Account..");
					progress_dialog.show();
					
					user.signUpInBackground(new SignUpCallback() {
						
						public void done(ParseException e) {
							
							if(progress_dialog != null) {
								progress_dialog.dismiss();
							}
							
							if (e == null) {
							
								Bitmap bmp = ((BitmapDrawable) profile_picture.getDrawable()).getBitmap();
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								Bitmap thumbnail = Bitmap.createScaledBitmap(bmp, 128, 128, true);
								thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos);
								byte[] imageBytes = baos.toByteArray();
								
								final ParseFile pFile = new ParseFile("profileImage.png", imageBytes);
								pFile.saveInBackground();
								user.put("profileImage", pFile);
								user.saveInBackground();
								
								finish();
								Intent i = new Intent(SignUp.this, AccountCreated.class);
								i.putExtra("Email", email.getText().toString());
								startActivity(i);
								
							} else {
								
								custom_dialog = new CustomDialogClass(SignUp.this);
								custom_dialog.setVariables("Sign Up Failed", e.getMessage());
								custom_dialog.show();
								
							}
						}
					});
					
				}

			}
		};
		create_account_button.setOnClickListener(myListener);
	    
	    profile_picture.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
            	
            	image_select_dialog = new ImageSelectDialog(SignUp.this);
				image_select_dialog.show();
            	
            }
            
        });
	    
	}
	
	@Override
	public void onPositiveClick(int pos) {
		
		if (pos == 0) {
    	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
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
         
    	ImageView imageView = (ImageView) findViewById(R.id.profile_picture);
        
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
        	        	
        	Uri uri = data.getData();
        	
        	String path;
        	Cursor cursor = null;
        	
        	try {
        		cursor = getContentResolver().query(uri, null, null, null, null);
        		cursor.moveToFirst();
        		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        		path = cursor.getString(idx);
        	}
        	
        	finally {
        		if(cursor != null) {
        			cursor.close();
        		}
        	}
           
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            
            int height = opts.outHeight;
            int width = opts.outWidth;
            
            int reqHeight = imageView.getHeight();
            int reqWidth = imageView.getWidth();
            
            int heightRatio = Math.round((float)height / (float)reqHeight);
            int widthRatio = Math.round((float)width / (float)reqWidth);
            
            int sampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            
            opts.inSampleSize = sampleSize;
            
            opts.inJustDecodeBounds = false;
            
            Bitmap gallery_photo = BitmapFactory.decodeFile(path, opts);
            
            imageView.setImageBitmap(gallery_photo);     
            
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {  
                    		
        	Bitmap camera_photo = (Bitmap) data.getExtras().get("data"); 
            imageView.setImageBitmap(camera_photo);
                    	
        }  
     
    }

    @Override
	public void onBackPressed() {
	    
		finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);  
		
	}
    
    @Override
    protected void onDestroy() {
    	
        super.onDestroy();
        System.gc();
        
    }
	
}