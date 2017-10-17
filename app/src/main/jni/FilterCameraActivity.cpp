#include "com_lockscreenplayer_js_lockscreenplayer_FilterCameraActivity.h"

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <stdio.h>

#include <cv.h>
#include <highgui.h>
#include <iostream>

using namespace std;
using namespace cv;

int toGray(Mat img, Mat& gray);
JNIEXPORT jint JNICALL Java_com_lockscreenplayer_js_lockscreenplayer_FilterCameraActivity_convertNativeGray(JNIEnv*, jobject, jlong addrRgba, jlong addrGray);

//-------------------------------------------------------
JNIEXPORT jint JNICALL Java_com_lockscreenplayer_js_lockscreenplayer_FilterCameraActivity_convertNativeGray(JNIEnv*, jobject, jlong addrRgba, jlong addrGray) {

    Mat& mRgb = *(Mat*)addrRgba;
    Mat& mGray = *(Mat*)addrGray;

    int conv;
    jint retVal;
    conv = toGray(mRgb, mGray);
    retVal = (jint)conv;
    return retVal;
}

int toGray(Mat img, Mat& gray){
    cvtColor(img, gray, CV_RGBA2GRAY);
    if (gray.rows == img.rows && gray.cols == img.cols)
        return (1);
    return(0);
}


