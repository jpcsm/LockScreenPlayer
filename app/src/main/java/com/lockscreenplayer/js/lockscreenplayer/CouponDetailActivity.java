package com.lockscreenplayer.js.lockscreenplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CouponDetailActivity extends AppCompatActivity {

    ActionBar actionBar;
    ImageView couponimage;
    TextView couponname, name, date, change;
    TextView couponnum;
    ImageView QRcord;
    Intent intent;
    home home;
    LinearLayout coupon_linear;
    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    ProgressDialog mProgressDialog;

    //이미지업로드
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    String upLoadServerUrl = Server.localhost+"/UploadToServer.php";//서버업로드 주소
    String uploadFilePath = "storage/emulated/0/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
    String uploadFileName = null; //전송하고자하는 파일 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActionBar 뒤로가기 버튼 생성
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //Toast.makeText(getApplicationContext(),"Uploading file path :- '/mnt/sdcard/"+uploadFileName+"'",Toast.LENGTH_SHORT).show();

        //프로세스다이얼로그 생성
        mProgressDialog = new ProgressDialog(this );
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("잠시만 기다려주세요...");


        setContentView(R.layout.activity_coupon_detail);
        container = (LinearLayout) findViewById(R.id.coupon_linear);
        intent = getIntent();
        couponimage = (ImageView) findViewById(R.id.couponimage);
        couponname = (TextView) findViewById(R.id.couponname);
        couponnum = (TextView) findViewById(R.id.couponnum);
        date = (TextView) findViewById(R.id.validity);
        change = (TextView) findViewById(R.id.change);
        name = (TextView) findViewById(R.id.name);
        coupon_linear= (LinearLayout) findViewById(R.id.coupon_linear);
        String URL = intent.getStringExtra("image");
        Glide.with(this).load(Server.localhost + "/img/" + URL + ".jpg").into(couponimage);
        couponname.setText(intent.getStringExtra("name"));
        couponnum.setText("쿠폰번호 : " + intent.getStringExtra("couponnum"));
        change.setText("교환처 : " + intent.getStringExtra("brand"));
        name.setText("쿠폰명 : " + intent.getStringExtra("name"));
        date.setText("유효기간 : " + intent.getStringExtra("validity"));
        QRcord = (ImageView) findViewById(R.id.QRcodeImage);

        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //QR코드생성
                bp = generateQRCode(Server.localhost + "/QRcode.php?num=" +
                        intent.getStringExtra("couponnum"), 200, 200);
                handler.sendEmptyMessage(1);//QR코드 셋이미지
            }
        }).start();


        try { //카카오링크 쿠폰 선물하기
            kakaoLink = KakaoLink.getKakaoLink(this);
            kakaoTalkLinkMessageBuilder =
                    kakaoLink.createKakaoTalkLinkMessageBuilder();
            //텍스트전송
            String text = "선물이 도착했습니다.\n" +
                "쿠폰명 : "+intent.getStringExtra("name")+"\n"+
                "유효기간 : "+intent.getStringExtra("validity")+"\n"+
                "교환처 : "+intent.getStringExtra("brand")+"\n"+
                "쿠폰번호 : "+intent.getStringExtra("couponnum");
            kakaoTalkLinkMessageBuilder.addText(text);
            //이미지전송
            String imageSrc = "카카오링크 테스트메세지";
            int width = 144*3; //쿠폰 이미지 사이즈
            int height = 224*3;
            kakaoTalkLinkMessageBuilder.addImage(Server.localhost + "/uploads/" + intent.getStringExtra("couponnum") + ".jpg", width, height);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }
    Bitmap bp;
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :  // 스크린샷완료
                    Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    QRcord.setImageBitmap(bp);//QR코드 셋이미지
                    mProgressDialog.dismiss(); //다이얼로그 종료
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
            }

        }
    };


    //QR_Code 생성
    public Bitmap generateQRCode(String data, int width, int height) {
        Bitmap bmp = null;
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix =
                    writer.encode(data, BarcodeFormat.QR_CODE, width, height);

            int bitHeight = bitMatrix.getHeight();
            int bitWidth = bitMatrix.getWidth();

            bmp = Bitmap.createBitmap(bitHeight, bitWidth, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
    LinearLayout container;
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_gift) {//선물하기 클릭
            //Toast.makeText(getApplicationContext(), "action_gift클릭", Toast.LENGTH_SHORT).show();
            dialog = new ProgressDialog(this); // 다이얼로그생성
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("잠시만 기다려주세요...");

            //서버에 이미지업로드
            new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() { //UI스레드
                           // messageText.setText("uploading started.....");
                        }
                    });

                    Log.d("락스크린 서버이미지업로드","1");
                    //container.setDrawingCacheEnabled(true);
                    coupon_linear.buildDrawingCache();
                    coupon_linear.setDrawingCacheEnabled(true);
                    Bitmap captureView = coupon_linear.getDrawingCache(); //쿠폰레이아웃 비트맵으로 변환
                    Log.d("락스크린 파일업로드 비트맵",captureView+"");

                    String filepath = saveBitmapToJpeg(getApplicationContext(),captureView);//비트맵을 임시파일로 저장 후 파일경로(String) 리턴

                    //웹서버 파일유무 확인하기

                    uploadFile(filepath); // 이미지파일 업로드

                    //FileToServer(filepath);

                    //uploadFile(uploadFilePath + "" + uploadFileName);


                }
            }).start();

            //카톡전송
            try {
                kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
            } catch (KakaoParameterException e) {
                e.printStackTrace();
            }
            return true;
        }
