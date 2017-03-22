package edu.fpt.prm.com.mediamanagement;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import entry.MediaEntry;
import es.dmoral.toasty.Toasty;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import tools.AlbumTool;

@RuntimePermissions
public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    RecyclerView recyclerView;
    GridLayoutManager mLayoutManager;
    Toolbar mToolbar;
    MyRecycleView adapter;

    //Capture
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    private static final int REQUEST_CODE_CREATOR = 3;
    private Bitmap mBitmapToSave;
    private Uri fileUri;
    FloatingActionButton fab, fab_Cam, fab_Video;
    boolean change = false;
    //driver api
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_OPENER = 2;
    private GoogleApiClient mGoogleApiClient;
    private boolean fileOperation = false;
    private DriveId mFileId;
    public DriveFile file;
    Animation fab_close, fab_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //get read permission
        HomeActivityPermissionsDispatcher.getReadPermissionWithCheck(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //Navigation Drawer
        addNavigationDrawer();

        createFileOnDrive();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        recyclerView = (RecyclerView) findViewById(R.id.listItem);
        mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        ArrayList<MediaEntry> dataSet = AlbumTool.getAllListAlbum(this);
        adapter = new MyRecycleView(dataSet, this);
        recyclerView.setAdapter(adapter);

        //setup floating button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_Cam = (FloatingActionButton) findViewById(R.id.fab_Cam);
        fab_Video = (FloatingActionButton) findViewById(R.id.fab_Video);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (change == false) {
                    fab_Video.show();
                    fab_Cam.show();
                    fab.setAnimation(fab_open);
                    change = true;
                } else {
                    fab.setAnimation(fab_close);
                    hide();
                    change = false;
                }
            }
        });

        //Open Cam by using floating_button
        fab_Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        fab_Video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    //luu anh
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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


            //hoanglg
            switch (requestCode) {
                case REQUEST_CODE_CREATOR:
                    if (requestCode == RESULT_OK) {
                        Log.i(TAG, "Image successfully saved.");
                    }
                    break;
                case REQUEST_CODE_OPENER:
                    if (resultCode == RESULT_OK) {
                        mFileId = (DriveId) data.getParcelableExtra(
                                OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                        Log.e("file id", mFileId.getResourceId() + "");
                        String url = "https://drive.google.com/open?id=" + mFileId.getResourceId();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void getReadPermission() {
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

    private void takePhoto(String description) {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy-hhmmss");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "IMG-" + format.format(new Date()));
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        Uri imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void systemAlertWindowOnShowRationale(PermissionRequest request) {
        showRationaleDialog(R.string.permission_read_external_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void systemAlertWindowOnPermissionDenied() {
        Toasty.error(this, getString(R.string.permission_read_external_denied), Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void systemAlertWindowOnNeverAskAgain() {
        Toasty.error(getApplicationContext(), getString(R.string.permission_read_external_ask_again), Toast.LENGTH_SHORT).show();
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

    public void addNavigationDrawer() {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_upload_all);

//create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(HomeActivity.this)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        long identifier = drawerItem.getIdentifier();
                        if (identifier == 2) {
                            Log.d(TAG, "222222");
                            uploadOnePhototoGDrive();
                        }
                        return false;
                    }
                })
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {

            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            Log.i(TAG, "google client is null");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            // disconnect Google Android Drive API connection.
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toasty.info(getApplicationContext(), "Connected to Google Drive account!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());

        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */
        try {

            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            e.printStackTrace();
        }
    }

    public void connectApi() {
        if (mGoogleApiClient == null) {
            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    //demo create a text file
    public void createFileOnDrive() {
        connectApi();
        fileOperation = true;
        // create new contents resource
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    /**
     * Create a new file and save it to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        final Bitmap image = mBitmapToSave;
        new Thread() {
            @Override
            public void run() {
                Drive.DriveApi.newDriveContents(mGoogleApiClient)
                        .setResultCallback(new ResultCallback<DriveContentsResult>() {
                            @Override
                            public void onResult(DriveContentsResult result) {
                                // If the operation was not successful, we cannot do anything
                                // and must
                                // fail.
                                if (!result.getStatus().isSuccess()) {
                                    Log.i(TAG, "Failed to create new contents.");
                                    return;
                                }
                                // Otherwise, we can write our data to the new contents.
                                Log.i(TAG, "New contents created.");
                                // Get an output stream for the contents.
                                OutputStream outputStream = result.getDriveContents().getOutputStream();
                                // Write the bitmap data from it.
                                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                                try {
                                    outputStream.write(bitmapStream.toByteArray());
                                } catch (IOException e1) {
                                    Log.i(TAG, "Unable to write file contents.");
                                }
                                // Create the initial metadata - MIME type and title.
                                // Note that the user will be able to change the title later.
                                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                        .setMimeType("image/jpeg").setTitle("Android Photo.png").build();
                                // Create an intent for the file chooser, and start it.
                                IntentSender intentSender = Drive.DriveApi
                                        .newCreateFileActivityBuilder()
                                        .setInitialMetadata(metadataChangeSet)
                                        .setInitialDriveContents(result.getDriveContents())
                                        .build(mGoogleApiClient);
                                try {
                                    startIntentSenderForResult(
                                            intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                                } catch (SendIntentException e) {
                                    Log.i(TAG, "Failed to launch file chooser.");
                                }
                            }
                        });
            }
        }.start();
    }

    public void uploadOnePhototoGDrive() {
        connectApi();
        //save bitmap with each image
        ArrayList<MediaEntry> list = AlbumTool.getAllListAlbum(this);
        mBitmapToSave = BitmapFactory.decodeFile(list.get(0).getPath());
        saveFileToDrive();
    }

    //demo create 1 file text type.
    public void onClickCreateFile(View view) {
        fileOperation = true;
        // create new contents resource
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    public void onClickOpenFile(View view) {
        fileOperation = false;

        // create new contents resource
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    /**
     * Open list of folder and file of the Google Drive
     */
    public void OpenFileFromGoogleDrive() {

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"text/plain", "text/html"})
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.w(TAG, "Unable to send intent", e);
        }
    }


    /**
     * This is Result result handler of Drive contents.
     * this callback method call CreateFileOnGoogleDrive() method
     * and also call OpenFileFromGoogleDrive() method, send intent onActivityResult() method to handle result.
     */
    final ResultCallback<DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {
                        if (fileOperation == true) {
                            CreateFileOnGoogleDrive(result);
                        } else {
                            OpenFileFromGoogleDrive();
                        }
                    }
                }
            };

    /**
     * Create a file in root folder using MetadataChangeSet object.
     *
     * @param result
     */
    public void CreateFileOnGoogleDrive(DriveContentsResult result) {


        final DriveContents driveContents = result.getDriveContents();

        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {
                // write content to DriveContents
                OutputStream outputStream = driveContents.getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);
                try {
                    writer.write("Hello hoang!");
                    writer.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("okokokok")
                        .setMimeType("text/plain")
                        .setStarred(true).build();

                // create a file in root folder
                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, driveContents)
                        .setResultCallback(fileCallback);
            }
        }.start();
    }

    /**
     * Handle result of Created file
     */
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (result.getStatus().isSuccess()) {
                        Toasty.success(getApplicationContext(), "file created: " + "" +
                                result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            };

}
