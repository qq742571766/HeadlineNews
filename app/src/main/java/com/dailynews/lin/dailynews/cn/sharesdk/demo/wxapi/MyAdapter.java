package com.dailynews.lin.dailynews.cn.sharesdk.demo.wxapi;

import cn.sharesdk.framework.TitleLayout;
import cn.sharesdk.framework.authorize.AuthorizeAdapter;
import cn.sharesdk.tencent.qq.QQWebShareAdapter;

import com.mob.tools.utils.R;

public class MyAdapter extends AuthorizeAdapter {
    @Override
    public void onCreate() {
        super.onCreate();
        TitleLayout llTitle = getTitleLayout();
        int resID = R.getStringRes(getActivity(), "second_title");
        llTitle.getTvTitle().setText(resID);
    }

    public void onDestroy() {
        System.out.println("> ShareSDKUIShell will be destroyed.");
    }
}
