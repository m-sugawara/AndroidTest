/******************************
 * 撮影したムービーファイルを再生
 */
package com.example.mywalkapplication;


import java.io.File;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;


public class MoviePlayActivity extends CommonActivity {

	private static final String TAG = "tag";
	private static final int CONTENT_VIEW_ID = 101010;
	
	LinearLayout ll_root;
	
	// LayoutParamsにセットする基本パラメータ
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    
    private VideoView mVideoView;
    private ProgressBar mProgressBar;
    
    private TextView mTextView;
    
    private int max_duration;
    
    private String file_name = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		//setContentView(R.layout.activity_main);

		//前画面からの情報取得
		Intent data = getIntent();		
		//インテントの付加情報取得
		Bundle extras = data.getExtras();
		//付加情報から選択された値取得
		file_name = extras != null ? extras.getString("FILE_NAME") : "";
		
		//画面を生成する
		createLayout();
        
        new myAsync().execute();
	}
	
	/******
	 * 画面を生成する
	 */
	public void createLayout() {
		//最下層のレイアウト作成
		ll_root = new LinearLayout(this);
		ll_root.setOrientation(LinearLayout.VERTICAL);
		ll_root.setId(CONTENT_VIEW_ID);
		
		// TextViewを生成しLinearLayoutの下部に追加
        TextView textView = new TextView(this);
        textView.setText(file_name);
        //textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MP, WC);
        ll_root.addView(textView, param);

        // VideoViewを生成して動画再生
        mVideoView = new VideoView(this);
        //videoView.setLayoutParams(new LayoutParams(WC, WC));
        //Videoのディレクトリを取得
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
        //VideoViewにセット
        String file_url = mediaStorageDir.toString()+"/"+file_name;
        mVideoView.setVideoPath(file_url);
        Log.e("file_url", file_url);
        ll_root.addView(mVideoView);
        
        //プログレスバー生成
        mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);
        ll_root.addView(mProgressBar);
        
        //TextView生成
        mTextView = new TextView(this);
        mTextView.setText("please start...");
        ll_root.addView(mTextView);
        
        createNewButton("test", new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("getCurrentPostion", String.valueOf(mVideoView.getCurrentPosition()));
			}
        });
        createNewButton("goto_end", new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mVideoView.seekTo(max_duration);
			}
        });
        

        setContentView(ll_root);
	}
	
	/*******
	 * VideoViewの進捗を管理する
	 * @author m_sugawara
	 *
	 */
    private class myAsync extends AsyncTask<Void, Integer, Void>
    {
        int duration = 0;
        int current = 0;
        
        int setupTime = 0;
        int currTime = 0;
        
        int currProgress = 0;
        @Override
        protected Void doInBackground(Void... params) {
            mVideoView.setOnPreparedListener(new OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                	
                    duration = mVideoView.getDuration();
                    max_duration = duration;
                }
            });
            mVideoView.start();
            setupTime = (int)System.currentTimeMillis();

            do {
                current = mVideoView.getCurrentPosition();
                currTime = (int)System.currentTimeMillis() - setupTime;
                System.out.println("duration - " + duration + " current - "
                        + current);
                
                try {
                	if(duration != 0 && currProgress != (int) (currTime * 100 / duration))
                	{
                		currProgress = (int) (currTime * 100 / duration);
                		//onProgressUpdateを呼び出す。
                		publishProgress(currProgress);
                	}
                    //進捗が100を超えたら終わり。
                    if(mProgressBar.getProgress() >= 100){
                        break;
                    }
                } catch (Exception e) {
                	Log.e("progress-error", "bye");
                	publishProgress(100);
                	break;
                }
            } while (mProgressBar.getProgress() <= 100);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            System.out.println(values[0]);
            Log.e("onProgessUpdate", String.valueOf(values[0]));
            mTextView.setText(String.valueOf(values[0]));
            mProgressBar.setProgress(values[0]);
        }
    }
    
    private void createNewButton(String button_text, OnClickListener OCL) {
        Button newBt = new Button(this);
        newBt.setText(button_text);
        newBt.setOnClickListener(OCL);
        ll_root.addView(newBt);
    }
	
}
