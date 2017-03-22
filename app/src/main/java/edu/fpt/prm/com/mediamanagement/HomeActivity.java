package edu.fpt.prm.com.mediamanagement;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.media.CameraProfile;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import entry.MediaEntry;
import tools.AlbumTool;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class HomeActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    GridLayoutManager mLayoutManager;
    MyRecycleView adapter;

    //Capture
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;

    private Uri fileUri;
    FloatingActionButton fab,fab_Cam,fab_Video;
    boolean change = false;
    Animation fab_close,fab_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        recyclerView = (RecyclerView) findViewById(R.id.listItem);
        mLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        ArrayList<MediaEntry> dataSet = AlbumTool.getAllListAlbum(this);
        adapter = new MyRecycleView(dataSet,this);
        recyclerView.setAdapter(adapter);

        //setup floating button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_Cam = (FloatingActionButton) findViewById(R.id.fab_Cam);
        fab_Video = (FloatingActionButton) findViewById(R.id.fab_Video);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(change== false){
                    fab_Video.show();
                    fab_Cam.show();
                    fab.setAnimation(fab_open);
                    change=true;
                }
                else{
                    fab.setAnimation(fab_close);
                    hide();
                    change=false;
                }
            }
        });

        //Open Cam by using floating_button
        fab_Cam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Intent open = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if(open.resolveActivity(getPackageManager())!=null){
//                    startActivityForResult(open,REQUEST_IMAGE_CAPTURE);
//                    //hide floating_button
//                    hide();
//                }
                takePhoto("");
                hide();
                fab.setAnimation(fab_close);
            }
        });

        //Open video_record
        fab_Video.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
//                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
//                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//                    hide();
//                }
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
                hide();
                fab.setAnimation(fab_close);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

//    //luu anh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
//            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//            try {
//                FileOutputStream fos = new FileOutputStream(pictureFile);
//                Bitmap bit = (Bitmap) data.getExtras().get("data");
//                bit.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.flush();
//                fos.close();
//
//            }catch (Exception ex){}
            adapter.mDataset = AlbumTool.getAllListAlbum(getBaseContext());
            adapter.notifyDataSetChanged();
        }
    }

    private static Uri getOutputMediaFileUri(int type){

        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        //Create folder to store image - Name : Media_Master
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Media_Master");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Media_Master", "failed to create directory");
                return null;
            }
        }
        // Create a media file name : IMG_<Time>.jpg
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    //Exif data


    //-----------------------
    public void hide(){
        fab_Cam.hide();
        fab_Video.hide();
    }
    private void takePhoto(String description){
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy-hhmmss");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "IMG-"+format.format(new Date()));
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        Uri imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1);
    }
}
