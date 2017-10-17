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
    public Object instantiateItem(ViewGroup container, int position) { // 프래그먼트가 생성되면 리스트에 담는다
        UserCustomFragment fragment = (UserCustomFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) { //프래그먼트가 소멸되면 리스트에서 버린다
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) { //필요할때마다 리스트에서 꺼낸다
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

    public void setUserVisibleHint(boolean isVisibleToUser) {//현재화면에 보이는 프래그먼트
        if (isVisibleToUser) {
            // 보인다.
            if(videoView!=null) {
                //Toast.makeText(getContext(),"화면에보이는프래그먼트"+getArguments().getInt("H_num"),Toast.LENGTH_SHORT).show();
                //videoView.seekTo(VideoPlayTime);
                //videoView.setVisibility(View.VISIBLE);
                videoView.start();
                //VideoParentLayout.setVisibility(View.GONE);
                mediaController.hide();
                mediaController = new MediaController(getContext());
                //컨트롤러 해당 동영상뷰의 부모뷰에 위치시키기
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
            }
            Log.d("락스크린 setUserVisibleHint 현재보이는페이지",getArguments().getInt("H_num")+"\nisVisibleToUser :"
                      +isVisibleToUser);

            //Toast.makeText(getContext(),"햔재보이는페이지"+getArguments().getInt("H_num"),Toast.LENGTH_SHORT).show();
        }else {
            // 안보인다.

            if(videoView!=null) {
                videoView.setMediaController(null);
                videoView.pause();
                mediaController.hide();
            }
            Log.d("락스크린 setUserVisibleHint 안보임",getArguments().getInt("H_num")+"\nisVisibleToUser :"
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
////                if (currentPosition != 0) {//일시중지 후 다시재생
////                    videoView.seekTo(currentPosition);
////                    videoView.start();
////                } else {
////                    videoView.start();
////                }
//
////                videoView.pause();//동영상중지
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
        Log.d("락스크린 StopToVideo","pos "+getArguments().getInt("H_num")+"\n");
        videoView.pause();
        mediaController.hide();
    }
    public void StartToVideo(){
        Log.d("락스크린 StartToVideo","pos "+getArguments().getInt("H_num")+"\n");
        videoView.start();
        //mediaController.hide();
    }
    @Override
    public void onResume() {
        super.onResume();
        //if(videoView!=null)videoView.start();
        //Toast.makeText(getContext(),"onResume",Toast.LENGTH_SHORT).show();
        //배경이미지 setImageURI
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
            Log.d("락스크린  유저커스텀잠금화면 onresume progress",sp.getString("bright"+getArguments().getInt("H_num"),null));
            int progress = Integer.parseInt(sp.getString("bright"+getArguments().getInt("H_num"),null));
            bg_alpha = 1.0f-progress/100f;
            black.setAlpha(bg_alpha);
            Log.d("락스크린 유저커스텀잠금화면 onresume 밝기",bg_alpha+"");
        }
        //앨범에서 가져온이미지 처리
        if(mImageCaptureUri!=null) {
            tv_empty.setVisibility(View.GONE);
            //배경이미지 블러
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
            //배경이미지 블러
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

        //동영상
        //if(mediaController!=null) mediaController.setVisibility(View.GONE);
        if(sp.getString("VideoURI"+getArguments().getInt("H_num"),null)!=null) {//저장한 동영상이 있을경우

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

            //동영상썸네일
//            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath,
//                    MediaStore.Images.Thumbnails.MINI_KIND);
//            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
//            videoView.setBackgroundDrawable(bitmapDrawable);

            //동영상 재생시간 가져오기
            Log.d("VideoPlayTime",sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0)+"\n\n\n\n\n\n");
            VideoPlayTime = sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0);
            videoView.seekTo(VideoPlayTime);
            Log.d("락스크린 유저커스텀잠금화면 onResume 동영상경로존재",videoPath+"\n플레이시간 : "+VideoPlayTime);

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
    public void onPause() {//Fragment가 사용자의 Action과 상호 작용을 중지한다.
        super.onPause();
        Log.d("락스크린  onPause ","가로커스텀화면"+getArguments().getInt("H_num"));
        //mediaController.setVisibility(mediaController.GONE);
        videoView.pause();//화면꺼져있는 상태에서 동영상재생x
        sp = getContext().getSharedPreferences("LockScreenBackgroundImage",getContext().MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        //비디오 재생시간 저장
        if(videoView.getCurrentPosition()!=0){
            ed.putInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),videoView.getCurrentPosition());
            ed.commit();
        }
        //Toast.makeText(getContext(),"가로커스텀화면 onpause"+getArguments().getInt("H_num"),Toast.LENGTH_SHORT).show();
        Log.d("락스크린 onPause isActivated","\n"+
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
//                Log.d("락스크린 onTouch",mediaController.getVisibility()+"");
//                return false;
//            }
//        });

        if(sp.getString("VideoURI"+getArguments().getInt("H_num"),null)!=null){
            Log.d("락스크린 onCreateView VideoURI",sp.getString("VideoURI"+getArguments().getInt("H_num"),null));
            videoView.setVisibility(View.VISIBLE);
            VideoURI =  Uri.parse(sp.getString("VideoURI"+getArguments().getInt("H_num"),null));
            videoPath = (sp.getString("VideoURI"+getArguments().getInt("H_num"),null));
            videoView.setVideoPath(videoPath);
        }else{
            //저장된 동영상경로가 없을경우 비디오뷰를 없앤다
            videoView.setVisibility(View.GONE);
            Log.d("락스크린 nCreateView - videoView","null");
        }
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) { //비디오뷰 준비완료
                Log.d("락스크린 onPrepared","동영상준비 :"+VideoPlayTime);
//                videoView.seekTo(VideoPlayTime);
//                videoView.start();
                //videoView.pause();//동영상중지
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() { //비디오뷰 터치이벤트
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("락스크린 유저커스텀잠금화면 비디오뷰 터치",btn_fullscreen.getVisibility()+"\nmotionEvent : "+motionEvent);
                //videoView.setMediaController(mediaController);

                if(btn_fullscreen.getVisibility()!=View.VISIBLE){
                    mediaController.setVisibility(View.VISIBLE);
                    btn_fullscreen.setVisibility(View.VISIBLE); //전체화면 버튼보이기
//                mediaController.show(); //컨트롤러보이기
//

                    td = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(3000); // 3초뒤에 버튼 사라짐

                            handler.sendEmptyMessage(0);//전체화면, 컨트롤러 보이지 않기
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

        //배경 투명도
//        black = (ImageView)rootView.findViewById(R.id.Black);
//        Drawable alpha1 = black.getBackground();
//        alpha1.setAlpha(1-bg_alpha/100);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//유저커스텀 잠금화면 좌측 상단 옵션버튼 이벤트
                Btn_usercustom_option(getArguments().getInt("H_num"));
            }
        });

        SharedPreferences.Editor ed = sp.edit();

        //배경이미지 setImageURI
//        String imageUriString = sp.getString("pos"+getArguments().getInt("H_num"),null);
//        if(imageUriString!=null){
//            Uri imageUri = Uri.parse(imageUriString);
//            getimageView.setImageURI(imageUri);
//
//            tv_empty.setVisibility(View.GONE);
//        }
        //Toast.makeText(getContext(),"onCreateView : "+imageUriString, Toast.LENGTH_SHORT).show();
        //lock_pager.userCustomAdapter.notifyDataSetChanged();

        btn_fullscreen.setOnClickListener(new View.OnClickListener() { //비디오뷰 전체화면 클릭이벤트
            @Override
            public void onClick(View view) {
                //비디오뷰 전체화면으로 재생하기
                Intent i = new Intent(getContext(), VideoViewFullscreenActivity.class);
                i.putExtra("videoPath",videoPath);//비디오 경로
                i.putExtra("VideoPlayTime",videoView.getCurrentPosition());//비디오 플레이시간
                i.putExtra("position",getArguments().getInt("H_num"));//현재포지션 전달
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
            Log.d("락스크린 유저커스텀잠금화면 핸들러 msg.what", msg.what+"");
            if(msg.what == 0){   //비디오뷰 터치이벤트 스레드
                btn_fullscreen.setVisibility(View.GONE);//버튼 사라짐
               // mediaController. hide();//컨트롤러숨기기
                Log.d("락스크린 유저커스텀잠금화면 비디오뷰 터치 스레드끝",btn_fullscreen.getVisibility()+"");
            }else if(msg.what == 1){ //로그인상태

            }else if(msg.what == 2){
            }

        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //앨범에서 이미지선택

        if ( requestCode == PICK_FROM_ALBUM ) {
            if (data != null) {
                SharedPreferences sp = getContext().getSharedPreferences("LockScreenBackgroundImage",getContext().MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();


                //해당이미지uri 쉐어드에 저장
                Toast.makeText(getContext(),"getposition : "+pos, Toast.LENGTH_SHORT).show();
                mImageCaptureUri = data.getData();
                ed.putString("pos"+ getArguments().getInt("H_num"),""+mImageCaptureUri.toString());
                ed.commit();

                Log.d("getPathtoString", mImageCaptureUri.getPath().toString());
                Log.d("toString", mImageCaptureUri.toString());

            }
        }else if (requestCode == SELECT_MOVIE){//동영상선택
            if(data!=null){
                VideoURI = data.getData();
                videoPath = getPath(getContext(),VideoURI);
                ed.putString("VideoURI"+getArguments().getInt("H_num"),videoPath);
                ed.remove("videoView.getCurrentPosition"+getArguments().getInt("H_num"));//동영상 새로 가져올 때 미디어컨트롤러 재생시간 초기화
                ed.commit();
                String name = getName(VideoURI);
                String uriId = getUriId(VideoURI);
                videoView.setVideoURI(VideoURI);
                videoView.setVisibility(View.VISIBLE);
                Log.d("락스크린 onActivityResult SELECT_MOVIE", "pos : "+getArguments().getInt("H_num")+"\n실제경로 : " + videoPath + "\n파일명 : " + name + "\nuri : " + VideoURI.toString() + "\nuri id : " + uriId);
                videoView.start();
            }else{ //동영상 가져오기 실패
                Log.d("락스크린 onActivityResult SELECT_MOVIE", "동영상 가져오기 실패");

            }
        }else if (requestCode == FULLSCREEN_TO_VIDEO) {//동영상 전체화면에서 잠금화면으로 복귀

            if(data!=null){
                videoView.seekTo(data.getIntExtra("VideoPlayTime",0));
                Log.d("락스크린 전체화면에서 잠금화면으로 복귀 VideoPlayTime",data.getIntExtra("VideoPlayTime",0)+"");
            }
            //videoView.seekTo(sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0)); //재생시간 가져오기
            videoView.start();
        }

        Log.d("락스크린 유저커스텀잠금화면 requestCode",requestCode+"\nVideoPlayTime : "+sp.getInt("videoView.getCurrentPosition"+getArguments().getInt("H_num"),0));
        if(mediaController!=null)mediaController.setVisibility(View.GONE);
        if(btn_fullscreen!=null)btn_fullscreen.setVisibility(View.GONE); //전체화면 버튼보이지않기
    }

    // 동영상선택
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
            Log.d("락스크린 bright.settext getArguments",sp.getString("bright"+getArguments().getInt("H_num"),null));
            bright.setText(sp.getString("bright"+getArguments().getInt("H_num"),null));
            seekbrightness.setProgress(Integer.parseInt(sp.getString("bright"+getArguments().getInt("H_num"),null)));
        }else {
            Float al = black.getAlpha();
            Log.d("락스크린 bright.settext (int) (al*100-1)","");
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
                //Log.d("밝기", progress+"");
                bright.setText(""+progress);
                //Log.d("bg_alpha", bg_alpha+"");

                ed.putString("bright"+getArguments().getInt("H_num"),progress+"");
                ed.commit();
                bg_alpha = 1.0f-progress/100f;
                black.setAlpha(bg_alpha);
                Log.d("전경색 알파", black.getAlpha()+"," + bg_alpha);
            }
        });
    }

    @Override
    public void onStart() { // Fragment가 화면에 표시될때 호출된다. 사용자의 Action과 상호 작용 할 수 없다.
        super.onStart();
        Log.d("락스크린  onStart ","가로커스텀화면"+getArguments().getInt("H_num"));
    }

    @Override
    public void onStop() { //Fragment가 화면에서 더이상 보여지지 않게 되며, Fragment기능이 중지 되었을때 호출 된다.
        super.onStop();
        Log.d("락스크린  onStop ","가로커스텀화면"+getArguments().getInt("H_num"));

    }

    public void Btn_usercustom_option(final int position) {
        //잠금화면 옵션메뉴
        sp = getContext().getSharedPreferences("LockScreenBackgroundImage",getContext().MODE_PRIVATE);
        ed = sp.edit();

        final CharSequence[] items;
        if(sp.getInt("Page_Num",1)>1) {
            items = new CharSequence[]{"배경이미지 설정하기","동영상 가져오기",  "페이지 추가하기","배경밝기", "현재페이지 삭제하기"};

        }else{
            items = new CharSequence[]{ "배경이미지 설정하기","동영상 가져오기",  "페이지 추가하기","배경밝기"};

        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // 제목셋팅
        //alertDialogBuilder.setTitle("편집하기");
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {


                        //Toast.makeText(getContext(),"position : "+getArguments().getInt("H_num"), Toast.LENGTH_SHORT).show();

                        if(id==0) {
                            //이미지 가져오기
                            final CharSequence[] GetImageitems;
                            GetImageitems = new CharSequence[]{ "갤러리","사진필터"};
                            AlertDialog.Builder GetImagealertDialogBuilder = new AlertDialog.Builder(getContext());
                            GetImagealertDialogBuilder.setItems(GetImageitems, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i){
                                        case 0:
                                            // 앨범 호출
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
                            //동영상가져오기
                            doSelectMovie();


                        }else if(id==2) {
                            //페이지 추가하기

                            UserCustomAdapter.Page_Num++;

                            ed.putInt("Page_Num",UserCustomAdapter.Page_Num);
                            ed.commit();
                            Toast.makeText(getContext(),"페이지 추가"+UserCustomAdapter.Page_Num,Toast.LENGTH_SHORT).show();

                        }else if(id==3) {
                           //화면밝기
                            View innerView = getLayoutInflater(getArguments()).inflate(R.layout.seek_bar, null);
                            AlertDialog.Builder adialog = new AlertDialog.Builder(
                                    getContext());
                            adialog.setView(innerView);
                            seekbrightness = (SeekBar) innerView.findViewById(R.id.seekbrightness);
                            bright = (TextView) innerView.findViewById(R.id.bright);

                            setSeekbar();

                            AlertDialog alert = adialog.create();
                            //alert.setTitle("밝기");
                            alert.show();

                        }else if(id==4) {
                            //페이지 삭제하기
//                            Toast.makeText(getContext(),"i"+position+"\n"+adapter.menu_itemlist.get(position).getName()+"(이)가 삭제되었습니다",Toast.LENGTH_SHORT).show();
                            UserCustomAdapter.Page_Num--;
                            ed.putInt("Page_Num",UserCustomAdapter.Page_Num);

                            //포지션반영
                            for(int j=getArguments().getInt("H_num");j<=UserCustomAdapter.Page_Num;++j) {
                                //이미지uri pos 1씩
                                ed.remove("pos"+j);
                                ed.commit();
                                String s1 = "";
                                s1 = sp.getString("pos"+(j+1),null);
                                ed.putString("pos"+j,s1);
                                ed.putString("pos"+(j+1),null);

                                //동영상 VideoURI 1씩
                                ed.remove("VideoURI"+j);
                                ed.commit();
                                String s2 = "";
                                s2 = sp.getString("VideoURI"+(j+1),null);
                                ed.putString("VideoURI"+j,s2);
                                ed.putString("VideoURI"+(j+1),null);
                                //Toast.makeText(getContext(),"pos"+getArguments().getInt("H_num"),Toast.LENGTH_SHORT).show();

                                // 배경 밝기 알파값 1씩
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
                        // 다이얼로그 종료
                        dialog.dismiss();
                        lock_pager.userCustomAdapter.notifyDataSetChanged();

                    }
                });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        //alertDialog.getWindow().getAttributes().windowAnimations = R.style.;
//        alertDialog.getListView().setLayoutAnimation(controller);

        Animation anim  = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,1.0f , Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF,0.0f , Animation.RELATIVE_TO_SELF, 0.0f
        );
        anim.setDuration(1000);

        // 다이얼로그 보여주기
        alertDialog.show();
    }
    // 실제 경로 찾기
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

    // 파일명 찾기
    private String getName(Uri uri)
    {
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // uri 아이디 찾기
    private String getUriId(Uri uri)
    {
        String[] projection = { MediaStore.Images.ImageColumns._ID };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}