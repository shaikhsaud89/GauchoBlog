package com.saud.gauchoblog;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

import android.app.Application;

public class App extends Application {

	 @Override
	  public void onCreate() {
	    super.onCreate();
	 
	    Parse.initialize(this, "tCcJBMwmJgIaRgWXWBEpiku9fhj4IWHtk7QxJJeZ", "dgtaTNioLz9qCjHgYFgn96B5y3BPTe7C4p2qDVgc");
	    PushService.setDefaultPushCallback(this, Animation.class);
	    ParseInstallation.getCurrentInstallation().saveInBackground();
	    PushService.subscribe(this, "gauchoblog", Animation.class, R.drawable.ic_launcher);
	    
	  }
	
}
