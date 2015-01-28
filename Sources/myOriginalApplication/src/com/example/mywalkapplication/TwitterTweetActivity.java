/**********************************
 * ツイートを行う
 */
package com.example.mywalkapplication;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TwitterTweetActivity extends Activity {
	
	private EditText mInputText;
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
			setContentView(R.layout.activity_tweeter_tweet);
			//Twitterインスタンス作成
			mTwitter = TwitterUtils.getTwitterInstance(this);
			//テキスト読取
			mInputText = (EditText) findViewById(R.id.input_text);
			//つぶやきボタン押下処理
			findViewById(R.id.action_tweet).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					tweet();
				}
			});
			
			//前画面からの情報取得
			Intent data = getIntent();		
			//インテントの付加情報取得
			Bundle extras = data.getExtras();
			//送信情報から選択された値取得
			String default_text = extras != null ? extras.getString("MESSAGE") : "";
			//テキスト設定
			mInputText.setText(default_text);
		}
	}
	
	private void tweet() {
		AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					mTwitter.updateStatus(params[0]);
					return true;
				} catch (TwitterException e) {
					Log.e("Tweet failed", e.getMessage());
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if(result)
				{
					showToast("ツイートが完了しました!");
					finish();
				}
				else
				{
					showToast("ツイートに失敗しました。。");
				}
			}
		};
		task.execute(mInputText.getText().toString());
	}
	
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
