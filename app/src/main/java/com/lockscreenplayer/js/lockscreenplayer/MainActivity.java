package com.lockscreenplayer.js.lockscreenplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {
 
    private YouTubePlayerView ytpv;
    private YouTubePlayer ytp;
    final String serverKey="AIzaSyDOc_L1Otd5lCHqpPl29f5hS9ikv98Gi44"; //�ֿܼ��� �޾ƿ� ����Ű�� �־��ݴϴ�
 
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         ytpv = (YouTubePlayerView) findViewById(R.id.youtubeplayer);
         ytpv.initialize(serverKey, this);
         
    }
 


    @Override
    public void onInitializationFailure(Provider arg0,
                                        YouTubeInitializationResult arg1) {
        Toast.makeText(this, "Initialization Fail"+arg1, Toast.LENGTH_LONG).show();
        Log.d("유튜브재생실패",arg1+"");
    }

    @Override
    public void onInitializationSuccess(Provider provider,
                                        YouTubePlayer player, boolean wasrestored) {
        ytp = player;
         
        Intent gt =getIntent();
        ytp.loadVideo(gt.getStringExtra("id"));
    }
 
}