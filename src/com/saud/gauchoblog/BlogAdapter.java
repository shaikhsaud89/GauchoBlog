package com.saud.gauchoblog;

import java.text.SimpleDateFormat;
import java.util.List;
import com.parse.ParseUser;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BlogAdapter extends ArrayAdapter<BlogEntry>{
	
	private List<BlogEntry> objects;
	private MarkAsSpamDialog customDialog;
	
	public BlogAdapter(Context context, int resource, List<BlogEntry> objects) {
		super(context, resource, objects);
		this.objects = objects;
	}
	
	public void removePosition(int position)
	{
		objects.remove(position);
		notifyDataSetChanged();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		if(v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.micro_blog, null);
		}
		
		BlogEntry entry = objects.get(position);
		
		if(entry != null) {
			final String entryId = entry.getObjectId();
			
			TextView tv_message = (TextView) v.findViewById(R.id.message_text);
			TextView tv_userName = (TextView) v.findViewById(R.id.user_full_name);
			TextView tv_date = (TextView) v.findViewById(R.id.time_of_blog);
			ImageView iv_profileImage = (ImageView) v.findViewById(R.id.user_profile_image);
			ImageView iv_blogImage = (ImageView) v.findViewById(R.id.list_row_image);
			ImageView spam = (ImageView) v.findViewById(R.id.mark_as_spam);
			final int pos = position;
			
			View.OnClickListener spamListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					customDialog = new MarkAsSpamDialog(getContext());
					customDialog.setVariables("Mark as Spam?", "This post will no longer be visible to you.  If enough unique users mark this post as spam it will no longer be visible for any user.", ParseUser.getCurrentUser().getObjectId(),entryId, pos);
					customDialog.show();
					
				}
			};
			spam.setOnClickListener(spamListener);			
			
			if(tv_message != null) {
				tv_message.setText(entry.getText());
			}
			
			if(tv_userName != null) {
				tv_userName.setText(entry.getUserName());
			}
			
			if(tv_date != null) {
				SimpleDateFormat format = new SimpleDateFormat(DateHelper.DATE_FORMAT);
				tv_date.setText(format.format(entry.getDate()));
			}
			
			if(iv_profileImage != null) {
				iv_profileImage.setImageBitmap(entry.getProfileImage());
			}
			
			if(iv_blogImage != null) {
				iv_blogImage.setImageBitmap(entry.getBlogImage());
			}
			
		}
		
		return v;
		
	}
}