package com.lockscreenplayer.js.lockscreenplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.directionalviewpager.DirectionalViewPager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class lock_pager extends AppCompatActivity {

    private ViewPager mViewPager;
    public static ViewPager ChildViewPager;
    private VerticalViewPager vViewPager;
    //항상 보이게 할 뷰. 멤버필드로 선언
    private DirectionalViewPager mDirectionalViewPager;
    static String imgUrl = "http://172.30.1.19/img/";
    static Bitmap bmImg;
    //static back task;
    //static Bitmap[] arrbit;
    static int[] intbit;
    PagerAdapter pagerAdapter;
    static byte[] bytearray;
    static byte[][] bytebit;
    static int b;
    private static byte[] image;
    private static Bitmap bitmapImage;
    static BitmapFactory.Options options;
    static int pos;
    static int H_pos;
    static int mCurrentPosition;
    TextView HHmm;
    TextView MMDD;
    TextView AM_PM;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    GoogleApiClient mGoogleApiClient;
    FirebaseUser mFirebaseUser;
    String mUsername;
    String mPhotoUrl;
    static UserCustomAdapter userCustomAdapter;
    static int Hcurrentposition;

    ImageButton btn1;
    ImageButton btn2;
    ImageButton btn3;
    ImageButton btn4;
    UserCustomFragment ucf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_pager);

        getWindow().addFlags(
                // 기본 잠금화면보다 우선출력
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                // 기본 잠금화면 해제시키기
                //| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //세로페이저 수정
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        //mViewPager = (ViewPager) findViewById(R.id.container);
        vViewPager = (VerticalViewPager)findViewById(R.id.container);
        vViewPager.setAdapter(pagerAdapter);

        btn1 = (ImageButton)findViewById(R.id.btn_youtube);
        btn2 = (ImageButton)findViewById(R.id.btn2);
        btn3 = (ImageButton)findViewById(R.id.btn3);
        //btn4 = (FloatingActionButton)findViewById(R.id.btn4);


        Hcurrentposition=0; //유저커스텀뷰페이지 초기 포지션값 0


        vViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               Log.d("onPageScrolled", position+" / "+positionOffset+" / "+positionOffsetPixels);
                //액티비티에서 프래그먼트 제어
                ucf = doSomethingWithCurrentFragment();
                if(ucf.mediaController!=null) ucf.mediaController.hide(); //세로페이지 이동시 미디어컨트롤러(유저커스텀뷰페이저의 비디오뷰) 보이지 않기
                if(ucf.btn_fullscreen!=null) ucf.btn_fullscreen.setVisibility(View.GONE);
                if(ucf.td!=null) ucf.td.interrupt();
                //if(ucf.videoView!=null)ucf.videoView.pause(); //세로페이지 이동시 비디오 중지
            }

            @Override
            public void onPageSelected(int position) {
                RelativeLayout Time = (RelativeLayout)findViewById(R.id.TimeLayout);
                Log.d("onPageSelected", position+"");
                mCurrentPosition = position;


                //Toast.makeText(getApplicationContext(),""+position+"가로 "+H_pos,Toast.LENGTH_SHORT).show();
                switch(position) {
                    case 0:
                        if(ucf.videoView!=null)ucf.videoView.start(); //비디오뷰 재생
                        Time.setVisibility(View.VISIBLE);
                        btn1.setImageResource(R.drawable.lock1_click);
                        btn2.setImageResource(R.drawable.lock2_nonclick);
                        btn3.setImageResource(R.drawable.lock3_nonclick);
                        break;
                    case 1:
                        if(ucf!=null)ucf.StopToVideo();
                        Time.setVisibility(View.VISIBLE);
                        btn1.setImageResource(R.drawable.lock1_nonclick);
                        btn2.setImageResource(R.drawable.lock2_click);
                        btn3.setImageResource(R.drawable.lock3_nonclick);
                        break;
                    case 2:
                        if(ucf!=null)ucf.StopToVideo();
                        Time.setVisibility(View.GONE);
                        btn1.setImageResource(R.drawable.lock1_nonclick);
                        btn2.setImageResource(R.drawable.lock2_nonclick);
                        btn3.setImageResource(R.drawable.lock3_click);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("", "onPageScrollStateChanged"+state+"");

            }
        });





        b =0;
        //가로세로 페이저
