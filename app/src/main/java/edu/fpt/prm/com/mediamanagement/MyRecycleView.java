package edu.fpt.prm.com.mediamanagement;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by HuongLX on 3/20/2017.
 */

public class MyRecycleView extends RecyclerView.Adapter<MyRecycleView.ViewHolder> {
    private String[] mDataset;

    public MyRecycleView(String[] mDataset) {
        this.mDataset = mDataset;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView textView = (TextView) holder.mView.findViewById(R.id.tvDate);
        textView.setText(mDataset[position]);
        textView.setVisibility(View.VISIBLE);
    }


    @Override
    public int getItemCount() {
        return mDataset.length;
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
