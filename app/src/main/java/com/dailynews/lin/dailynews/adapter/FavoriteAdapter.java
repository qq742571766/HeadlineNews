package com.dailynews.lin.dailynews.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.Utils.CommUtil;
import com.dailynews.lin.dailynews.Utils.CommonUtils;
import com.dailynews.lin.dailynews.Utils.ImageLoaderUtil;
import com.dailynews.lin.dailynews.entity.NewsInfo;
import com.squareup.picasso.Picasso;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    List<NewsInfo> data;
    private OnButtonClickListener listener = null;
    private ImageLoaderUtil util;
    private Context context;
    static ImageView views;

    public void addDatas(List<NewsInfo> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_favorite, null);
        context = parent.getContext();
        final FavoriteAdapter.ViewHolder holder = new FavoriteAdapter.ViewHolder(view);
        holder.newsview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = holder.getAdapterPosition();
                    NewsInfo info = data.get(position);
                    //listener = (NewsListAdapter.OnButtonClickListener) parent.getContext();
                    listener.OnButtonClick(info);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsInfo info = data.get(position);
        holder.imageViews.setImageResource(R.mipmap.ic_launcher);
        holder.biaoti.setText(info.title);
        holder.shijian.setText(CommUtil.formatTime(System.currentTimeMillis()));
        util = new ImageLoaderUtil(context);
        Bitmap bitmap = util.loadImage(info.thumbnail_pic_s, l);
        if (bitmap != null) {
            String url = CommonUtils.encodeUrl(info.thumbnail_pic_s);
            Picasso.with(context).load(url).into(holder.imageViews);
            holder.imageViews.setImageBitmap(bitmap);
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
            TSnackbar.make(views,R.string.userid_found,TSnackbar.LENGTH_SHORT).show();
        }
    };

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView biaoti;
        TextView shijian;
        ImageView imageViews;
        public View newsview;

        public ViewHolder(View itemView) {
            super(itemView);
            newsview = itemView;
            imageViews = (ImageView) itemView.findViewById(R.id.imageViews);
            biaoti = (TextView) itemView.findViewById(R.id.biaoti);
            shijian = (TextView) itemView.findViewById(R.id.shijian);
            NewsListAdapter.views = imageViews;
        }
    }

    public interface OnButtonClickListener {
        void OnButtonClick(NewsInfo info);
    }

    public void setOnButtonClickListener(OnButtonClickListener l) {
        this.listener = l;
    }
}
