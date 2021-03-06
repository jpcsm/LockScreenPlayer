package com.lockscreenplayer.js.lockscreenplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Coupon_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Coupon_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Coupon_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    CouponAdapter mAdapter;
    RecyclerView   mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    coupon_item item;
    home home;
    JSONObject jsonObject;
    JSONArray jsonArray;
    private OnFragmentInteractionListener mListener;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    FirebaseUser mFirebaseUser;
    public Coupon_fragment() {
        // Required empty public constructor
    }



    // TODO: Rename and change types and number of parameters
    public static Coupon_fragment newInstance() {
        Coupon_fragment fragment = new Coupon_fragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    String result;
    POST post;
    String json;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        mAdapter = new CouponAdapter(getContext());

//        try {
//            result =  new HttpAsyncTask(getContext()).execute("http://sjmyweb.esy.es/goodsinfo.php", json).get();
//            //Toast.makeText(getContext(),"???????????? ???????????? ????????????"+result,Toast.LENGTH_SHORT).show();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }
    static Thread couponThread;
    static ProgressDialog mProgressDialog;
    TextView tv_empty ;



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAuth = mFirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_page_one_,container,false);

        mAdapter = new CouponAdapter(getContext());
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.CouponRecyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        tv_empty = (TextView)rootView.findViewById(R.id.tv_empty);
        mRecyclerView.setAdapter(mAdapter);
        tv_empty.setVisibility(View.GONE);
        //???????????? ???????????? ????????????

        //??????????????????
//        mProgressDialog = ProgressDialog.show(
//                getContext(), "?????? ???????????????",
//                "????????? ??????????????????..");
//        mProgressDialog = new ProgressDialog(getContext() );
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.setMessage("????????? ??????????????????...");


        result = "";
        json = "";

        if(mFirebaseUser!=null){ //????????????
            CouponThread thread = new CouponThread(); //??????????????? ????????????
            thread.start();
        }else{
            //??????????????? ????????????
            tv_empty.setVisibility(View.VISIBLE);
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }

        return rootView;
    }

    private class CouponThread extends Thread {
        private static final String TAG = "CouponThread";

        public CouponThread() {
            // ????????? ??????
        }
        public void run() {// ??????????????? ???????????? ????????? ??????
            post = new POST();
            Log.d("?????????????????????","??????");
            //handler.sendEmptyMessage(1);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("user_E_mail",mFirebaseUser.getEmail());
                json = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result = post.POST(Server.localhost+"/coupon_info.php", json);
            Log.d("??????????????? ?????????",result);
            if(result!=null) {
//                        if(result=="[]"){
//                            //??????????????? ??????
//                            Log.d("????????????",result);
//                            handler.sendEmptyMessage(4);
//                        }else{
                //???????????? ????????? ???????????????????????? ???????????? ??????
                try {
                    jsonArray = new JSONArray(result);

                    for(int i=0; i < jsonArray.length(); i++){
                        item = new coupon_item();

                        JSONObject jObject = jsonArray.getJSONObject(i);  // JSONObject ??????
                        String Name = jObject.getString("name");
                        int price = jObject.getInt("price");
                        String Brand = jObject.getString("brand");

                        String validity = jObject.getString("validity");

                        String ImageURL = jObject.getString("image");
                        String couponnum  = jObject.getString("num");
                        Log.d("Name",Name);

                        if(jObject.getInt("price")==0){
                            //??????????????? ??????
                            Log.d("????????????",result);
                            handler.sendEmptyMessage(4);
                        }else{
                            item.setValidity(validity);
                            item.setBrand(Brand);
                            item.setImage(ImageURL);
                            item.setName(Name);
                            item.setPrice(price);
                            item.setCouponnum(couponnum);
                            //????????? ????????????
                            Log.d("item",item.getName());
                            mAdapter.add(item);
                        }
                    }
                    handler.sendEmptyMessage(0);

                    //????????????
                    handler.sendEmptyMessage(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }else{
                //????????????????????? ????????????
                //Toast.makeText(getContext(), "??????????????? ?????????????????????"+result.toString(), Toast.LENGTH_SHORT).show();
                Log.d("??????????????????",result);
            }
            Log.d("?????????????????????","???");

            //??????????????????
            home.mProgressDialog.dismiss();

            //handler.sendEmptyMessage(3);
        }
    }

    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

//            if(msg.what == 0){   // Message id ??? 0 ??????
//                mAdapter.notifyDataSetChanged();
//                //Log.d("???????????????",item.getName());
//            }else if(msg.what == 1){
//                mAdapter.clear();
//                mAdapter.notifyDataSetChanged();
//            }else if(msg.what == 2){
//                //Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
//                Log.d("???????????????onCreatView?????????????????????",result);
////                Toast.makeText(getApplicationContext(),"????????? ?????????????????????",Toast.LENGTH_SHORT).show();
//
//            }
//
            switch (msg.what){
                case 0 : mAdapter.notifyDataSetChanged();
                    break;
                case 1 :  mAdapter.clear();
                    mAdapter.notifyDataSetChanged();
                    break;
                case 2 :Log.d("???????????????onCreatView?????????????????????",result);
                    break;
                case 3 :
//                    mProgressDialog.dismiss();
//                    boolean retry = true;
//                    while (retry) {
//                        try {
//                            couponThread.join();
//                            retry = false;
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    break;
                case 4 : tv_empty.setVisibility(View.VISIBLE);
                    break;
            }

        }
    };
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
