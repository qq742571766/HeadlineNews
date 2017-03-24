package com.dailynews.lin.dailynews.cn.sharesdk.utils;

import android.util.Log;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


public class ShareSDKUtils {
    private static String type = "";
    /**
     * 登录
     */
    public static void Login(String name) {
        type = "login";
        Platform mPlatform = ShareSDK.getPlatform(name);
        mPlatform.setPlatformActionListener(mPlatformActionListener);
        mPlatform.authorize();//单独授权,OnComplete返回的hashmap是空的
        mPlatform.showUser(null);//授权并获取用户信息
    }

    public static PlatformActionListener mPlatformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            if (type.equals("login")) {
                Log.e("onComplete", "登录成功");
                Log.e("openid", platform.getDb().getUserId());//拿到登录后的openid
                Log.e("username", platform.getDb().getUserName());//拿到登录用户的昵称
            } else {
                Log.e("onComplete", "分享成功");
            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.e("onError", throwable.toString() + "");
            if (type.equals("login")) {
                Log.e("onError", "登录失败" + throwable.toString());
            } else {
                Log.e("onError", "分享失败" + throwable.toString());
            }
        }

        @Override
        public void onCancel(Platform platform, int i) {
            if (type.equals("login")) {
                Log.e("onCancel", "登录取消");
            } else {
                Log.e("onCancel", "分享取消");
            }
        }
    };
}