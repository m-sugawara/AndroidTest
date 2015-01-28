/*********************
 * メインメニュー
 */
package com.example.mywalkapplication;

import com.example.mywalkapplication.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends ActionBarActivity {
	
	private static final String BUTTON_TAG_MOVIE = "movie";
	private static final String BUTTON_TAG_MOVIE_PLAY = "movie_play";
	private static final String BUTTON_TAG_SAVED_DATA_LIST = "saved_data_list";
	private static final String BUTTON_TAG_MAP_VIEW = "map_view";
	private static final String BUTTON_TAG_TWITTER = "twitter";
	private static final String BUTTON_TAG_TEST = "test";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		setContentView(R.layout.main_menu);
		
		//各メニューボタン生成
		createButton(R.id.bt_movie, BUTTON_TAG_MOVIE);
		createButton(R.id.bt_movie_play, BUTTON_TAG_MOVIE_PLAY);
		createButton(R.id.bt_saved_data_list, BUTTON_TAG_SAVED_DATA_LIST);
		createButton(R.id.bt_map_view, BUTTON_TAG_MAP_VIEW);
		createButton(R.id.bt_twitter, BUTTON_TAG_TWITTER);
		createButton(R.id.bt_test, BUTTON_TAG_TEST);
	}
	
	public void createButton(int id, String tag) {
		Button button = (Button)findViewById(id);
		button.setTag(tag);
		button.setOnClickListener(new ButtonClickListener());
		
		if(id == R.id.bt_test)//テスト機能を中止
		{
			button.setEnabled(false);
		}
	}
	
	public class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String tag = v.getTag().toString();
			
			if(tag == BUTTON_TAG_MOVIE) {
				startNewActivity(MovieActivity.class);
			}else if(tag == BUTTON_TAG_MOVIE_PLAY) {
				startNewActivity(MovieListActivity.class);
			}else if(tag == BUTTON_TAG_SAVED_DATA_LIST) {
				startNewActivity(SavedDataListActivity.class);
			}else if(tag == BUTTON_TAG_MAP_VIEW) {
				startNewActivity(MapViewActivity.class);
			}else if(tag == BUTTON_TAG_TWITTER) {
				startNewActivity(TwitterMainActivity.class);
			}else if(tag == BUTTON_TAG_TEST) {
				startNewActivity(testActivity.class);
			}
		}
		
	}
	
	/*****************
	 * アクティビティを起動する
	 * 
	 * @param cls 対象アクティビティ
	 */
	public void startNewActivity(Class cls) {
		Intent intent = new Intent(MenuActivity.this, cls);
		startActivity(intent);
	}

}
