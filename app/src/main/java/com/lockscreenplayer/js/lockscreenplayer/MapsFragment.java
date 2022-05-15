package com.lockscreenplayer.js.lockscreenplayer;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapTapi;

import java.util.ArrayList;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private static final String TAG = "googlemap";
    private LocationRequest mLocationRequest;
    private static final int REQUEST_CODE_GPS = 2001;

    private static final int DENIED = 100;

    private GoogleMap googleMap;
    private MapView mapView;
    LatLng SEOUL = new LatLng(37.56, 126.97);


    coupon_item item;
    home home;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;


    cGPlacesAPI places;
    LocationManager lm;
    Marker curMarker;
    GpsInfo gps;
    PolylineOptions polylineOptions;
//    protected synchronized void bulidGoogleApiClient() {
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
//                    .enableAutoManage(getActivity(), this)
//                    .addConnectionCallbacks(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//        createLocationRequest();
//    }

    public MapsFragment() {
        // Required empty public constructor
    }


    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    String result;
    POST post;
    String json;
    Location mCurrentLocation;
    boolean mLocationPermissonGranted = false;

//    private void getDeviceLocation() {
//        //퍼미션 체크
//        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mLocationPermissonGranted = true;
//        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this
//        );
//
//
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    static Thread couponThread;
    static ProgressDialog mProgressDialog;
    TextView tv_empty;
    SupportMapFragment fragment;



    ViewGroup rootView;

//    private void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000); //ms단위
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);// 가장 높은 정확도 배터리 소모 많음
//
//    }


    ArrayList<TMapPoint> arrayPoint;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mFirebaseAuth = mFirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

//        bulidGoogleApiClient();
//        mGoogleApiClient.connect();

        //ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.google_maps_fragment,container,false);

        // 뷰페이저 구글맵 중복문제 해결
//        ※ 프래그먼트를 전환하는 구성할때 프래그먼트를 구성하는 뷰(View)를 중복해서 에러가 발생 할수 있으므로
//        onDestroyView() 에서 프래그먼트가 화면에서 사라질때 프래그먼트의 뷰를 뷰에서 제거해주면 됨.
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null){
                parent.removeView(rootView);
            }

        }

        try {
            rootView = (ViewGroup) inflater.inflate(R.layout.google_maps_fragment, container, false);
        } catch (InflateException e) {

        }

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
            fragment.getMapAsync(this);
        }

        //검색버튼 클릭리스너
        SearchButtonClicked(rootView);

//

//        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//
//            @Override
//            public boolean onMyLocationButtonClick() {
//                mGoogleApiClient.reconnect();
//
//                return true;
//            }
//        });

        return rootView;
    }

    /** Map 클릭시 터치 이벤트 */
    public void onMapClick(LatLng point) {

        // 현재 위도와 경도에서 화면 포인트를 알려준다
        Point screenPt = googleMap.getProjection().toScreenLocation(point);

        // 현재 화면에 찍힌 포인트로 부터 위도와 경도를 알려준다.
        LatLng latLng = googleMap.getProjection().fromScreenLocation(screenPt);

        Log.d("맵좌표", "좌표: 위도(" + String.valueOf(point.latitude) + "), 경도("
                + String.valueOf(point.longitude) + ")");
        Log.d("화면좌표", "화면좌표: X(" + String.valueOf(screenPt.x) + "), Y("
                + String.valueOf(screenPt.y) + ")");
    }


     @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //권한체크
            return;
        }

        googleMap = map;

//        Marker seoul = googleMap.addMarker(new MarkerOptions().position(SEOUL)
//                .title("Seoul"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

