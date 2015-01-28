/****************************
 * 保存されたデータのリストを一覧表示する
 */

package com.example.mywalkapplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SavedDataListActivity extends CommonListActivity {
	
	private static final String TAG = "SavedDataListActivity";
	//マシン非依存の改行コード
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	//ユーザの保存データ
	List<UserLocation> user_locations = new ArrayList<UserLocation>();
	//DB関連
	CreateDBHelper db = null;
	//データリスト用アダプター
	ListAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    //DBの初期設定
        db = new CreateDBHelper(this);
        
        //Adapterの作成
		mAdapter = new ListAdapter(this);
		setListAdapter(mAdapter);
		
		//データリストの読み込み
		reloadDataList();

	}
	
	
	/**
	 * 保存されているデータを読み込む
	 */
	private void reloadDataList() {
		AsyncTask<Void, Void, List<UserLocation>> task = new AsyncTask<Void, Void, List<UserLocation>>(){

			@Override
			protected List<UserLocation> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					List<UserLocation> user_locations = db.getAllUserLocation();

					return user_locations;
				} catch (Exception e) {
					Log.e("reloadTimeline", e.getMessage());
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<UserLocation> result) {
				if (result != null) {
					mAdapter.clear();
					for (UserLocation user_location : result) {
						mAdapter.add(user_location);
					}
					getListView().setSelection(0);
				}
				else
				{
					showToast("保存データの取得に失敗しました。。。");
				}
			}
			
		};
		task.execute();
	}
	
	/***
	 * 読み込んだデータをリスト形式で表示する。
	 * android.R.layout.simple_list_item_1はもともと用意されている定義済みのレイアウトファイルのID
	 *
	 */
	private class ListAdapter extends ArrayAdapter<UserLocation> {
		
		private LayoutInflater mInflater;
		
		public ListAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1);
			mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
			{
				convertView = mInflater.inflate(R.layout.list_item_save_data, null);
			}
			UserLocation item = getItem(position);
			
			String show_data = "ファイル保存場所：" + item.getMovieFileName();

			TextView tv_save_data_info = (TextView)convertView.findViewById(R.id.tv_save_data_info);
			tv_save_data_info.setText(show_data);
			
			Button bt_play_save_data = (Button)convertView.findViewById(R.id.bt_play_save_data);
			bt_play_save_data.setOnClickListener(new PlaySaveDataClickListener(item.getId(), item.getMovieFileName()));
			
			Button bt_delete_save_data = (Button)convertView.findViewById(R.id.bt_delete_save_data);
			bt_delete_save_data.setOnClickListener(new DeleteSaveDataClickListener(item.getId(), item.getMovieFileName()));
			
			return convertView;
		}
	}
	
	/*****
	 * 再生画面へ遷移するためのClickListener
	 * @param 
	 */
	private class PlaySaveDataClickListener implements OnClickListener {
		private Long record_id;
		private String movie_file_path;
		
		PlaySaveDataClickListener(Long record_id, String movie_file_path) {
			this.record_id = record_id;
			this.movie_file_path = movie_file_path;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = null;
			intent = new Intent(SavedDataListActivity.this, SavedDataPlayActivity.class);
			intent.putExtra("RECORD_ID", record_id);
			intent.putExtra("FILE_PATH", movie_file_path);
			if(intent != null){
				startActivity(intent);
			}
		}
	}
	
	/*****
	 * データを削除するためのClickListener
	 * @param 
	 */
	private class DeleteSaveDataClickListener implements OnClickListener {
		private Long record_id;
		private String movie_file_path;
		
		DeleteSaveDataClickListener(Long record_id, String movie_file_path) {
			this.record_id = record_id;
			this.movie_file_path = movie_file_path;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//ムービーファイルを削除
			File file = new File(movie_file_path);
			//ファイルの存在をチェック
			if(file.exists())
			{
				Log.e(TAG, "delete " + movie_file_path);
				file.delete();
			}
			else 
			{
				Log.e(TAG, "can't delete file not exist");
			}
			//DBから該当レコードIDのデータを削除
			db.deleteUserLocation(record_id);
			db.deleteUserLocationRecordsByRecordId(record_id);
			//データリストの再読み込み
			reloadDataList();
		}
	}
	
	private void showToast(String text) {
	    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
