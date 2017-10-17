package com.lockscreenplayer.js.lockscreenplayer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class GoodsDetailActivity extends AppCompatActivity {

    TextView brand_name,price,CurrentPoint,AfterPoint;
    ImageView image;
    ImageButton purchase;

    int getpoint;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    FirebaseUser mFirebaseUser;
    Intent intent;
    POST post;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);

        //ActionBar 뒤로가기 버튼 생성
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        brand_name = (TextView)findViewById(R.id.detail_brand_name);
        price = (TextView)findViewById(R.id.detailprice);
        image = (ImageView) findViewById(R.id.detail_image);
        CurrentPoint = (TextView)findViewById(R.id.detail_current_point);
        AfterPoint = (TextView)findViewById(R.id.after_point);


        intent = getIntent();
        brand_name.setText(intent.getStringExtra("brand")+"_"+intent.getStringExtra("name"));
        price.setText(Comma_won(intent.getIntExtra("price",0)+"")+" P");
        Glide.with(this).load(Server.localhost+"/img/"+intent.getStringExtra("image")+".jpg").into(image);

        mProgressDialog = new ProgressDialog(GoodsDetailActivity.this );
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("잠시만 기다려주세요...");

        if(mFirebaseUser!=null) {
            mProgressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("user_E_mail", mFirebaseUser.getEmail());
                        json = jsonObject.toString();

                        String result="";
                        post = new POST();
                        result = post.POST(Server.localhost+"/Gmail_user_insert.php", json);


                        //서버에 포인트를 가져와서 보여준다
                        JSONObject jsonObject2;
                        jsonObject2 = new JSONObject(result);
                        getpoint = jsonObject2.getInt("user_point");
                        //CurrentPoint.setText(getpoint+" P");
                        handler.sendEmptyMessage(3);
                        if(getpoint>=intent.getIntExtra("price",0)){
                            //AfterPoint.setText((getpoint-intent.getIntExtra("price",0))+" P");
                            handler.sendEmptyMessage(4);
                        }else if(getpoint<intent.getIntExtra("price",0)){
                            //AfterPoint.setText("0 P");
                            handler.sendEmptyMessage(5);
                        }else{
                            Log.d("getpoint오류","");
                            //Toast.makeText(getApplicationContext(),"로그인 후에 포인트 사용이 가능합니다",Toast.LENGTH_SHORT).show();
                        }

                        Log.d("포인트가져오기",getpoint+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }
            }).start();

        }else {
            Toast.makeText(getApplicationContext(),"로그인 후에 이용가능합니다",Toast.LENGTH_SHORT).show();
        }