//        // LocationManager 객체를 얻어온다
//        lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
//        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
//                100, // 통지사이의 최소 시간간격 (miliSecond)
//                1, // 통지사이의 최소 변경거리 (m)
//                (android.location.LocationListener) mLocationListener);
//        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
//                100, // 통지사이의 최소 시간간격 (miliSecond)
//                1, // 통지사이의 최소 변경거리 (m)
//                (android.location.LocationListener) mLocationListener);
        getContext();
        // T map 앱 연동
        TMapTapi tmaptapi = new TMapTapi(getContext());
        tmaptapi.setSKPMapAuthentication ("9ccf3adc-6564-3b8d-9462-833a25492a9c");
        tmaptapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
            @Override
            public void SKPMapApikeySucceed() {
               Log.d("","락스크린 SKPMapApikeySucceed");
            }

            @Override
            public void SKPMapApikeyFailed(String s) {
                Log.d("","락스크린 SKPMapApikeyFailed"+s);
            }

            @Override
            public void SKPMapBizAppIdSucceed() {
                Log.d("락스크린 ","SKPMapBizAppIdSucceed");

            }

            @Override
            public void SKPMapBizAppIdFailed(String s) {

                Log.d("락스크린 ","SKPMapBizAppIdFailed"+s);
            }
        });

        //경로를 호출한다
//        HashMap<String, String> pathInfo = new HashMap<String, String>();
//        pathInfo.put("rGoName", "신도림"); // 목적지
//        pathInfo.put("rGolat", "37.50861147");
//        pathInfo.put("rGolon", "126.8911457");
////        pathInfo.put("rStName", "현재위치"); // 출발지
////        pathInfo.put("rStlat", "37.50861147");
////        pathInfo.put("rStlon", "126.8911457");
//        tmaptapi.invokeRoute(pathInfo);


        Log.d("락스크린 tmaptapi",tmaptapi.getMnoInfoString());





        //현재위치 주변 카테고리 위치검색(초기값 - 카페,베이커리,편의점 전체)
        SerachToTalMaker();

//
//
//
//        //현재위치 주변 카페 검색
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (gps.isGetLocation())
//                {
//                    Log.d("락스크린 구글맵 카페 검색 스레드","시작");
//                    places = new cGPlacesAPI(getContext(), gps.getLatitude(), gps.getLongitude(), 500, "cafe");
//                    places.parsing();
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            for (cList Item : cGPlacesAPI.mList) {
//                                addMarker(Item,BitmapDescriptorFactory.HUE_GREEN);
//                            }
//                            Log.d("락스크린 구글맵 카페 검색 스레드","끝");
//                        }
//                    });
//                }
//            }
//        }).start();
//        if(mCurrentLocation != null) {
//            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
//
//        }

        googleMap.setMyLocationEnabled(false);//현재위치 주기적으로 나타냄
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //마커클릭리스너 구글맵에 경로를 그려준다
        setOnMarkerClickListener();

    }

    private Marker addMarker(cList Item) {
        LatLng position = new LatLng(Item.getLat(), Item.getLon());
        String name = Item.getname();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(name);
        markerOptions.position(position);
        //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(Item.getcolor()));
        //마커아이콘 설정
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(Item.getIcon(),200,200)));
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(Item.getIcon()));
//        Message msg = handler.obtainMessage();
//        msg.what = 0;
//        msg.obj = markerOptions;
//        handler.handleMessage(msg);

        return googleMap.addMarker(markerOptions);

    }

    //마커아이콘 리사이즈 
    public Bitmap resizeMapIcons(int icon, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),icon);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
//    public android.os.Handler handler = new android.os.Handler() {
//
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 0 :  // 스크린샷완료
//                        //googleMap.addMarker((MarkerOptions)msg.obj);
//                        break;
//                    case 1:
//                        break;
//                    case 2:
//                        break;
//                    case 3:
//                        break;
//                    case 4:
//                        break;
//                    case 5:
//                        break;
//                }
//
//            }
//    };

//    private final LocationListener mLocationListener = new LocationListener() {
//        public void onLocationChanged(Location location) {
//            //여기서 위치값이 갱신되면 이벤트가 발생한다.
//            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
//
//            Log.d("락스크린 mLocationListener", "onLocationChanged, location:" + location+"\n");
//            double longitude = location.getLongitude(); //경도
//            double latitude = location.getLatitude();   //위도
//            double altitude = location.getAltitude();   //고도
//            float accuracy = location.getAccuracy();    //정확도
//            String provider = location.getProvider();   //위치제공자
//            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
//            //Network 위치제공자에 의한 위치변화
//            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
//            Log.d("락스크린 mLocationListener","위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
//                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);
//            Toast.makeText(getContext(),"위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
//                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy,Toast.LENGTH_SHORT).show();
//        }
//        public void onProviderDisabled(String provider) {
//            // Disabled시
//            Log.d("test", "onProviderDisabled, provider:" + provider);
//        }
//
//        public void onProviderEnabled(String provider) {
//            // Enabled시
//            Log.d("test", "onProviderEnabled, provider:" + provider);
//        }
//
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            // 변경시
//            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
//        }
//    };

