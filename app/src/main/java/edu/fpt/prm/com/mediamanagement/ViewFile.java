package edu.fpt.prm.com.mediamanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.VideoView;

import java.util.ArrayList;

public class ViewFile extends AppCompatActivity {
    ArrayList<MediaEntry> list;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);
        list = ListAlbum.getAllListAlbum(this);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        if(position == -1){
            ImageView imageView = (ImageView) findViewById(R.id.display_image);
            imageView.setImageResource(R.drawable.none_media);
            imageView.setVisibility(View.VISIBLE);
        } else{
            MediaEntry item = list.get(position);
            if(item.getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                ImageView imageView = (ImageView) findViewById(R.id.display_image);
                Bitmap bitmap = BitmapFactory.decodeFile(item.getPath());
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }
            if(item.getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO){
                VideoView videoView = (VideoView) findViewById(R.id.play_video);
                videoView.setVideoPath(item.getPath());
                videoView.setVisibility(View.VISIBLE);
                videoView.start();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
