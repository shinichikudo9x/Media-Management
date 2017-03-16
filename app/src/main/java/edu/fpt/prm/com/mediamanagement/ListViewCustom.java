package edu.fpt.prm.com.mediamanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import entry.MediaEntry;
import tools.DateTimeUtils;

/**
 * Created by HuongLX on 3/14/2017.
 */

public class ListViewCustom extends ArrayAdapter<MediaEntry> {
    private Context context;
    ArrayList<MediaEntry> items;
    ImageLoader loader;
    LayoutInflater inflater;
    private SparseBooleanArray mSelectedItemsIds;

    public ListViewCustom(Context context, int resource, ArrayList<MediaEntry> items,ImageLoader loader) {
        super(context, resource,items);
        mSelectedItemsIds = new SparseBooleanArray();
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.loader = loader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) view = inflater.inflate(R.layout.list_album,null);

        MediaEntry item = items.get(position);
        String curDate = DateTimeUtils.convertToDate(item.getDateAdded());
        if(items == null){
            ImageView imageView = (ImageView) view.findViewById(R.id.display_image);
            imageView.setImageResource(R.drawable.none_media);
            return view;
        }
        if(position == 0){
            TextView textView = (TextView) view.findViewById(R.id.tvDate);
            textView.setText(curDate);
            textView.setVisibility(View.VISIBLE);
        }
        if(position >0){
            if(!DateTimeUtils.convertToDate(items.get(position-1).getDateAdded()).equals(curDate)){
                TextView textView = (TextView) view.findViewById(R.id.tvDate);
                textView.setText(curDate);
                textView.setVisibility(View.VISIBLE);
            }
        }
        if(item.getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
            ImageView imageView = (ImageView) view.findViewById(R.id.display_image);
            loader.displayImage("file://"+item.getPath(),imageView);
            ImageView btnPlay = (ImageView) view.findViewById(R.id.btnPlay);
            btnPlay.setVisibility(View.GONE);
        }
        if(item.getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO){
            ImageView imageView = (ImageView) view.findViewById(R.id.display_image);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(item.getPath(),MediaStore.Video.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(bitmap);
            ImageView btnPlay = (ImageView) view.findViewById(R.id.btnPlay);
            btnPlay.setVisibility(View.VISIBLE);
        }
        return view;
    }
    @Override
    public void remove(MediaEntry object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public ArrayList<MediaEntry> getWorldPopulation() {
        return items;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
