package com.lockscreenplayer.js.lockscreenplayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URL;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.R.attr.fragment;

/**
 * Created by lenovo on 2017-01-31.
 */
 class UserCustomAdapter extends FragmentStatePagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    Context context;
    static int Page_Num;

//        private final int[] galImages = new int[] {
//                R.drawable.common_google_signin_btn_icon_light_focused,
//                R.drawable.common_ic_googleplayservices,
//                R.drawable.common_plus_signin_btn_icon_light_normal,
//                R.drawable.common_plus_signin_btn_icon_light_normal,
//                R.drawable.common_ic_googleplayservices,
//                R.drawable.common_google_signin_btn_icon_light_focused
//
//        };


    UserCustomAdapter(FragmentManager fragmentManager,Context context) {
        super(fragmentManager);
        this.context=context;
        if(Page_Num==0)Page_Num=1;
    }


    @Override
    public int getCount() {

        return Page_Num;//lock_pager.bytebit.length;
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return super.POSITION_NONE;
//    }

    @Override
    public Fragment getItem(int position) {
        //return PageFragment.create(galImages[position]);
        //byte[] a = bytebit[position];

        return UserCustomFragment.newInstance(position);
    }



    void updatePageCount(ViewPager viewPager) {
        int currentItem = viewPager.getCurrentItem();
        viewPager.setAdapter(viewPager.getAdapter());
        viewPager.setCurrentItem(currentItem);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) { // ?????????????????? ???????????? ???????????? ?????????
        UserCustomFragment fragment = (UserCustomFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) { //?????????????????? ???????????? ??????????????? ?????????
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) { //?????????????????? ??????????????? ?????????
        return registeredFragments.get(position);
    }
    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}


public class UserCustomFragment extends Fragment  {

    static BitmapFactory.Options options;
    static int pos;
    String ad_site_url;
    String ad_overlap;
    URL url;
    String ad_point;
    TextView bright;
    UserCustomAdapter userCustomAdapter;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    GoogleApiClient mGoogleApiClient;
    FirebaseUser mFirebaseUser;
    String mUsername;
    String mPhotoUrl;
    ViewGroup rootView;
    FrameLayout VideoParentLayout;
    DBHelper dbHelper;
    ViewPager ChildViewPager;

    int FULLSCREEN_TO_VIDEO = 10023;
    private Uri mImageCaptureUri;
    String videoPath;
    int PICK_FROM_ALBUM = 1009;

    FrameLayout black;
    private final int SELECT_IMAGE = 1;
    private final int SELECT_MOVIE = 2;
    private final int CAMERA_FILTER = 3;
    Uri VideoURI;
    VideoView videoView;
    int VideoPlayTime;
    public static MediaController mediaController;

    SharedPreferences sp;

    SeekBar seekbrightness;
    int setProgress=0;
    float bg_alpha;
    SharedPreferences.Editor ed;

    public static UserCustomFragment newInstance(int sectionNumber) {
        UserCustomFragment fragment = new UserCustomFragment();
        Bundle argsn = new Bundle();

        argsn.putInt("H_num", sectionNumber);
        fragment.setArguments(argsn);

        return fragment;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {//??????????????? ????????? ???????????????
        if (isVisibleToUser) {
            // ?????????.
            if(videoView!=null) {
                //Toast.makeText(getContext(),"?????????????????????????????????"+getArguments().getInt("H_num"),Toast.LENGTH_SHORT).show();
                //videoView.seekTo(VideoPlayTime);
                //videoView.setVisibility(View.VISIBLE);
                videoView.start();
                //VideoParentLayout.setVisibility(View.GONE);
                mediaController.hide();
                mediaController = new MediaController(getContext());
                //???????????? ?????? ??????????????? ???????????? ???????????????
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
            }
            Log.d("???????????? setUserVisibleHint ????????????????????????",getArguments().getInt("H_num")+"\nisVisibleToUser :"
                      +isVisibleToUser);

            //Toast.makeText(getContext(),"????????????????????????"+getArguments().getInt("H_num"),Toast.LENGTH_SHORT).show();
        }else {
            // ????????????.

            if(videoView!=null) {
                videoView.setMediaController(null);
                videoView.pause();
                mediaController.hide();
            }
            Log.d("???????????? setUserVisibleHint ?????????",getArguments().getInt("H_num")+"\nisVisibleToUser :"
                        +isVisibleToUser);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //image = getArguments().getByteArray("image");
            //image = getArguments().getByteArray("image");
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //FragmentTransaction add(this, );

//        lock_pager.ChildViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                //Toast.makeText(getContext(),"onPageScrolled",Toast.LENGTH_SHORT).show();
//               // Log.d("onPageScrolled", position+"");
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Toast.makeText(getContext(),"onPageSelected"+position,Toast.LENGTH_SHORT).show();
//                Log.d("onPageSelected", position+"");
//
////                if (currentPosition != 0) {//???????????? ??? ????????????
////                    videoView.seekTo(currentPosition);
////                    videoView.start();
////                } else {
////                    videoView.start();
////                }
//
////                videoView.pause();//???????????????
////                currentPosition = videoView.getCurrentPosition();
//            }
//
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Log.d("onPageScrolled", state+"");
//                Toast.makeText(getContext(),"onPageScrollStateChanged"+state,Toast.LENGTH_SHORT).show();
//
//            }
//        });
            //SQLite
            dbHelper = new DBHelper(getContext(), "lockpager.db", null, 1);
        }

//    private void requestPermissionDenial() {
//
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_CONTACTS)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.READ_CONTACTS)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(thisActivity,
//                        new String[]{Manifest.permission.READ_CONTACTS},
//                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }
//    }

    public void StopToVideo(){
        Log.d("???????????? StopToVideo","pos "+getArguments().getInt("H_num")+"\n");
        videoView.pause();
        mediaController.hide();
    }
    public void StartToVideo(){
        Log.d("???????????? StartToVideo","pos "+getArguments().getInt("H_num")+"\n");
        videoView.start();
        //mediaController.hide();
    }
    @Override
    public void onResume() {
        super.onResume();
        //if(videoView!=null)videoView.start();
        //Toast.makeText(getContext(),"onResume",Toast.LENGTH_SHORT).show();
        //??????????????? setImageURI
        sp = getContext().getSharedPreferences("LockScreenBackgroundImage",getContext().MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        RelativeLayout relativeLayout = (RelativeLayout)rootView.findViewById(R.id.UserCustomLayout);
        ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.Btn_usercustom_option);
        ImageView main = (ImageView)rootView.findViewById(R.id.Btn_usercustom_BG);
        //FrameLayout black = (FrameLayout)rootView.findViewById(R.id.Black_FL);
        ImageView front = (ImageView)rootView.findViewById(R.id.front);
        TextView tv_empty = (TextView)rootView.findViewById(R.id.tv_empty);
        black = (FrameLayout) rootView.findViewById(R.id.Black_FL);


        if(sp.getString("bright"+getArguments().getInt("H_num"),null)!=null){
            Log.d("????????????  ??????????????????????????? onresume progress",sp.getString("bright"+getArguments().getInt("H_num"),null));
            int progress = Integer.parseInt(sp.getString("bright"+getArguments().getInt("H_num"),null));
            bg_alpha = 1.0f-progress/100f;
            black.setAlpha(bg_alpha);
            Log.d("???????????? ??????????????????????????? onresume ??????",bg_alpha+"");
        }
        //???????????? ?????????????????? ??????
        if(mImageCaptureUri!=null) {
            tv_empty.setVisibility(View.GONE);
            //??????????????? ??????
            Glide.with(this).load(mImageCaptureUri)
                    .bitmapTransform(new BlurTransformation(getContext()))
                    .into(main);
            // Drawable alpha1 = black.getBackground();
            //alpha1.setAlpha(1-bg_alpha/100);
//            Glide.with(this).load(mImageCaptureUri)
//                    .bitmapTransform(new CropCircleTransformation(getContext()))
//                    .into(front);
            //main.setImageURI(mImageCaptureUri);


        }

        String imageUriString = sp.getString("pos"+getArguments().getInt("H_num"),null);
        if(imageUriString!=null){
            Uri imageUri = Uri.parse(imageUriString);

            tv_empty.setVisibility(View.GONE);
            //??????????????? ??????
            Glide.with(this).load(imageUri)
                    .bitmapTransform(new BlurTransformation(getContext()))
                    .into(main);
            //Drawable alpha1 = black.getBackground();
            //alpha1.setAlpha(1-bg_alpha/100);
//            Glide.with(this).load(imageUri)
//                    .bitmapTransform(new CropCircleTransformation(getContext()))
//                    .into(front);
            //main.setImageURI(mImageCaptureUri);
        }

        //?????????
        //if(mediaController!=null) mediaController.setVisibility(View.GONE);
        if(sp.getString("VideoURI"+getArguments().getInt("H_num"),null)!=null) {//????????? ???????????? ????????????

//            if(getArguments().getInt("H_num")==0){
//
            //mediaController.setVisibility(View.VISIBLE);
                videoView.setMediaController(mediaController);
               //if(getArguments().getInt("H_num")==0) videoView.start();
//            }
//            else{
//                videoView.setMediaController(null);
//            if(mediaController!=null) mediaController.setVisibility(View.GONE);
//            }
            videoPath = (sp.getString("VideoURI"+getArguments().getInt("H_num"),null));
            videoView.setVideoPath(videoPath);
            //videoView.setVisibility(View.VISIBLE);

            //??????????????????
//            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath,
//                    MediaStore.Images.Thumbnails.MINI_KIND);
//            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
//            videoView.setBackgroundDrawable(bitmapDrawable);

            //????????? ???????????? ????????????
            Log.d("VideoPlayTime",sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0)+"\n\n\n\n\n\n");
            VideoPlayTime = sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0);
            videoView.seekTo(VideoPlayTime);
            Log.d("???????????? ??????????????????????????? onResume ?????????????????????",videoPath+"\n??????????????? : "+VideoPlayTime);

        }

//        UserCustomAdapter.Page_Num = sp.getInt("Page_Num",1);
//        lock_pager.userCustomAdapter.notifyDataSetChanged();
        //Toast.makeText(getContext(),"onResume MAX_Page : "+UserCustomAdapter.Page_Num, Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getArguments().getInt("H_num");
        Log.d("onDestroyView ","");
    }
    @Override
    public void onPause() {//Fragment??? ???????????? Action??? ?????? ????????? ????????????.
        super.onPause();
        Log.d("????????????  onPause ","?????????????????????"+getArguments().getInt("H_num"));
        //mediaController.setVisibility(mediaController.GONE);
        videoView.pause();//?????????????????? ???????????? ???????????????x
        sp = getContext().getSharedPreferences("LockScreenBackgroundImage",getContext().MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        //????????? ???????????? ??????
        if(videoView.getCurrentPosition()!=0){
            ed.putInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),videoView.getCurrentPosition());
            ed.commit();
        }
        //Toast.makeText(getContext(),"????????????????????? onpause"+getArguments().getInt("H_num"),Toast.LENGTH_SHORT).show();
        Log.d("???????????? onPause isActivated","\n"+
                "pos : "+getArguments().getInt("H_num")+"/"+ "\n"+
                "videoPalyTime : "+videoView.getCurrentPosition()+" / "+sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0)+"\n");
    }
    Button btn_fullscreen;
    Thread td;
    Boolean stop =false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.usercustom_fragment_horizon, container, false);
        pos = getArguments().getInt("H_num");
        videoView = (VideoView)rootView.findViewById(R.id.videoView);
        RelativeLayout relativeLayout = (RelativeLayout)rootView.findViewById(R.id.UserCustomLayout);
        ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.Btn_usercustom_option);
        ImageView getimageView = (ImageView)rootView.findViewById(R.id.Btn_usercustom_BG);
        TextView tv_empty = (TextView)rootView.findViewById(R.id.tv_empty);
        sp = getContext().getSharedPreferences("LockScreenBackgroundImage",getContext().MODE_PRIVATE);
        VideoParentLayout  = (FrameLayout)rootView.findViewById(R.id.VideoParentLayout);
        btn_fullscreen = (Button)rootView.findViewById(R.id.full_video);
        black = (FrameLayout) rootView.findViewById(R.id.Black_FL);
        //black.setAlpha(1.0f);

        //Toast.makeText(getContext(),"oncreatview",Toast.LENGTH_SHORT).show();
        VideoPlayTime = sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0);

        //mediaController.setEnabled(true);
        //mediaController.setVisibility(View.GONE);
        mediaController = new MediaController(getContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
//        videoView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                mediaController.setVisibility(View.VISIBLE);
//
//
//                Log.d("???????????? onTouch",mediaController.getVisibility()+"");
//                return false;
//            }
//        });

        if(sp.getString("VideoURI"+getArguments().getInt("H_num"),null)!=null){
            Log.d("???????????? onCreateView VideoURI",sp.getString("VideoURI"+getArguments().getInt("H_num"),null));
            videoView.setVisibility(View.VISIBLE);
            VideoURI =  Uri.parse(sp.getString("VideoURI"+getArguments().getInt("H_num"),null));
            videoPath = (sp.getString("VideoURI"+getArguments().getInt("H_num"),null));
            videoView.setVideoPath(videoPath);
        }else{
            //????????? ?????????????????? ???????????? ??????????????? ?????????
            videoView.setVisibility(View.GONE);
            Log.d("???????????? nCreateView - videoView","null");
        }
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) { //???????????? ????????????
                Log.d("???????????? onPrepared","??????????????? :"+VideoPlayTime);
//                videoView.seekTo(VideoPlayTime);
//                videoView.start();
                //videoView.pause();//???????????????
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() { //???????????? ???????????????
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("???????????? ??????????????????????????? ???????????? ??????",btn_fullscreen.getVisibility()+"\nmotionEvent : "+motionEvent);
                //videoView.setMediaController(mediaController);

                if(btn_fullscreen.getVisibility()!=View.VISIBLE){
                    mediaController.setVisibility(View.VISIBLE);
                    btn_fullscreen.setVisibility(View.VISIBLE); //???????????? ???????????????
//                mediaController.show(); //?????????????????????
//

                    td = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(3000); // 3????????? ?????? ?????????

                            handler.sendEmptyMessage(0);//????????????, ???????????? ????????? ??????
    //                        getActivity().runOnUiThread(new Runnable() {
    //                            @Override
    //                            public void run() {
    //
    //                            }
    //                        });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    td.start();
                }else {
                    td.interrupt();
                    btn_fullscreen.setVisibility(View.GONE);
                }

                return false;
            }
        });

        //?????? ?????????