LatLng GPSlatLng;
    //현재위치 가져오기{
    public void GPS_getcurrentpoint(){
        //구글 현재위치 정보가져오기
        gps = new GpsInfo(getContext());
        // GPS 사용유무 가져오기
        Log.d("락스크린 구글맵 프래그먼트 마커", gps.isGetLocation() + "");
        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // Creating a LatLng object for the current location
            GPSlatLng = new LatLng(latitude, longitude);

            // Showing the current location in Google Map
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(GPSlatLng));

            // Map 을 zoom 합니다.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

            // 마커 설정.
            MarkerOptions optFirst = new MarkerOptions();
            optFirst.position(GPSlatLng);// 위도 • 경도
            optFirst.title("현재위치");// 제목 미리보기
            //optFirst.snippet("snippet");
            //현재위치마커 아이콘 설정
            optFirst.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            //optFirst.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.current, 200,200)));
            if(curMarker!=null) curMarker.remove();
            curMarker = googleMap.addMarker(optFirst);
            curMarker.showInfoWindow();
            Log.d("락스크린 구글맵 마커설정 성공", "위도 : " + gps.getLatitude() + " / 경도 : " + gps.getLongitude());
        } else {
            //gps정보를 가져오지 못했을 때 설정창으로 이동
            gps.showSettingsAlert();
            Log.d("락스크린 구글맵 프래그먼트 마커설정 실패", gps.isGetLocation() + "");
        }
    }
    //마커클릭리스너
    public void setOnMarkerClickListener(){
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(final Marker marker) {
                String markertitle = marker.getTitle();
                SerachToTalMaker();// 맵초기화 마커 카테고리전체 다시찍기
                final String text = "latitude ="
                        + marker.getPosition().latitude + "\nlongitude ="
                        + marker.getPosition().longitude;
//                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT)
//                        .show();

                marker.showInfoWindow();//마커 정보창 보이기
                //APIRequest.setAppKey("##APPKEY_INPUTHERE##");

                //POI 검색, 경로검색 등의 지도데이터를 관리하는 클래스
                TMapData tmapdata = new TMapData();

                arrayPoint = null;
                //출발지 목적지 위도,경도 설정 - 티맵
                TMapPoint startpoint = new TMapPoint(gps.getLatitude(), gps.getLongitude());
                TMapPoint endpoint = new TMapPoint(marker.getPosition().latitude, marker.getPosition().longitude);
//                try {
//                    String Address = tmapdata.convertGpsToAddress(marker.getPosition().latitude, marker.getPosition().longitude);// 위도경도를 주소로 반환
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ParserConfigurationException e) {
//                    e.printStackTrace();
//                } catch (SAXException e) {
//                    e.printStackTrace();
//                }
                Log.d("락스크린 startpoint",gps.getLatitude()+"/"+gps.getLongitude());
                Log.d("락스크린 endpoint",marker.getPosition().latitude+"/"+marker.getPosition().longitude);
                tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint,//보행자경로찾기,출발지,목적지
                        new TMapData.FindPathDataListenerCallback() {
                            @Override
                            public void onFindPathData(TMapPolyLine polyLine) {
                                Log.d("락스크린 onFindPathData","");
                                arrayPoint = polyLine.getLinePoint();
                                final double distance = polyLine.getDistance(); // double 이동거리를 리턴


                                //mMapView.addTMapPath(polyLine); 티맵에 경로그리기

                                //구글맵에 경로그리기
                                LatLng startLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());
                                polylineOptions = new PolylineOptions();
                                polylineOptions.width(20).color(Color.RED).add(startLatLng);
                                for(int i =0;i<arrayPoint.size();++i){
                                    TMapPoint tMapPoint = arrayPoint.get(i);
                                    LatLng point = new LatLng(tMapPoint.getLatitude(), tMapPoint.getLongitude());
                                    polylineOptions.add(point);
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //... UI 업데이트 작업
//                                        Toast.makeText(getContext(),"이동거리 : "+String.valueOf((int)distance), Toast.LENGTH_SHORT)
//                                                .show();
                                        //googleMap.clear();// 경로를지워준다
                                        googleMap.addPolyline(polylineOptions);

                                        //도착지 마커 덧붙이기
                                        LatLng position = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                                        String name = marker.getTitle();
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.title(name);
                                        String km = String.valueOf((int)distance);

                                        markerOptions.snippet("이동거리 : "+km+"m");
                                        markerOptions.position(position);
                                        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                        //m0arkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.end));//도착지마커아이콘
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.end, 200,200)));
                                        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cast_off_light));
                                        googleMap.addMarker(markerOptions).showInfoWindow();

                                        //출발지 마커 생성
