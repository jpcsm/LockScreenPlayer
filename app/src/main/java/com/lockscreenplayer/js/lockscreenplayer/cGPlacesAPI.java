package com.lockscreenplayer.js.lockscreenplayer;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Created by lenovo on 2017-03-29.
 */
public class cGPlacesAPI
{


    Context mContext;
    StringBuilder mResponseBuilder = new StringBuilder();
    Float color;
    static LinkedList<cList> mList = new LinkedList<>();

    cGPlacesAPI(Context _con, double _lat, double _lon, double _radius, String _type, String next_page_token)
    {
        mContext = _con;
        try
        {
            String uStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+next_page_token+"location=" + _lat + "," + _lon + "&radius=" + _radius + "&types=" + _type + "&key=AIzaSyBiAmqEiGZE9dzVfsudC3n3dFl85FK4O-0";
            URL url = new URL(uStr);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String          inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                mResponseBuilder.append(inputLine);
        }
        in.close();
    }
    catch (MalformedURLException me)
    {
        me.printStackTrace();
    }
    catch (UnsupportedEncodingException ue)
    {
            ue.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Log.d("락스크린 cGPlacesAPI생성자 ", "");
    }
    String next_page_token;
    public String parsing()
    {
        next_page_token="";
        try
        {
        JSONArray jArr;
        JSONObject      jObj;

        jObj            = new JSONObject(mResponseBuilder.toString());
        jArr            = jObj.getJSONArray("results");
//        if(jObj.getString("next_page_token")!=null){
//            next_page_token = jObj.getString("next_page_token");
//        }
        Log.d("락스크린", " parsing() next_page_token"+next_page_token);

        for (int i = 0; i < jArr.length(); i++)
        {
            // 결과별로 결과 object 얻기
            JSONObject result = jArr.getJSONObject(i);

            // 위도, 경도 얻기
            JSONObject  geo = result.getJSONObject("geometry");
            JSONObject  location = geo.getJSONObject("location");
            String      sLat = location.getString("lat");
            String      sLon = location.getString("lng");

            // 이름 얻기
            String name = result.getString("name");

            //결과상태
//            String status = result.getString("status");
//            if(status.equals("ZERO_RESULTS")) Toast.makeText(mContext,"현재 주변에 위치한 정보가 없습니다",Toast.LENGTH_SHORT).show();

            //다음페이지 20개 요청토큰
          //  next_page_token = result.getString("next_page_token");

            // Rating 얻기
            String rating = "0";
            if (result.has("rating") == true)
                rating  = result.getString("rating");

            if(name.contains("세븐일레븐")||name.contains("GS25")||name.contains("CU")) {
                //편의점
                color = BitmapDescriptorFactory.HUE_ORANGE;
                mList.add(new cList(Float.valueOf(rating), Double.valueOf(sLat), Double.valueOf(sLon), name, color, R.drawable.convenience_marker));
            }else if(name.contains("이디야")){
                //카페
                color = BitmapDescriptorFactory.HUE_CYAN;
                mList.add(new cList(Float.valueOf(rating), Double.valueOf(sLat), Double.valueOf(sLon), name, color,R.drawable.cafe_marker));
            }else if(name.contains("파리바게뜨")||name.contains("빠리바게뜨")){
                //베이커리
                color = BitmapDescriptorFactory.HUE_VIOLET;
                mList.add(new cList(Float.valueOf(rating), Double.valueOf(sLat), Double.valueOf(sLon), name, color,R.drawable.bakery_marker));
            }else{
                //기타
                color = BitmapDescriptorFactory.HUE_AZURE;
                //mList.add(new cList(Float.valueOf(rating), Double.valueOf(sLat), Double.valueOf(sLon), name, color));
            }



            Log.d("락스크린 구글맵 카테고리 위치가져오기 ",sLat+" / "+sLon+"\n"+rating+" "+name);


        }
    }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return next_page_token;
    }

    public LinkedList<cList> getList()
    {
        return mList;
    }
}

class  cList{
    private Float rating;
    private Double sLat,sLon;
    private String name;
    private Float color;
    private int icon;

    cList(Float rating ,Double sLat, Double sLon, String name, Float color, int icon) {
        this.rating = rating;
        this.sLat = sLat;
        this.sLon = sLon;
        this.name = name;
        this.color = color;
        this.icon = icon;
    }

//    private String brand;
//    private int price;
//    private String image ;
//
    public Double getLat() {
        return sLat;
    }
    public Double getLon() {
        return sLon;
    }
    public int getIcon() {
        return icon;
    }
    public String getname() {
        return name;
    }

    public Float getcolor() {
        return color;
    }
//
//    public void setBrand(String brand) {
//        this.brand = brand;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }
//    public void setPrice(int price) {
//        this.price = price;
//    }
//    public void setImage(String image) {
//        this.image = image;
//    }

}