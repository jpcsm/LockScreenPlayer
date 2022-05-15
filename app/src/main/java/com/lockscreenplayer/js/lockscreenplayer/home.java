package com.lockscreenplayer.js.lockscreenplayer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener{

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    FirebaseUser mFirebaseUser;
    String mUsername;
    String mPhotoUrl;
    GoogleApiClient mGoogleApiClient;
    private Menu m_Menu = null;
    static Boolean bLog =false;
    static final String TAG = SignInActivity.class.getName();
    static final int RC_GOOGLE_SIGN_IN = 9001;
    int PICK_FROM_ALBUM = 1009;

    private static final int        MENU_OPEN_WEB_BROWSER   = Menu.FIRST + 1;
    private static final int        MENU_ADD_QUEUE          = MENU_OPEN_WEB_BROWSER + 1;
    private static final int        MENU_PLAY_QUEUE         = MENU_ADD_QUEUE + 1;
    private static final int        MENU_LIST               = MENU_PLAY_QUEUE + 1;
    private static final int        MENU_CONNECT_INFO       = MENU_LIST + 1;
    private static final int        MENU_DETAILS            = MENU_CONNECT_INFO + 1;

    boolean first;
    int point, i;
    NavigationView navigationView;
    TextView tv_name ;
    TextView tv_email;
    ImageView iv_photo;
    TextView tv_point;
    View headerview;
    LinearLayout login_header_layout;
    String json;
    private Uri mImageCaptureUri;
    FirebaseUser user;
    int getpoint;
    ViewPager tab_viewPager;
    TabPagerAdapter tabPagerAdapter;
    static ProgressDialog mProgressDialog;

    static Thread google;
    Thread loginpoint ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        GpsInfo gps = new GpsInfo(this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

          /*  Toast.makeText(
        getApplicationContext(),
                "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                Toast.LENGTH_LONG).show();*/
    } else {
        // GPS 를 사용할수 없으므로
        gps.showSettingsAlert();
    }

        //툴바설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //파이어베이스 FCM 주제를 등록하고 토큰을 받아 온다.
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();
//      위 샘플은 news라는 토픽에 구독등록을 한다는 의미인것 같다.
//      그래서 꼭 news를 해야 하는 것은 아니다.
//      나중에 메세지를 보낼때 news를 선택해서 보내면 해당 기기에만 선별해서 메세지를 보내게 된다


        // 권한체크
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS) == PackageManager.PERMISSION_GRANTED){
//            //Manifest.permission.MANAGE_DOCUMENTS 접근 승낙 상태 일때
//            Log.d("권한 홈","MANAGE_DOCUMENTS 권한 접근 가능");
//        }else{
//            //Manifest.permission.MANAGE_DOCUMENTS 접근 거절 상태 일때
//            Log.d("권한 홈","MANAGE_DOCUMENTS 권한 접근 불가");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1);
//                requestPermissions(new String[]{Manifest.permission.MANAGE_DOCUMENTS},1);
//                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR},1);
//                requestPermissions(new String[]{Manifest.permission.BODY_SENSORS},1);
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                requestPermissions(new String[]{Manifest.permission.CAMERA},1);
//                Log.d("권한 VERSION_CODES.M","MANAGE_DOCUMENTS 권한 접근 불가");
//                Toast.makeText(getApplicationContext(),"MANAGE_DOCUMENTS 권한 접근 불가",Toast.LENGTH_SHORT).show();
//            }
//
//            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_DOCUMENTS},MANAGE_DOCUMENTS);
//        }



        //메시지버튼
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //네비게이션드로어
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerview = (View) navigationView.getHeaderView(0);
//        headerview = (View) navigationView.inflateHeaderView(R.layout.nav_header_lock_screen);
        tv_name = (TextView)headerview.findViewById(R.id.user_name);
        tv_email = (TextView)headerview.findViewById(R.id.user_email);
//        iv_photo = (ImageView) headerview.findViewById(R.id.user_photo);
        login_header_layout = (LinearLayout)headerview.findViewById(R.id.login_header);
        tv_point = (TextView)headerview.findViewById(R.id.user_point);

        //구글로그인 세팅
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        i = 0;
        first=true;



        //탭뷰페이저 생성
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        tab_viewPager = (ViewPager)findViewById(R.id.home_viewpager);
        tab_viewPager.setAdapter(tabPagerAdapter);


        TabLayout mTab = (TabLayout)findViewById(R.id.tabs);
        mTab.setupWithViewPager(tab_viewPager);


        mProgressDialog = new ProgressDialog(home.this );
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("잠시만 기다려주세요...");

        //구글계정 상태리스너
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                mFirebaseUser = firebaseAuth.getCurrentUser();


                //중복호출문제 해결 1회만 호출(로그인,로그아웃시)
                if(first) {

                    //if(mFirebaseUser!=null)Toast.makeText(getApplicationContext(),mFirebaseUser.getEmail(),Toast.LENGTH_SHORT).show();

                    if (mFirebaseUser != null) { //로그인시


                        Log.d("구글계정상태변경 ", "로그인" +mFirebaseUser.getEmail());

                        google = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                    String result="";
                                    //서버에 사용자데이터가 없을 때
                                    try {
                                        //서버에 사용자 구글계정정보 전송
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.accumulate("user_E_mail", mFirebaseUser.getEmail());
                                        jsonObject.accumulate("user_photoURL", mFirebaseUser.getPhotoUrl());
                                        jsonObject.accumulate("user_name", mFirebaseUser.getDisplayName());
                                        json = jsonObject.toString();

                                        POST post = new POST();
                                        result = post.POST(Server.localhost+"/Gmail_user_insert.php", json);

                                        //로그인시 서버에 데이터가 있을경우 포인트를 가져와서 보여준다
                                        JSONObject jsonObject2 = new JSONObject(result);
                                        getpoint = jsonObject2.getInt("user_point");
                                        String s_getpoint = jsonObject2.getString("user_point");
//                            Toast.makeText(getApplicationContext(),getpoint+" / "+s_getpoint,Toast.LENGTH_SHORT).show();
                                        Log.d("포인트가져오기",getpoint+" / "+s_getpoint);

                                        //로그인정보 드로어에 반영
                                        handler.sendEmptyMessage(1);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                handler.sendEmptyMessage(1);
                                mProgressDialog.dismiss();
                            }
                        });
                        google.start();
                    }
                    else if(mFirebaseUser == null) { //로그아웃상태
                        Log.d("구글계정상태변경", "로그아웃");
                        handler.sendEmptyMessage(0);

                    }
                    //프래그먼트 새로고침
                    tabPagerAdapter.notifyDataSetChanged();
                    //한번만호출
                    first=false;


                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);


    }
    final int MANAGE_DOCUMENTS = 12432;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case MANAGE_DOCUMENTS:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            Log.d("","MANAGE_DOCUMENTS 권한 접근 허가");
                        } else {
                            Log.d("","MANAGE_DOCUMENTS 권한 접근 불가");
                        }
                    }
                }
                break;
            case 0: Log.d("","0");
                break;
            case 1:Log.d("","1");
                break;
            case -1:Log.d("","-1");
                break;

        }
    }

    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

        if(msg.what == 0){   // 비로그인상태
            tv_name.setText("로그인 상태가 아닙니다.");
            tv_point.setText("0");
            tv_email.setVisibility(View.GONE);
            //                    iv_photo.setVisibility(View.GONE);
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }else if(msg.what == 1){ //로그인상태
            login_header_layout.setVisibility(View.VISIBLE);
            tv_name.setText(mFirebaseUser.getDisplayName());
            tv_email.setText(mFirebaseUser.getEmail());
            //                    tv_name.setVisibility(View.VISIBLE);
            tv_email.setVisibility(View.VISIBLE);
            tv_point.setText(Comma_won(getpoint+""));
            Log.d("getpoint", getpoint+"");
        }else if(msg.what == 2){

            tabPagerAdapter.notifyDataSetChanged();
        }

    }
    };
    loginThread loginThread;

    @Override
    protected void onResume() {
        super.onResume();
        first=true;

        //프래그먼트새로고침
        tabPagerAdapter.notifyDataSetChanged();
        Log.d("프래그먼트새로고침","onResume");

        //tabPagerAdapter.notifyDataSetChanged();
        //Toast.makeText(getApplicationContext(),"first "+first,Toast.LENGTH_SHORT).show();
        if(mFirebaseAuth.getCurrentUser()!=null) {
            String result="";
            mProgressDialog.show();
            loginThread = new loginThread();
            loginThread.start();
        }
        //썸네일 추출
        if(mImageCaptureUri!=null) {

//            ImageView getimageView = (ImageView)findViewById(R.id.addimageView2);
//            getimageView.setImageURI(mImageCaptureUri);
        }

//
    }

    class loginThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (user != null) {

                String result="";



                //서버에 사용자데이터가 없을 때
                try {
                    //서버에 사용자 구글계정정보 전송
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("user_E_mail", user.getEmail());
//                    jsonObject.accumulate("user_photoURL", mFirebaseUser.getPhotoUrl());
//                    jsonObject.accumulate("user_name", mFirebaseUser.getDisplayName());
                    json = jsonObject.toString();

                    POST post = new POST();
                    result = post.POST(Server.localhost+"/Gmail_user_insert.php", json);

                    //로그인시 서버에 데이터가 있을경우 포인트를 가져와서 보여준다
                    JSONObject jsonObject2 = new JSONObject(result);
                    getpoint = jsonObject2.getInt("user_point");
                    String s_getpoint = jsonObject2.getString("user_point");
//                            Toast.makeText(getApplicationContext(),getpoint+" / "+s_getpoint,Toast.LENGTH_SHORT).show();
                    Log.d("포인트가져오기",getpoint+" / "+s_getpoint);

                    //로그인정보 드로어에 반영
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                first=false;

            } else if(user == null) {
                handler.sendEmptyMessage(0);
            }
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.setting_button, menu);
        m_Menu=null;
        menu.add("설정");
        menu.add("로그인");
        menu.add("로그아웃");
        m_Menu=menu;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if ( mFirebaseUser == null ) {
            //Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            m_Menu.getItem(2).setVisible(false);
            m_Menu.getItem(1).setVisible(true);
        }
        else {

            mUsername = mFirebaseUser.getDisplayName();
            if ( mFirebaseUser.getPhotoUrl() != null ) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }

