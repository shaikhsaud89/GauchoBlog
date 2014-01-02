package com.saud.gauchoblog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;

public class ImageSelectDialog extends Dialog implements android.view.View.OnClickListener {
	
	Activity a;
	int position; 
	Button Ok, Cancel;
	ClickListener my_listener;
	
	public ImageSelectDialog(Activity a) {
		super(a);
		this.a = a;
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.image_select_dialog);
		
		my_listener = (ClickListener) a;
		
		final RadioButton take_photo = (RadioButton) findViewById(R.id.radio_camera);
		final RadioButton choose_existing_photo = (RadioButton) findViewById(R.id.radio_gallery);
		take_photo.setChecked(true);
		take_photo.setBackgroundColor(Color.TRANSPARENT);
				
		Ok = (Button) findViewById(R.id.option_select_btn_ok);
		Ok.setOnClickListener(this);
		
		Cancel = (Button) findViewById(R.id.option_select_btn_cancel);
		Cancel.setOnClickListener(this);
		
		View.OnClickListener mycameraListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				take_photo.setBackgroundColor(Color.TRANSPARENT);
				position = 0;

			}
		};
		take_photo.setOnClickListener(mycameraListener);
		
		View.OnClickListener myListener3 = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				choose_existing_photo.setBackgroundColor(Color.TRANSPARENT);
				position = 1;

			}
		};
		choose_existing_photo.setOnClickListener(myListener3);

    }
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id.option_select_btn_ok:
			dismiss();
			my_listener.onPositiveClick(position);
			break;
			
	    case R.id.option_select_btn_cancel:
	    	dismiss();
	    	break;
	    	
	    }
						
	}
	
}