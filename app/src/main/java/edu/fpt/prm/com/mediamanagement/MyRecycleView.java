package edu.fpt.prm.com.mediamanagement;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import entry.MediaEntry;

/**
 * Created by HuongLX on 3/20/2017.
 */

public class MyRecycleView extends RecyclerView.Adapter<MyRecycleView.ViewHolder> {
    ArrayList<MediaEntry> mDataset;
    private Context context;

    public MyRecycleView(ArrayList<MediaEntry> mDataset, Context context) {
        this.mDataset = mDataset;
        this.context = context;
    }


    @Override
    public MyRecycleView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mDataset.isEmpty()) return;
        MediaEntry entry = mDataset.get(position);
        ImageView view = (ImageView) holder.mView.findViewById(R.id.image_view_thum);
        Glide.with(context).load("file://"+entry.getPath()).into(view);

        if (entry.getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            holder.mView.findViewById(R.id.play).setVisibility(View.GONE);
        }
        if (entry.getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            holder.mView.findViewById(R.id.play).setVisibility(View.VISIBLE);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ImageDetail.class);
                intent.putExtra("entry", position);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size() ;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

}
