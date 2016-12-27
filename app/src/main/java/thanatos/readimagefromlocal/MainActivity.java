package thanatos.readimagefromlocal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    private LruCache<String,Bitmap> mCache=new LruCache<String, Bitmap>(maxMemory/8){
        @Override
        protected int sizeOf(String key, Bitmap value) {
             return value.getRowBytes() * value.getHeight();
        }
    };

    private String url="http://img5.imgtn.bdimg.com/it/u=191929538,2303740503&fm=23&gp=0.jpg";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView= (ImageView) findViewById(R.id.iv);
        getPic(imageView);
    }

    public void camera(View view){
        Utils.getInstance(this).startCamera("12-27");
    }

    public void photo(View view){
        Utils.getInstance(this).startPhoto();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = Utils.getInstance(this).getBitmap(requestCode, data);
        if (bitmap==null)return;
        imageView.setImageBitmap(bitmap);

    }

    private void getPic(final ImageView imageView){
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (mCache.get(url)!=null){
                    Log.w(TAG, "run:  image -----"+url+"is in cache" );
                    Message msg=Message.obtain();
                    msg.obj=mCache.get(url);
                    handler.sendMessage(msg);
                }
                requestPic( imageView);
            }
        }.start();
    }

    private void requestPic(ImageView iv) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            int responseCode = urlConnection.getResponseCode();
            if (responseCode==200){

                ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
                int rc = 0;
                while ((rc = urlConnection.getInputStream().read(buff, 0, 100)) > 0) {
                    swapStream.write(buff, 0, rc);
                }
                byte[] in_b = swapStream.toByteArray(); //in_b为转换之后的结果
                Log.w(TAG, "requestPic: download success    " +in_b);
                reSizeBitmap(in_b,iv);
            }else {
                Log.w(TAG, "requestPic: download failer" );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reSizeBitmap(byte[] b,ImageView imageView) {
        IVSizeUtil.ImageSize imageViewSize = IVSizeUtil.getImageViewSize(imageView);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeByteArray(b,0,b.length);
        int ow=options.outWidth;
        int oh=options.outHeight;
        int iw=imageViewSize.width;
        int ih=imageViewSize.height;
        int inSampleSize=1;
        if (ow>iw||oh>ih){
            int widthRadio = Math.round(ow * 1.0f / imageViewSize.width);
            int heightRadio = Math.round(oh * 1.0f / imageViewSize.height);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        options.inSampleSize=inSampleSize;
        options.inJustDecodeBounds=false;
        Message msg=Message.obtain();
        msg.obj=BitmapFactory.decodeByteArray(b,0,b.length);
        mCache.put(url,BitmapFactory.decodeByteArray(b,0,b.length));
        handler.sendMessage(msg);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.w(TAG, "handleMessage: "+msg.obj );
            imageView.setImageBitmap((Bitmap) msg.obj);
        }
    };


}
