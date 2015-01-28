/******************************
 * 撮影したムービーファイルを再生
 * かつ移動した地図情報を表示
 */
package com.example.mywalkapplication;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.R.color;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;


public class SavedDataPlayActivity extends CommonFragmentActivity {

	private static final String TAG = "SavedDataPlayActivity";
	private static final int CONTENT_VIEW_ID = 101010;
	
	private static final int MENU_SELECT_TWEET = 99;
	private static final int MENU_SELECT_RESET = 100;
	
	private static final String TWEET_MESSAGE = "m移動しました！";
	
	// LayoutParamsにセットする基本パラメータ
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    
    //選択された動画のパス、レコードID
    String file_path = "";
    Long record_id;
    
    private Fragment mapViewFragment;
    
    //動画を再生するView
    VideoView mVideoView;
    //動画再生処理
    private VideoPlayAsync mVideoPlayAsync;
    //地図
    private GoogleMap mMap;
    //初期表示倍率
    private final float FIRST_VIEW_ZOOM_RATE = 17.0f;
    //地図上の線の色・幅
    private final int MAP_LINE_COLOR = 0xcc00ffff;
    private final float MAP_LINE_WIDTH = 10.0f;
    //表示中のマーカー
    private Marker mMarker;
    //動画の最大再生時間
    private int duration;
    //動画の再生開始時間
    private int setupTime = 0;
    //動画の再生開始からの経過時間
    private int currTime = 0;
    //動画の進捗度（MAX100)
    private int currProgress = 0; 
    
    
    
    //保存された位置データ
    List<UserLocationRecord> user_location_records = new ArrayList<UserLocationRecord>();
	//DB関連
	CreateDBHelper db = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		//setContentView(R.layout.activity_main);

