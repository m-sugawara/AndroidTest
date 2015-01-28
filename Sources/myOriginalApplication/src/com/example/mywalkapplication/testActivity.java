/*******************************
 * LocationRecordServiceを起動するためだけのActivity
 * 
 */
package com.example.mywalkapplication;

import com.example.mywalkapplication.LocationRecordService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class testActivity extends Activity {
	
	private static final int MP = LinearLayout.LayoutParams.MATCH_PARENT;
	
	LocationRecordService mService;
    boolean mBound = false;
    boolean isRecording = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LinearLayout.LayoutParams(MP, MP));
		
		Button bt = new Button(this);
		bt.setText("button");
		bt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Bind to LocalService
				if(mBound == false){
					Log.e("testAct", "onClick-false");
					Intent intent = new Intent(testActivity.this, LocationRecordService.class);
		        	bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
				} else {
					// Unbind from the service
					Log.e("testAct", "onClick-true");
			        if (mBound) {
			            unbindService(mConnection);
			            mBound = false;
			        }
				}
			}
		});
		ll.addView(bt);
		
		setContentView(ll);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
	}
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            Log.e("mConnection", "onServiceConnected");
            mService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	Log.e("mConnection", "onServiceDisconnected");
            mBound = false;
        }
    };
}
