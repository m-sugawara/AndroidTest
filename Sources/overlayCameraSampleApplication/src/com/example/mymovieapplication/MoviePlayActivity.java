package com.example.mymovieapplication;


import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;


public class MoviePlayActivity extends ActionBarActivity {

	private static final String TAG = "tag";
	
	// LayoutParamsにセットする基本パラメータ
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		//setContentView(R.layout.activity_main);

		//前画面からの情報取得
		Intent data = getIntent();		
		//インテントの付加情報取得
		Bundle extras = data.getExtras();
		//不可情報から選択された値取得
		String file_name = extras != null ? extras.getString("FILE_NAME") : "";
		
		LinearLayout ll_root = new LinearLayout(this);
		ll_root.setOrientation(LinearLayout.VERTICAL);
		
		// TextViewを生成しLinearLayoutの下部に追加
        TextView textView = new TextView(this);
        textView.setText(file_name);
        //textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MP, WC);
        ll_root.addView(textView, param);

        // VideoViewを生成して動画再生
        VideoView videoView = new VideoView(this);
        //videoView.setLayoutParams(new LayoutParams(WC, WC));
        //Videoのディレクトリを取得
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
        //VideoViewにセット
        String file_url = mediaStorageDir.toString()+"/"+file_name;
        videoView.setVideoPath(file_url);
        Log.e("file_url", file_url);
        ll_root.addView(videoView);
        videoView.start();
        
        setContentView(ll_root);

	}
	
}
