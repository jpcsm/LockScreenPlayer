package com.lockscreenplayer.js.lockscreenplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by lenovo on 2017-02-17.
 */
 class HttpAsyncTask extends AsyncTask<String, Void, String> {

    private   home mainAct;
    public Context getcontext;
    String ad_site_url;
    String ad_overlap;
    URL url;
    String ad_point;



    HttpAsyncTask(home home) {
        this.mainAct = home;
    }

    public HttpAsyncTask(Context getcontext) {
        this.getcontext = getcontext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d("asynctask","시작");
    }

    @Override
    protected String doInBackground(String... urls) {

        return POST(urls[0],urls[1]);
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);

        Log.d("asynctask","종료");
//        Toast.makeText(mainAct, result, Toast.LENGTH_LONG).show();
//            strJson = result;
//            mainAct.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(mainAct, "Received!", Toast.LENGTH_LONG).show();
//                    try {
//                        JSONArray json = new JSONArray(strJson);
//                        mainAct.tvResponse.setText(json.toString(1));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        if(getcontext!=null) {

//
////            Toast.makeText(getcontext, "result : "+result, Toast.LENGTH_LONG).show();
//            Log.d("서버응답: ", result);
//            if(result!=null) {
//                //서버연결실패시 예외처리
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(result);
//                    ad_site_url = jsonObject.getString("ad_site_url");
//                    ad_point = jsonObject.getString("ad_point");
//                    ad_overlap = jsonObject.getString("ad_overlap");
//
//                    if(mFirebaseUser!=null){
//                        if(!ad_overlap.contains(mFirebaseUser.getEmail())){
//                            //광고본적없음
//                            String point = ad_point;
//
//
//                        }else{
//                            //광고본적있음
//                        }
//
//                    }
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    Uri u = Uri.parse(ad_site_url);
//                    i.setData(u);
//                    getcontext.startActivity(i);
//                    Toast.makeText(getcontext,ad_point+"포인트 적립", Toast.LENGTH_SHORT).show();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
////                    Toast.makeText(getcontext, "JSONException"+jsonObject.toString(), Toast.LENGTH_LONG).show();
////                    Log.d("JSONException",jsonObject.toString())     ;
//                }
//            }else{
//                Toast.makeText(getcontext, "서버연결에 실패하였습니다"+result.toString(), Toast.LENGTH_SHORT).show();
//                Log.d("서버연결실패",result)     ;
//            }
            //로그인시 구글계정데이터 서버에 저장
        }else if(mainAct!=null){




            mainAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(mainAct, result, Toast.LENGTH_SHORT).show();

                    //mainAct.tvResponse.setText(result);


                }
            });
        }

    }




    public String POST(String url, String json){
//        public static String POST(String url, String name,String email,String photourl){
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

//            String json = "";

            // build jsonObject
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.accumulate("name", name);
//            jsonObject.accumulate("email", email);
//            jsonObject.accumulate("photourl", photourl);

            // convert JSONObject to JSON to String
//            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // Set some headers to inform server about the type of the content

            // 서버 Response Data를 JSON 형식의 타입으로 요청.
            httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("Content-type", "application/json");
            httpCon.setRequestMethod("POST"); // POST로 연결
            //httpCon.setConnectTimeout(10000); // 타임아웃: 10초

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            try{
                OutputStream os = httpCon.getOutputStream();
                os.write(json.getBytes("utf-8"));
                os.flush();
            }catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d("서버연결실패","UnknownHostException");
                Toast.makeText(getcontext,"서버연결에 실패하였습니다",Toast.LENGTH_SHORT).show();
            }


            Log.d("json", json.toString());

            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


}

