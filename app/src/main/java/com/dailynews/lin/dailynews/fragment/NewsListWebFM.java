package com.dailynews.lin.dailynews.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.config.NewsConfig;
import com.dailynews.lin.dailynews.entity.NewsInfo;
import com.trycatch.mysnackbar.TSnackbar;

public class NewsListWebFM extends Fragment {
    public NewsInfo info;
    public WebView mWebView;
    public View view;

    @Override
    public void onAttach(Context context) {
        info = getArguments().getParcelable("info");
        super.onAttach(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_news_web, null);
        initWidget();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.mToolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    boolean switchs = true;
                    if (NewsConfig.NewsCollectList.size() <= 0) {
                        NewsConfig.NewsCollectList.add(info);
                        TSnackbar.make(mWebView, "收藏成功", TSnackbar.LENGTH_SHORT).show();
                    } else {
                        for (NewsInfo infos : NewsConfig.NewsCollectList) {
                            if (infos.url.equals(info.url)) {
                                TSnackbar.make(mWebView, "收藏失败", TSnackbar.LENGTH_SHORT).show();
                                switchs = false;
                            }
                        }
                        if (switchs) {
                            NewsConfig.NewsCollectList.add(info);
                            TSnackbar.make(mWebView, "收藏成功", TSnackbar.LENGTH_SHORT).show();
                        }
                    }
                }
                return true;
            }
        });
        return view;
    }

    private void initWidget() {
        mWebView = (WebView) view.findViewById(R.id.wv);
        //设置在当前Fragment打开
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(info.url);
    }
}
