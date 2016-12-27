package thanatos.readimagefromlocal.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 2016/12/27.
 * 作者：by thanatos
 * 作用：
 */

public class DateUtils {
    public static String getFileName() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(System.currentTimeMillis()));// 2016年07月17日 23:41:31
    }

    public static String getDateEN() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format1.format(new Date(System.currentTimeMillis()));// 2016-07-17 23:41:31
    }


}
