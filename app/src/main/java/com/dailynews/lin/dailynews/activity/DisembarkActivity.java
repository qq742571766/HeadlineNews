package com.dailynews.lin.dailynews.activity;

import java.util.HashMap;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.base.BaseActivity;
import com.mob.tools.utils.UIHandler;
import com.trycatch.mysnackbar.TSnackbar;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

public class DisembarkActivity extends BaseActivity implements Callback, OnClickListener, PlatformActionListener {
    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;
    static Platform platform;
    HashMap<String, Object> res;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(this);
        setContentView(R.layout.activity_disembark);
        textView = (TextView) findViewById(R.id.tvQq);
        textView.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvQq: {
                authorize(new QQ(this));
            }
            break;
        }
    }

    private void authorize(Platform plat) {
        if (plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                login(null);
                return;
            }
        }
        plat.setPlatformActionListener(this);
        plat.SSOSetting(false);
        plat.showUser(null);
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
        DisembarkActivity.platform = platform;
        this.res = res;
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
            login(res);
        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
        }
        t.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    private void login(HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = userInfo;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_USERID_FOUND: {
                TSnackbar.make(textView,R.string.userid_found,TSnackbar.LENGTH_SHORT).show();
                SharedPreferences sp = getSharedPreferences("isFirstRunning", Context.MODE_PRIVATE);
                //添加进Boolean值
                boolean isRegister = sp.getBoolean("isRegister", true);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isRegister", false);
                //提交
                editor.apply();
                if (!isRegister) {
                    startActivity(MainActivity.class);
                    DisembarkActivity.this.finish();
                }
            }
            break;
            case MSG_LOGIN: {
                startActivity(MainActivity.class);
                DisembarkActivity.this.finish();
            }
            break;
            case MSG_AUTH_CANCEL: {
                TSnackbar.make(textView,R.string.auth_cancel,TSnackbar.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_ERROR: {
                TSnackbar.make(textView,R.string.auth_error,TSnackbar.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_COMPLETE: {
                TSnackbar.make(textView,R.string.auth_complete,TSnackbar.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences("isFirstRunnings", Context.MODE_PRIVATE).edit();
                editor.putString("UserName", (String) res.get("nickname"));
                editor.putString("res", (String) res.get("figureurl_qq_1"));
                editor.apply();
                startActivity(MainActivity.class);
                DisembarkActivity.this.finish();
            }
            break;
        }
        return false;
    }
}