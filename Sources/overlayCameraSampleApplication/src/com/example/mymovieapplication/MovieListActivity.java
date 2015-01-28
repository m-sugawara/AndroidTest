package com.example.mymovieapplication;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MovieListActivity extends ActionBarActivity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		//setContentView(R.layout.movie_play);
		
		LinearLayout ll_file_list = new LinearLayout(this);
		ll_file_list.setOrientation(LinearLayout.VERTICAL);
		
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
		
		TextView tv_message = new TextView(this);
		tv_message.setText(mediaStorageDir.toString());
		ll_file_list.addView(tv_message);
		
		File[] files = mediaStorageDir.listFiles();
		int i = 1;
		for(File f : files)
		{
			if(f.isFile() || f.isDirectory())
			{
				try
				{
					Button bt = new Button(this);
					bt.setText(f.getName());
					bt.setTag(f.getName());
					bt.setMinimumWidth(400);
					bt.setOnClickListener(new MoviePlayButtonClickListener());
					ll_file_list.addView(bt);
					i++;
				}
				catch(Exception e){}
			}
		}
		LinearLayout ll_root = new LinearLayout(this);
		ScrollView sv_scroll = new ScrollView(this);
		sv_scroll.addView(ll_file_list);
		ll_root.addView(sv_scroll);
		setContentView(ll_root);
		/*
		Button bt_movie = (Button)findViewById(R.id.bt_movie);
		bt_movie.setTag(BUTTON_TAG_MOVIE);
		bt_movie.setOnClickListener(new ButtonClickListener());
		*/
	}
	
	public class MoviePlayButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String tag = v.getTag().toString();

			Intent intent = new Intent(MovieListActivity.this, MoviePlayActivity.class);
			intent.putExtra("FILE_NAME", tag);
			startActivity(intent);
		}
		
	}

}
