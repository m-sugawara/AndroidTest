/*********************
 * マップデータを表示する
 */
package com.example.mywalkapplication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.support.v4.app.FragmentTransaction;

public class MapViewActivity extends CommonFragmentActivity implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
	private static final int CONTENT_VIEW_ID = 101010;
	
	private Fragment newFragment;
	private GoogleMap mMap;
	//Locationclient
	private LocationClient locationClient;
	//表示倍率
    private final float ZOOM_RATE = 14.0f;
	//通常時は5000ms間隔で現在地を更新
	//他のアプリケーションの割り込みなどがあれば50msで更新
	//現在位置取得の精度はGPS優先、GPS使えない場合はWiFiの電波位置で取得する
	private static final LocationRequest REQUEST = LocationRequest.create().
			setInterval(5000)
			.setFastestInterval(50)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //アクションバーの有効化
    getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

    //setContentView(R.layout.map_view);
    FrameLayout frame = new FrameLayout(this);
    frame.setId(CONTENT_VIEW_ID);
    setContentView(frame, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    
    if (savedInstanceState == null) {
    	newFragment = new MapViewFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(CONTENT_VIEW_ID, newFragment).commit();
        
       //MAPの初期設定
        //setUpMapIfNeeded();
    }
  }
    
    private void setUpMapIfNeeded() {
    	//Try to obtain the map from the SupporMapFragment.
    	//mMap = ((SupportMapFragment) newFragment.get))
    	//		.getMap();
    	if (mMap != null) 
    	{
    		//現在地などの情報を取得できるようにする
    		mMap.setMyLocationEnabled(true);
    	}
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
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	private boolean isFirst = true;
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(mMap != null && isFirst == true){
			//初期表示位置はデータの先頭座標にセット
			changeCameraPosition(new LatLng(location.getLatitude(),location.getLongitude()), ZOOM_RATE);
			isFirst = false;
		}
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

	/*************************
	 * MAPのカメラ表示を移動
	 * @param latlng 移動先緯度経度
	 * @param zoom 表示倍率
	 */
	private void changeCameraPosition(LatLng latlng, Float zoom) {
		if(mMap == null)
		{
			return;
		}
		CameraPosition cp = new CameraPosition.Builder()
			.target(latlng).zoom(zoom).bearing(0).build();
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
	}
	

}