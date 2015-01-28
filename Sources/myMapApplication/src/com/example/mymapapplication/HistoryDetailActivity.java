package com.example.mymapapplication;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HistoryDetailActivity extends FragmentActivity {
	
	//DB関連
	CreateDBHelper db = null;
	List<UserLocationRecord> user_location_records = new ArrayList<UserLocationRecord>();
	
	//Might be null if Google Play services APK is not available.
	private static GoogleMap mMap;
	
	//マシン非依存の改行コード
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_detail);
		
		//MAPの初期設定
        setUpMapIfNeeded();
		
		Intent data = getIntent();
		Bundle extras = data.getExtras();
		//レコードID取得
		Long record_id = extras != null ? extras.getLong("RECORD_ID") : 0;
		
        //DBの初期設定
        db = new CreateDBHelper(HistoryDetailActivity.this);
        if(record_id != null && record_id != 0){
        	user_location_records = db.getUserLocationRecordsByRecordId(record_id);
        }
        
    	//戻るボタンの設定
    	Button bt_return = (Button)findViewById(R.id.bt_return);
    	bt_return.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
    	});
    	
    	//画面表示
    	displayRecord();
        
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
    	
    }
	
	
	public void displayRecord(){
		
		TextView tv_record_detail = (TextView)findViewById(R.id.tv_record_detail);
		String text = "";
		
		// Instantiates a new Polyline object and adds points to define a rectangle
		PolylineOptions rectOptions = new PolylineOptions().width(25).color(Color.BLUE);
		
		for(int i=0;i<user_location_records.size();i++) {
			text += user_location_records.get(i).getCreatedAt();
			text += "(" + user_location_records.get(i).getLatitude() + "," + user_location_records.get(i).getLongitude() + ")";
			text += LINE_SEPARATOR;
			
			rectOptions.add(new LatLng(user_location_records.get(i).getLatitude(), user_location_records.get(i).getLongitude()));
		}
		
		tv_record_detail.setText(text);
		// Get back the mutable Polyline
		Polyline polyline = mMap.addPolyline(rectOptions);
		
		//カメラを初期位置にセット
		CameraPosition cp = new CameraPosition.Builder()
			.target(new LatLng(user_location_records.get(0).getLatitude(), user_location_records.get(0).getLongitude()))
			.zoom(18.0f)
			.bearing(0)
			.build();
		//現在位置にカメラを移動
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
		
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		db.close();
	}
	
	
}
