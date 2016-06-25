package com.wangban.yzbbanban.myapplication_achieve_picture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {
    private ImageView ivImage;
    private TextView tvPath1;
    private TextView tvPath2;
    private URL url;
    private static final String TAG = "supergirl";
    private String imagePath;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivImage = (ImageView) findViewById(R.id.iv_image);
        tvPath1 = (TextView) findViewById(R.id.tv_path1);
        tvPath2 = (TextView) findViewById(R.id.tv_path2);
        pauseImagePath();


    }

    private void pauseImagePath() {
        new Thread() {
            @Override
            public void run() {
                String webPath = "http://m.xxxiao.com";
                try {
                    Document doc = Jsoup.connect(webPath).get();

                    Elements e1 = doc.getElementsByClass("post-thumb");
                    String path1 = e1.get(0).getElementsByTag("a").attr("href");

                    String path2 = e1.get(0).getElementsByTag("img").attr("src");

                    Log.i(TAG, "pauseImagePath: " + path1 + "\n" + "path2: " + path2);

                    Message msg = Message.obtain();
                    msg.what = 2;
                    msg.obj = path2;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap bm2 = (Bitmap) msg.obj;
                    ivImage.setImageBitmap(bm2);
                    break;
                case 2:
                    String imPath = (String) msg.obj;
                    tvPath2.setText(imPath);
                    imagePath = imPath;
                    thread=new ImageThread();
                    thread.start();

            }


        }
    };

    class ImageThread extends Thread {


        @Override
        public void run() {
            try {
                Log.i(TAG, "run: " + imagePath);
                url = new URL(imagePath);

                URLConnection connection = url.openConnection();

                connection.connect();

                InputStream is = connection.getInputStream();

                Bitmap bm = BitmapFactory.decodeStream(is);

                Message msg = Message.obtain();
                msg.obj = bm;
                msg.what = 1;
                handler.sendMessage(msg);


            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


}
