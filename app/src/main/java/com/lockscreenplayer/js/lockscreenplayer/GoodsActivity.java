package com.lockscreenplayer.js.lockscreenplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class GoodsActivity extends AppCompatActivity {

    GoodsAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    goods_item item;
    POST post;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String result,json;
    Intent intent;
    ProgressDialog mProgressDialog;
    ActionBar actionBar;
//    static String Name ;
//    static int Need_Point;
//    static String Brand;
//    static String Category;
//    static String ImageURL;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.goods_activity);

            //ActionBar 뒤로가기 버튼 생성
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            mAdapter = new GoodsAdapter(getApplicationContext());
            mRecyclerView = (RecyclerView)findViewById(R.id.GoodsRecyclerView);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.setAdapter(mAdapter);

            //쿠폰 카테고리
            intent = getIntent();
            intent.getStringExtra("category");
            Log.d("카테고리",intent.getStringExtra("category"));
            mProgressDialog = new ProgressDialog(GoodsActivity.this );
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("잠시만 기다려주세요...");
            mProgressDialog.show();

            //서버에서 상품정보 가져오기
            result="";
            JSONObject categoryInfo = new JSONObject();
            try {
                categoryInfo.put("category", intent.getStringExtra("category"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = categoryInfo.toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    post= new POST();
                    result = post.POST(Server.localhost+"/goodsinfo.php", json);

                    if (result != null) {
                        //상품정보 어댑터에 추가
                        try {
                            jsonArray = new JSONArray(result);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                item = new goods_item();

                                JSONObject jObject = jsonArray.getJSONObject(i);  // JSONObject 추출
                                String Name = jObject.getString("Name");
                                int Need_Point = jObject.getInt("Need_Point");
                                String Brand = jObject.getString("Brand");
                                String Category = jObject.getString("Category");
                                String ImageURL = jObject.getString("ImageURL");
                                //Log.d("Name",Name);

                                item.setBrand(Brand);
                                item.setImage(ImageURL);
                                item.setName(Name);
                                item.setPrice(Need_Point);

                                //어댑터 업데이트
                                Log.d("item",item.getName());
                                mAdapter.add(item);

                                //Log.d("상품목록스레드결과",result);
                            }
                            handler.sendEmptyMessage(1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //프로그래스바
                        mProgressDialog.dismiss();
                    } else {
                        //서버연결실패시 예외처리
                        //Toast.makeText(getApplicationContext(), "서버연결에 실패하였습니다"+result.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("서버연결실패", result);
                    }
                    Log.d("상품정보 가져옴", result);
                }
            }).start();



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

    public static String Comma_won(String junsu) {
        int inValues = Integer.parseInt(junsu);
        DecimalFormat Commas = new DecimalFormat("#,###");
        String result_int = (String)Commas.format(inValues);
        return result_int;
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0){   // Message id 가 0 이면
                mAdapter.add(item);
            }else if(msg.what == 1){
                mAdapter.notifyDataSetChanged();
            }else if(msg.what == 2){
//                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(),"구매가 완료되었습니다",Toast.LENGTH_SHORT).show();

            }

        }
    };
}
