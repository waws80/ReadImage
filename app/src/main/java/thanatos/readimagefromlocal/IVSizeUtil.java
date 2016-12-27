package thanatos.readimagefromlocal;

import android.graphics.BitmapFactory.Options;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Created on 2016/12/27.
 * 作者：by thanatos
 * 作用：
 */
public class IVSizeUtil {
	/**
	 * 获取图片的尺寸
	 * 
	 * @param imageView 计算大小的控件
	 * @return 返回ImageSize
	 */
	public static ImageSize getImageViewSize(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
		LayoutParams lp = imageView.getLayoutParams();
		int width = imageView.getWidth();// 获取imageview显示的宽度
		if (width <= 0) {
			if (lp!=null&&lp.width>=0)width = lp.width;// 获取imageview在layout中声明的宽度
		}
		if (width <= 0) {
			width = getImageViewMaxWidth(imageView, "mMaxWidth");
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels;
		}
		int height = imageView.getHeight();// 获取imageview显示的高度
		if (height <= 0) {
			if (lp!=null&&lp.height>=0)height = lp.height;// 获取imageview在layout中声明的宽度
		}
		if (height <= 0) {
			height = getImageViewMaxWidth(imageView, "mMaxHeight");
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels;
		}
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;
	}

	/**
	 * 存储控件的尺寸
	 */
	public static class ImageSize {
		int width;
		int height;
	}
	
	/**
	 * 通过反射获取imageview的某个属性值
	 * @param object  控件的实例
	 * @param fieldName 变量名字
	 * @return  int
	 */
	private static int getImageViewMaxWidth(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
}
