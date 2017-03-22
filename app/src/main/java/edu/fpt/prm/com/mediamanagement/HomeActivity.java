package edu.fpt.prm.com.mediamanagement;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import entry.MediaEntry;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import tools.AlbumTool;

@RuntimePermissions
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    RecyclerView recyclerView;
    GridLayoutManager mLayoutManager;

    //Capture
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;

    private Uri fileUri;
    FloatingActionButton fab, fab_Cam, fab_Video;
    boolean change = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //get read permission
        HomeActivityPermissionsDispatcher.getReadPermissionWithCheck(this);

        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        recyclerView = (RecyclerView) findViewById(R.id.listItem);
        mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        ArrayList<MediaEntry> dataSet = AlbumTool.getAllListAlbum(this);
        MyRecycleView adapter = new MyRecycleView(dataSet, this);
        recyclerView.setAdapter(adapter);

        //setup floating button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_Cam = (FloatingActionButton) findViewById(R.id.fab_Cam);
        fab_Video = (FloatingActionButton) findViewById(R.id.fab_Video);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (change == false) {
                    fab_Video.show();
                    fab_Cam.show();
                    change = true;
                } else {
                    hide();
                    change = false;
                }
            }
        });

        //Open Cam by using floating_button
        fab_Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent open = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (open.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(open, REQUEST_IMAGE_CAPTURE);
                    //hide floating_button
                    hide();
                }
            }
        });

        //Open video_record
        fab_Video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                    hide();
                }

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    //luu anh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap bit = (Bitmap) data.getExtras().get("data");
                bit.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

            } catch (Exception ex) {
            }
        }
    }
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void getReadPermission(){
    }
    private static Uri getOutputMediaFileUri(int type) {
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
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    //Exif data


    //-----------------------
    public void hide() {
        fab_Cam.hide();
        fab_Video.hide();
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void systemAlertWindowOnShowRationale(PermissionRequest request) {
        showRationaleDialog(R.string.permission_read_external_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void systemAlertWindowOnPermissionDenied() {
        Toast.makeText(this, R.string.permission_read_external_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void systemAlertWindowOnNeverAskAgain() {
        Toast.makeText(this, R.string.permission_read_external_ask_again, Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
