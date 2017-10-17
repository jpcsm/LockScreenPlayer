package com.lockscreenplayer.js.lockscreenplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by lenovo on 2017-01-31.
 */
public class YouTubeAdater extends FragmentStatePagerAdapter {
    String url;

    YouTubeAdater(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 2;//lock_pager.bytebit.length;
    }

    @Override
    public Fragment getItem(int position) {
        //return PageFragment.create(galImages[position]);
        //byte[] a = bytebit[position];

        return YouTubePageFragment.newInstance(position);
    }
}

class YouTubePageFragment extends Fragment {
    static DrawableManager DM = new DrawableManager();
    private EditText et;
    AsyncTask<?, ?, ?> searchTask;
    ArrayList<SearchData> sdata = new ArrayList<SearchData>();
    boolean lastitemVisibleFlag = false;
    final String serverKey="AIzaSyDOc_L1Otd5lCHqpPl29f5hS9ikv98Gi44"; //�ֿܼ��� �޾ƿ� ����Ű�� �־��ݴϴ�
    int maxResults ;
    private boolean mLockListView;
    StoreListAdapter mAdapter;
    ListView searchlist;
    String nextPageToken;
    static int Horizin_pos;
    boolean first;
    HttpGet httpGet;
    static int pos;
    ViewGroup rootView;
    public static final String DEVELOPER_KEY = "AIzaSyBiAmqEiGZE9dzVfsudC3n3dFl85FK4O-0";


    public YouTubePageFragment(){}

    public static YouTubePageFragment newInstance(int sectionNumber) {
        YouTubePageFragment fragment = new YouTubePageFragment();

        Bundle argsn = new Bundle();
        //argsn.putString("url",url);
        //argsn.putString("url",url);
        argsn.putInt("num", sectionNumber);
        fragment.setArguments(argsn);
        //fragment.init();

        return fragment;
    }




//    private void init() {
//        initialize(DEVELOPER_KEY,new YouTubePlayer.OnInitializedListener(){
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
//                if (!wasRestored) {
//                    player.cueVideo("dssYPZMZKhA"); //video id.
//                    //player.cueVideo(getArguments().getString("url"));
//                    //player.cueVideo("IA1hox-v0jQ");  //http://www.youtube.com/watch?v=IA1hox-v0jQ
//
//                    //cueVideo(String videoId)
//                    //지정한 동영상의 미리보기 이미지를 로드하고 플레이어가 동영상을 재생하도록 준비하지만
//                    //play()를 호출하기 전에는 동영상 스트림을 다운로드하지 않습니다.
//                    //https://developers.google.com/youtube/android/player/reference/com/google/android/youtube/player/YouTubePlayer
//                }
//            }
//        });
//    }
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

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Horizin_pos = getArguments().getInt("num");


        //if(pos==0) {
            rootView = (ViewGroup) inflater.inflate(R.layout.youtube_list, container, false);
            et = (EditText) rootView.findViewById(R.id.eturl);

            Button search = (Button) rootView.findViewById(R.id.search);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                Toast.makeText(getApplicationContext(),"클릭",Toast.LENGTH_SHORT).show();
                    if(!et.getText().toString().equals("") && mLockListView == false) {
                        first = true;
                        maxResults=20;
                        searchTask = new searchTask().execute();
                        //Toast.makeText(getApplicationContext(),nextPageToken,Toast.LENGTH_SHORT).show();
                    }else if(et.getText().toString().equals("") ) {
                        Toast.makeText(getContext(),"검색어를 입력하세요",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            searchlist = (ListView) rootView.findViewById(R.id.searchlist);
            //화면에 리스트의 마지막 아이템이 보여지는지 체크
            searchlist.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                    lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                    //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
                    if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag && mLockListView == false) {
                        //TODO 화면이 바닦에 닿을때 처리
                        pos = searchlist.getFirstVisiblePosition();

                        first=false;
                        //Toast.makeText(getContext(),"끝",Toast.LENGTH_SHORT).show();
                        searchTask = new searchTask().execute();
                        //+mAdapter.items.size()

                    }
                }

            });
