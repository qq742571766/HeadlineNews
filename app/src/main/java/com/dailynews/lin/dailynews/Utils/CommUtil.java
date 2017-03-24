package com.dailynews.lin.dailynews.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/23.
 */

public class CommUtil {
    private static DecimalFormat format = new DecimalFormat("#.00");

    public static String getFileSize(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + "B";
        } else if (fileSize < 1024 * 1024) {
            return format.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1024 * 1024 * 1024) {
            return format.format((double) fileSize / 1024 / 1024) + "MB";
        } else {
            return format.format((double) fileSize / 1024 / 1024 / 1024) + "GB";
        }
    }

    public static String formatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }
}
