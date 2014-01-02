package com.saud.gauchoblog;

public class BuildingDTO {

	private String parseId;
	private String imageId;
	private String name;
	private double latitude;
	private double longitude;
	
	public BuildingDTO() {
		
	}
	
	public BuildingDTO(String parseId, String imageId, String name, double latitude, double longitude) {
		this.setParseId(parseId);
		this.setImageId(imageId);
		this.setName(name);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParseId() {
		return parseId;
	}
	public void setParseId(String parseId) {
		this.parseId = parseId;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	
}
