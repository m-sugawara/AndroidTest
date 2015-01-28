package com.example.mymovieapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends ActionBarActivity {
	
	private static final String BUTTON_TAG_MOVIE = "movie";
	private static final String BUTTON_TAG_MOVIE_PLAY = "movie_play";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		setContentView(R.layout.activity_main);
		
		Button bt_movie = (Button)findViewById(R.id.bt_movie);
		bt_movie.setTag(BUTTON_TAG_MOVIE);
		bt_movie.setOnClickListener(new ButtonClickListener());
		
		Button bt_movie_play = (Button)findViewById(R.id.bt_movie_play);
		bt_movie_play.setTag(BUTTON_TAG_MOVIE_PLAY);
		bt_movie_play.setOnClickListener(new ButtonClickListener());
	}
	
	public class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String tag = v.getTag().toString();
			
			if(tag == BUTTON_TAG_MOVIE) {
				Intent intent = new Intent(MenuActivity.this, MovieActivity.class);
				startActivity(intent);
			}else if(tag == BUTTON_TAG_MOVIE_PLAY) {
				Intent intent = new Intent(MenuActivity.this, MovieListActivity.class);
				startActivity(intent);
			}
		}
		
	}

}