//        if (item.getTitle() == "선물하기") {//선물하기 클릭
//            Toast.makeText(getApplicationContext(), "선물하기클릭", Toast.LENGTH_SHORT).show();
//            return true;
//        }
        if (id == R.id.action_save) {//저장하기 클릭
            new Thread(new Runnable() {
                @Override
                public void run() {
                    takeScreenshot();//스크린샷찍기
                    handler.sendEmptyMessage(0);//완료 토스트메세지 출력
                }
            }).start();
            return true;
        }
        switch (item.getItemId()) { //생성된 ActionBar에서 뒤로가기 버튼을 클릭시 이벤트
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    String fileName;

    //웹서버 파일유무 확인 - 업로드쿠폰이미지확인
    public  boolean isExists(String URLName) {
        try {

            // Sets whether HTTP redirects  (requests with response code 3xx)
            // should be automatically followed by this class.  True by default.
            HttpURLConnection.setFollowRedirects(false);

            /** HTTP 요청 메소드 SET
             * 본 예제는 파일의 존재여부만 확인하려니 간단히 HEAD 요청을 보냄
             * HEAD요청에 대해 웹서버는 수정된 시간이 포함된 리소스의 해더 정보를 간단히 리턴
             *  GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE 값등이 올 수 있다.
             * 디폴트는 GET
             **/
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");

            //FILE이 있는 경우 HTTP_OK 200
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //파일 있음
                return true;
            } else {//파일 없음
                return false;
            }
        } catch (Exception e) {//파일 없음
            e.printStackTrace();
            return false;
        }
    }

    //비트맵을 임시파일로 저장 후 파일경로 리턴
    public String saveBitmapToJpeg(Context context, Bitmap bitmap){

        // 파일이름을 현재 날짜로 저장하기
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        // 년월일시분초
        Date currentTime_1 = new Date();
        fileName = formatter.format(currentTime_1); //업로드 파일이름

        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로

        File tempFile = new File(storage,fileName);

        try{
            tempFile.createNewFile();  // 파일을 생성해주고

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌

            out.close(); // 마무리로 닫아줍니다.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴
    }

    //Multipart를 이용하여 이미지, 문자열 등 다른 값 한 번에 전송하기
    public void FileToServer(String filepath) {
        try {
            URL url = new URL(upLoadServerUrl);
            String boundary = "SpecificString";
            URLConnection con = url.openConnection();
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setDoOutput(true);
            // 여기까지가 서버에 접속하기 위한 기본 설정이다.

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes("\r\n--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\""+fileName+".jpg\"\r\n");
            wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            FileInputStream fileInputStream = new FileInputStream(filepath);

            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0)
            {
                // Upload file part(s)
                DataOutputStream dataWrite = new DataOutputStream(con.getOutputStream());
                dataWrite.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeScreenshot() {
        View view = getWindow().getDecorView(); //전체화면 가져오기

        String folder = "Test_Directory"; // 폴더 이름
        //Toast.makeText(getApplicationContext(), "저장하기 클릭", Toast.LENGTH_SHORT).show();

        try {

            // 현재 날짜로 파일을 저장하기
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            // 년월일시분초
            Date currentTime_1 = new Date();
            String dateString = formatter.format(currentTime_1);

            File sdCardPath = Environment.getExternalStorageDirectory();
            File dirs = new File(Environment.getExternalStorageDirectory(), folder);

            if (!dirs.exists()) { // 원하는 경로에 폴더가 있는지 확인
                dirs.mkdirs(); // Test 폴더 생성
                Log.d("락스크린 CAMERA_TEST", "Directory Created");
            }
            Log.d("락스크린 스크린샷","1");
            //container.setDrawingCacheEnabled(true);
            coupon_linear.buildDrawingCache();
            coupon_linear.setDrawingCacheEnabled(true);
            Bitmap captureView = coupon_linear.getDrawingCache(); //쿠폰레이아웃 비트맵으로 변환
            Log.d("락스크린 비트맵",captureView+"");
            FileOutputStream fos;
            String save;
            Log.d("락스크린 스크린샷","2");
            try {
                save = sdCardPath.getPath() + "/" + folder + "/" + dateString + ".jpg";
                // 저장 경로

                fos = new FileOutputStream(save);
                captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 쿠폰레이아웃 캡쳐 저장
                File file = new File(sdCardPath.getPath() + "/" + folder, dateString);
                // 미디어 스캐너를 통해 스크린샷이미지를 갱신시킨다.
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://"+sdCardPath.getPath() + "/" +folder+"/"+dateString+".jpg")));
                Log.d("락스크린 스크린샷","완료");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

//            Toast.makeText(this, dateString + ".jpg 저장",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Screen", "" + e.toString());
        }
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush(); fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.coucpon_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CouponDetail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.lockscreenplayer.js.lockscreenplayer/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CouponDetail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.lockscreenplayer.js.lockscreenplayer/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    //이미지업로드
    public int uploadFile(String filepath) {

        // 파일이름을 현재 날짜로 저장하기
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        // 년월일시분초
        Date currentTime_1 = new Date();
        //fileName = formatter.format(currentTime_1)+".jpg"; //업로드 파일이름 : 업로드시간

        fileName = intent.getStringExtra("couponnum")+".jpg";//업로드 파일이름 : 쿠폰일련번호

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(filepath);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(getApplicationContext(), "Source File not exist :"
                            +uploadFilePath + "" + uploadFileName, Toast.LENGTH_LONG).show();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUrl);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +uploadFileName;

                            Log.d("락스크린 서버응답 msg File Upload Complete",msg+"");
//                            Toast.makeText(getApplication(), "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("락스크린","check script url.");
                        Toast.makeText(getApplication(), "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("락스크린 Got Exception","see logcat.");
                        Toast.makeText(getApplication(), "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


}
