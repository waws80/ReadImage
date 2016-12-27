package thanatos.readimagefromlocal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;

import thanatos.readimagefromlocal.utils.DateUtils;
import thanatos.readimagefromlocal.utils.MiPictureHelper;

/**
 * Created on 2016/12/27.
 * 作者：by thanatos
 * 作用：
 */

public class Utils {

    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1;
    private static final int REQUEST_CODE_PICK_IMAGE = 2;
    private WeakReference<Activity> mActivityWeakReference=null;

    private static Utils mUtils;
    private String mFilePath;

    private Activity mActivity;

    private Utils(Activity activity) {
        mActivityWeakReference=new WeakReference<>(activity);
        mActivity=mActivityWeakReference.get();
    }

    public static Utils getInstance(Activity activity){
        if (mUtils==null){
            synchronized (Utils.class){
                if (mUtils==null){
                    mUtils=new Utils(activity);
                }
            }
        }
        return mUtils;
    }

    /**
     *
     * @param fileName 存放在内存中的文件夹名字
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void startCamera(String fileName){
        // 获取SD卡路径
        mFilePath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(mFilePath + "/"+fileName+"/");
        if (!file.exists()){
            file.mkdirs();
        }
        // 文件名
        mFilePath = mFilePath + "/"+fileName+"/" + DateUtils.getDateEN()+"photo.png";
        // 指定拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 加载路径
        Uri uri = Uri.fromFile(new File(mFilePath));
        // 指定存储路径，这样就可以保存原图了
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 拍照返回图片
        mActivity.startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
    }

    public void startPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        mActivity.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    public String getPath(int requestCode, Intent data){
        try {
            String path="";
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                //相册获取到的数据
                Uri uri = data.getData();
                //to do find the path of pic by uri
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor =mActivity. managedQuery(uri, proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                if (cursor==null){
                    path = MiPictureHelper.getPath(mActivity, uri);  // 获取图片路径的方法调用
                }else {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    path = cursor.getString(column_index);
                }

            } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {//从相机获取到的数据
                path=mFilePath;
            }
            return path;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }




    }

    public Bitmap getBitmap(int requestCode, Intent data){
        try {
            return getimagefor480(getPath(requestCode,data));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public String getString( int requestCode, Intent data){

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            getBitmap(requestCode,data).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] result = baos.toByteArray();//转换成功了
            return Base64.encodeToString(result, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public String getString(Bitmap data){

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            data.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] result = baos.toByteArray();//转换成功了
            return Base64.encodeToString(result, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 通过获取图片路径对图片进行压缩
     *
     * @param srcPath
     * @return
     */
    private static Bitmap getimagefor480(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap;//此时返回bm为空

        BitmapFactory.decodeFile(srcPath, newOpts);
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 159;//这里设置高度为800f
        float ww = 300;//这里设置宽度为480f
        int inSampleSize = 1;
        if (w > ww || h > hh){
            int widthRadio = Math.round(w * 1.0f / ww);
            int heightRadio = Math.round(h * 1.0f / hh);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        newOpts.inSampleSize = inSampleSize;//设置缩放比例
        newOpts.inJustDecodeBounds = false;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
        //return bitmap;
    }

    public Bitmap getBm(String srcPath, ImageView imageView) {
        IVSizeUtil.ImageSize imageViewSize = IVSizeUtil.getImageViewSize(imageView);
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap;//此时返回bm为空
        BitmapFactory.decodeFile(srcPath, newOpts);
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        int inSampleSize = 1;
        if (w > imageViewSize.width || h > imageViewSize.height){
            int widthRadio = Math.round(w * 1.0f / imageViewSize.width);
            int heightRadio = Math.round(h * 1.0f / imageViewSize.height);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        newOpts.inSampleSize = inSampleSize;//设置缩放比例
        newOpts.inJustDecodeBounds = false;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 对bitmap压缩
     *
     * @param image bitmap
     * @return bitmap
     */
    private static Bitmap compressImage(Bitmap image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
                image.compress(Bitmap.CompressFormat.PNG, 100, baos);//这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;//每次都减少10
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
