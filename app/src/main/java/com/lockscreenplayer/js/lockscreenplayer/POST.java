package com.lockscreenplayer.js.lockscreenplayer;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by lenovo on 2017-03-01.
 */
public class POST {



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
                //Toast.makeText(getap,"서버연결에 실패하였습니다",Toast.LENGTH_SHORT).show();
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

    public void GetPointToServer(String str , String str2){

    }


}
