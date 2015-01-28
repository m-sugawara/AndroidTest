package com.example.mywalkapplication;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class TwitterOAuthActivity extends Activity {
	
	private String mCallbackURL;
	private Twitter mTwitter;
	private RequestToken mRequestToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_oauth);
		
		mCallbackURL = getString(R.string.twitter_callback_url);
		Log.e("mCallbackURL", mCallbackURL);
		mTwitter = TwitterUtils.getTwitterInstance(this);
		mTwitter.setOAuthAccessToken(null);
		
		findViewById(R.id.action_start_oauth).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAuthorize();
			}
		});
		
	}
	
	/**
	 * OAuth認証（厳密には認可）を開始します。
	 * 
	 * @param lisner
	 */
	private void startAuthorize() {
		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					mRequestToken = mTwitter.getOAuthRequestToken(mCallbackURL);
					//
					return mRequestToken.getAuthorizationURL();
				} catch (TwitterException e) {
					Log.e("TwitterException", e.getMessage());
					e.printStackTrace();
				}
				return null;
			}
			
			//memo:doInBackgroundの戻り値がonPostExecuteの引数になる。
			//このときの型がAsyncTaskの三つ目の型になる。
			@Override
			protected void onPostExecute(String url) {
				if (url != null) 
				{
					Log.e("startActivity", "go to "+url);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				}
				else
				{
					Log.e("error", "onPostExecute don't have url");
				}
			}
			
		};
		task.execute();
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (intent == null
				|| intent.getData() == null
				|| !intent.getData().toString().startsWith(mCallbackURL))
		{
			Log.e("onNewIntent", "intent is null?");
			return;
		}
		String verifier = intent.getData().getQueryParameter("oauth_verifier");
		
		AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {

			@Override
			protected AccessToken doInBackground(String... params) {
				// TODO Auto-generated method stub
				Log.e("doInBackground", "callbackparam->"+params[0]);
				try {
					return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
				} catch (TwitterException e) {
					Log.e("TwitterException2", e.getMessage());
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(AccessToken accessToken) {
				if (accessToken != null)
				{
					//認証成功
					showToast("認証成功!");
					successOAuth(accessToken);
				}
				else
				{
					//認証失敗
					showToast("認証失敗");
				}
			}
			
		};
		task.execute(verifier);
	}
	
	private void successOAuth(AccessToken accessToken) {
		TwitterUtils.storeAccessToken(this, accessToken);
		Intent intent = new Intent(this, TwitterMainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
