/**************
 * メニュー対応するためのベースアクティビティ
 */
package com.example.mywalkapplication;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;


public class CommonFragmentActivity extends FragmentActivity {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) 
		{
			case R.id.menu_mapview:
				startNewActivity(MapViewActivity.class);
				return true;
			case R.id.menu_movie_record:
				startNewActivity(MovieActivity.class);
				return true;
			case R.id.menu_play_save_data:
				startNewActivity(SavedDataListActivity.class);
				return true;
			case R.id.menu_play_movie:
				startNewActivity(MovieListActivity.class);
				return true;
			case R.id.menu_twitter:
				startNewActivity(TwitterMainActivity.class);
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void startNewActivity(Class Cls) {
		Intent intent = new Intent(getApplicationContext(), Cls);
		startActivity(intent);
	}
}
