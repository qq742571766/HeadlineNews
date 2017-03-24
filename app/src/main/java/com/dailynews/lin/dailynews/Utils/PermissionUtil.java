package com.dailynews.lin.dailynews.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by z-Lin on 2017/2/26.
 */

public class PermissionUtil {
    private static int mRequestCode = -1;
    private static OnRequestPermissionListener mListener;

    public interface OnRequestPermissionListener{
        void onRequestGranted();//请求成功
        void onRequestDenied();//请求失败
    }


    //请求权限
    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermissions(Context context, int requestCode, String[] permissions,
                                          OnRequestPermissionListener listener){
        if (context instanceof Activity){
            mListener = listener;
            List<String> deniedPermissions = getDeniedPermissions(context, permissions);
            if (deniedPermissions.size() > 0){
                mRequestCode = requestCode;
                ((Activity)context).requestPermissions(deniedPermissions.toArray(new String[
                        deniedPermissions.size()]), requestCode);
            }else{
                if (mListener != null){
                    mListener.onRequestGranted();
                }
            }
        }else {
            throw new RuntimeException("Context must be an Activity");
        }
    }

    //获取请求权限中需要被授权的权限
    private static List<String> getDeniedPermissions(Context context, String... permissions){
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    //请求权限的结果，对应Activity中的onRequestPermissionsResult()方法
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (mRequestCode != -1 && mRequestCode == requestCode){
            if (mListener != null){
                if (verifyPermissions(grantResults)){
                    mListener.onRequestGranted();
                }else{
                    mListener.onRequestDenied();
                }
            }
        }
    }

    //验证所请求的权限是否已经被授权
    private static boolean verifyPermissions(int[] grantResults){
        for(int grantResult : grantResults){
            if (grantResult != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