//        black = (ImageView)rootView.findViewById(R.id.Black);
//        Drawable alpha1 = black.getBackground();
//        alpha1.setAlpha(1-bg_alpha/100);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//??????????????? ???????????? ?????? ?????? ???????????? ?????????
                Btn_usercustom_option(getArguments().getInt("H_num"));
            }
        });

        SharedPreferences.Editor ed = sp.edit();

        //??????????????? setImageURI
//        String imageUriString = sp.getString("pos"+getArguments().getInt("H_num"),null);
//        if(imageUriString!=null){
//            Uri imageUri = Uri.parse(imageUriString);
//            getimageView.setImageURI(imageUri);
//
//            tv_empty.setVisibility(View.GONE);
//        }
        //Toast.makeText(getContext(),"onCreateView : "+imageUriString, Toast.LENGTH_SHORT).show();
        //lock_pager.userCustomAdapter.notifyDataSetChanged();

        btn_fullscreen.setOnClickListener(new View.OnClickListener() { //???????????? ???????????? ???????????????
            @Override
            public void onClick(View view) {
                //???????????? ?????????????????? ????????????
                Intent i = new Intent(getContext(), VideoViewFullscreenActivity.class);
                i.putExtra("videoPath",videoPath);//????????? ??????
                i.putExtra("VideoPlayTime",videoView.getCurrentPosition());//????????? ???????????????
                i.putExtra("position",getArguments().getInt("H_num"));//??????????????? ??????
                startActivityForResult(i,FULLSCREEN_TO_VIDEO);
            }
        });
        if(getArguments().getInt("H_num")==0) videoView.start();
        UserCustomAdapter.Page_Num = sp.getInt("Page_Num",1);
        lock_pager.userCustomAdapter.notifyDataSetChanged();
        return rootView;
    }

    public android.os.Handler handler = new android.os.Handler(){

        @Override
        public void handleMessage(Message msg) {
            Log.d("???????????? ??????????????????????????? ????????? msg.what", msg.what+"");
            if(msg.what == 0){   //???????????? ??????????????? ?????????
                btn_fullscreen.setVisibility(View.GONE);//?????? ?????????
               // mediaController. hide();//?????????????????????
                Log.d("???????????? ??????????????????????????? ???????????? ?????? ????????????",btn_fullscreen.getVisibility()+"");
            }else if(msg.what == 1){ //???????????????

            }else if(msg.what == 2){
            }

        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //???????????? ???????????????

        if ( requestCode == PICK_FROM_ALBUM ) {
            if (data != null) {
                SharedPreferences sp = getContext().getSharedPreferences("LockScreenBackgroundImage",getContext().MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();


                //???????????????uri ???????????? ??????
                Toast.makeText(getContext(),"getposition : "+pos, Toast.LENGTH_SHORT).show();
                mImageCaptureUri = data.getData();
                ed.putString("pos"+ getArguments().getInt("H_num"),""+mImageCaptureUri.toString());
                ed.commit();

                Log.d("getPathtoString", mImageCaptureUri.getPath().toString());
                Log.d("toString", mImageCaptureUri.toString());

            }
        }else if (requestCode == SELECT_MOVIE){//???????????????
            if(data!=null){
                VideoURI = data.getData();
                videoPath = getPath(getContext(),VideoURI);
                ed.putString("VideoURI"+getArguments().getInt("H_num"),videoPath);
                ed.remove("videoView.getCurrentPosition"+getArguments().getInt("H_num"));//????????? ?????? ????????? ??? ????????????????????? ???????????? ?????????
                ed.commit();
                String name = getName(VideoURI);
                String uriId = getUriId(VideoURI);
                videoView.setVideoURI(VideoURI);
                videoView.setVisibility(View.VISIBLE);
                Log.d("???????????? onActivityResult SELECT_MOVIE", "pos : "+getArguments().getInt("H_num")+"\n???????????? : " + videoPath + "\n????????? : " + name + "\nuri : " + VideoURI.toString() + "\nuri id : " + uriId);
                videoView.start();
            }else{ //????????? ???????????? ??????
                Log.d("???????????? onActivityResult SELECT_MOVIE", "????????? ???????????? ??????");

            }
        }else if (requestCode == FULLSCREEN_TO_VIDEO) {//????????? ?????????????????? ?????????????????? ??????

            if(data!=null){
                videoView.seekTo(data.getIntExtra("VideoPlayTime",0));
                Log.d("???????????? ?????????????????? ?????????????????? ?????? VideoPlayTime",data.getIntExtra("VideoPlayTime",0)+"");
            }
            //videoView.seekTo(sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0)); //???????????? ????????????
            videoView.start();
        }

        Log.d("???????????? ??????????????????????????? requestCode",requestCode+"\nVideoPlayTime : "+sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0));
        if(mediaController!=null)mediaController.setVisibility(View.GONE);
        if(btn_fullscreen!=null)btn_fullscreen.setVisibility(View.GONE); //???????????? ?????????????????????
    }

    // ???????????????
    private void doSelectMovie() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try
        {
            startActivityForResult(i, SELECT_MOVIE);
        } catch (android.content.ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private void setSeekbar(){

        sp = getContext().getSharedPreferences("LockScreenBackgroundImage",getContext().MODE_PRIVATE);
        ed = sp.edit();

        seekbrightness.setMax(100);
        if(sp.getString("bright"+getArguments().getInt("H_num"),null)!=null){
            Log.d("???????????? bright.settext getArguments",sp.getString("bright"+getArguments().getInt("H_num"),null));
            bright.setText(sp.getString("bright"+getArguments().getInt("H_num"),null));
            seekbrightness.setProgress(Integer.parseInt(sp.getString("bright"+getArguments().getInt("H_num"),null)));
        }else {
            Float al = black.getAlpha();
            Log.d("???????????? bright.settext (int) (al*100-1)","");
            bright.setText(70+"");
            seekbrightness.setProgress(70);
        }

        seekbrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                //Log.d("??????", progress+"");
                bright.setText(""+progress);
                //Log.d("bg_alpha", bg_alpha+"");

                ed.putString("bright"+getArguments().getInt("H_num"),progress+"");
                ed.commit();
                bg_alpha = 1.0f-progress/100f;
                black.setAlpha(bg_alpha);
                Log.d("????????? ??????", black.getAlpha()+"," + bg_alpha);
            }
        });
    }

    @Override
    public void onStart() { // Fragment??? ????????? ???????????? ????????????. ???????????? Action??? ?????? ?????? ??? ??? ??????.
        super.onStart();
        Log.d("????????????  onStart ","?????????????????????"+getArguments().getInt("H_num"));
    }

    @Override
    public void onStop() { //Fragment??? ???????????? ????????? ???????????? ?????? ??????, Fragment????????? ?????? ???????????? ?????? ??????.
        super.onStop();
        Log.d("????????????  onStop ","?????????????????????"+getArguments().getInt("H_num"));

    }

    public void Btn_usercustom_option(final int position) {
        //???????????? ????????????
        sp = getContext().getSharedPreferences("LockScreenBackgroundImage",getContext().MODE_PRIVATE);
        ed = sp.edit();

        final CharSequence[] items;
        if(sp.getInt("Page_Num",1)>1) {
            items = new CharSequence[]{"??????????????? ????????????","????????? ????????????",  "????????? ????????????","????????????", "??????????????? ????????????"};

        }else{
            items = new CharSequence[]{ "??????????????? ????????????","????????? ????????????",  "????????? ????????????","????????????"};

        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // ????????????
        //alertDialogBuilder.setTitle("????????????");
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {


                        //Toast.makeText(getContext(),"position : "+getArguments().getInt("H_num"), Toast.LENGTH_SHORT).show();

                        if(id==0) {
                            //????????? ????????????
                            final CharSequence[] GetImageitems;
                            GetImageitems = new CharSequence[]{ "?????????","????????????"};
                            AlertDialog.Builder GetImagealertDialogBuilder = new AlertDialog.Builder(getContext());
                            GetImagealertDialogBuilder.setItems(GetImageitems, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i){
                                        case 0:
                                            // ?????? ??????
                                            Intent intent = new Intent(Intent.ACTION_PICK);
                                            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                                            //intent.putExtra("pos",position);
                                            startActivityForResult(intent, PICK_FROM_ALBUM);
                                            break;
                                        case 1:
                                            Intent intent1 = new Intent(getContext(), FilterCameraActivity.class);
                                            intent1.putExtra("pos",getArguments().getInt("H_num"));
                                            startActivityForResult(intent1,CAMERA_FILTER);
                                            break;
                                        case 2:
                                            Intent intent2 = new Intent(getContext(), Tutorial3Activity.class);
                                            startActivity(intent2);
                                            break;
                                    }
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog GetImagealertDialog = GetImagealertDialogBuilder.create();
                            GetImagealertDialog.show();



                        }else if(id==1) {
                            //?????????????????????
                            doSelectMovie();


                        }else if(id==2) {
                            //????????? ????????????

                            UserCustomAdapter.Page_Num++;

                            ed.putInt("Page_Num",UserCustomAdapter.Page_Num);
                            ed.commit();
                            Toast.makeText(getContext(),"????????? ??????"+UserCustomAdapter.Page_Num,Toast.LENGTH_SHORT).show();

                        }else if(id==3) {
                           //????????????
                            View innerView = getLayoutInflater(getArguments()).inflate(R.layout.seek_bar, null);
                            AlertDialog.Builder adialog = new AlertDialog.Builder(
                                    getContext());
                            adialog.setView(innerView);
                            seekbrightness = (SeekBar) innerView.findViewById(R.id.seekbrightness);
                            bright = (TextView) innerView.findViewById(R.id.bright);

                            setSeekbar();

                            AlertDialog alert = adialog.create();
                            //alert.setTitle("??????");
                            alert.show();

                        }else if(id==4) {
                            //????????? ????????????
//                            Toast.makeText(getContext(),"i"+position+"\n"+adapter.menu_itemlist.get(position).getName()+"(???)??? ?????????????????????",Toast.LENGTH_SHORT).show();
                            UserCustomAdapter.Page_Num--;
                            ed.putInt("Page_Num",UserCustomAdapter.Page_Num);

                            //???????????????
                            for(int j=getArguments().getInt("H_num");j<=UserCustomAdapter.Page_Num;++j) {
                                //?????????uri pos 1???
                                ed.remove("pos"+j);
                                ed.commit();
                                String s1 = "";
                                s1 = sp.getString("pos"+(j+1),null);
                                ed.putString("pos"+j,s1);
                                ed.putString("pos"+(j+1),null);

                                //????????? VideoURI 1???
                                ed.remove("VideoURI"+j);
                                ed.commit();
                                String s2 = "";
                                s2 = sp.getString("VideoURI"+(j+1),null);
                                ed.putString("VideoURI"+j,s2);
                                ed.putString("VideoURI"+(j+1),null);
                                //Toast.makeText(getContext(),"pos"+getArguments().getInt("H_num"),Toast.LENGTH_SHORT).show();

                                // ?????? ?????? ????????? 1???
                                ed.remove("bright"+j);
                                ed.commit();
                                String s3 = "";
                                s3 = sp.getString("bright"+(j+1),null);
                                ed.putString("bright"+j,s3);
                                ed.putString("bright"+(j+1),null);
                            }
                            ed.commit();

                            if(getArguments().getInt("H_num")!=0){
                                lock_pager.ChildViewPager.setCurrentItem(getArguments().getInt("H_num")-1);
                            }else {
                                lock_pager.ChildViewPager.setCurrentItem(0);
                            }

                            Toast.makeText(getContext(),"pos"+getArguments().getInt("H_num"),Toast.LENGTH_SHORT).show();

                            //lock_pager.userCustomAdapter.updatePageCount(lock_pager.ChildViewPager);
                            //lock_pager.PageFragment.newInstance(0);
                            //lock_pager.userCustomAdapter.notifyDataSetChanged();

                            getActivity().finish();
                            Intent intent = new Intent(getContext(), lock_pager.class);
                            startActivity(intent);
                        }
                        // ??????????????? ??????
                        dialog.dismiss();
                        lock_pager.userCustomAdapter.notifyDataSetChanged();

                    }
                });
        // ??????????????? ??????
        AlertDialog alertDialog = alertDialogBuilder.create();
        //alertDialog.getWindow().getAttributes().windowAnimations = R.style.;
//        alertDialog.getListView().setLayoutAnimation(controller);

        Animation anim  = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,1.0f , Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF,0.0f , Animation.RELATIVE_TO_SELF, 0.0f
        );
        anim.setDuration(1000);

        // ??????????????? ????????????
        alertDialog.show();
    }
    // ?????? ?????? ??????
    public static String getPath(final Context context, final Uri uri) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                    && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId;
                    docId = DocumentsContract.getDocumentId(uri);

                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);

                }
            }
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;


    }
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    // ????????? ??????
    private String getName(Uri uri)
    {
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // uri ????????? ??????
    private String getUriId(Uri uri)
    {
        String[] projection = { MediaStore.Images.ImageColumns._ID };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}