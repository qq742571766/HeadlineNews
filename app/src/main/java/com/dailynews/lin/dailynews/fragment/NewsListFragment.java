package com.dailynews.lin.dailynews.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.activity.MainActivity;
import com.dailynews.lin.dailynews.adapter.NewsListAdapter;
import com.dailynews.lin.dailynews.config.NewsConfig;
import com.dailynews.lin.dailynews.entity.NewsInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsListFragment extends Fragment {
    private String mType;
    private RecyclerView mRecyclerView;
    private View view;
    String newsurl;
    String jsonData;//网络请求的json数据
    List<NewsInfo> newsData;
    NewsListAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                newsData = (List<NewsInfo>) msg.obj;
                adapter.adds(newsData);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_newslist, null);
        mType = getArguments().getString("type");
        initWidget();
        return view;
    }

    private void initWidget() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_newslist);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        adapter = new NewsListAdapter();
        adapter.setOnButtonClickListener(new NewsListAdapter.OnButtonClickListener() {
            @Override
            public void OnButtonClick(NewsInfo info) {
                NewsListWebFM newsfm = new NewsListWebFM();
                Bundle bundle = new Bundle();
                bundle.putParcelable("info",info);
                newsfm.setArguments(bundle);
                ((MainActivity) getActivity()).changFragment(newsfm);
            }
        });
        requestNetwork();
    }

    private void requestNetwork() {
        newsurl = NewsConfig.getNewsUrl(mType);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    // 请求
                    Request request = new Request.Builder()
                            .get()//get请求
                            .url(newsurl)//请求网址
                            .build();
                    //new一个Call对象，需要传入Request请求对象，
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {//请求成功
                        jsonData = response.body().string();
                        List<NewsInfo> datas = jsonParse(jsonData);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = datas;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private List<NewsInfo> jsonParse(String jsonData) {
        List<NewsInfo> newsdatas = new ArrayList<>();
        try {
            JSONObject jsonObj1 = new JSONObject(jsonData);
            if (jsonObj1.getString("reason").equals("成功的返回")) {
                JSONObject jsonObj2 = jsonObj1.getJSONObject("result");
                JSONArray array = jsonObj2.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObj = array.getJSONObject(i);
                    NewsInfo info = new NewsInfo();
                    info.title = jsonObj.getString("title");
                    info.date = jsonObj.getString("date");
                    info.category = jsonObj.getString("category");
                    info.author_name = jsonObj.getString("author_name");
                    info.url = jsonObj.getString("url");
                    info.thumbnail_pic_s = jsonObj.getString("thumbnail_pic_s");
                    newsdatas.add(info);
                }
                return newsdatas;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public void changeFragment(String url) {
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.add(R.id.News_web, new NewsListWebFM());
//        ft.addToBackStack(null);
//        ft.commit();
//    }

//    private void doHttp() {
//        newsurl = NewsConfig.getNewsUrl(mType);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL(newsurl);
//                    conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setDoInput(true);// 设置能不能读
//                    conn.setDoOutput(true);
//                    conn.setReadTimeout(5000);
//                    conn.setConnectTimeout(5000);
//                    conn.connect();
//                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                        is = conn.getInputStream();
//                        reader = new BufferedReader(new InputStreamReader(is));
//                        sb = new StringBuffer();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line);
//                        }
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                textView.setText(sb.toString());
//                            }
//                        });
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (is != null) {
//                            is.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
}
