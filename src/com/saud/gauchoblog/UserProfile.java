package com.saud.gauchoblog;

import java.io.ByteArrayOutputStream;
import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class UserProfile extends Activity implements ClickListener {
	
	private ImageSelectDialog image_select_dialog;
    private static int CAMERA_REQUEST = 0;
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView profile_picture;
    private EditText username, emailaddress;
    private Button save_changes;
	private CustomDialogClass custom_dialog;
	private ParseUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		profile_picture = (ImageView) findViewById(R.id.profile_picture_user);
		username = (EditText) findViewById(R.id.profile_username);
		emailaddress = (EditText) findViewById(R.id.profile_email_address);
		save_changes = (Button) findViewById(R.id.save_changes);
		
		user = ParseUser.getCurrentUser();
		
		username.setText(user.getUsername());
		emailaddress.setText(user.getEmail());
		
		ParseFile profileImage = user.getParseFile("profileImage");
		profileImage.getDataInBackground(new GetDataCallback() {
			public void done(byte[] data, ParseException e) {
				if(e == null) {
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
					profile_picture.setImageBitmap(bmp);
				} else {
					custom_dialog = new CustomDialogClass(UserProfile.this);
					custom_dialog.setVariables("Error", e.getMessage());
					custom_dialog.show();
				}
			}
		});
		
		profile_picture.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {       	
            	image_select_dialog = new ImageSelectDialog(UserProfile.this);
				image_select_dialog.show();
            }
            
        });
		
        save_changes.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
            	
            	if (username.getText().toString().equals("") || emailaddress.getText().toString().equals("")) {
			    	custom_dialog = new CustomDialogClass(UserProfile.this);
					custom_dialog.setVariables("Error", "Missing information. All fields are required.");
					custom_dialog.show();
				} else if (isEmailValid(emailaddress.getText().toString()) == false) {
					emailaddress.setError("Please enter a valid email address");
				} else {
					
					Bitmap bmp = ((BitmapDrawable) profile_picture.getDrawable()).getBitmap();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					Bitmap thumbnail = Bitmap.createScaledBitmap(bmp, 128, 128, true);
					thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos);
					byte[] imageBytes = baos.toByteArray();
					
					final ParseFile pFile = new ParseFile("profileImage.png", imageBytes);
					pFile.saveInBackground();
					user.put("profileImage", pFile);
	            	user.setUsername(username.getText().toString());
	            	user.setEmail(emailaddress.getText().toString());
					user.saveInBackground();
					
					custom_dialog = new CustomDialogClass(UserProfile.this);
					custom_dialog.setVariables("Changes Saved", "Your profile was successfully updated.");
					custom_dialog.show();

				}
 	
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
         
    	ImageView imageView = (ImageView) findViewById(R.id.profile_picture_user);
        
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
	
	private boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	@Override
	public void onBackPressed() {
	    
		super.onBackPressed();
	    finish();
		overridePendingTransition(R.anim.blog_slide_in_left,R.anim.blog_slide_out_left);
		
	}

}