package edu.fpt.prm.com.mediamanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by HuongLX on 3/14/2017.
 */

public class ListViewCustom extends BaseAdapter {
    private Context context;
    ArrayList<MediaEntry> items;
    ImageLoader loader;
    LayoutInflater inflater;

    public ListViewCustom(Context context, ArrayList<MediaEntry> items, ImageLoader loader) {
        this.context = context;
        this.items = items;
        this.loader = loader;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
}
