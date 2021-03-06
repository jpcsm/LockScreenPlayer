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
        //??????????????? ??????
        Glide.with(this).load(Server.localhost+"/img/ad_"+(pos+1)+".jpg")
                .bitmapTransform(new BlurTransformation(getContext()))
                .into(imageView);
        Drawable alpha1 = black.getBackground();
        alpha1.setAlpha(70);

        //Glide.with(this).load(Server.localhost+"/img/ad_"+(pos+1)+".jpg").into(smallImage);
        //???????????? ??????
        Glide.with(this).load(Server.localhost+"/img/ad_"+(pos+1)+".jpg")
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(smallImage);
//Server.localhost+"/img/ad_"+(pos+1)+".jpg"
        //??????
        //.bitmapTransform(new CropCircleTransformation(this))

        //Toast.makeText(this.getContext(),""+pos,Toast.LENGTH_SHORT).show();
        //imageView.setImageBitmap(bmImg);
        btn_gotopage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //?????????????????? ?????? url??????,???????????????,???????????? ??????
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

                    //???????????? ???????????? ????????????
                    try {
                        result =  new HttpAsyncTask(getContext()).execute(Server.localhost+"/ad_info.php", json).get();
                        //Toast.makeText(getContext(),"???????????? ???????????? ????????????"+result,Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }


                    //???????????? ?????????
                    if(result!=null) {

                    try {
                        //???????????? ????????? ????????????
                        jsonObject = new JSONObject(result);
                        ad_site_url = jsonObject.getString("ad_site_url");
                        ad_point = jsonObject.getString("ad_point");
                        ad_overlap = jsonObject.getString("ad_overlap");
                        //String ad_idx = jsonObject.getString("idx");



//                        Toast.makeText(getContext(),"ad_point : "+ad_point,Toast.LENGTH_LONG).show();
                        if(mFirebaseUser!=null){
                            if(!ad_overlap.contains(mFirebaseUser.getEmail())){
                                //??????????????????


                                //????????? ????????? ?????? ??????
                                jsonObject.accumulate("user_E_mail", mFirebaseUser.getEmail());
                                //jsonObject.accumulate("ad_idx", ad_idx);
                                String json2 = jsonObject.toString();
                                try {
                                    result =  new HttpAsyncTask(getContext()).execute(Server.localhost+"/Gmail_point_update.php", json2).get();
//                                    Toast.makeText(getContext(),"????????? ????????? ?????? ??????"+result,Toast.LENGTH_LONG).show();
                                    Log.d("????????? ????????? ?????? ??????", result);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getContext(),ad_point+"????????? ??????", Toast.LENGTH_SHORT).show();


                                //???????????? ???????????? FCM??????
                                FCMThread fcmThread = new FCMThread();
                                fcmThread.start();

                            }else{
                                //??????????????????
                            }

                        }
                        //???????????????????????? ??????
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
                    //????????????????????? ????????????
                    Toast.makeText(getContext(), "??????????????? ?????????????????????"+result.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("??????????????????",result)     ;
                }

            }
        });


        return rootView;
    }
    String json;

    //FCM ???????????? ?????????????????????
    class FCMThread extends Thread {
        @Override
        public void run() {
            super.run();

                String result="";
                try {
                    //????????? ??????????????? ??????
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("ad_point", ad_point);
                    jsonObject.accumulate("Token", FirebaseInstanceId.getInstance().getToken());
                    json = jsonObject.toString();

                    POST post = new POST();
                    result = post.POST(Server.localhost+"/fcm/push_notification.php", json);//FCM?????? ??????php ????????? ????????? ??????

                    Log.d("???????????? FCMThread","ad_point : "+ad_point);
                    Log.d("???????????? FCMThread","result : "+result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            //mProgressDialog.dismiss();
        }
    }
}