//                                        MarkerOptions mOptions = new MarkerOptions();
//                                        mOptions.title("현재위치");
//                                        mOptions.position(GPSlatLng);
//                                        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//                                        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.end));//도착지마커아이콘
//                                        mOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.start, 200,200)));
//                                        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cast_off_light));
//                                        googleMap.addMarker(mOptions).showInfoWindow();
                                    }
                                });
                            }
                        });
//        ArrayList<TMapPOIItem> arTMapPOIItem_1 = tmapdata.findTitlePOI(“SKT 타워”);
//        ArrayList<TMapPOIItem> arTMapPOIItem_2 = tmapdata.findAddressPOI(“서울 용산구 이태원동”);
//
//        TMapPoint tpoint = new TMapPoint(37.570841, 126.985302);
//        ArrayList<TMapPOIItem> arTMapPOIItem_3 = tmapdata.findGetPOI(tpoinr);





//                TMapPolygon tpolygon = new TMapPolygon();
//                ArrayList<TMapPoint> arPoint = tpolygon.getPolygonPoint();

                //출발, 목적지 값으로 경로탐색을 요청
//                TMapPolyLine pathdata = null;
//                if(tmapdata!=null){
//                    try {
//                        pathdata = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,startpoint, endpoint);
//                        arrayPoint  = pathdata.getLinePoint();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (ParserConfigurationException e) {
//                        e.printStackTrace();
//                    } catch (SAXException e) {
//                        e.printStackTrace();
//                    }
//                }



//                tpolygon.addPolygonPoint();


