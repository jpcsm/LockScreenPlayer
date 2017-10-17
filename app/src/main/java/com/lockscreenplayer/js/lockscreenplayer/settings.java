package com.lockscreenplayer.js.lockscreenplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class settings extends AppCompatActivity {
    private LockScreenService lockScreenService;
    private RestartService restartService;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTitle("Settings");
        setContentView(R.layout.settings);

        // 특정 Activity에서만 animation 효과 없애기
        getWindow().setWindowAnimations(0);
        overridePendingTransition(0, 0);

        final Switch sw = (Switch)findViewById(R.id.btn_lock);

        //ActionBar 뒤로가기 버튼 생성
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //스위치 상태 불러오기
        final SharedPreferences pref = getSharedPreferences("lockscreen", Activity.MODE_PRIVATE);

        boolean set_lock = pref.getBoolean("switch", false);
        if(set_lock) {

            sw.setChecked(true);
        }
        Toast.makeText(settings.this, "체크상태 = " + set_lock, Toast.LENGTH_SHORT).show();

        //스위치의 체크 이벤트를 위한 리스너 등록
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {



            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                //Toast.makeText(settings.this, "체크상태 = " + isChecked, Toast.LENGTH_SHORT).show();

                if (isChecked){
                    Toast.makeText(settings.this, "토글클릭-ON", Toast.LENGTH_SHORT).show();
                    //서비스 시작

                    initData();

                    //스위치 체크 저장
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("switch", true );
                    editor.commit();


                } else {
                    Toast.makeText(settings.this, "토글클릭-OFF", Toast.LENGTH_SHORT).show();
                    //서비스 중지
                    Intent service = new Intent(settings.this, LockScreenService.class);
                    stopService(service);

                    Intent receiver = new Intent(settings.this, RestartService.class);
                    stopService(receiver);

                    try{
                        //브로드 캐스트 해제
                        unregisterReceiver(restartService);

                    }catch (IllegalArgumentException e) {
                        Toast.makeText(getApplicationContext(),"브로드캐스트 : null", Toast.LENGTH_SHORT).show();
                    }
                    //스위치 값 지우기
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("switch");
                    editor.commit();

                }

            }

        });
    }

    //생성된 ActionBar에서 뒤로가기 버튼을 클릭시 이벤트
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
    private Intent intent;

    private void initData(){

        //리스타트 서비스 생성
        lockScreenService = new LockScreenService();
        intent = new Intent(settings.this, LockScreenService.class);


        IntentFilter intentFilter = new IntentFilter("com.woong.service.PersistentService");
        //브로드 캐스트에 등록
        registerReceiver(restartService,intentFilter);
        // 서비스 시작
        startService(intent);


    }

}
