package com.example.mywalkapplication;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterListActivity extends ListActivity {
	
	private TweetAdapter mAdapter;
	private Twitter mTwitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//OAuth認証が完了してなければ認証ページに飛ばす
		if (!TwitterUtils.hasAccessToken(this))
		{
			Intent intent = new Intent(this, TwitterOAuthActivity.class);
			startActivity(intent);
			finish();
		} 
		else
		{
			mAdapter = new TweetAdapter(this);
			setListAdapter(mAdapter);
			
			mTwitter = TwitterUtils.getTwitterInstance(this);
			reloadTimeLine();
		}
	}
	
	/**
	 * Twitterのタイムラインを読み込む
	 */
	private void reloadTimeLine() {
		AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>(){

			@Override
			protected List<twitter4j.Status> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					ResponseList<twitter4j.Status> timeline = mTwitter.getHomeTimeline();
					/*
					ArrayList<String> list = new ArrayList<String>();
					for (twitter4j.Status status : timeline) {
						list.add(status.getText());
					}
					return list;
					*/
					return timeline;
				} catch (TwitterException e) {
					Log.e("reloadTimeline", e.getMessage());
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<twitter4j.Status> result) {
				if (result != null) {
					mAdapter.clear();
					for (twitter4j.Status status : result) {
						mAdapter.add(status);
					}
					/*
					for (String status : result) {
						mAdapter.add(status);
					}
					*/
					getListView().setSelection(0);
				}
				else
				{
					showToast("タイムラインの取得に失敗しました。。。");
				}
			}
			
		};
		task.execute();
	}
	
	/***
	 * タイムラインをリスト形式で表示する。
	 * android.R.layout.simple_list_item_1はもともと用意されている定義済みのレイアウトファイルのID
	 *
	 */
	private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {
		
		private LayoutInflater mInflater;
		
		public TweetAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1);
			mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
			{
				convertView = mInflater.inflate(R.layout.list_item_tweet, null);
			}
			twitter4j.Status item = getItem(position);
			
			TextView name = (TextView)convertView.findViewById(R.id.tweet_name);
			name.setText(item.getUser().getName());
			
			TextView screenName = (TextView)convertView.findViewById(R.id.tweet_screen_name);
			screenName.setText("@" + item.getUser().getScreenName());
			
			TextView text = (TextView)convertView.findViewById(R.id.tweet_text);
			text.setText(item.getText());
			
			return convertView;
		}
	}
	
	private void showToast(String text) {
	    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.twitter_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
			case R.id.menu_refresh:
				reloadTimeLine();
				return true;
			case R.id.menu_tweet:
				Intent intent = new Intent(this, TwitterTweetActivity.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
