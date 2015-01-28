/*************************
 * 現在地を記録しながらムービーを撮影する
 */
package com.example.mywalkapplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.example.mywalkapplication.R;
import com.example.mywalkapplication.LocationRecordService.LocalBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MovieActivity extends CommonActivity {

	private static final String TAG = "tag";
	
	// LayoutParamsにセットする基本パラメータ
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	//カメラオブジェクト
	Camera mCamera;
	CameraPreview mPreview;
	MediaRecorder mMediaRecorder;
	//位置情報記録サービス
	LocationRecordService mService;
    boolean mBound = false;
    //保存するムービーファイルのフルパス
    private String movie_file_path = "";
    
	
	private boolean isRecording = false;
	
	/***********************
	 * キャプチャーボタンが押されたときの処理
	 * @author m_sugawara
	 *
	 */
	private class CaptureButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
                if (isRecording == true) {
                    // stop recording and release camera
                	Log.e("stopprocess", "button_pushed");
                	try {
                		mMediaRecorder.stop();  // stop the recording
                	} catch (Exception e) {
                		Log.e("stopprocess", e.getMessage());
                	} finally {
                		Log.e("stopprocess", "MediaRecorder_stoped");
                        releaseMediaRecorder(); // release the MediaRecorder object
                	}
                    mCamera.lock();         // take camera access back from MediaRecorder
                    Log.e("stopprocess", "camera_locked");
			        //位置情報サービスアンバウンド（終了）
                    if (mBound) {
			            unbindService(mConnection);
			            mBound = false;
			            Log.e("stopprocess", "service_unbinded");
			        }
                    // inform the user that recording has stopped
                    setCaptureButtonText("Capture");
                    isRecording = false;
                } else {
                    // initialize video camera
                    if (prepareVideoRecorder()) {
                    	//位置情報取得サービス起動
                    	Intent intent = new Intent(MovieActivity.this, LocationRecordService.class);
                    	intent.putExtra("MOVIE_FILE_PATH", movie_file_path);
    		        	bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    		        	//動画撮影開始
                        // Camera is available and unlocked, MediaRecorder is prepared,
                        // now you can start recording
                        mMediaRecorder.start();

                        // inform the user that recording has started
                        setCaptureButtonText("Stop");
                        isRecording = true;
                    } else {
                        // prepare didn't work, release the camera
                        releaseMediaRecorder();
                        // inform user
                    }
                }
            
		}
	}
	/** 
	 * 位置情報記録サービスをバインドするときの処理
	 * Defines callbacks for service binding, passed to bindService()
	 *  */
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		setContentView(R.layout.movie_record);
		// Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new CaptureButtonClickListener());
	}
	
	@Override
    protected void onResume() {
        super.onResume();
    }
	
	@Override
    protected void onPause() {
        super.onPause();
    }
	
	@Override
	public void onStop() {
		super.onStop();
		//撮影を停止する
		if(isRecording == true)
		{
			mMediaRecorder.stop();
			if (mBound) 
			{
				unbindService(mConnection);
				mBound = false;
			}
	        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
	        mCamera.lock();               // take camera access back from MediaRecorder
	        isRecording = false;
		}

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        releaseCamera();              // release the camera immediately on pause event

	}
	
	public void setCaptureButtonText(String text) {
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setText(text);
	}
	
	public static Camera getCameraInstance() {
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e) {
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	
	void releaseMediaRecorder() {
		if(mMediaRecorder != null) {
			mMediaRecorder.reset();  //clear recorder configuration
			mMediaRecorder.release(); //release the recorder object
			mMediaRecorder = null;
			mCamera.lock();  //lock camera for later use
		}
	}
	
	void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();  //release the camera for other applications
			mCamera = null;
		}
	}
	

	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}


	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    Log.e("myCameraApp", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	private boolean prepareVideoRecorder(){

		if(mCamera == null){
			mCamera = getCameraInstance();
		}
		
		try {
			mCamera.setPreviewDisplay(null);
		} catch (java.io.IOException ioe) {
			Log.d(TAG, "IOException nullifying preview display: " + ioe.getMessage());
		}
		mCamera.stopPreview();
		
	    mMediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

	    // Step 4: Set output file
	    movie_file_path = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
	    mMediaRecorder.setOutputFile(movie_file_path);

	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
	    
	    // !!!!!!Step5+: Set Size!!!!!!
	    CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
	    mMediaRecorder.setVideoSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
    	// !!!!!!Step5+: Set Size!!!!!!

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
	
}