//        }else if(pos==1) {
//            rootView = (ViewGroup) inflater.inflate(R.layout.youtube_fragment, container, false);
//            searchTask = new searchTask().execute();
//        }

        //초기화면
        first = true;
        maxResults=20;
        searchTask = new searchTask().execute();



        return rootView;
    }
    public void VideoSelected(final String vid) { //메뉴 클릭시 수정,삭제 팝업창정보

        final CharSequence[] items = { "재생하기", "잠금화면에 추가하기"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this.getContext());

        // 제목셋팅
        alertDialogBuilder.setTitle("선택 목록 대화 상자");
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {



                        if(id==0) { //재생

                            Intent intent = new Intent(getContext(),
                                    MainActivity.class);
                            intent.putExtra("id",vid);
                            startActivity(intent); //����Ʈ ��ġ�� ����ϴ� ��Ƽ��Ƽ�� �̵��մϴ�. ������ ���̵� �Ѱ��ݴϴ�..

                        }else if(id==1) { //잠금화면 리스트에 추가하기
                            Toast.makeText(getContext(),"잠금화면에 추가되었습니다"+"\n"+vid,Toast.LENGTH_SHORT).show();
                            Log.d("락스크린 유튜브 비디오id",vid);
                        }
                        // 다이얼로그 종료
                        dialog.dismiss();
                    }
                });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        //alertDialog.getWindow().getAttributes().windowAnimations = R.style.;
        //alertDialog.getListView().setLayoutAnimation(controller);

//        Animation anim  = new TranslateAnimation(
//                Animation.RELATIVE_TO_SELF,1.0f , Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF,0.0f , Animation.RELATIVE_TO_SELF, 0.0f
//        );
//        anim.setDuration(1000);

        // 다이얼로그 보여주기
       alertDialog.show();
    }

    private class searchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLockListView = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                paringJsonData(getUtube());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            searchlist = (ListView) rootView.findViewById(R.id.searchlist);

            mAdapter = new StoreListAdapter(
                    getContext(), R.layout.listview_start, sdata
            ); //Json�Ľ��ؼ� ������ ��Ʃ�� �����͸� �̿��ؼ� ����Ʈ�� ������ݴϴ�.

            searchlist.setAdapter(mAdapter);
            searchlist.setSelection(pos);
            pos=0;
            mLockListView = false;
        }
    }

    public JSONObject getUtube() {

        if(first) {
            httpGet = new HttpGet(
                    "https://www.googleapis.com/youtube/v3/search?"
                            + "part=snippet&maxResults="+maxResults+"&q=" + et.getText().toString()
                            + "&key="+ serverKey);
        }else{
            httpGet = new HttpGet(
                    "https://www.googleapis.com/youtube/v3/search?pageToken="
                            +nextPageToken+"&part=snippet&maxResults="+maxResults+"&q=" + et.getText().toString()
                            + "&key="+ serverKey);
        }
//        httpGet = new HttpGet(
//                "www.youtube.com/playlist?list=LL1FULu5xb16RNGr9Ui81PTA"
//                        );

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    //�Ľ��� �ϸ� �������� ���� ���� �� �ִµ� �ʿ��� ������ �����ϼż� ����Ͻø� �˴ϴ�.
    private void paringJsonData(JSONObject jsonObject) throws JSONException {
        if(first) sdata.clear();

        JSONArray contacts = jsonObject.getJSONArray("items");
        nextPageToken = jsonObject.getString("nextPageToken");

        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            String vodid = c.getJSONObject("id").getString("videoId");  //��Ʃ�� ������ ���̵� ���Դϴ�. ����� �ʿ��մϴ�.

            String title = c.getJSONObject("snippet").getString("title"); //��Ʃ�� ������ �޾ƿɴϴ�
            String changString = "";
            try {
                changString = new String(title.getBytes("8859_1"), "utf-8"); //�ѱ��� ������ ���ڵ� ���־����ϴ�
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String date = c.getJSONObject("snippet").getString("publishedAt") //��ϳ�¥
                    .substring(0, 10);
            String imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails")
                    .getJSONObject("default").getString("url");  //�泻�� �̹��� URL��

            sdata.add(new SearchData(vodid, changString, imgUrl, date));
        }

    }

    String vodid = "";

    public class StoreListAdapter extends ArrayAdapter<SearchData> {
        private ArrayList<SearchData> items;
        SearchData fInfo;

        public StoreListAdapter(Context context, int textViewResourseId,
                                ArrayList<SearchData> items) {
            super(context, textViewResourseId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {// listview

            // ���
            View v = convertView;
            fInfo = items.get(position);

            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.listview_start, null);
            ImageView img = (ImageView) v.findViewById(R.id.img);

            String url = fInfo.getUrl();

            String sUrl = "";
            String eUrl = "";
            sUrl = url.substring(0, url.lastIndexOf("/") + 1);
            eUrl = url.substring(url.lastIndexOf("/") + 1, url.length());
            try {
                eUrl = URLEncoder.encode(eUrl, "EUC-KR").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String new_url = sUrl + eUrl;

            DM.fetchDrawableOnThread(new_url, img);  //�񵿱� �̹��� �δ�

            v.setTag(position);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = (Integer) v.getTag();

                    VideoSelected( items.get(pos).getVideoId() );
                }
            });

            ((TextView) v.findViewById(R.id.title)).setText(fInfo.getTitle());
            ((TextView) v.findViewById(R.id.date)).setText(fInfo
                    .getPublishedAt());

            return v;
        }
    }
}