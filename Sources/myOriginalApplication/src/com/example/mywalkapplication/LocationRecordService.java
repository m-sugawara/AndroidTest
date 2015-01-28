/*************************
 * 現在地の位置情報を一定間隔で取得し続ける
 */
package com.example.mywalkapplication;

import com.example.mywalkapplication.CreateDBHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationRecordService extends Service implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
	
	//マシン非依存の改行コード
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	//記録の制御
	private Boolean isRecording = false;
	//Locationclient
	private LocationClient locationClient;
	//DB関連
	CreateDBHelper db = null;
	private Long record_id;  //レコードのキーになる
	private String movie_file_path = "";
	//通常時は5000ms間隔で現在地を更新
	//他のアプリケーションの割り込みなどがあれば50msで更新
	//現在位置取得の精度はGPS優先、GPS使えない場合はWiFiの電波位置で取得する
	private static final LocationRequest REQUEST = LocationRequest.create().
			setInterval(5000)
			.setFastestInterval(50)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	//位置記録用
	private LatLng now_location;
	private float[] distance = {0.0f};
	private float total_distance = 0.0f;
	//直前位置表示
	private Long before_id;
	private LatLng before_location;
	//経過時間（ms）を取得する
	int startTime = 0;
    int currentTime = 0;
	
	//Binder given to clients
	private final IBinder mBinder = new LocalBinder();
	
	/**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocationRecordService getService() {
        	Log.e("getService", "return-Binder");
            // Return this instance of LocalService so clients can call public methods
            return LocationRecordService.this;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e("onBind", "onBind");
		//MAPの初期設定
        setUpMapIfNeeded();
        //DBの初期設定
        db = new CreateDBHelper(this);
        //ムービーファイルパスの取得
        Bundle extras = intent.getExtras();
        movie_file_path = extras.getString("MOVIE_FILE_PATH");
;        
		return mBinder;
	}
	
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate", "serviceCreate");
        /*
        //MAPの初期設定
        setUpMapIfNeeded();
        //DBの初期設定
        db = new CreateDBHelper(this);
         */

    }
    
    private void setUpMapIfNeeded() {
    	//Try to obtain the map from the SupporMapFragment.
    	/*
    	mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
    			.getMap();
    	if (mMap != null) 
    	{
    		//現在地などの情報を取得できるようにする
    		mMap.setMyLocationEnabled(true);
    	}
    	*/
    	Log.e("LocationRecorderService", "setUpMapIfNeeded");
    	locationClient = new LocationClient(
    			getApplicationContext(), this, this);
    	if(locationClient != null)
    	{
    		Log.e("LocationRecorderService", "setUpMapIfNeeded-locationClient is not null");
    		//Connects the client to Google Play services.
    		locationClient.connect();
    	}
    	
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Log.e("LocationRecorderService", "onConnected");
		try {
			locationClient.requestLocationUpdates(REQUEST, this);
		} catch (Exception e) {
			Log.e("onConnected", "Fail to requestLocationUpdates");
		}
		//DBにレコードを作成
		Log.e("create_record", movie_file_path);
		record_id = db.createUserLocation(movie_file_path);
		//DBにレコードを作成するタイミングで時間の計測開始
		startTime = (int) System.currentTimeMillis();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("LocationRecordService", "onDestroy");
		if(locationClient != null)
    	{
			locationClient.disconnect();
    	}
	}

	@Override
	public void onDisconnected() {
		Log.e("LocationRecordService", "onDisconnected");
		// TODO Auto-generated method stub
		db.close();
	}

	/********
	 * 位置情報を更新するたびに呼び出されるメソッド
	 */
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		String temp_message = "";
		//計測記録中のみ
		now_location = new LatLng(location.getLatitude(), location.getLongitude());
		//前回位置を取得
		if(before_id != null)
		{
			before_location = db.getUserLocationRecord(before_id);
			//二点間の距離を計測
			Location.distanceBetween(before_location.latitude,
						before_location.longitude,
						now_location.latitude,
						now_location.longitude,
						distance);
			//合計移動距離を加算
			total_distance += distance[0];
		}

		//位置情報を画面に表示
		temp_message = "now:(" + String.valueOf(now_location.latitude) + "," + String.valueOf(now_location.longitude) + ")"
					+ LINE_SEPARATOR + "distance:" + String.valueOf(distance[0])
					+ LINE_SEPARATOR + "total distance:" + String.valueOf(total_distance);
		//前回位置を続けて表示
		if(before_location != null)
		{
			temp_message += 
				LINE_SEPARATOR + 
				"old:(" + String.valueOf(before_location.latitude) + "," + String.valueOf(before_location.longitude) + ")";
		}
		
		
		//Log.e("onLocationChanged", temp_message);
		Toast.makeText(getApplicationContext(), temp_message, Toast.LENGTH_SHORT).show();
		
		//経過時間を取得
		currentTime = (int) System.currentTimeMillis() - startTime;
		Log.e("onLocationChanged", "recording..." + String.valueOf(currentTime));
		//DBに位置情報、移動距離、経過時間を登録
		//次に呼び出すときにIDを使うので、beforeになる
		before_id = db.createUserLocationRecord(record_id, now_location, distance[0], total_distance, currentTime);
		
	}


}