//            TextView usernameTextView = (TextView) findViewById(R.id.username_textview);
//            usernameTextView.setText(mUsername);

            m_Menu.getItem(1).setVisible(false);
            m_Menu.getItem(2).setVisible(true);
            //Toast.makeText(this, mUsername + "님 환영합니다.", Toast.LENGTH_SHORT).show();

            // ImageView photoImageView = (ImageView) findViewById(R.id.photo_imageview);
        }


        return super.onCreateOptionsMenu(m_Menu);
    }

    @Override
    //TODO 우측상단 버튼
    public boolean onOptionsItemSelected(MenuItem item) {
        // 옵션버튼 클릭리스너
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(getApplicationContext(), settings.class);
//            startActivity(intent); //로그인 요청코드 1004
//            return true;
//        }
//        if (id == R.id.action_login) {
//            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
//            bLog=true;
//            return true;
//        }
//        if (id == R.id.action_logout) {
//            mFirebaseAuth.signOut();
//            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//            bLog=false;
//            return true;
//        }

        if (item.getTitle()=="설정") {
            Intent intent = new Intent(getApplicationContext(), settings.class);
            startActivity(intent); //로그인 요청코드 1004
            return true;
        }
        if (item.getTitle()=="로그인") {
            mProgressDialog.show();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            return true;
        }
        if (item.getTitle()=="로그아웃") {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            Toast.makeText(getApplicationContext(),"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
            m_Menu.getItem(2).setVisible(false);//로그아웃 안보이기
            m_Menu.getItem(1).setVisible(true);//로그인 보이기
            first=true;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("test", "onPrepareOptionsMenu - 옵션메뉴가 " +
                "화면에 보여질때 마다 호출됨");



        return super.onPrepareOptionsMenu(menu);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //네비게이션 드로어 클릭리스너
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      /*  if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) { //이미지가져오기
            // 앨범 호출
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_FROM_ALBUM);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(getApplicationContext(), youtubeActivity.class);
            startActivity(intent);
        } else */if (id == R.id.nav_manage) {
            Intent intent = new Intent(getApplicationContext(), settings.class);
            startActivity(intent);
        }/* else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == RC_GOOGLE_SIGN_IN ) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if ( result.isSuccess() ) {
                String token = result.getSignInAccount().getIdToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
                mFirebaseAuth.signInWithCredential(credential);

                m_Menu.getItem(1).setVisible(false);//로그인 안보이기
                m_Menu.getItem(2).setVisible(true);//로그아웃 보이기
            }
            else {
                Log.d(TAG, "Google Login Failed." + result.getStatus());
                Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
            }
        }

        //앨범에서 이미지선택
        if ( requestCode == PICK_FROM_ALBUM ) {
            if (data != null) {
                mImageCaptureUri = data.getData();
                Log.d("SmartWheel", mImageCaptureUri.getPath().toString());
            }
        }

    }

    //구매하기 탭 - 상품선택
    public void Use_point_item(View v){
               int id = v.getId();
        switch (id){
            case R.id.Convenience_Store :

                if(mFirebaseUser!=null){

                    Intent intent = new Intent(getApplicationContext(),GoodsActivity.class);
                    intent.putExtra("category","Convenience_Store");
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"로그인 후 이용가능합니다",Toast.LENGTH_SHORT).show();
                }

                break;
            //case R.id.Donation :

            case R.id.Bakery :
                if(mFirebaseUser!=null){

                    Intent intent = new Intent(getApplicationContext(),GoodsActivity.class);
                    intent.putExtra("category","Bakery");
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"로그인 후 이용가능합니다",Toast.LENGTH_SHORT).show();
                } break;
            //case R.id.Beauty :
            //Toast.makeText(getApplicationContext(),id+"",Toast.LENGTH_SHORT).show();break;
            case R.id.Eat_Out :
                if(mFirebaseUser!=null){

                    Intent intent = new Intent(getApplicationContext(),GoodsActivity.class);
                    intent.putExtra("category","Eat_Out");
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"로그인 후 이용가능합니다",Toast.LENGTH_SHORT).show();
                }break;
            case R.id.Gift :
                if(mFirebaseUser!=null){

                    Intent intent = new Intent(getApplicationContext(),GoodsActivity.class);
                    intent.putExtra("category","Gift");
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"로그인 후 이용가능합니다",Toast.LENGTH_SHORT).show();
                } break;
            case R.id.Cafe :
                if(mFirebaseUser!=null){

                    Intent intent = new Intent(getApplicationContext(),GoodsActivity.class);
                    intent.putExtra("category","Cafe");
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"로그인 후 이용가능합니다",Toast.LENGTH_SHORT).show();
                }break;
            case R.id.Culture_Performence :
                if(mFirebaseUser!=null){

                    Intent intent = new Intent(getApplicationContext(),GoodsActivity.class);
                    intent.putExtra("category","Culture_Performence");
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"로그인 후 이용가능합니다",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(getApplicationContext(),"default",Toast.LENGTH_SHORT).show();break;

        }

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        //tabPagerAdapter.notifyDataSetChanged();
    }

    public static String Comma_won(String junsu) {
        int inValues = Integer.parseInt(junsu);
        DecimalFormat Commas = new DecimalFormat("#,###");
        String result_int = (String)Commas.format(inValues);
        return result_int;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


}
