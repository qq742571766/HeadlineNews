package com.dailynews.lin.dailynews.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.activity.MainActivity;
import com.dailynews.lin.dailynews.adapter.FavoriteAdapter;
import com.dailynews.lin.dailynews.config.NewsConfig;
import com.dailynews.lin.dailynews.entity.NewsInfo;

import java.util.List;

public class FavoriteFragment extends Fragment {
    RecyclerView mRecyclerView;
    FavoriteAdapter adapter;
    List<NewsInfo> data;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_favortefragment, null);
        initWidget();
        return view;
    }

    private void initWidget() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_favortefragment);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        adapter = new FavoriteAdapter();
        adapter.setOnButtonClickListener(new FavoriteAdapter.OnButtonClickListener() {
            @Override
            public void OnButtonClick(NewsInfo info) {
                NewsListWebFM newsfm = new NewsListWebFM();
                Bundle bundle = new Bundle();
                bundle.putParcelable("info",info);
                newsfm.setArguments(bundle);
                ((MainActivity) getActivity()).changFragment(newsfm);
            }
        });
        data = NewsConfig.NewsCollectList;
        adapter.addDatas(data);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}