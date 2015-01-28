package com.example.mywalkapplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CreateDBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "myMapDB";
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
    
    // Table Names
    private static final String TABLE_USER_LOCATION = "user_location";
    private static final String TABLE_USER_LOCATION_RECORD = "user_location_record";
    
    // Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_RECORD_ID = "record_id";
    private static final String KEY_CREATED_AT = "created_at";
    
    // USER_LOCATION Table - column names
    private static final String KEY_MOVIE_FILE_PATH = "movie_file_path";
    
    // USER_LOCATION_RECORD Table - column names
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_TOTAL_DISTANCE = "total_distance";
    private static final String KEY_DURATION = "duration";
    
    // Table Create Statements
    // user_location table create statement
    private static final String CREATE_TABLE_USER_LOCATION = "CREATE TABLE "
            + TABLE_USER_LOCATION + "("
    		+ KEY_RECORD_ID + " INTEGER PRIMARY KEY," 
    		+ KEY_MOVIE_FILE_PATH + " TEXT, "
            + KEY_CREATED_AT + " TEXT" + ")";
    // user_location_record table create statement
    private static final String CREATE_TABLE_USER_LOCATION_RECORD = "CREATE TABLE "
            + TABLE_USER_LOCATION_RECORD + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
    		+ KEY_RECORD_ID + " INTEGER," 
            + KEY_LATITUDE + " REAL," 
    		+ KEY_LONGITUDE + " REAL," 
    		+ KEY_DISTANCE + " REAL," 
    		+ KEY_TOTAL_DISTANCE + " REAL," 
    		+ KEY_DURATION + " INTEGER," 
            + KEY_CREATED_AT + " TEXT" + ")";

	//コンストラクタ
	public CreateDBHelper (Context context) {
		//SQLiteOpenHelperのコンストラクタ呼び出し
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("CreateDBHelperConst", "CreateDBHelperConstructer");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// creating required tables
        db.execSQL(CREATE_TABLE_USER_LOCATION);
        db.execSQL(CREATE_TABLE_USER_LOCATION_RECORD);
        
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
    // ------------------------ "user_location" table methods ----------------//
    /**
    * Creating a user location
    */
   public Long createUserLocation(String movie_file_path) {
       SQLiteDatabase db = this.getWritableDatabase();

       ContentValues values = new ContentValues();
       values.put(KEY_MOVIE_FILE_PATH, movie_file_path);
       values.put(KEY_CREATED_AT, getDateTime());

       // insert row
       long record_id = db.insert(TABLE_USER_LOCATION, null, values);
       Log.e("DBTEST", String.valueOf(record_id));

       return record_id;
   }
   
   /**
   * Delete a user location
   */
  public int deleteUserLocation(Long record_id) {
	  if(record_id == null)
	  {
		  return -1;
	  } 
      SQLiteDatabase db = this.getWritableDatabase();

      // create where
      String where = "record_id = ?";
      String[] where_args = new String[]{String.valueOf(record_id)};
      // delete row
      int result = db.delete(TABLE_USER_LOCATION, where, where_args);
      Log.e("DBTEST", "delete user_location : " + String.valueOf(result));

      return result;
  }
   
   /**
    * get all user location
    */
   public List<UserLocation> getAllUserLocation() {
 	  List<UserLocation> user_location = new ArrayList<UserLocation>();
       SQLiteDatabase db = this.getReadableDatabase();

       String selectQuery = "SELECT * FROM " + TABLE_USER_LOCATION;

       Cursor c = db.rawQuery(selectQuery, null);

       // looping through all rows and adding to list
       if (c.moveToFirst()) {
           do {
               UserLocation ul = new UserLocation(
             		  c.getLong(c.getColumnIndex(KEY_RECORD_ID))
             		  ,c.getString(c.getColumnIndex(KEY_MOVIE_FILE_PATH))
             		  ,c.getString(c.getColumnIndex(KEY_CREATED_AT))
             		  );
               // adding to latlng list
               user_location.add(ul);
           } while (c.moveToNext());
       }

       return user_location;
   }

   
  
   // ------------------------ "user_location_record" table methods ----------------//
   /**
   * Creating a user location
   * 
   */
  public Long createUserLocationRecord(Long record_id, LatLng latlng, float distance, float total_distance, int duration) {
      SQLiteDatabase db = this.getWritableDatabase();

      ContentValues values = new ContentValues();
      values.put(KEY_RECORD_ID, record_id);
      values.put(KEY_LATITUDE, latlng.latitude);
      values.put(KEY_LONGITUDE, latlng.longitude);
      values.put(KEY_DISTANCE, distance);
      values.put(KEY_TOTAL_DISTANCE, total_distance);
      values.put(KEY_DURATION, duration);
      values.put(KEY_CREATED_AT, getDateTime());

      // insert row
      long _id = db.insert(TABLE_USER_LOCATION_RECORD, null, values);
      //Log.d("DBTEST", String.valueOf(_id));

      return _id;
  }
  
  /**
   * get single user location record by id
   */
  public LatLng getUserLocationRecord(long _id) {
	  SQLiteDatabase db = this.getReadableDatabase();
	  
	  String selectQuery = 
			  "SELECT * " +
			  "FROM " + TABLE_USER_LOCATION_RECORD +
			  " WHERE " + KEY_ID + " = " + _id;
	  
	  Cursor c = db.rawQuery(selectQuery, null);
	  
	  if (c != null){
		  c.moveToFirst();
	  }
	  
	  LatLng ll = new LatLng(
    		  c.getDouble(c.getColumnIndex(KEY_LATITUDE))
    		  ,c.getDouble(c.getColumnIndex(KEY_LONGITUDE))
    		  );
	  //Log.e("datacheck",String.valueOf(c.getDouble(c.getColumnIndex(KEY_LATITUDE))));
	  
	  return ll;
  }
  
  /**
   * get all user location record by record_id
   */
  public List<UserLocationRecord> getUserLocationRecordsByRecordId(long record_id) {
	  List<UserLocationRecord> user_location_records = new ArrayList<UserLocationRecord>();
      SQLiteDatabase db = this.getReadableDatabase();

      String selectQuery = "SELECT  * FROM " + TABLE_USER_LOCATION_RECORD + " WHERE "
              + KEY_RECORD_ID + " = " + record_id;

      Cursor c = db.rawQuery(selectQuery, null);

      // looping through all rows and adding to list
      if (c.moveToFirst()) {
          do {
              UserLocationRecord ulr = new UserLocationRecord(
            		  c.getLong(c.getColumnIndex(KEY_ID))
            		  ,c.getLong(c.getColumnIndex(KEY_RECORD_ID))
            		  ,c.getDouble(c.getColumnIndex(KEY_LATITUDE))
            		  ,c.getDouble(c.getColumnIndex(KEY_LONGITUDE))
            		  ,c.getFloat(c.getColumnIndex(KEY_DISTANCE))
            		  ,c.getFloat(c.getColumnIndex(KEY_TOTAL_DISTANCE))
            		  ,c.getInt(c.getColumnIndex(KEY_DURATION))
            		  ,c.getString(c.getColumnIndex(KEY_CREATED_AT))
            		  );
              // adding to user location record list
              user_location_records.add(ulr);
          } while (c.moveToNext());
      }

      return user_location_records;
  }
  
  /**
  * Delete user location records
  */
 public int deleteUserLocationRecordsByRecordId(Long record_id) {
	  if(record_id == null)
	  {
		  return -1;
	  } 
     SQLiteDatabase db = this.getWritableDatabase();

     // create where
     String where = "record_id = ?";
     String[] where_args = new String[]{String.valueOf(record_id)};
     // delete row
     int result = db.delete(TABLE_USER_LOCATION_RECORD, where, where_args);
     Log.e("DBTEST", "delete user_location_record : " + String.valueOf(result));

     return result;
 }
   
   // closing database
   public void closeDB() {
       SQLiteDatabase db = this.getReadableDatabase();
       if (db != null && db.isOpen())
           db.close();
   }

   /**
    * get datetime
    * */
   private String getDateTime() {
       SimpleDateFormat dateFormat = new SimpleDateFormat(
               "yyyy-MM-dd HH:mm:ss.sss", Locale.getDefault());
       Date date = new Date();
       return dateFormat.format(date);
   }
}
