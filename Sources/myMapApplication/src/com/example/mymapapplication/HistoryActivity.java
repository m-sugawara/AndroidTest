package com.example.mymapapplication;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class HistoryActivity extends Activity implements OnClickListener {
	
	//マシン非依存の改行コード
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	//DB関連
	CreateDBHelper db = null;
	private List<UserLocation> user_location = new ArrayList<UserLocation>();
	
	//Button用タグ
	private static final String BUTTON_TAG_RECORD = "record";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.history);
    	LinearLayout layout = (LinearLayout)findViewById(R.id.ll_history);
    	
        //DBの初期設定
        db = new CreateDBHelper(HistoryActivity.this);
        
        //データを取得
        user_location = db.getAllUserLocation();
        //DBのデータを一覧表示
        for(int i=0;i<user_location.size();i++){
        	//データ単位でボタンを作成
        	Button bt_new = new Button(this);
        	bt_new.setText(user_location.get(i).getCreatedAt());
        	//タグにrecord_idをつけて判定に使う
        	bt_new.setTag(BUTTON_TAG_RECORD + String.valueOf(user_location.get(i).getId()));
        	bt_new.setOnClickListener(this);
        	layout.addView(bt_new);

        }
    	
    	//戻るボタン
    	Button bt_return = (Button)findViewById(R.id.bt_return);
    	bt_return.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
    	});
    	
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Long record_id;
		String tag = (String)v.getTag();
		
	    record_id = getRecordIdFromTag(tag);
	    
		//選択したレコードIDの詳細レコード画面を起動
	    Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
	    intent.putExtra("RECORD_ID", record_id);
	    startActivity(intent);
	}
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		db.close();
	}

	
	/*******************
	 * ボタンのタグからrecord_idだけを抜き出す
	 * 
	 * @param record_data
	 * @return
	 */
	public Long getRecordIdFromTag(String record_data) {
		if(record_data == null){
			return 0L;
		}
		String record_id;
		record_id = record_data.substring(BUTTON_TAG_RECORD.length(), record_data.length());

		return Long.parseLong(record_id);
	}
}
