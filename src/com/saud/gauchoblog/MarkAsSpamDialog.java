package com.saud.gauchoblog;

import com.parse.ParseObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MarkAsSpamDialog extends Dialog implements android.view.View.OnClickListener {
	
	Context a;
	String title, message, userId, entryId; 
	Button Yes, No;
	TextView t, m;
	MarkAsSpamListener my_listener;
	int position;
	
	public MarkAsSpamDialog(Context a) {
		super(a);
		this.a = a;
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}
	
	public void setVariables(String title, String message, String userId, String entryId, int position) {
		
		this.title = title;
		this.message = message;
		this.userId = userId;
		this.entryId = entryId;
		this.position = position;
	}
	
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog_1);
		my_listener = (MarkAsSpamListener) a;
		
        t = (TextView) findViewById(R.id.title_1);
        m = (TextView) findViewById(R.id.message_1);
        
        t.setText(title);
        m.setText(message);
		
		Yes = (Button) findViewById(R.id.btn_yes);
		Yes.setOnClickListener(this);
		
		No = (Button) findViewById(R.id.btn_no);
		No.setOnClickListener(this);

    }
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id.btn_yes:
			dismiss();
			ParseObject o = ParseObject.createWithoutData("BlogEntry", entryId);
			o.addUnique("spam", userId);
			o.increment("spamCount");
			o.saveInBackground();
			my_listener.onMarkedAsSpam(position);
			break;
			
	    case R.id.btn_no:
	    	dismiss();
	    	break;
	    	
	    }
						
	}
}

