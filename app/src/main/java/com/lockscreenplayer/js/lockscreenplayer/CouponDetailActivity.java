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

    //??????????????????
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    String upLoadServerUrl = Server.localhost+"/UploadToServer.php";//??????????????? ??????
    String uploadFilePath = "storage/emulated/0/";//????????? ???????????????, ????????? ?????????????????? ?????? ??????->?????? ??????
    String uploadFileName = null; //????????????????????? ?????? ??????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActionBar ???????????? ?????? ??????
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //Toast.makeText(getApplicationContext(),"Uploading file path :- '/mnt/sdcard/"+uploadFileName+"'",Toast.LENGTH_SHORT).show();

        //??????????????????????????? ??????
        mProgressDialog = new ProgressDialog(this );
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("????????? ??????????????????...");


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
        couponnum.setText("???????????? : " + intent.getStringExtra("couponnum"));
        change.setText("????????? : " + intent.getStringExtra("brand"));
        name.setText("????????? : " + intent.getStringExtra("name"));
        date.setText("???????????? : " + intent.getStringExtra("validity"));
        QRcord = (ImageView) findViewById(R.id.QRcodeImage);

        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //QR????????????
                bp = generateQRCode(Server.localhost + "/QRcode.php?num=" +
                        intent.getStringExtra("couponnum"), 200, 200);
                handler.sendEmptyMessage(1);//QR?????? ????????????
            }
        }).start();


        try { //??????????????? ?????? ????????????
            kakaoLink = KakaoLink.getKakaoLink(this);
            kakaoTalkLinkMessageBuilder =
                    kakaoLink.createKakaoTalkLinkMessageBuilder();
            //???????????????
            String text = "????????? ??????????????????.\n" +
                "????????? : "+intent.getStringExtra("name")+"\n"+
                "???????????? : "+intent.getStringExtra("validity")+"\n"+
                "????????? : "+intent.getStringExtra("brand")+"\n"+
                "???????????? : "+intent.getStringExtra("couponnum");
            kakaoTalkLinkMessageBuilder.addText(text);
            //???????????????
            String imageSrc = "??????????????? ??????????????????";
            int width = 144*3; //?????? ????????? ?????????
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
                case 0 :  // ??????????????????
                    Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    QRcord.setImageBitmap(bp);//QR?????? ????????????
                    mProgressDialog.dismiss(); //??????????????? ??????
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


    //QR_Code ??????
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

        if (id == R.id.action_gift) {//???????????? ??????
            //Toast.makeText(getApplicationContext(), "action_gift??????", Toast.LENGTH_SHORT).show();
            dialog = new ProgressDialog(this); // ?????????????????????
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("????????? ??????????????????...");

            //????????? ??????????????????
            new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() { //UI?????????
                           // messageText.setText("uploading started.....");
                        }
                    });

                    Log.d("???????????? ????????????????????????","1");
                    //container.setDrawingCacheEnabled(true);
                    coupon_linear.buildDrawingCache();
                    coupon_linear.setDrawingCacheEnabled(true);
                    Bitmap captureView = coupon_linear.getDrawingCache(); //?????????????????? ??????????????? ??????
                    Log.d("???????????? ??????????????? ?????????",captureView+"");

                    String filepath = saveBitmapToJpeg(getApplicationContext(),captureView);//???????????? ??????????????? ?????? ??? ????????????(String) ??????

                    //????????? ???????????? ????????????

                    uploadFile(filepath); // ??????????????? ?????????

                    //FileToServer(filepath);

                    //uploadFile(uploadFilePath + "" + uploadFileName);


                }
            }).start();

            //????????????
            try {
                kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
            } catch (KakaoParameterException e) {
                e.printStackTrace();
            }
            return true;
        }
