package com.lockscreenplayer.js.lockscreenplayer;

/**
 * Created by lenovo on 2017-08-09.
 */

public class NDK {
    static {
        System.loadLibrary("sample_ffmpeg");
    }

    public native int scanning(String filepath);
}
