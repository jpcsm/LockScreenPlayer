package com.lockscreenplayer.js.lockscreenplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by lenovo on 2017-01-31.
 */
public class ChildPagerAdapter extends FragmentStatePagerAdapter {

    Context context;


//        private final int[] galImages = new int[] {
//                R.drawable.common_google_signin_btn_icon_light_focused,
//                R.drawable.common_ic_googleplayservices,
//                R.drawable.common_plus_signin_btn_icon_light_normal,
//                R.drawable.common_plus_signin_btn_icon_light_normal,
//                R.drawable.common_ic_googleplayservices,
//                R.drawable.common_google_signin_btn_icon_light_focused
//
//        };

    ChildPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 5;//lock_pager.bytebit.length;
    }

    @Override
    public Fragment getItem(int position) {
        //return PageFragment.create(galImages[position]);
        //byte[] a = bytebit[position];

        return ChildPageFragment.newInstance(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}

class ChildPageFragment extends Fragment {
    static BitmapFactory.Options options;
    static int pos;
    String ad_site_url;
    String ad_overlap;
    URL url;
    String ad_point;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    GoogleApiClient mGoogleApiClient;
    FirebaseUser mFirebaseUser;
    String mUsername;
    String mPhotoUrl;

    public static ChildPageFragment newInstance(int sectionNumber) {
        ChildPageFragment fragment = new ChildPageFragment();
        Bundle argsn = new Bundle();

        argsn.putInt("H_num", sectionNumber);
        fragment.setArguments(argsn);

        return fragment;
    }

    //    public static PageFragment create(int image) {
//        PageFragment fragment = new PageFragment();
//        Bundle args = new Bundle();
//        args.putInt("image", image);
//        fragment.setArguments(args);
//        return fragment;
//    }
//        public static PageFragment create(int pos) {
//            PageFragment fragment = new PageFragment();
//            Bundle args = new Bundle();
//            //args.putByteArray("image", image);
//            //args.putByteArray("image", image);
//            args.putInt("pos", pos);
//            fragment.setArguments(args);
//            return fragment;
//        }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //image = getArguments().getByteArray("image");
        //image = getArguments().getByteArray("image");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.ad_fragment_horizon, container, false);
        pos = getArguments().getInt("H_num");
        options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
        Button btn_gotopage = (Button)rootView.findViewById(R.id.gotopage);
        ImageView smallImage = (ImageView)rootView.findViewById(R.id.smallImage);
        ImageView black = (ImageView)rootView.findViewById(R.id.black);
        //배경이미지 블러
        Glide.with(this).load(Server.localhost+"/img/ad_"+(pos+1)+".jpg")
                .bitmapTransform(new BlurTransformation(getContext()))
                .into(imageView);
        Drawable alpha1 = black.getBackground();
        alpha1.setAlpha(70);

        //Glide.with(this).load(Server.localhost+"/img/ad_"+(pos+1)+".jpg").into(smallImage);
        //글라이드 원형
        Glide.with(this).load(Server.localhost+"/img/ad_"+(pos+1)+".jpg")
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(smallImage);
//Server.localhost+"/img/ad_"+(pos+1)+".jpg"
        //원형
        //.bitmapTransform(new CropCircleTransformation(this))

        //Toast.makeText(this.getContext(),""+pos,Toast.LENGTH_SHORT).show();
        //imageView.setImageBitmap(bmImg);
        btn_gotopage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //광고이미지에 맞는 url주소,적립포인트,중복확인 요청
                pos=getArguments().getInt("H_num");
                //HttpAsyncTask httpTask = new HttpAsyncTask(getContext());

                String json = "";

                //build jsonObject
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.accumulate("pos", pos+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //convert JSONObject to JSON to String
                json = jsonObject.toString();
                String result ="";

                    //서버에서 광고정보 가져오기
                    try {
                        result =  new HttpAsyncTask(getContext()).execute(Server.localhost+"/ad_info.php", json).get();
                        //Toast.makeText(getContext(),"서버에서 광고정보 가져오기"+result,Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }


                    //서버연결 성공시
                    if(result!=null) {

                    try {
                        //서버에서 가져온 광고정보
                        jsonObject = new JSONObject(result);
                        ad_site_url = jsonObject.getString("ad_site_url");
                        ad_point = jsonObject.getString("ad_point");
                        ad_overlap = jsonObject.getString("ad_overlap");
                        //String ad_idx = jsonObject.getString("idx");



//                        Toast.makeText(getContext(),"ad_point : "+ad_point,Toast.LENGTH_LONG).show();
                        if(mFirebaseUser!=null){
                            if(!ad_overlap.contains(mFirebaseUser.getEmail())){
                                //광고본적없음


                                //서버에 적립된 캐시 적용
                                jsonObject.accumulate("user_E_mail", mFirebaseUser.getEmail());
                                //jsonObject.accumulate("ad_idx", ad_idx);
                                String json2 = jsonObject.toString();
                                try {
                                    result =  new HttpAsyncTask(getContext()).execute(Server.localhost+"/Gmail_point_update.php", json2).get();
//                                    Toast.makeText(getContext(),"서버에 적립된 캐시 적용"+result,Toast.LENGTH_LONG).show();
                                    Log.d("서버에 적립된 캐시 적용", result);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getContext(),ad_point+"포인트 적립", Toast.LENGTH_SHORT).show();


                                //캐시적립 푸시알림 FCM요청
                                FCMThread fcmThread = new FCMThread();
                                fcmThread.start();

                            }else{
                                //광고본적있음
                            }

                        }
                        //해당광고페이지로 이동
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        Uri u = Uri.parse(ad_site_url);
                        i.setData(u);
                        startActivity(i);


                    } catch (JSONException e) {
                        e.printStackTrace();
//                    Toast.makeText(getcontext, "JSONException"+jsonObject.toString(), Toast.LENGTH_LONG).show();
//                    Log.d("JSONException",jsonObject.toString())     ;
                    }
                }else{
                    //서버연결실패시 예외처리
                    Toast.makeText(getContext(), "서버연결에 실패하였습니다"+result.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("서버연결실패",result)     ;
                }

            }
        });


        return rootView;
    }
    String json;

    //FCM 캐시적립 푸시알림스레드
    class FCMThread extends Thread {
        @Override
        public void run() {
            super.run();

                String result="";
                try {
                    //서버에 적립포인터 전송
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("ad_point", ad_point);
                    jsonObject.accumulate("Token", FirebaseInstanceId.getInstance().getToken());
                    json = jsonObject.toString();

                    POST post = new POST();
                    result = post.POST(Server.localhost+"/fcm/push_notification.php", json);//FCM푸시 요청php 파일로 데이터 전송

                    Log.d("락스크린 FCMThread","ad_point : "+ad_point);
                    Log.d("락스크린 FCMThread","result : "+result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            //mProgressDialog.dismiss();
        }
    }
}