package com.lockscreenplayer.js.lockscreenplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;

public class Tutorial3View extends JavaCameraView implements PictureCallback  {

    private static final String TAG = "Sample::Tutorial3View";
    private String mPictureFileName;

    public Tutorial3View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }
    Mat mRgba;
    Mat mGray;
    int viewMode;
    CameraBridgeViewBase.CvCameraViewFrame inputFrame;
    public void takePicture(final String fileName, CameraBridgeViewBase.CvCameraViewFrame inputFrame, int viewMode) {
        this.viewMode=viewMode;
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        this.inputFrame = inputFrame;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null,this);
    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        Bitmap bitmap = byteArrayToBitmap(data);
        bitmap = imgRotate(bitmap);//90도회전

        Mat mat = Utils.bitmapToMat(bitmap,mRgba);
//
//        CameraBridgeViewBase.CvCameraViewFrame inputFrame = null;
//
        if(viewMode!=0){
            //그레이효과
            Mat mGray = new Mat( mat.rows(),mat.cols(), CvType.CV_8UC1);
            Mat gmat = Utils.bitmapToMat(bitmap,mGray);
            //FilterCameraActivity.convertNativeGray(mat.getNativeObjAddr(),mGray.getNativeObjAddr());
            Imgproc.cvtColor(mat, mGray, Imgproc.COLOR_RGB2GRAY);
            Imgproc.cvtColor(mat, mGray, Imgproc.COLOR_RGB2GRAY, 4);
            if(viewMode==2){
                //윤곽선
                Imgproc.Canny(mGray, mRgba, 80, 100);
                Imgproc.cvtColor(mRgba, mat, Imgproc.COLOR_GRAY2RGBA, 4);
            }
            if(viewMode==1){
                mat = mGray;
            }
        }


        bitmap = Utils.matToBitmap(mat,bitmap);
        data = bitmapToByteArray(bitmap);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);

            fos.write(data);
            fos.close();

        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }



    }

    public byte[] bitmapToByteArray( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }


    public Bitmap byteArrayToBitmap( byte[] $byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;
        return bitmap ;
    }

    private Bitmap imgRotate(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        return resizedBitmap;
    }

}
