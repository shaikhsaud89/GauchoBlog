package com.saud.gauchoblog;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "LocationsManager";

	private static final String TABLE_BUILDINGS = "Buildings";
	private static final String TABLE_BIKE_RACKS = "BikeRack";

	private static final String KEY_PARSE_ID = "parse_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_UPDATED_AT = "updated_at";
	private static final String KEY_STATUS = "status";
	private static final String KEY_IMAGE_ID = "image_id";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_BUILDINGS_TABLE = "CREATE TABLE " + TABLE_BUILDINGS + "("
				+ KEY_PARSE_ID + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_LATITUDE + " REAL," + KEY_LONGITUDE + " REAL,"
				+ KEY_IMAGE_ID + " TEXT" + ")";
		String CREATE_BIKE_RACKS_TABLE = "CREATE TABLE " + TABLE_BIKE_RACKS
				+ "(" + KEY_PARSE_ID + " TEXT PRIMARY KEY," + KEY_LATITUDE
				+ " REAL," + KEY_LONGITUDE + " REAL," + KEY_UPDATED_AT
				+ " INTEGER," + KEY_STATUS + " TEXT" + ")";
		db.execSQL(CREATE_BUILDINGS_TABLE);
		db.execSQL(CREATE_BIKE_RACKS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDINGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIKE_RACKS);
		onCreate(db);

	}

	public void addBuilding(BuildingDTO b) {

		SQLiteDatabase db = this.getWritableDatabase();

		try {

			ContentValues values = new ContentValues();
			values.put(KEY_PARSE_ID, b.getParseId());
			values.put(KEY_IMAGE_ID, b.getImageId());
			values.put(KEY_NAME, b.getName());
			values.put(KEY_LATITUDE, b.getLatitude());
			values.put(KEY_LONGITUDE, b.getLongitude());

			db.insertWithOnConflict(TABLE_BUILDINGS, KEY_IMAGE_ID, values,
					SQLiteDatabase.CONFLICT_REPLACE);

		} finally {
			db.close();
		}

	}

	public void addBikeRack(BikeRack b) {

		SQLiteDatabase db = this.getWritableDatabase();

		try {

			ContentValues values = new ContentValues();
			values.put(KEY_PARSE_ID, b.getParseId());
			values.put(KEY_LATITUDE, b.getLatitude());
			values.put(KEY_LONGITUDE, b.getLongitude());
			values.put(KEY_UPDATED_AT, b.getDate());
			values.put(KEY_STATUS, b.getStatus());

			db.insertWithOnConflict(TABLE_BIKE_RACKS, null, values,
					SQLiteDatabase.CONFLICT_REPLACE);

		} finally {
			db.close();
		}

	}

	public void addBuildingList(List<BuildingDTO> list) {

		SQLiteDatabase db = this.getWritableDatabase();

		db.beginTransaction();

		try {
			for (BuildingDTO b : list) {
				ContentValues values = new ContentValues();
				values.put(KEY_PARSE_ID, b.getParseId());
				values.put(KEY_IMAGE_ID, b.getImageId());
				values.put(KEY_NAME, b.getName());
				values.put(KEY_LATITUDE, b.getLatitude());
				values.put(KEY_LONGITUDE, b.getLongitude());

				db.insertWithOnConflict(TABLE_BUILDINGS, KEY_IMAGE_ID, values,
						SQLiteDatabase.CONFLICT_REPLACE);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}

	}

	public void addBikeRackList(List<BikeRack> list) {

		SQLiteDatabase db = this.getWritableDatabase();

		db.beginTransaction();

		try {
			for (BikeRack b : list) {
				ContentValues values = new ContentValues();
				values.put(KEY_PARSE_ID, b.getParseId());
				values.put(KEY_LATITUDE, b.getLatitude());
				values.put(KEY_LONGITUDE, b.getLongitude());
				values.put(KEY_UPDATED_AT, b.getDate());
				values.put(KEY_STATUS, b.getStatus());

				db.insertWithOnConflict(TABLE_BIKE_RACKS, null, values,
						SQLiteDatabase.CONFLICT_REPLACE);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}

	}

	public List<BuildingDTO> getAllBuildings() {

		List<BuildingDTO> list = new ArrayList<BuildingDTO>();

		String selectQuery = "SELECT  * FROM " + TABLE_BUILDINGS;

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				BuildingDTO b = new BuildingDTO();
				b.setParseId(c.getString(c.getColumnIndex(KEY_PARSE_ID)));
				b.setImageId(c.getString(c.getColumnIndex(KEY_IMAGE_ID)));
				b.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				b.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
				b.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));

				list.add(b);
			} while (c.moveToNext());
		}

		db.close();
		return list;

	}

	public List<BikeRack> getAllBikeRacks() {

		List<BikeRack> list = new ArrayList<BikeRack>();

		String selectQuery = "SELECT  * FROM " + TABLE_BIKE_RACKS;

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				BikeRack b = new BikeRack();
				b.setParseId(c.getString(c.getColumnIndex(KEY_PARSE_ID)));
				b.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
				b.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
				b.setDate(c.getLong(c.getColumnIndex(KEY_UPDATED_AT)));
				b.setStatus(c.getString(c.getColumnIndex(KEY_STATUS)));

				list.add(b);
			} while (c.moveToNext());
		}

		db.close();
		return list;

	}

	public void updateBikeRackStatus(String parseId, String status,
			long updatedAt) {

		SQLiteDatabase db = null;
		ContentValues cv = new ContentValues();
		cv.put(KEY_STATUS, status);
		cv.put(KEY_UPDATED_AT, updatedAt);
		String whereClause = KEY_PARSE_ID + "='" + parseId + "'";

		try {
			db = this.getWritableDatabase();
			db.update(TABLE_BIKE_RACKS, cv, whereClause, null);
		} finally {
			if (null != db) {
				db.close();
			}
		}

	}

	public long getNumBikeRacks() {
		String sql = "SELECT COUNT(*) FROM " + TABLE_BIKE_RACKS;
		SQLiteDatabase db = null;
		try {
			db = this.getReadableDatabase();
			SQLiteStatement s = db.compileStatement(sql);
			return s.simpleQueryForLong();
		} finally {
			if (null != db) {
				db.close();
			}
		}
	}

	public long getNumBuildings() {
		String sql = "SELECT COUNT(*) FROM " + TABLE_BUILDINGS;
		SQLiteDatabase db = null;
		try {
			db = this.getReadableDatabase();
			SQLiteStatement s = db.compileStatement(sql);
			return s.simpleQueryForLong();
		} finally {
			if (null != db) {
				db.close();
			}
		}
	}

}