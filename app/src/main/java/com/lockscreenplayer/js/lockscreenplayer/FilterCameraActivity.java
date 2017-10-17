package com.lockscreenplayer.js.lockscreenplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FilterCameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2,View.OnClickListener{

    public static native int convertNativeGray(long matAddrRgba, long matAddrGray);

    private Mat mRgba, mGray;
    private Tutorial3View mOpenCvCameraView;
    private Mat mIntermediateMat;
    static {
        System.loadLibrary("opencv_java3");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:{
                    Log.i("linsoo", "OpenCV loaded successfully");
                    System.loadLibrary("nativegray");// Load Native module
                    mOpenCvCameraView.enableView();
                } break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    ImageButton Btncapture;
    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //상단 상태바 없애기 (풀스크린)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //상단 상태바 없애기 (풀스크린)
        setContentView(R.layout.activity_filter_camera);

        //가로모드 고정
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Btncapture = (ImageButton)findViewById(R.id.btn_camera);
        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

//        Point center = new Point(mOpenCvCameraView.getWidth()/2,mOpenCvCameraView.getHeight()/2); // 이미지 중심
//        Mat matRotation = getRotationMatrix2D(center, -90, 1); //-90 : 시계방향 90도
//
        pos = getIntent().getIntExtra("pos",0);
        Btncapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("락스크린 카메라캡쳐버튼 클릭","onClick");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String currentDateandTime = sdf.format(new Date());
                String fileName = Environment.getExternalStorageDirectory().getPath() +
                        "/sample_picture_" + currentDateandTime + ".jpg";
                mOpenCvCameraView.takePicture(fileName,inputFrame,viewMode);
                Toast.makeText(getApplication(), fileName + " saved", Toast.LENGTH_SHORT).show();



                SharedPreferences sp = getSharedPreferences("LockScreenBackgroundImage",MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                //해당이미지uri 쉐어드에 저장
                Uri uri = Uri.fromFile(new File(fileName));
//                        Uri uri = Uri.fromFile(getFileStreamPath(fileName));
                ed.putString("pos"+ pos,""+uri.toString());
                ed.commit();
                // 미디어 스캐너를 통해 스크린샷이미지를 갱신시킨다.
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/"+fileName+".jpg")));


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                }).start();



            }
        });
    }



    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (OpenCVLoader.initDebug() == false) {
            Log.d("linsoo", "Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,mLoaderCallback);
        } else {
            Log.d("linsoo", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    private int mViewMode;
    private static final int       VIEW_MODE_RGBA     = 0;
    private static final int       VIEW_MODE_GRAY     = 1;
    private static final int       VIEW_MODE_CANNY    = 2;
    private static final int       VIEW_MODE_FEATURES = 5;
    //프리뷰 카메라필터 변경
    CameraBridgeViewBase.CvCameraViewFrame inputFrame;
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.inputFrame = inputFrame;
        if (inputFrame.rgba() != null) {
//            mRgba = inputFrame.rgba();
//            if ( mGray!=null )
//                mGray.release();
            mGray = new Mat( inputFrame.rgba().rows(),inputFrame.rgba().cols(), CvType.CV_8UC1);
            //convertNativeGray(mRgba.getNativeObjAddr(),mGray.getNativeObjAddr());


            switch (viewMode) {
                case VIEW_MODE_GRAY:
                    // input frame has gray scale format
                    Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                    break;
                case VIEW_MODE_RGBA:
                    // input frame has RBGA format
                    mRgba = inputFrame.rgba();
                    break;
                case VIEW_MODE_CANNY:
                    // input frame has gray scale format
                    mRgba = inputFrame.rgba();
                    Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
                    Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                    break;
                case VIEW_MODE_FEATURES:
                    // input frame has RGBA format
                    mRgba = inputFrame.rgba();
                    mGray = inputFrame.gray();
                    //FindFeatures(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
                    break;
            }
        }

        return mRgba;
    }
    int viewMode = VIEW_MODE_RGBA;
    public void onclickRGB(View v) {
        viewMode = VIEW_MODE_RGBA;
    }
    public void onclickGRAY(View v) {
        viewMode = VIEW_MODE_GRAY;
    }
    public void onclickCANNY(View v) {
        viewMode = VIEW_MODE_CANNY;
    }

    public void onclickFEATURES(View v) {
        viewMode = VIEW_MODE_FEATURES;
    }
    @Override
    public void onClick(View v) {

    }
    //public native void FindFeatures(long matAddrGr, long matAddrRgba);
}
