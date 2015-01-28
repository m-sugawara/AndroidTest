package com.example.mymapapplication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends FragmentActivity
	implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener,OnClickListener {
	
	//マシン非依存の改行コード
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static GoogleMap mMap; //Might be null if Google Play services APK is not available.
	private LocationClient locationClient;
	//ボタン制御
	private Boolean isFirstLocation = true;
	private Boolean isRecording = false;
	//位置記録用
	private LatLng first_location;
	private LatLng now_location;
	//画面表示用
	private String message ="";
	//直前位置表示
	private Long before_id;
	private LatLng before_location;
	
	//通常時は5000ms間隔で現在地を更新
	//他のアプリケーションの割り込みなどがあれば50msで更新
	//現在位置取得の精度はGPS優先、GPS使えない場合はWiFiの電波位置で取得する
	private static final LocationRequest REQUEST = LocationRequest.create().
			setInterval(5000)
			.setFastestInterval(50)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	//DB関連
	CreateDBHelper db = null;
	private Long record_id;
	//Button用タグ
	private static final String BUTTON_TAG_START = "start";
	private static final String BUTTON_TAG_STOP = "stop";
	private static final String BUTTON_TAG_HISTORY = "history";

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //MAPの初期設定
        setUpMapIfNeeded();
        //DBの初期設定
        db = new CreateDBHelper(MainActivity.this);
        //ボタンの初期設定
        Button bt_start = (Button)findViewById(R.id.bt_start);
        bt_start.setTag(BUTTON_TAG_START);
        bt_start.setOnClickListener(this);
        
        Button bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_stop.setTag(BUTTON_TAG_STOP);
        bt_stop.setOnClickListener(this);
        
        Button bt_history = (Button)findViewById(R.id.bt_history);
        bt_history.setTag(BUTTON_TAG_HISTORY);
        bt_history.setOnClickListener(this);
        //メッセージ表示
        displayText("計測準備完了");

    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	setUpMapIfNeeded();
    }
    
    private void setUpMapIfNeeded() {
    	//Try to obtain the map from the SupporMapFragment.
    	mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
    			.getMap();
    	if (mMap != null) 
    	{
    		//現在地などの情報を取得できるようにする
    		mMap.setMyLocationEnabled(true);
    	}
    	locationClient = new LocationClient(
    			getApplicationContext(), this, this);
    	if(locationClient != null)
    	{
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

	/********
	 * 位置情報を更新するたびに呼び出されるメソッド
	 */
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		String temp_message = "";
		//計測記録中のみ
		if(isRecording == true) 
		{
			//計測開始位置取得
			if(isFirstLocation == true) 
			{
				first_location = new LatLng(location.getLatitude(), location.getLongitude());
				//DBに位置情報を登録
				db.createUserLocationRecord(record_id, first_location);
				
				//カメラを初期位置にセット
				CameraPosition cp = new CameraPosition.Builder()
					.target(first_location)
					.zoom(18.0f)
					.bearing(0)
					.build();
				//現在位置にカメラを移動
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
				
				//初期処理終了
				isFirstLocation = false;
			}
			now_location = new LatLng(location.getLatitude(), location.getLongitude());
			//前回位置を取得
			if(before_id != null)
			{
				before_location = db.getUserLocationRecord(before_id);
			}

			//位置情報を画面に表示
			temp_message = "(" + String.valueOf(now_location.latitude) + "," + String.valueOf(now_location.longitude) + ")";
			//前回位置を続けて表示
			if(before_location != null)
			{
				temp_message += 
					LINE_SEPARATOR + 
					"(" + String.valueOf(before_location.latitude) + "," + String.valueOf(before_location.longitude) + ")";
			}
			
			displayText(message + LINE_SEPARATOR + temp_message);
			
			//DBに位置情報を登録
			//次に呼び出すときにIDを使うので、beforeになる
			before_id = db.createUserLocationRecord(record_id, now_location);
			
			
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String button_tag = (String)v.getTag();
		
		if(button_tag == BUTTON_TAG_START) 
		{
			//記録開始
			message = "記録中...";
			displayText(message);
			//DB作成
			record_id = db.createUserLocation();
			//計測初期設定
			isRecording = true;
			isFirstLocation = true;
			//ボタン状態変更
			findViewById(R.id.bt_start).setEnabled(false);
			findViewById(R.id.bt_stop).setEnabled(true);
		}
		else if(button_tag == BUTTON_TAG_STOP) 
		{
			//記録終了
			displayText("記録終了");
			isRecording = false;
			//ボタン状態変更
			findViewById(R.id.bt_start).setEnabled(true);
			findViewById(R.id.bt_stop).setEnabled(false);
			
		}
		else if(button_tag == BUTTON_TAG_HISTORY)
		{
			//履歴画面を呼び出す
			Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
			
			startActivity(intent);
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		db.close();
	}

	/*************
	 * テキストを画面に表示
	 * @param _message　表示するテキスト
	 */
	public void displayText(String _message){
		TextView tv_message = (TextView)findViewById(R.id.tv_message);
		tv_message.setText(_message);
	}


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
}
