package com.example.mymovieapplication;

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
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,PictureCallback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private static final String TAG = "CameraPreview";
    private boolean mProgressFlag = false;
    private boolean isRecording = false;
    
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
    
    private final Camera.PreviewCallback editPreviewImage =
            new Camera.PreviewCallback() {
     
        public void onPreviewFrame(byte[] data, Camera camera) {
        	Log.e("testtesttesefsfes", "aaaa");
            mCamera.setPreviewCallback(null);  // プレビューコールバックを解除
            mCamera.stopPreview();
            
            /*
            // 画像の保存処理
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MMddHH_mmss_SSS");
     
            // 　画像を保存
            String path = Environment.getExternalStorageDirectory().getPath() +
                    '/' + dateFormat.format(new Date()) + ".raw";
     
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                Log.e("CAMERA", e.getMessage());
            }
            */
     
            mCamera.startPreview();
            mProgressFlag = false;
        }
    };
    
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
		/*
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	
        	mCamera = Camera.open();
        	//======add for Video======//
        	mMediaRecorder = new MediaRecorder();
        	Log.e("STEP", "Step 1: Unlock and set camera to MediaRecorder");
        	mCamera.unlock();
        	mMediaRecorder.setCamera(mCamera);
        	Log.e("STEP", "Step 2: Set sources");
        	mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        	mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        	Log.e("STEP", "Step 3: Set a CamcorderProfile (requires API Level 8 or higher)");
        	mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        	Log.e("STEP", "Step 4: Set output file");
        	mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        	Log.e("STEP", "Step 5: Set the preview output");
        	//mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        	//======add for Video======//
        	
            mCamera.setPreviewDisplay(holder);
            
            //======add for Video======//
            Log.e("STEP", "Step 6: Prepare configured MediaRecorder");
            try {
            	mMediaRecorder.prepare();
            } catch (IllegalStateException e) {
            	Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            	releaseMediaRecorder();
            } catch (IOException e) {
            	Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            	releaseMediaRecorder();
            }
            //======add for Video======//
             
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
        */
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    	mCamera.stopPreview();
    	releaseMediaRecorder();
    	releaseCamera();
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
        	/*
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
        	*/
        	
        	mCamera.setParameters(parameters);
        	mCamera.startPreview();
        	
            //mCamera.setPreviewDisplay(mHolder);
            //mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    
    // 画面の向きを取得する
    protected boolean isPortrait() {
        return (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }
    
    public void takePreviewRawData () {
    	Log.e("takePreviewRawData", "aaaa");
    	if (!mProgressFlag) {
    		mProgressFlag = true;
    		mCamera.setPreviewCallback(editPreviewImage);
    	}
    }

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		//Bitmap bmp = BitmapFactory.decodeByteArray(data,  0,  data.length, null);
		//MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bmp, "", null);
		
		File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
		if (pictureFile == null) {
			//Log.d(TAG, "Error creating media file, check storage permissions:" +
			//		e.getMessage());
			Log.d(TAG, "Error creating media file, check storage permissions:");
			return;
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(TAG,"File not found:" + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG,"Error accessing file:" + e.getMessage());
		}
		//Preview画面を再表示
		mCamera.startPreview();
	}
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
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
	
	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if(me.getAction()==MotionEvent.ACTION_DOWN) {
			//mCamera.takePicture(null, null, this);
			if (isRecording) {
				//stop recording and release camera
				mMediaRecorder.stop(); //stop the recording
				releaseMediaRecorder(); //release the MediaRecorder object
				mCamera.lock(); //take camera access back from MediaRecorder
				
				//inform the user that recording has stopped
				isRecording=false;
			} else {
				if (prepareVideoRecorder()) {
					//initialize video camera
					//mMediaRecorder.start();
					
					//inform the user that recording has started
					isRecording = true;
				} else {
					releaseMediaRecorder();
				}
				
			}
		}
		return true;
	}
	
	/** A safe way to get an instance of the Camera object. */
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
	
    public boolean prepareVideoRecorder() {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	if(mCamera==null)
        	{
        		mCamera = getCameraInstance();
        		
        		Camera.Parameters parameters = mCamera.getParameters();
        		parameters.set("orientation", "portrait");
        		mCamera.setParameters(parameters);
        	}
        	//======add for Video======//
        	mMediaRecorder = new MediaRecorder();
        	Log.e("STEP", "Step 1: Unlock and set camera to MediaRecorder");
        	mCamera.unlock();
        	mMediaRecorder.setCamera(mCamera);
        	Log.e("STEP", "Step 2: Set sources");
        	mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        	mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        	Log.e("STEP", "Step 3: Set a CamcorderProfile (requires API Level 8 or higher)");
        	mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        	Log.e("STEP", "Step 4: Set output file");
        	mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        	
        	//利用できるプレビューサイズを列挙
        	
        	try{
        		//なぜか取得できません
        		Parameters mParameters = mCamera.getParameters();
	        	List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
	        	
	        	/*
	        	boolean first = true;
	        	for (Iterator iterator = sizes.iterator(); iterator.hasNext();) {
	        	Size size = (Size) iterator.next();
	        	Log.e("size_check", String.valueOf(size.width) + String.valueOf(size.height));
	        	//最初の(いちばん大きい)値を設定する
	        	if(first)mMediaRecorder.setVideoSize(size.width , size.height);
	        	first = false;
	        	}
	        	*/
	        	if(sizes == null) {
	        		Log.e("size_null", "size_null");
	        		CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
	        		mMediaRecorder.setVideoSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
	        	}else{
	        		Log.e("size_check", String.valueOf(sizes.get(0).width) + String.valueOf(sizes.get(0).height));
	        		mMediaRecorder.setVideoSize(sizes.get(0).width , sizes.get(0).height);
	        	}
        	}catch(Exception e){
        		Log.e("ERROR", e.getMessage());
        	}
        	
        	
        	
        	
        	Log.e("STEP", "Step 5: Set the preview output");
        	mMediaRecorder.setPreviewDisplay(this.getHolder().getSurface());
        	//======add for Video======//
            //mCamera.setPreviewDisplay(this.getHolder());
            
            //======add for Video======//
            Log.e("STEP", "Step 6: Prepare configured MediaRecorder");
            try {
            	mMediaRecorder.prepare();
            } catch (IllegalStateException e) {
            	Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            	releaseMediaRecorder();
            	return false;
            } catch (IOException e) {
            	Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            	releaseMediaRecorder();
            	return false;
            }
            //======add for Video======//
        } catch (Exception e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            return false;
        }
        return true;
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
}