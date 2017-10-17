package com.lockscreenplayer.js.lockscreenplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoViewFullscreenActivity extends AppCompatActivity {
    //비디오뷰 전체화면 액티비티
    MyVideoView myVideoView;
    MediaController mediaController;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //상단 상태바 없애기 (풀스크린)

        setContentView(R.layout.activity_video_view_fullscreen);

        myVideoView = (MyVideoView)findViewById(R.id.fullscreen_videoView);
        Intent intent = getIntent(); //인텐트 받아오기
        String videoPath = intent.getStringExtra("videoPath"); //경로
        int VideoPlayTime = intent.getIntExtra("VideoPlayTime",0);//플레이시간
        position = intent.getIntExtra("position",0); //포지션가져오기
        Log.d("락스크린 풀스크린비디오뷰액티비티",videoPath+" / "+VideoPlayTime);


        mediaController = new MediaController(this); //컨트롤러 생성
        mediaController.setAnchorView(myVideoView);//비디오뷰 부모뷰에 컨트롤러 위치
        myVideoView.setMediaController(mediaController);//비디오뷰에 컨트롤러 달기

        myVideoView.setVideoPath(videoPath); //비디오경로 가져오기
        myVideoView.seekTo(VideoPlayTime); //플레이시간 가져오기
        myVideoView.start();//비디오재생
    }
    @Override
    public void onBackPressed() {//백버튼 이벤트처리
        super.onBackPressed();
        //호출한 액티비티에 값전달
        Intent intent = new Intent();
        intent.putExtra("VideoPlayTime",myVideoView.getCurrentPosition());//재생시간 리턴

        SharedPreferences sp =getSharedPreferences("LockScreenBackgroundImage",MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("videoView.getCurrentPosition"+position,myVideoView.getCurrentPosition());//재생시간저장
        ed.commit();
        setResult(RESULT_OK, intent);
        finish();
    }

}



//비디오뷰 전체화면으로 할 수 있게
class MyVideoView extends VideoView
{
    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Display dis =((WindowManager)getContext().
                getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        setMeasuredDimension(dis.getWidth(), dis.getHeight() );

    }
}
