package com.lockscreenplayer.js.lockscreenplayer;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class LockScreenService extends Service {
    boolean call;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Intent i = new Intent(context, lock_pager.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

//                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
                //액티비티 애니메이션 제거
                //overridependingTransition(0,0);

            }


            //전화왔을 때 예외처리
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(new PhoneStateListener() {

                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:
                            Log.i("락스크린 전화",
                                    "MyPhoneStateListener->onCallStateChanged() -> CALL_STATE_IDLE "
                                            + incomingNumber);
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            Log.i("락스크린 전화",
                                    "MyPhoneStateListener->onCallStateChanged() -> CALL_STATE_OFFHOOK "
                                            + incomingNumber);
                            break;
                        case TelephonyManager.CALL_STATE_RINGING: //전화 수신
                            call = true;
                            Log.i("락스크린 전화",
                                    "MyPhoneStateListener->onCallStateChanged() -> CALL_STATE_RINGING "
                                            + incomingNumber);
                            break;
                        default:
                            Log.i("락스크린 전화",
                                    "MyPhoneStateListener->onCallStateChanged() -> default -> "
                                            + Integer.toString(state));
                            break;
                    }
                }

            }, PhoneStateListener.LISTEN_CALL_STATE);
        }


    };

    public class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i("락스크린 전화",
                            "MyPhoneStateListener->onCallStateChanged() -> CALL_STATE_IDLE "
                                    + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i("락스크린 전화",
                            "MyPhoneStateListener->onCallStateChanged() -> CALL_STATE_OFFHOOK "
                                    + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_RINGING: //전화 수신
                    Log.i("락스크린 전화",
                            "MyPhoneStateListener->onCallStateChanged() -> CALL_STATE_RINGING "
                                    + incomingNumber);
                    break;
                default:
                    Log.i("락스크린 전화",
                            "MyPhoneStateListener->onCallStateChanged() -> default -> "
                                    + Integer.toString(state));
                    break;
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private View mView;
    private WindowManager mManager;
    private WindowManager.LayoutParams mParams;

    private float mTouchX, mTouchY;
    private int mViewX, mViewY;

    private boolean isMove = false;
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);



    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1,new Notification());
        Intent settings = new Intent(this, settings.class); //푸시알림 클릭시 설정으로 이동
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, settings,
                PendingIntent.FLAG_ONE_SHOT);
        /**
         * startForeground 를 사용하면 noti fication 을 보여주어야 하는데 없애기 위한 코드
         */
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Slide Player")
                    .setContentText("잠금화면 실행 중...")
                    .setSmallIcon(R.drawable.mainicon)
                    .setContentIntent(pendingIntent)
                    .build();

        }else{
            notification = new Notification(0, "", System.currentTimeMillis());

            //notification.setLatestEventInfo(getApplicationContext(), "", "", null);
        }

        nm.notify(startId, notification);
        nm.cancel(startId);

        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        /**
         * 서비스 종료 시 알람 등록을 통해 서비스 재 실행
                */

//        SharedPreferences sp = getSharedPreferences("lockscreen",getApplication().MODE_PRIVATE);
//        registerRestartAlarm(sp.getBoolean("switch",false));
//        Toast.makeText(getApplicationContext(),"switch"+String.valueOf(sp.getBoolean("switch",false)),Toast.LENGTH_SHORT).show();
//        //stopForeground(true);
//        registerRestartAlarm(false);
    }


    /**
     * 알람 매니져에 서비스 등록
     */
    private void registerRestartAlarm(Boolean bool){
        if(bool){
            Log.i("000 LockScreenService" , "registerRestartAlarm" );
            Intent intent = new Intent(LockScreenService.this,RestartService.class);
            intent.setAction("ACTION.RESTART.LockScreenService");
            PendingIntent sender = PendingIntent.getBroadcast(LockScreenService.this,0,intent,0);

            long firstTime = SystemClock.elapsedRealtime();
            firstTime += 1*1000;

            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

            /**
             * 알람 등록
             */
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstTime,1*1000,sender);

        }

    }

    /**
     * 알람 매니져에 서비스 해제
     */
    public void unregisterRestartAlarm(){

        Log.i("000 LockScreenService" , "unregisterRestartAlarm" );

        Intent intent = new Intent(LockScreenService.this,RestartService.class);
        intent.setAction("ACTION.RESTART.LockScreenService");
        PendingIntent sender = PendingIntent.getBroadcast(LockScreenService.this,0,intent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);



    }


}
