/****************
 * カメラプレビューを表示するためのSurfaceView
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private static Camera mCamera;
    
    private final String TAG = "CameraPreview";
	
    
    @SuppressWarnings("deprecation")
	public CameraPreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

	@Override
    public void surfaceCreated(SurfaceHolder holder) {
		try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
    	Log.e("surfaceChange", "Change!!!!");

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
        	Camera.Parameters parameters = mCamera.getParameters();
        	
        	boolean portrait = isPortrait();
        	 
            // 画面の向きを変更する
            if (portrait) {
            	Log.e("portrait", "vertical");
                mCamera.setDisplayOrientation(90);
            } else {
            	Log.e("portrait", "oriental");
                mCamera.setDisplayOrientation(0);
            }
            // サイズを設定
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Camera.Size size = sizes.get(0);
            parameters.setPreviewSize(size.width, size.height);
            Log.e("width", String.valueOf(size.width));
            Log.e("height", String.valueOf(size.height));
       
            // レイアウト調整
            ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
            if (portrait) {
                layoutParams.width = size.height;
                layoutParams.height = size.width;
            } else {
                layoutParams.width = size.width;
                layoutParams.height = size.height;
            }
        	//サイズを設定
        	//parameters.setPreviewSize(w, h);
        	this.setLayoutParams(layoutParams);
        	
        	
        	//mCamera.setParameters(parameters);
        	//mCamera.startPreview();
        	
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    
    // 画面の向きを取得する
    protected boolean isPortrait() {
        return (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

}