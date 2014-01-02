package com.saud.gauchoblog;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class ParseProtocolModule {

	private static String LOCATION = "location";
	private static String STATUS = "status";
	private static String NAME = "name";
	private static String IMAGE_ID = "imageId";
	
	public static String BIKE_RACK_CLASS_NAME = "Rack";
	public static String BUILDING_CLASS_NAME = "Building";
	
	public static BikeRack convertBikeRackParseToLocal(ParseObject o) {
		BikeRack b = new BikeRack();
		b.setParseId(o.getObjectId());
		b.setDate(o.getUpdatedAt().getTime());
		b.setStatus(o.getString(STATUS));
		ParseGeoPoint geo = o.getParseGeoPoint(LOCATION);
		b.setLatitude(geo.getLatitude());
		b.setLongitude(geo.getLongitude());
		return b;
	}
	
	public static BuildingDTO convertBuildingParseToLocal(ParseObject o) {
		BuildingDTO b = new BuildingDTO();
		b.setParseId(o.getObjectId());
		b.setName(o.getString(NAME));
		ParseGeoPoint geo = o.getParseGeoPoint(LOCATION);
		b.setLatitude(geo.getLatitude());
		b.setLongitude(geo.getLongitude());
		b.setImageId(o.getString(IMAGE_ID));
		return b;
	}

}
