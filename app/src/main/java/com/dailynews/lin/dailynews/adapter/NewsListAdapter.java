package com.dailynews.lin.dailynews.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.Utils.ImageLoaderUtil;
import com.dailynews.lin.dailynews.entity.NewsInfo;

import java.util.List;

import static android.content.ContentValues.TAG;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.mViewHolder> {
    private List<NewsInfo> datas;
    private OnButtonClickListener listener = null;
    private ImageLoaderUtil util;
    private Context context;
    static ImageView views;

    public void adds(List<NewsInfo> datas) {
        this.datas = datas;
    }

    @Override
    public mViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_news_list, null);
        this.context = parent.getContext();
        final mViewHolder holder = new mViewHolder(view);
        holder.newsview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = holder.getAdapterPosition();
                    NewsInfo info = datas.get(position);
                    //listener = (NewsListAdapter.OnButtonClickListener) parent.getContext();
                    listener.OnButtonClick(info);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        NewsInfo bean = datas.get(position);
        holder.thumbnail_pic_s.setImageResource(R.mipmap.ic_launcher);
        holder.title.setText(bean.title);
        holder.author_name.setText(bean.author_name);
        holder.date.setText(bean.date);
        holder.url.setText(bean.url);
        util = new ImageLoaderUtil(context);
        Bitmap bitmap = util.loadImage(bean.thumbnail_pic_s, l);
        if (bitmap != null) {
            holder.thumbnail_pic_s.setImageBitmap(bitmap);
        }
    }

    ImageLoaderUtil.OnLoadImageListener l = new ImageLoaderUtil.OnLoadImageListener() {
        @Override
        public void onImageLoadOK(String url, Bitmap bitmap) {
            views.setImageBitmap(bitmap);
        }

        @Override
        public void onImageLoadError(String url) {
            views.setImageResource(R.mipmap.ic_launcher);
            Toast.makeText(context, "加载出错", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public int getItemCount() {
        if (datas == null || datas.size() <= 0) {
            Log.d(TAG, "数据未加载完成");
        } else {
            return datas.size();
        }
        return 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail_pic_s;
        TextView title;
        TextView date;
        TextView author_name;
        TextView url;
        public View newsview;

        mViewHolder(View itemView) {
            super(itemView);
            newsview = itemView;
            thumbnail_pic_s = (ImageView) itemView.findViewById(R.id.imageView_f);
            title = (TextView) itemView.findViewById(R.id.textView_title);
            date = (TextView) itemView.findViewById(R.id.f_date);
            author_name = (TextView) itemView.findViewById(R.id.textView_author_name);
            url = (TextView) itemView.findViewById(R.id.textView_url);
            NewsListAdapter.views = thumbnail_pic_s;
        }
    }

    public interface OnButtonClickListener {
        void OnButtonClick(NewsInfo info);
    }

    public void setOnButtonClickListener(OnButtonClickListener l) {
        this.listener = l;
    }
}