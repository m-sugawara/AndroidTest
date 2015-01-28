package com.example.mymovieapplication;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MovieActivity extends ActionBarActivity {

	private static final String TAG = "tag";
	
	// LayoutParamsにセットする基本パラメータ
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		//setContentView(R.layout.activity_main);
		//画面の設定
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_CONTEXT_MENU);
		
		LinearLayout l = new LinearLayout(this);
		CameraPreview cp = new CameraPreview(this);
		l.addView(cp, new LinearLayout.LayoutParams(MP, 1000));
		
		
		// TextViewを生成しLinearLayoutの下部に追加
        TextView textView = new TextView(this);
        textView.setText("Sample");
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MP, WC);
        l.addView(textView, param);
        
        setContentView(l);

	}
	
}