		//DBの初期設定
        db = new CreateDBHelper(this);
		//前画面からの情報取得
		Intent data = getIntent();		
		//インテントの付加情報取得
		Bundle extras = data.getExtras();
		//送信情報から選択された値取得
		file_path = extras != null ? extras.getString("FILE_PATH") : "";
		record_id = extras != null ? extras.getLong("RECORD_ID") : 0;
		Log.e("selectedRecordID", String.valueOf(record_id));
        //DBに保存された位置データを取得
        user_location_records = db.getUserLocationRecordsByRecordId(record_id);
		//レイアウト生成
		createLayout();
		
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//データをMapに描画
        initMapData();
        
	}
	
	@Override
	public void onStop() {
		super.onStop();
		//再生中の動画をストップさせる。
		if ((mVideoPlayAsync != null) && (!mVideoPlayAsync.isCancelled())) {
			mVideoPlayAsync.cancel(false);
		}

	}
	
	/******
	 * 画面のレイアウトを生成する
	 * 
	 */
	private void createLayout() {
		LinearLayout ll_root = new LinearLayout(this);
		ll_root.setOrientation(LinearLayout.VERTICAL);
		ll_root.setId(CONTENT_VIEW_ID);
		
		//MAP表示用Fragmentを画面に追加
	    mapViewFragment = new MapViewFragment();
	    mapViewFragment.getId();
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    ft.add(CONTENT_VIEW_ID, mapViewFragment).commit();
	    
	    //Video表示用Frame
	    FrameLayout fl_video_viewer = new FrameLayout(this);
	    fl_video_viewer.setLayoutParams(new FrameLayout.LayoutParams(MP, WC));
	    fl_video_viewer.setBackgroundColor(color.black);

        // VideoViewを生成して動画再生
        mVideoView = new VideoView(this);
        mVideoView.setLayoutParams(new LayoutParams(MP, MP));
        mVideoView.setVideoPath(file_path);
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
            	Log.e("onPrepared", "ready...OK");
            	duration = mVideoView.getDuration();
            }
        });
        
        ImageButton ib_video_button = new ImageButton(this);
        ib_video_button.setLayoutParams((new LinearLayout.LayoutParams(WC, WC)));
        ib_video_button.setImageResource(R.drawable.ic_launcher);
        ib_video_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//再生中の動画をストップさせる。
				if ((mVideoPlayAsync != null) && (!mVideoPlayAsync.isCancelled())) {
					mVideoPlayAsync.cancel(false);
				} else {
					//ビデオ処理を生成
			      	mVideoPlayAsync = new VideoPlayAsync();
			        
			        mVideoPlayAsync.execute();
				}
			}
        });
        
        
        fl_video_viewer.addView(mVideoView);
        fl_video_viewer.addView(ib_video_button);
        
        ll_root.addView(fl_video_viewer);
        
        setContentView(ll_root);
	}
	
	/*******
	 * VideoViewの進捗を管理する
	 * @author m_sugawara
	 *
	 */
    private class VideoPlayAsync extends AsyncTask<Void, Integer, Void>
    {
        int current = 0;   //ビデオの現在位置（不正確なのでcurrTimeで代用）
        int before_currTime = 0; //前回再生時の経過時間

        @Override
        protected void onPreExecute() {
            Log.e("call onPreExecute() ", "duration-" + String.valueOf(duration));
        }
        
        @Override
        protected Void doInBackground(Void... params) {
        	//ビデオ再生スタート
	      	setupTime = (int)System.currentTimeMillis();
	      	if(currTime != 0) {
	      		before_currTime = currTime; 
	      	}
	      	mVideoView.start();
	      	
            do {
                current = mVideoView.getCurrentPosition();
                currTime = (int)System.currentTimeMillis() - setupTime + before_currTime;
                
                try {
                	//if(duration != 0 && currProgress != (int) (currTime * 100 / duration))
                	if(duration != 0 && duration != -1)
                	{
                		currProgress = (int) (currTime * 100 / duration);
                		//onProgressUpdateを呼び出す。
                		publishProgress(currTime);
                	}
                    //進捗が100を超えたら終わり。
                    if(currProgress >= 100){
                        break;
                    }
                } catch (Exception e) {
                	Log.e("progress-error", e.getMessage());
                	publishProgress(100);
                	break;
                }
                
                try {
                	//1秒おきに実行
                	Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Log.e("sleepError", e.getMessage());
					e.printStackTrace();
				}
            } while (currProgress <= 100 && !mVideoPlayAsync.isCancelled());

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //System.out.println(values[0]);
            //Log.e("onProgessUpdate", String.valueOf(values[0]));
            int before_time = 0;
    		for(UserLocationRecord user_location_record : user_location_records) {
    			//経過時間を見て現在位置を探す
    			if(values[0] >= before_time && values[0] < user_location_record.getDuration())
    			{
    				//Log.e("user_location_record.getDuration", String.valueOf(user_location_record.getDuration()));
	    			//マーカーを設置
	    			createMaker(user_location_record.getLatLng()
	    					, user_location_record.getCreatedAt());
	    			//Log.e(String.valueOf(user_location_record.getId()), user_location_record.getCreatedAt());
    			}
    			//今回の時間を記録して次の比較に使用
    			before_time = user_location_record.getDuration();
    		}
        }
        
        @Override
        protected void onCancelled() {
        	currTime = (int)System.currentTimeMillis() - setupTime + before_currTime;
        	mVideoView.pause();
        }
    }
	
	/************************
	 * 保存された位置データを使って、MAPに移動の軌跡を表示
	 */
	private void initMapData() {
		try {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		} catch (Exception e) {
			if(e.getMessage() == null){
				Log.e("error", "map is null");
			} else {
				Log.e("error", e.getMessage());
			}
		}
		if(mMap != null)
		{
			//現在地表示ボタンON
			mMap.setMyLocationEnabled(true);
			//初期表示位置設定
			if(user_location_records != null){
				//初期表示位置はデータの先頭座標にセット
				changeCameraPosition(user_location_records.get(0).getLatLng(), FIRST_VIEW_ZOOM_RATE);
				//移動の軌跡を表示
				loadMapData();
			}
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
	
	/**************************
	 * 保存データから軌跡を表示
	 */
	private void loadMapData() {
		if(mMap == null) 
		{
			return;
		}
		PolylineOptions options = new PolylineOptions();
		for(UserLocationRecord user_location_record : user_location_records) {
			//線を引く
			options.add(user_location_record.getLatLng());
			/*
			//マーカーを設置
			createMaker(user_location_record.getLatLng()
					, user_location_record.getCreatedAt());
			Log.e(String.valueOf(user_location_record.getId()), user_location_record.getCreatedAt());
			*/
		}
		options.color(MAP_LINE_COLOR);
		options.width(MAP_LINE_WIDTH);
		options.geodesic(true);  //測地線で表示
		mMap.addPolyline(options);
	}
	
	/****************************
	 * 地図上にマーカーを設置
	 */
	public void createMaker(LatLng latlng, String title) {
		if(mMap == null)
		{
			return;
		}
		//すでに表示されているマーカーは削除する
		if(mMarker != null)
		{
			mMarker.remove();
		}
		MarkerOptions options = new MarkerOptions();
		options.position(latlng);
		options.title(title);
		try{
			mMarker = mMap.addMarker(options);
		} catch (Exception e) {
			Log.e("markeradderror", e.getMessage());
		}
	}
	
	/*************
	 * メニューを追加
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
	    menu.add(0, MENU_SELECT_TWEET, 0, "Twitterでつぶやく");
	    menu.add(0, MENU_SELECT_RESET, 0, "リセット");
	 
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
	    switch (item.getItemId()) {
	    case MENU_SELECT_TWEET:
	        Intent intent = new Intent(SavedDataPlayActivity.this, TwitterTweetActivity.class);
	        
	        String message = "";
	        message += user_location_records.get(user_location_records.size()-1).getTotalDistance();
	        message += TWEET_MESSAGE;
	        intent.putExtra("MESSAGE", message);
	        
	        startActivity(intent);
	        return true;
	 
	    case MENU_SELECT_RESET:
	        Log.e("Menu","Select Menu RESET");
	        //再生中の動画をストップさせる。
			if ((mVideoPlayAsync != null) && (!mVideoPlayAsync.isCancelled())) {
				mVideoPlayAsync.cancel(false);
			}
	        //動画を初期位置に戻す
	        mVideoView.seekTo(0);
	        //動画の再生開始からの経過時間
	        currTime = 0;
	        //動画の進捗度（MAX100)
	        currProgress = 0; 
	        
	        return true;
	 
	    }
	    return false;
	}
	
}
