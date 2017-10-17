#소스파일들의 위치
LOCAL_PATH := $(call my-dir)

#LOCAL_PATH 를 제외한 LOCAL_ 로 시작되는 변수의 값을 초기화
include $(CLEAR_VARS)

#opencv
OPENCVROOT:= /Users/lenovo/AppData/Local/Android/Sdk/OpenCV-android-sdk

OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED

include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

#라이브러리 생성에 필요한 소스 코드 목록
LOCAL_SRC_FILES := FilterCameraActivity.cpp

LOCAL_LDLIBS += -llog

#생성할 라이브러리 파일의 이름
LOCAL_MODULE := nativegray

#위에 내용을 바탕으로 share library 를 생성
include $(BUILD_SHARED_LIBRARY)
include $(CLEAR_VARS)






#LOCAL_PATH := $(call my-dir)

#include $(CLEAR_VARS)

#LOCAL_MODULE := ndkTest
#LOCAL_SRC_FILES := ndkTest.cpp

#include $(BUILD_SHARED_LIBRARY)