//        DirectionalViewPager pager = (DirectionalViewPager)findViewById(R.id.pager);
//        pager.setOrientation(DirectionalViewPager.HORIZONTAL);
//        pager.setAdapter(pagerAdpater);
//        pager.setOrientation(DirectionalViewPager.VERTICAL);
//        pager.setAdapter(pagerAdpater);

        // Set up the ViewPager with the sections adapter.

        //메모리에서 사라지는 문제해결
        vViewPager.setOffscreenPageLimit(3);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        //잠금해제 버튼
        findViewById(R.id.btn_unlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

//        //서버 이미지 가져오기
//        task = new back();
//        //task.execute(imgUrl+"outer"+getArguments().getInt(ARG_SECTION_NUMBER)+"_1");
//        task.execute(imgUrl+"outer2_1");

//        imView = (ImageView) findViewById(R.id.imageView2);
//        imView.setImageBitmap(bmImg);





//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
//        {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
//            {
//
//            }
//
//            @Override
//            public void onPageSelected(int position)
//            {
//                Toast.makeText(getApplicationContext(),"onpage : "+position,Toast.LENGTH_SHORT).show();
//
//                //서버 이미지 가져오기
//                task = new back();
//                if(position==0) {
//                    task.execute(imgUrl+"outer3_1");
//                    FrameLayout frame = (FrameLayout)findViewById(R.id.lock_frag);
//                    Drawable d =new BitmapDrawable(bmImg);
//                    frame.setBackgroundDrawable(d);
//                }else if(position==1) {
//                    task.execute(imgUrl+"outer3_2");
//                    FrameLayout frame = (FrameLayout)findViewById(R.id.lock_frag);
//                    Drawable d =new BitmapDrawable(bmImg);
//                    frame.setBackgroundDrawable(d);
//                }else {
//                    task.execute(imgUrl+"outer3_3");
//                    FrameLayout frame = (FrameLayout)findViewById(R.id.lock_frag);
//                    Drawable d =new BitmapDrawable(bmImg);
//                    frame.setBackgroundDrawable(d);
//                }
//
//
//
//
//                //Toast.makeText(getApplicationContext(),"onpageSelected : "+position,Toast.LENGTH_LONG).show();
//                Log.d("onPageSelected",(position+1)+"");
//
//
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state)
//            {
//                //bytearray=bitmapToByteArray(bmImg);
//
//            }
//        });


        if ( mFirebaseUser != null ) {
            //구글로그인시
            //Toast.makeText(getApplicationContext(),mFirebaseUser.getEmail(),Toast.LENGTH_SHORT).show();
        }
        else {
            //비로그인시


        }

        TimeSetTextView();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS) == PackageManager.PERMISSION_GRANTED){
            //Manifest.permission.MANAGE_DOCUMENTS 접근 승낙 상태 일때
            Log.d("잠금화면","MANAGE_DOCUMENTS 접근 가능");
        }else{
            //Manifest.permission.MANAGE_DOCUMENTS 접근 거절 상태 일때
            Log.d("잠금화면","MANAGE_DOCUMENTS 접근 불가");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.MANAGE_DOCUMENTS},0);
            }
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.MANAGE_DOCUMENTS},0);

        }

    }
    public void TimeSetTextView() {

        //잠금화면에 시간날짜요일 표시하기
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        //SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat HHmm_Format = new SimpleDateFormat("hh:mm");
        SimpleDateFormat MMDD_Format =  new SimpleDateFormat("MM월dd일");
        // nowDate 변수에 값을 저장한다.
        String formatDate = MMDD_Format.format(date);
        String formatDate2 = HHmm_Format.format(date);
        MMDD=(TextView)findViewById(R.id.MMDD);
        HHmm=(TextView)findViewById(R.id.HHmm);
        AM_PM = (TextView)findViewById(R.id.AM_PM);
        MMDD.setText(formatDate+String.valueOf(doDayOfWeek()));
        HHmm.setText(formatDate2);

        Calendar cal = Calendar.getInstance();
        int isAMorPM = cal.get(Calendar.AM_PM);
        switch (isAMorPM) {
            case Calendar.AM:
                AM_PM.setText("오전");
                break;
            case Calendar.PM:
                AM_PM.setText("오후");
                break;
        }

    }
    private UserCustomFragment doSomethingWithCurrentFragment() {
        UserCustomFragment fragment = (UserCustomFragment) userCustomAdapter.getRegisteredFragment(Hcurrentposition);
        if (fragment == null) {
            return null ;
        }

        // do something with current fragment..
        return fragment ;
    }
    BroadcastReceiver mBR;
    @Override
    public void onStart()
    {
        super.onStart();
        mBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent)
            {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {

                    //매분마다 시간을 업데이트한다
                    TimeSetTextView();
                }
            }
        };

        registerReceiver(mBR, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mBR!= null)
            unregisterReceiver(mBR);
    }


    public void btn_front(View v){

    }
    private String doDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        String strWeek = null;

        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (nWeek == 1) {
            strWeek = "(일)";
        } else if (nWeek == 2) {
            strWeek = "(월)";
        } else if (nWeek == 3) {
            strWeek = "(화)";
        } else if (nWeek == 4) {
            strWeek = "(수)";
        } else if (nWeek == 5) {
            strWeek = "(목)";
        } else if (nWeek == 6) {
            strWeek = "(금)";
        } else if (nWeek == 7) {
            strWeek = "(토)";
        }

        return strWeek;
    }
    //잠그화면 좌측 플로팅 버튼 onClick
    public void btn_youtube(View v) {

//        startActivity(intent);
        vViewPager.setCurrentItem(0);
    }
    public void btn2(View v) {
        vViewPager.setCurrentItem(1);
    }
    public void btn3(View v) {
        vViewPager.setCurrentItem(2);
    }
    public void btn4(View v) {
        vViewPager.setCurrentItem(0);
    }

    public static byte[] bitmapToByteArray(Bitmap $bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    public void btn_ad(View v) {

    }
    @Override
    protected void onResume(){
        overridePendingTransition(0,0);

        //시간업데이트
        TimeSetTextView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {overridePendingTransition(0,0);

        super.onDestroy();
    }


    //inputstream을 bytearray로 변환
//    public static byte[] inputStreamToByteArray(InputStream is) {
//
//        byte[] resBytes = null;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//        byte[] buffer = new byte[1024];
//        int read = -1;
//        try {
//            while ( (read = is.read(buffer)) != -1 ) {
//                bos.write(buffer, 0, read);
//            }
//
//            resBytes = bos.toByteArray();
//            bos.close();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return resBytes;
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lock_pager, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * A placeholder fragment containing a simple view.
     */

    public class PagerAdapter extends FragmentStatePagerAdapter {

        Context context;
        Bitmap galImage;
        Drawable fdf;
//        private final int[] galImages = new int[] {
//                R.drawable.common_google_signin_btn_icon_light_focused,
//                R.drawable.common_ic_googleplayservices,
//                R.drawable.common_plus_signin_btn_icon_light_normal,
//                R.drawable.common_plus_signin_btn_icon_light_normal,
//                R.drawable.common_ic_googleplayservices,
//                R.drawable.common_google_signin_btn_icon_light_focused
//
//        };

        PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 3;//lock_pager.bytebit.length;
        }

        @Override
        public Fragment getItem(int position) {
            //return PageFragment.create(galImages[position]);
            //byte[] a = bytebit[position];

            return PageFragment.newInstance(position);
        }
    }

    public static class PageFragment extends Fragment {
        String url;
        UserCustomFragment ucf;
        int Hcurrentposition = 0;
        public static PageFragment newInstance(int sectionNumber) {
            PageFragment fragment = new PageFragment();
            Bundle argsn = new Bundle();

            argsn.putInt("num", sectionNumber);
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
            ;
        }
        private UserCustomFragment doSomethingWithCurrentFragment() {
            UserCustomFragment fragment = (UserCustomFragment) userCustomAdapter.getRegisteredFragment(Hcurrentposition);
            if (fragment == null) {
                return null ;
            }

            // do something with current fragment..
            return fragment ;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_page, container, false);
            pos = getArguments().getInt("num");

            options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            //bitmapImage = BitmapFactory.decodeResource(rootView.getResources(), image, options);
            //bitmapImage = BitmapFactory.decodeByteArray( image, 0, image.length ) ;

            //bitmapImage = BitmapFactory.decodeByteArray( bytearray, 0, bytearray.length ) ;
            //lock_pager.arrbit[0] = BitmapFactory.decodeResource(rootView.getResources(), image, options);

            //뷰페이저에 이미지 삽입
//            ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
//            Glide.with(this).load("http://172.30.1.34/img/outer"+(pos+1)+"_1").into(imageView);

            //세로뷰페이저 포지션테스트
            TextView textView2 = (TextView) rootView.findViewById(R.id.textView2);
            textView2.setText(pos+"");
            String strColor = "#00FFFF";
            textView2.setTextColor(Color.parseColor(strColor));

            //Toast.makeText(this.getContext(),""+pos,Toast.LENGTH_SHORT).show();
            //imageView.setImageBitmap(bmImg);


            //세로뷰페이저 프래그먼트 내부 가로뷰페이저 생성
            ChildViewPager = (ViewPager) rootView.findViewById(R.id.childcontainer);
//            ChildPagerAdapter mPagerAdapter = new ChildPagerAdapter( this.getChildFragmentManager());
//            viewPager.setAdapter(mPagerAdapter);


            if(pos==0){
                //사용자맞춤형 잠금화면
                //메모리에서 사라지는문제 해결
                //뷰페이저 미리 로딩한다
                //ChildViewPager.setCurrentItem(1);
                ChildViewPager.setOffscreenPageLimit(5);
                userCustomAdapter = new UserCustomAdapter( this.getChildFragmentManager(),getContext());
                ChildViewPager.setAdapter(userCustomAdapter);
                ChildViewPager.getCurrentItem();
                ChildViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        Log.d("락스크린", " onPageScrolled 유저커스텀페이지 가로스크롤"+position+"");
                        //액티비티에서 프래그먼트 제어
                        ucf = doSomethingWithCurrentFragment();
                        if(ucf.mediaController!=null) ucf.mediaController.hide(); //페이지 이동시 미디어컨트롤러(유저커스텀뷰페이저의 비디오뷰) 보이지 않기
                        if(ucf.btn_fullscreen!=null) ucf.btn_fullscreen.setVisibility(View.GONE);
                        if(ucf.td!=null) ucf.td.interrupt();
                        //if(ucf.videoView!=null)ucf.videoView.pause(); //세로페이지 이동시 비디오 중지
                        //Toast.makeText(getContext(),"onPag eScrolled",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPageSelected(int position) {
                        lock_pager.Hcurrentposition=position;
                        Hcurrentposition = position;
                        //Toast.makeText(getContext(),"onPageSelected : "+position,Toast.LENGTH_SHORT).show();
                        Log.d("onPageSelected", position+"");
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        Log.d("onPageScrolled", state+"");

                        //Toast.makeText(getContext(),"onPageScrollStateChanged : "+state,Toast.LENGTH_SHORT).show();
                    }
                });

//                ChildViewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener()
//                    {
//                        @Override
//                        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable android.support.v4.view.PagerAdapter oldAdapter, @Nullable android.support.v4.view.PagerAdapter newAdapter) {
//                            Toast.makeText(getContext(),"onAdapterChanged",Toast.LENGTH_SHORT).show();
//
//                            ChildViewPager.setCurrentItem(0);
//                        }
//                    }
//
//                );

            }else if(pos==1) {
                //광고페이지
//                ChildPagerAdapter mPagerAdapter = new ChildPagerAdapter( this.getChildFragmentManager());
//                ChildViewPager.setAdapter(mPagerAdapter);

            }else if(pos==2) {
                //뷰페이저 세번째 - 유튜브
//                url = "https://www.youtube.com/watch?v=dssYPZMZKhA";
//                YouTubePageFragment f = YouTubePageFragment.newInstance(pos);
//                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                transaction.add(R.id.youtube_frame, f).commit();

//                YouTubeAdater youTubeAdater = new YouTubeAdater(this.getChildFragmentManager());
//                ChildViewPager.setAdapter(youTubeAdater);
            }

            return rootView;
        }

    }

//    public void full_video() { //비디오뷰 전체화면 클릭이벤트
//
//    }
}

