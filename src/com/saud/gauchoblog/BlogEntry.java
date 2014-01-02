package com.saud.gauchoblog;

import java.util.Date;

import android.graphics.Bitmap;

public class BlogEntry {
	
	private String objectId;
	private String userId;
	private String text;
	private String userName;
	private Date date;
	private Bitmap blogImage;
	private Bitmap profileImage;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Bitmap getBlogImage() {
		return blogImage;
	}
	public void setBlogImage(Bitmap blogImage) {
		this.blogImage = blogImage;
	}
	public Bitmap getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(Bitmap profileImage) {
		this.profileImage = profileImage;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
		
}