//                LatLng endLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);

                return false;
            }
        });
    }
    //현재위치 주변 편의점,카페,베이커리 검색
    public void SerachToTalMaker() {
        //(final String endtitle)
        //현재위치 주변 편의점,카페,베이커리 검색
        googleMap.clear();
        GPS_getcurrentpoint();//현재위치 마커
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (gps.isGetLocation())
                {
                    String next_page_token="";
                    //do{
                    Log.d("락스크린 구글맵 편의점 검색 스레드","시작");
                    places = new cGPlacesAPI(getContext(), gps.getLatitude(), gps.getLongitude(), 1000, "grocery_or_supermarket",next_page_token);
                    places.parsing();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for(int i = 0 ; i< cGPlacesAPI.mList.size() ; i++)
                            {
                                addMarker(cGPlacesAPI.mList.get(i));
                            }
                            Log.d("락스크린 구글맵 편의점 검색 스레드","끝");
                        }
                    });
                    //next_page_token = "&next_page_token="+places.parsing(); //마커 추가후 다음페이지토큰 리턴


                    //현재위치 주변 베이커리 검색
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (gps.isGetLocation())
                            {
                                //cGPlacesAPI.mList.clear();
                                Log.d("락스크린 구글맵 베이커리 검색 스레드","시작");
                                String add = "keyword=파리&";
                                places = new cGPlacesAPI(getContext(), gps.getLatitude(), gps.getLongitude(), 1000, "", add);
                                places.parsing();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        for(int i = 0 ; i< cGPlacesAPI.mList.size() ; i++)
                                        {
                                            addMarker(cGPlacesAPI.mList.get(i));
                                        }
                                        Log.d("락스크린 구글맵 베이커리 검색 스레드","끝");
                                    }
                                });

                                //현재위치 주변 카페 검색
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (gps.isGetLocation())
                                        {
                                            //cGPlacesAPI.mList.clear();
                                            Log.d("락스크린 구글맵 베이커리 검색 스레드","시작");
                                            places = new cGPlacesAPI(getContext(), gps.getLatitude(), gps.getLongitude(), 1000, "cafe","");
                                            places.parsing();

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

//                                                    for (cList Item : cGPlacesAPI.mList) {
//                                                        addMarker(Item);
//                                                    }

                                                    for(int i = 0 ; i< cGPlacesAPI.mList.size() ; i++)
                                                    {
                                                        Marker marker = addMarker(cGPlacesAPI.mList.get(i));
//                                                        if(endtitle.equals(cGPlacesAPI.mList.get(i).getname())){
//
//                                                        }
                                                    }


                                                    Log.d("락스크린 구글맵 베이커리 검색 스레드","끝");
                                                }
                                            });
                                        }
                                    }
                                }).start();
                            }
                        }
                    }).start();
                }
            }
        }).start();
    }

    //검색버튼 클릭리스너
    public void SearchButtonClicked(ViewGroup rootView) {
        Button Category_All = (Button)rootView.findViewById(R.id.all);
        Button Category_Bagery = (Button)rootView.findViewById(R.id.bakery);
        Button Category_Cafe = (Button)rootView.findViewById(R.id.cafe);
        Button Category_Convenience = (Button)rootView.findViewById(R.id.convenience);

        Category_All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SerachToTalMaker();
            }
        });
        Category_Bagery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchBakeryMaker();
            }
        });
        Category_Cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchCafeMaker();
            }
        });
        Category_Convenience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchConvenienceStoreMaker();
            }
        });
    }


    //편의점검색 마커추가
    public void SearchConvenienceStoreMaker(){
        googleMap.clear();
        GPS_getcurrentpoint();//현재위치 마커
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (gps.isGetLocation()) {
                    String next_page_token = "";
                    //do{
                    Log.d("락스크린 구글맵 편의점 검색 스레드", "시작");
                    places = new cGPlacesAPI(getContext(), gps.getLatitude(), gps.getLongitude(), 1000, "grocery_or_supermarket", next_page_token);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for (cList Item : cGPlacesAPI.mList) {
                                addMarker(Item);
                            }
                            Log.d("락스크린 구글맵 편의점 검색 스레드", "끝");
                        }
                    });
                    //next_page_token = "&next_page_token=" + places.parsing(); //마커 추가후 다음페이지토큰 리턴
                }
            }
        }).start();
    }
    //베이커리검색 마커추가
    public void SearchBakeryMaker(){
        googleMap.clear();
        GPS_getcurrentpoint();//현재위치 마커
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (gps.isGetLocation()) {
                    //cGPlacesAPI.mList.clear();
                    Log.d("락스크린 구글맵 베이커리 검색 스레드", "시작");
                    String add = "keyword=파리&";
                    places = new cGPlacesAPI(getContext(), gps.getLatitude(), gps.getLongitude(), 1000, "", add);
                    places.parsing();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for (cList Item : cGPlacesAPI.mList) {
                                addMarker(Item);
                            }
                            Log.d("락스크린 구글맵 베이커리 검색 스레드", "끝");
                        }
                    });
                }
            }
        }).start();
    }
    //카페검색 마커추가
    public void SearchCafeMaker(){
        googleMap.clear();
        GPS_getcurrentpoint();//현재위치 마커
        //현재위치 주변 카페 검색
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (gps.isGetLocation())
                {
                    //cGPlacesAPI.mList.clear();
                    Log.d("락스크린 구글맵 카페 검색 스레드","시작");
                    places = new cGPlacesAPI(getContext(), gps.getLatitude(), gps.getLongitude(), 1000, "cafe","");
                    places.parsing();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for (cList Item : cGPlacesAPI.mList) {
                                addMarker(Item);
                            }
                            Log.d("락스크린 구글맵 카페 검색 스레드","끝");
                        }
                    });
                }
            }
        }).start();
    }
}
