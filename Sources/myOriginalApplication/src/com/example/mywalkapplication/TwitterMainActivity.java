package com.example.mywalkapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TwitterMainActivity extends CommonActivity implements OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_main);
		
		//OAuth認証が完了してなければ認証ページに飛ばす
		if (!TwitterUtils.hasAccessToken(this))
		{
			Intent intent = new Intent(this, TwitterOAuthActivity.class);
			startActivity(intent);
			finish();
		} 
		else
		{
			Button bt_list = (Button)findViewById(R.id.bt_twitter_list);
			bt_list.setTag("list");
			bt_list.setOnClickListener(this);
			
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String tag = (String)v.getTag();
		if(tag == "list") {
			Intent intent = new Intent(TwitterMainActivity.this, TwitterListActivity.class);
			startActivity(intent);
		}
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	*/
}
