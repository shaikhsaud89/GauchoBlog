package com.saud.gauchoblog;

public class BikeRack {

	private String parseId;
	private String status;
	private double latitude;
	private double longitude;
	private long date;
	
	public BikeRack() {
		
	}
	
	public BikeRack(String parseId, double latitude, double longitude, long date, String status) {
		this.setParseId(parseId);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setDate(date);
		this.setStatus(status);
	}
	
	public String getParseId() {
		return parseId;
	}
	public void setParseId(String parseId) {
		this.parseId = parseId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}

}