//        if (item.getTitle() == "????????????") {//???????????? ??????
//            Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
//            return true;
//        }
        if (id == R.id.action_save) {//???????????? ??????
            new Thread(new Runnable() {
                @Override
                public void run() {
                    takeScreenshot();//??????????????????
                    handler.sendEmptyMessage(0);//?????? ?????????????????? ??????
                }
            }).start();
            return true;
        }
        switch (item.getItemId()) { //????????? ActionBar?????? ???????????? ????????? ????????? ?????????
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    String fileName;

    //????????? ???????????? ?????? - ??????????????????????????????
    public  boolean isExists(String URLName) {
        try {

            // Sets whether HTTP redirects  (requests with response code 3xx)
            // should be automatically followed by this class.  True by default.
            HttpURLConnection.setFollowRedirects(false);

            /** HTTP ?????? ????????? SET
             * ??? ????????? ????????? ??????????????? ??????????????? ????????? HEAD ????????? ??????
             * HEAD????????? ?????? ???????????? ????????? ????????? ????????? ???????????? ?????? ????????? ????????? ??????
             *  GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE ????????? ??? ??? ??????.
             * ???????????? GET
             **/
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");

            //FILE??? ?????? ?????? HTTP_OK 200
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //?????? ??????
                return true;
            } else {//?????? ??????
                return false;
            }
        } catch (Exception e) {//?????? ??????
            e.printStackTrace();
            return false;
        }
    }

    //???????????? ??????????????? ?????? ??? ???????????? ??????
    public String saveBitmapToJpeg(Context context, Bitmap bitmap){

        // ??????????????? ?????? ????????? ????????????
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        // ??????????????????
        Date currentTime_1 = new Date();
        fileName = formatter.format(currentTime_1); //????????? ????????????

        File storage = context.getCacheDir(); // ??? ????????? ???????????? ?????? ??????

        File tempFile = new File(storage,fileName);

        try{
            tempFile.createNewFile();  // ????????? ???????????????

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out);  // ?????? ?????? bitmap??? jpeg(????????????)?????? ????????????

            out.close(); // ???????????? ???????????????.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile.getAbsolutePath();   // ???????????? ??????????????? ??????
    }

    //Multipart??? ???????????? ?????????, ????????? ??? ?????? ??? ??? ?????? ????????????
    public void FileToServer(String filepath) {
        try {
            URL url = new URL(upLoadServerUrl);
            String boundary = "SpecificString";
            URLConnection con = url.openConnection();
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setDoOutput(true);
            // ??????????????? ????????? ???????????? ?????? ?????? ????????????.

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
        View view = getWindow().getDecorView(); //???????????? ????????????

        String folder = "Test_Directory"; // ?????? ??????
        //Toast.makeText(getApplicationContext(), "???????????? ??????", Toast.LENGTH_SHORT).show();

        try {

            // ?????? ????????? ????????? ????????????
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            // ??????????????????
            Date currentTime_1 = new Date();
            String dateString = formatter.format(currentTime_1);

            File sdCardPath = Environment.getExternalStorageDirectory();
            File dirs = new File(Environment.getExternalStorageDirectory(), folder);

            if (!dirs.exists()) { // ????????? ????????? ????????? ????????? ??????
                dirs.mkdirs(); // Test ?????? ??????
                Log.d("???????????? CAMERA_TEST", "Directory Created");
            }
            Log.d("???????????? ????????????","1");
            //container.setDrawingCacheEnabled(true);
            coupon_linear.buildDrawingCache();
            coupon_linear.setDrawingCacheEnabled(true);
            Bitmap captureView = coupon_linear.getDrawingCache(); //?????????????????? ??????????????? ??????
            Log.d("???????????? ?????????",captureView+"");
            FileOutputStream fos;
            String save;
            Log.d("???????????? ????????????","2");
            try {
                save = sdCardPath.getPath() + "/" + folder + "/" + dateString + ".jpg";
                // ?????? ??????

                fos = new FileOutputStream(save);
                captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos); // ?????????????????? ?????? ??????
                File file = new File(sdCardPath.getPath() + "/" + folder, dateString);
                // ????????? ???????????? ?????? ???????????????????????? ???????????????.
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://"+sdCardPath.getPath() + "/" +folder+"/"+dateString+".jpg")));
                Log.d("???????????? ????????????","??????");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

//            Toast.makeText(this, dateString + ".jpg ??????",
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


    //??????????????????
    public int uploadFile(String filepath) {

        // ??????????????? ?????? ????????? ????????????
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        // ??????????????????
        Date currentTime_1 = new Date();
        //fileName = formatter.format(currentTime_1)+".jpg"; //????????? ???????????? : ???????????????

        fileName = intent.getStringExtra("couponnum")+".jpg";//????????? ???????????? : ??????????????????

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

                            Log.d("???????????? ???????????? msg File Upload Complete",msg+"");
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
                        Log.d("????????????","check script url.");
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
                        Log.d("???????????? Got Exception","see logcat.");
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