//        try{
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.accumulate("user_E_mail", mFirebaseUser.getEmail());
//            json = jsonObject.toString();
//            String result="";
//            try {
//                result =  new HttpAsyncTask(getApplicationContext()).execute(
//                        "http://sjmyweb.esy.es/Gmail_user_insert.php", json).get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//
//            //서버에 포인트를 가져와서 보여준다
//            JSONObject jsonObject2 = new JSONObject(result);
//            int getpoint = jsonObject2.getInt("user_point");
//            CurrentPoint.setText(getpoint+" P");
//            if(getpoint>=intent.getIntExtra("price",0)){
//                AfterPoint.setText((getpoint-intent.getIntExtra("price",0))+" P");
//            }else if(getpoint<intent.getIntExtra("price",0)){
//                AfterPoint.setText("0 P");
//            }else{
//                Toast.makeText(getApplicationContext(),"로그인 후에 포인트 사용이 가능합니다",Toast.LENGTH_SHORT).show();
//            }
//
//            Log.d("포인트가져오기",getpoint+" / ");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        //바코드 셋이미지
//        MultiFormatWriter gen = new MultiFormatWriter();
//        String data = "YOUR DATA";
//        try {
//            final int WIDTH = 320;
//            final int HEIGHT = 180;
//            BitMatrix bytemap = gen.encode(data, BarcodeFormat.CODE_128, WIDTH, HEIGHT);
//            Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
//            for (int i = 0 ; i < WIDTH ; ++i)
//                for (int j = 0 ; j < HEIGHT ; ++j) {
//                    bitmap.setPixel(i, j, bytemap.get(i,j) ? Color.BLACK : Color.WHITE);
//                }
//
//            ImageView view = (ImageView) findViewById(R.id.QRcodeImage);
//            view.setImageBitmap(bitmap);
//            view.invalidate();
//            System.out.println("done!");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }
    private Context thisActivity = (Context)this;
    String json;
    String result;
    int after_user_point;
    public static String Comma_won(String junsu) {
        int inValues = Integer.parseInt(junsu);
        DecimalFormat Commas = new DecimalFormat("#,###");
        String result_int = (String)Commas.format(inValues);
        return result_int;
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
    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0){   // Message id 가 0 이면
                CurrentPoint.setText(Comma_won(after_user_point+"")+" P");
                if(after_user_point>=intent.getIntExtra("price",0)){
                    AfterPoint.setText(Comma_won(after_user_point-intent.getIntExtra("price",0)+"")+" P");
                }else if(after_user_point<intent.getIntExtra("price",0)){
                    AfterPoint.setText("0 P");
                }else{
                    Toast.makeText(getApplicationContext(),"로그인 후에 포인트 사용이 가능합니다",Toast.LENGTH_SHORT).show();
                }
            }else if(msg.what == 1){
                Toast.makeText(getApplicationContext(),"포인트가 부족합니다",Toast.LENGTH_SHORT).show();
            }else if(msg.what == 2){

                Log.d("구매하기스레드결과",result);
                Toast.makeText(getApplicationContext(),"구매가 완료되었습니다",Toast.LENGTH_SHORT).show();
            }else if(msg.what == 3){
                CurrentPoint.setText(Comma_won(getpoint+"")+" P");
            }else if(msg.what == 4){
                AfterPoint.setText(Comma_won(getpoint-intent.getIntExtra("price",0)+"")+" P");
            }else if(msg.what == 5){
                AfterPoint.setText("0 P");
            }

        }
    };
    ProgressDialog mProgressDialog;
    public void good_purchase(View v) {
        //구매하기 클릭
//        final CharSequence[] items;
//        items = new CharSequence[]{ "배경이미지 설정하기", "페이지 추가하기"};
//
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        // 제목셋팅
//        //alertDialogBuilder.setTitle("편집하기");
//        alertDialogBuilder.setItems(items,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if(i==0) {
//
//                        }
//                    }
//                });

        purchase = (ImageButton)findViewById(R.id.good_purchase);

        alertDialogBuilder.setMessage("상품을 구매하시겠습니까?").setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                }).setCancelable(
                false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button
                        result="";
                        if(mFirebaseAuth!=null) {
                            mProgressDialog = new ProgressDialog(GoodsDetailActivity.this );
                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mProgressDialog.setMessage("잠시만 기다려주세요...");
                            mProgressDialog.show();

                            Thread purchaseThread = new PurchaseThread();
                            purchaseThread.start();

                        }else {
                            Toast.makeText(getApplicationContext(),"로그인 후에 이용가능합니다",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        // Title for AlertDialog
        //alert.setTitle("Title");
        // Icon for AlertDialog
        //alert.setIcon(R.drawable.icon);
        alert.show();






    }


    class PurchaseThread extends Thread{
        @Override
        public void run() {
            super.run();
            Log.d("핸들러","시작");
            result="";

            try {

                //구글계정 포인트 가져오기
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("user_E_mail", mFirebaseUser.getEmail());
                json = jsonObject.toString();

                POST post = new POST();
                result = post.POST( Server.localhost+"/Gmail_user_insert.php",json);


                JSONObject jsonObject2 = new JSONObject(result);
                int getpoint = jsonObject2.getInt("user_point");
//            String s_getpoint = jsonObject2.getString("user_point");
//                            Toast.makeText(getApplicationContext(),getpoint+" / "+s_getpoint,Toast.LENGTH_SHORT).show();
                Log.d("포인트가져오기",getpoint+" / ");


                //구매후 남은 포인트를 서버에 저장
                if(getpoint>=intent.getIntExtra("price",0)){//소지포인트가 상품가격보다 많거나 같을 때
                    //구매한 쿠폰정보를 서버에 전송
                    after_user_point = getpoint - intent.getIntExtra("price",0);
                    jsonObject = new JSONObject();
                    jsonObject.accumulate("getpoint", after_user_point);
                    jsonObject.accumulate("user_E_mail", mFirebaseUser.getEmail());
                    jsonObject.accumulate("price", intent.getIntExtra("price",0));
                    jsonObject.accumulate("brand", intent.getStringExtra("brand"));
                    jsonObject.accumulate("name", intent.getStringExtra("name"));
                    jsonObject.accumulate("image",intent.getStringExtra("image"));
                    json = jsonObject.toString();

                    post = new POST();
                    result = post.POST( Server.localhost+"/Gmail_point_reduce.php",json);

                    handler.sendEmptyMessage(2);
                    Log.d("Asynctask결과",result);
//
                    //현재적립금 , 결제후적립금 변경

                    handler.sendEmptyMessage(0);

                }else if(getpoint<intent.getIntExtra("price",0)){

                    handler.sendEmptyMessage(1);
                }
                mProgressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("핸들러","종료");
        }
    }

}


