
package com.example.mymovieapplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
 
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
 
/**
 * メインアクティビティ
 *
 */
public class MovieActivity extends Activity {
 
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        // タイトルなし
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
         
         
         
        // カメラビューを取り出す
        // カメラビューを先に作って。setZOrderXXXをしてからあとにレイアウトを作れば
        // Zオーダー問題はなんとか大丈夫っぽい。
        final CameraView cameraView = new CameraView(this);
        cameraView.setZOrderMediaOverlay(false);
        cameraView.setZOrderOnTop(false);
        this.setContentView(cameraView,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        cameraView.setOwner(this);
 
        // カメラビューにかぶせるコントローラービュー
        final RelativeLayout controlOverlayView = (RelativeLayout)this.getLayoutInflater().inflate(R.layout.overlay, null);
        this.addContentView(controlOverlayView,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
         
         
        // シャッターボタン
        Button shutter = (Button)controlOverlayView.findViewById(R.id.button_shutter2);
        shutter.setOnClickListener(new OnClickListener() {
             
            @Override
            public void onClick(View v) {
                cameraView.shutter();
            }
        });
    }
 
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_try_camera_overlay, menu);
        return true;
    }
    */
 
}
 
 
 
/////////////////////////////////////////////////////////////////////////////////////////////////////
 
/**
 * カメラのビューにかぶせるサーフェイスビュー
 *
 */
class OverlaySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
 
    private final static int ONE_FRAME_TICK = 1000 / 25;    // 1フレームの時間
    private final static int MAX_FRAME_SKIPS = 5;           // 時間が余ったとき最大何回フレームをスキップするか
  
    private int mScrWidth;  // 画面の幅
    private int mScrHeight; // 画面の高さ
    private SurfaceHolder mHolder;  // サーフェスホルダー
     
    private Thread mThreadMove; // 定期的に更新するためのスレッド
     
    private int mLineY; // ラインのある高さ
     
    /**
     * コンストラクタ
     * @param context
     */
    public OverlaySurfaceView(Context context) {
        super(context);
         
        // サーフェイスホルダーを取り出す
        this.mHolder = this.getHolder();
        this.mHolder.setFormat(PixelFormat.TRANSPARENT);
         
        // コールバック関数を登録する
        this.mHolder.addCallback(this);
        this.setZOrderMediaOverlay(true);
    }
     
    public OverlaySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
  
        // サーフェイスホルダーを取り出す
        this.mHolder = this.getHolder();
        this.mHolder.setFormat(PixelFormat.TRANSPARENT);
         
        // コールバック関数を登録する
        this.mHolder.addCallback(this);
        this.setZOrderMediaOverlay(true);
    }
  
    public OverlaySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
  
        // サーフェイスホルダーを取り出す
        this.mHolder = this.getHolder();
        this.mHolder.setFormat(PixelFormat.TRANSPARENT);
         
        // コールバック関数を登録する
        this.mHolder.addCallback(this);
        this.setZOrderMediaOverlay(true);
    }
 
    /**
     * サーフェイスの変更
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mScrWidth = width;
        this.mScrHeight = height;
    }
 
    /**
     * サーフェイスが作られた
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
         
        // 更新用スレッドの開始
        this.mThreadMove = new Thread(this);
        this.mThreadMove.start();
         
    }
     
    public void start() {
        // 更新用スレッドの開始
        this.mThreadMove = new Thread(this);
        this.mThreadMove.start();
                 
    }
 
    /**
     * サーフェイスが破棄された
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mThreadMove = null;
    }
 
    /**
     * 適当に呼び出される
     */
    @Override
    public void run() {
        Canvas canvas;
        long beginTime; // 処理開始時間
        long pastTick;  // 経過時間
        int sleep = 0;
        int frameSkipped;   // 何フレーム分スキップしたか
  
        // フレームレート関連
        int frameCount = 0;
        long beforeTick = 0;
        long currTime = 0;
        String tmp = "";
  
        // 文字書いたり
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setTextSize(60);
  
        int count = 0;
        // スレッドが消滅していない間はずっと処理し続ける
        while (this.mThreadMove != null) {
            canvas = null;
  
            // フレームレートの表示
            frameCount++;
            currTime = System.currentTimeMillis();
            if (beforeTick + 1000 < currTime) {
                beforeTick = currTime;
                tmp = "" + frameCount;
                frameCount = 0;
            }
  
            try {
  
                synchronized (this.mHolder) {
                    canvas = this.mHolder.lockCanvas();
                    // キャンバスとれなかった
                    if (canvas == null)
                        continue;
                     
                    // 背景をクリア
                    canvas.drawColor(0, Mode.CLEAR);
  
                    // 現在時刻
                    beginTime = System.currentTimeMillis();
                    frameSkipped = 0;
  
                    // ////////////////////////////////////////////////////////////
                    // ↓アップデートやら描画やら
                    this.move();
                    canvas.save();
                    this.draw(canvas);
                    canvas.restore();
 
                    // ////////////////////////////////////////////////////////////
  
                    // 経過時間
                    pastTick = System.currentTimeMillis() - beginTime;
  
                    // 余っちゃった時間
                    sleep = (int)(ONE_FRAME_TICK - pastTick);
  
                    // 余った時間があるときは待たせる
                    if (0 < sleep) {
                        try {
                            Thread.sleep(sleep);
                        } catch (Exception e) {}
                    }
  
                    // 描画に時間係過ぎちゃった場合は更新だけ回す
                    while (sleep < 0 && frameSkipped < MAX_FRAME_SKIPS) {
                        // ////////////////////////////////////////////////////////////
                        // 遅れた分だけ更新をかける
                        this.move();
                        // ////////////////////////////////////////////////////////////
                        sleep += ONE_FRAME_TICK;
                        frameSkipped++;
                    }
                    canvas.drawText("FPS:" + tmp, 10, 60, paint);
                }
            } finally {
                // キャンバスの解放し忘れに注意
                if (canvas != null) {
                    this.mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
     
    /**
     * サーフェイス内のものを動かす
     */
    private void move() {
        this.mLineY += 20;
        if (this.mScrHeight + (LINE_COUNT * LINE_STEP) < this.mLineY) {
            this.mLineY = 0;
        }
    }
     
    /**
     * システムからの描画呼び出し
     */
    public void onDraw() {
        Canvas canvas = this.mHolder.lockCanvas();
        this.draw(canvas);
        this.mHolder.unlockCanvasAndPost(canvas);
    }
     
     
    private static final int LINE_COUNT = 50;   // ラインの数
    private static final int LINE_STEP = 4; // ラインの数
     
    /**
     * サーフェイス内のものの描画
     */
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        for (int i = 0; i < LINE_COUNT; i++) {
            paint.setColor(Color.argb(0xff - (0xff / LINE_COUNT) * i, 0x7f, 0xff, 0x7f));
            canvas.drawLine(0, this.mLineY - i * 4, this.mScrWidth, this.mLineY - i * LINE_STEP, paint);
        }
    }
     
     
     
}
 
/////////////////////////////////////////////////////////////////////////////////////////////////////
 
/**
 * カメラのビュー用のサーフェイスビュー
 *
 */
class CameraView extends SurfaceView implements SurfaceHolder.Callback {
 
    private Activity mOwner;    // オーナーアクティビティ(画面の回転処理などに使う)
    private Camera mCamera; // カメラ
    private SurfaceHolder mHolder;  // サーフェイスホルダー
     
    /**
     * コンストラクタ
     * @param context
     */
    public CameraView(Context context) {
        super(context);
         
        // サーフェイスホルダーをとっとく
        this.mHolder = this.getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
     
    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
 
        // サーフェイスホルダーをとっとく
        this.mHolder = this.getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
 
    }
 
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
         
        // サーフェイスホルダーをとっとく
        this.mHolder = this.getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
 
    }
 
    /**
     * アクティビティをセットする
     * @param activity
     */
    public void setOwner(Activity activity) {
        this.mOwner = activity;
    }
     
    /**
     * サーフェイスが作られたときの呼び出し
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // カメラを開く
            this.mCamera = Camera.open();
             
            // プレビューディスプレイの設定
            this.mCamera.setPreviewDisplay(this.mHolder);
             
        } catch (Exception ep) {
            ep.printStackTrace();
             
            // 失敗したときはカメラを解放する
            this.mCamera.release();
            this.mCamera = null;
        }
    }
     
    /**
     * サーフェイスが破棄されたときに呼び出される
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
         
        // プレビューを停止
        this.mCamera.stopPreview();
         
        // カメラを解放
        this.mCamera.release();
        this.mCamera = null;
    }
 
     
    /**
     * サーフェスの変更時の呼び出し
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
         
        // 何かする前に一度プレビューを停止する
        this.mCamera.stopPreview();
         
        // カメラパラメータを取り出す
        Camera.Parameters params = this.mCamera.getParameters();
 
        // ベストなプレビューサイズを探す
        // 
        // 端末のサポートしているプレビューサイズを取り出す
        List<Size> listSize = params.getSupportedPreviewSizes();
         
        // 一番つ劣化が少ない画像のサイズ
        Size bestPrevSize = this.getBestPreviewSize(listSize, width, height);
         
        // プレビューサイズを設定する
        params.setPreviewSize(bestPrevSize.width, bestPrevSize.height);
         
        // ベストな保存サイズを取り出す
        //
         
        // 端末のサポートしてる画像サイズを取り出す
        listSize = params.getSupportedPictureSizes();
         
        // ベストな画像サイズを調べる
        Size bestPictureSize = this.getBestPreviewSize(listSize, width, height);
         
        // 画像サイズを設定する
        params.setPictureSize(bestPictureSize.width, bestPictureSize.height);
 
        // カメラの回転角度をセットする
        CameraView.setCameraDisplayOrientation(this.mOwner, 0, this.mCamera);
         
        // オートフォーカスの設定
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
         
        // パラメーターの更新
        this.mCamera.setParameters(params);
         
        // プレビューを再開
        this.mCamera.startPreview();
         
        // プレビューが再開したらオートフォーカスの設定(プレビュー中じゃないときにオートフォーカス設定すると落ちる)
        //this.mCamera.autoFocus(null);
    }
     
    private final float ASPECT_TOLERANCE = 0.05f;
     
    /**
     * 理想に近いサイズをプレビューサイズの中から探し出す
     * @param listPreviewSize サポートされているプレビューのサイズ
     * @param w 画面幅
     * @param h 画面の高さ
     * @return 適したプレビューサイズ
     */
    private Size getBestPreviewSize(List<Size> listPreviewSize, int w, int h) {
         
        // プレビューサイズリストがなかったときは何もしない
        if (listPreviewSize == null) {
            return null;
        }
         
        // 端末が立った状態の場合はWとHを入れ替える
        if (w < h) {
            int tmp = w;
            w = h;
            h = tmp;
        }
         
        float bestRatio = (float)w / h; // この比率に近いものをリストから探す
        float minHeightDiff = Float.MAX_VALUE;  // 一番高さに差がないもの
        int bestHeight = h; // プレビュー画面にベストな高さ
        float currRatio = 0;    // 今見ているもののアスペクト比
        Size bestSize = null;
         
        // 近いサイズのものを探す
        for (Size curr : listPreviewSize) {
             
            // 今見ているもののアスペクト比
            currRatio = (float)curr.width / curr.height;
             
            // 許容範囲を超えちゃってるやつは無視
            if (ASPECT_TOLERANCE < Math.abs(currRatio - bestRatio)) {
                continue;
            }
             
            // 前に見たやつより高さの差が少ない
            if (Math.abs(curr.height - bestHeight) < minHeightDiff) {
                 
                // 一番いいサイズの更新
                bestSize = curr;
                 
                // 今のところこれが一番差が少ない
                minHeightDiff = Math.abs(curr.height - bestHeight);
            }
        }
         
        // 理想的なものが見つからなかった場合、しょうがないので画面に入るようなやつを探しなおす
        if (bestSize == null) {
             
            // でっかい値をいれとく（未使用です）
            minHeightDiff = Float.MAX_VALUE;
 
            // 今度は画面に入りそうなものを探す
            for (Size curr : listPreviewSize) {
                 
                // 今見ているもののアスペクト比
                currRatio = (float)curr.width / curr.height;
                 
                // 前に見たやつより高さの差が少ない
                if (Math.abs(curr.height - bestHeight) < minHeightDiff) {
                     
                    // 一番いいサイズの更新
                    bestSize = curr;
                     
                    // 今のところこれが一番差が少ない
                    minHeightDiff = Math.abs(curr.height - bestHeight);
                }
            }
        }
         
        return bestSize;
    }
     
     
    /**
     * 画面の回転角度を設定する
     * @param activity アクティビティ
     * @param cameraId カメラID
     * @param camera カメラ
     */
    public static void setCameraDisplayOrientation(
            Activity activity, int cameraId, android.hardware.Camera camera) {
         
        // 向きを設定
        camera.setDisplayOrientation(CameraView.getCameraDisplayOrientation(activity));
    }
     
    /**
     * 画面の回転角度を取り出す
     * @param activity アクティビティ
     * @return 画面の回転角度
     */
    public static int getCameraDisplayOrientation(Activity activity) {
         
        // ディスプレイの回転角を取り出す
        int rot = activity.getWindowManager().getDefaultDisplay().getRotation();
         
        // 回転のデグリー角
        int degree = 0;
         
        // 取り出した角度から実際の角度への変換
        switch (rot) {
        case Surface.ROTATION_0:    degree = 0;     break;
        case Surface.ROTATION_90:   degree = 90;    break;
        case Surface.ROTATION_180:  degree = 180;   break;
        case Surface.ROTATION_270:  degree = 270;   break;
        }
         
        // 背面カメラだけの処理になるけど、画像を回転させて縦持ちに対応
        return (90 + 360 - degree) % 360;
    }
     
     
     
     
    /**
     * シャッターを押す
     */
    public void shutter() {
        this.mCamera.autoFocus(this.mAutofocusListener);
    }
     
 
    // オートフォーカスリスナー
    private Camera.AutoFocusCallback mAutofocusListener = 
            new Camera.AutoFocusCallback() {
                 
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    //camera.autoFocus(null);
                    camera.takePicture(
                            mShutterListener,
                            null,
                            mPictureListener);
                }
            };
             
    // シャッターリスナー
    private Camera.ShutterCallback mShutterListener =
            new Camera.ShutterCallback() {
                 
                @Override
                public void onShutter() {
                    Log.i("Shutter", "シャッター押された");
                }
            };
             
    private Camera.PictureCallback mPictureListener = 
            new Camera.PictureCallback() {
                 
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                     
                    // イメージデータがあります。
                    if (data != null) {
                        camera.startPreview();
 
                        CameraView.makeRotateImage(mOwner, data, 512 * 512);
                        Log.i("onPictureTaken", "写真が取れました");
                    }
                }
            };
 
    /**
     * 格納するイメージパスを作る
     * @return
     */
    private static String makeImagePath() {
         
        // ストレージのパスを取り出す
        String filePath = CameraView.getExternalStragePath();
         
        // 画像データの保存ディレクトリ
        filePath = filePath + "/tryCameraOverlay/";
         
         
        // 今の日付時間をバックアップファイル名とする
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = sdf.format(Calendar.getInstance().getTime()) + ".png";
         
        // バックアップ先のファイルパスを作る
        filePath = filePath + fileName;
         
        // 親のディレクトリを取り出す
        File imageFile = new File(filePath);
        File fileDir = imageFile.getParentFile();
         
        // ディレクトリがない場合は作る
        if (fileDir.exists() == false) {
            fileDir.mkdirs();
        }
         
        return filePath;
    }
     
     
    /**
     * 外部ストレージのパスを返す
     * GALAXY S3とかパスが違うもので。
     * @return 外部ストレージのパス
     */
    public static String getExternalStragePath() {
         
        String path;
         
        // S3
        path = "/mnt/extSdCard";
        if (new File(path).exists()) {
            return path;
        }
         
        // MOTROLA
        path = System.getenv("EXTERNAL_ALT_STORAGE");
        if (path != null) {
            return path;
        }
         
        // Sumsung
        path = System.getenv("EXTERNAL_STORAGE2");
        if (path != null) {
            return path;
        }
         
        // 旧Sumsung と 標準
        path = System.getenv("EXTERNAL_STORAGE");
        if (path != null) {
            path = Environment.getExternalStorageDirectory().getPath();
        }
         
        // HTC
        File file = new File(path + "/ext_sd");
        if (file.exists()) {
            path = file.getPath();
        }
         
        return path;
    }
     
     
    /**
     * 回転角に合わせた画像を作る
     * @param activity アクティビティ
     * @param data 画像のビットデータ
     */
    public static void makeRotateImage(Activity activity, byte[] data, int maxPixel) {
 
        // オリジナルのBMP
        Bitmap bitmapSrc = CameraView.makeTargetPixelImage(data, maxPixel);
         
        // 回転角を取り出す
        int degree = CameraView.getCameraDisplayOrientation(activity);
        int destWidth = 0;
        int destHeight = 0;
         
        // 反転、もしくはそのままの場合
        if (degree % 180 == 0) {
            destWidth = bitmapSrc.getWidth();
            destHeight = bitmapSrc.getHeight();
        } else {
            destWidth = bitmapSrc.getHeight();
            destHeight = bitmapSrc.getWidth();
        }
         
        // 新しくBitmapを作る
        Bitmap bitmapDest = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
         
        // キャンバスを作る
        Canvas canvas = new Canvas(bitmapDest);
        canvas.save();
         
        // キャンバスを使ってBMPを回転やら移動やらさせてデバイスの回転に合わせた画像を作る
        canvas.rotate(degree, destWidth / 2, destHeight / 2);
        int offset = (destHeight - destWidth) / 2 * ((degree - 180) % 180) / 90;
        canvas.translate(offset, -offset);
        canvas.drawBitmap(bitmapSrc, 0, 0, null);
        canvas.restore();
        bitmapSrc.recycle();
        bitmapSrc = null;
         
         
        try {
            // 画像として書き出す
            OutputStream os = new FileOutputStream(new File(CameraView.makeImagePath()));
            bitmapDest.compress(CompressFormat.PNG, 100, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
 
    }
     
    /**
     * 指定されたピクセル内に収まるようなBMPイメージを作る
     * @param data
     * @param maxPixel
     * @return
     */
    private static Bitmap makeTargetPixelImage(byte[] data, int maxPixel) {
         
        BitmapFactory.Options option = new BitmapFactory.Options();
        int samplingSize = 0;
         
        // 作成する予定のBMPの情報を取り出す
        option.inJustDecodeBounds = true;   // 情報のみ取り出す
        option.inSampleSize = 0;        // 等角
         
        // 情報だけ取り出す
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, option);
         
        // 指定されたピクセル数より多いBMPの場合は小さくする
        if (maxPixel < option.outWidth * option.outHeight) {
             
            // オーバーしてしまっている分を計算
            double overPixel = (double)(option.outWidth * option.outHeight) / maxPixel;
            samplingSize = (int)(Math.sqrt(overPixel) + 1);
             
        // 指定されたサイズより下のものだった
        } else {
            // 等角で。
            samplingSize = 1;
        }
         
         
        // 実際の画像を読み込む
        // 
         
        // データまで読み込み
        option.inJustDecodeBounds = false;
         
        // サンプリング係数
        option.inSampleSize = samplingSize;
         
        // 指定サイズの画像を作る
        return BitmapFactory.decodeByteArray(data, 0, data.length, option);
         
    }
     
    /**
     * 矩形に入るようにBMPを作り直す
     * @param src 元のBMP
     * @param destWidth BMPの横幅
     * @param destHeight BMPの高さ
     * @return 新しく作られたBMP
     */
    private Bitmap makeFitBitmap(Bitmap src, int destWidth, int destHeight) {
         
        // 指定の矩形に入る拡大率を取り出す
        float scale = CameraView.getBestFitScale(src.getWidth(), src.getHeight(), destWidth, destHeight);
         
        // Bitmapをリサイズする
        Matrix matScale = new Matrix();
        matScale.postScale(scale, scale);
         
        // Bitmapを作る
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matScale, true);
    }
     
    /**
     * 指定の範囲に入るようなスケール値を取り出す
     * @param srcWidth 元の横幅
     * @param srcHeight 元の高さ
     * @param destWidth 収めたい矩形の横幅
     * @param destHeight 収めたい矩形の高さ
     * @return
     */
    private static float getBestFitScale(int srcWidth, int srcHeight, int destWidth, int destHeight) {
         
        float scale = 0.0f;
         
        // ------------------------------
        // ↑縦長サイズにしたい
        if (destWidth < destHeight) {
             
            // ------------------------------
            // ↑縦長サイズのもの
            if (srcWidth < destHeight) {
                 
                scale = (float)destHeight / (float)srcHeight;
                 
                // 横にはみ出ちゃう場合は、はみ出ないように横幅のスケールにする
                if (destWidth < (srcWidth * scale)) {
                     
                    scale = (float)destWidth / (float)srcWidth;
                     
                }
                 
            // ------------------------------
            // →横長にしたい
            } else {
                 
                scale = (float)destWidth / (float)srcWidth;
                 
            }
             
        // ------------------------------
        // →横長サイズにしたい
        } else {
             
            // ------------------------------
            // →横長
            if (srcWidth < destHeight) {
                 
                scale = (float)destHeight / (float)srcHeight;
                 
            // ------------------------------
            // ↑縦長サイズ
            } else {
                 
                // スケール値を横に合わせる
                scale = (float)destHeight / srcHeight;
 
                // 横に合わせた場合はみ出てしまうときは縦に合わせる
                if (destHeight < (srcHeight * scale)) {
                     
                    scale = (float)destHeight / (float)srcHeight;
                     
                }
            }
        }
         
        return scale;
    }
}

/*
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MovieActivity extends ActionBarActivity {

	private static final String TAG = "tag";
	
	// LayoutParamsにセットする基本パラメータ
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		//setContentView(R.layout.activity_main);
		//画面の設定
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_CONTEXT_MENU);
		
		LinearLayout l = new LinearLayout(this);
		CameraPreview cp = new CameraPreview(this);
		l.addView(cp, new LinearLayout.LayoutParams(MP, 1000));
		
		
		// TextViewを生成しLinearLayoutの下部に追加
        TextView textView = new TextView(this);
        textView.setText("Sample");
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MP, WC);
        l.addView(textView, param);
        
        setContentView(l);

	}
	
}
*/
