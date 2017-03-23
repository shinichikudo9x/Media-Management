package edu.fpt.prm.com.mediamanagement;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import entry.MediaEntry;
import es.dmoral.toasty.Toasty;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import tools.AlbumTool;
import tools.Tool;

import static tools.AlbumTool.getAllListAlbum;

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
    private DriveId mFileId;
    public DriveFile file;
    Animation fab_close, fab_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AlbumTool.getByDirectory(this,11);
        //get read permission
        HomeActivityPermissionsDispatcher.getReadPermissionWithCheck(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //Navigation Drawer

            addNavigationDrawer();

//        createFileOnDrive();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        recyclerView = (RecyclerView) findViewById(R.id.listItem);
        mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        ArrayList<MediaEntry> dataSet = getAllListAlbum(this);
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
        adapter.mDataset.clear();
        adapter.mDataset.addAll(AlbumTool.getAllListAlbum(this));
        adapter.notifyDataSetChanged();
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
            adapter.mDataset = getAllListAlbum(getBaseContext());
            adapter.notifyDataSetChanged();
        }
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
        final HashMap<String,Integer> listMap = AlbumTool.getListDirectory(this);
        //if you want to update the items at a later time it is recommended to keep it in a variable
        int count=0;
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(count++).withName(R.string.drawer_item_home);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(count++).withName(R.string.view_gdrive);
        DrawerBuilder drawerBuilder = new DrawerBuilder().withActivity(HomeActivity.this)
                .withToolbar(mToolbar);
        drawerBuilder.addDrawerItems(item1,new DividerDrawerItem());
        drawerBuilder.addDrawerItems(item2,new DividerDrawerItem());
        for(String s:listMap.keySet()){
            drawerBuilder.addDrawerItems(new SecondaryDrawerItem().withIdentifier(count++).withName(s));
        }
        final long number = count;

//create the drawer and remember the `Drawer` result object
        drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                long identifier = drawerItem.getIdentifier();
                if (identifier == 1) {
                    openGDrive();
                }
                if(identifier == 0) {
                    adapter.mDataset = AlbumTool.getAllListAlbum(getBaseContext());
                    adapter.notifyDataSetChanged();
                }
                for(long i=2;i<number;i++){
                    if(i == drawerItem.getIdentifier()){
                        adapter.mDataset = AlbumTool.getByDirectory(getBaseContext(), listMap.get(((SecondaryDrawerItem)drawerItem).getName().getText()));
                        if(adapter.mDataset != null)
                            adapter.notifyDataSetChanged();
                        else recreate();
                    }
                }

                return false;
            }
        });
        drawerBuilder.build();
//        Drawer result = new DrawerBuilder()
//                .withActivity(HomeActivity.this)
//                .withToolbar(mToolbar)
//                .addDrawerItems(
//                        item1,
//                        new DividerDrawerItem(),
//                        item2,
//                        new DividerDrawerItem(),
//
//                )
//                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
//                    @Override
//                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                        // do something with the clicked item :D
//                        long identifier = drawerItem.getIdentifier();
//                        if (identifier == 2) {
//                            openGDrive();
//                        }
//                        return false;
//                    }
//                })
//                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Tool.isInternetAvailable(this)){
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
        Toasty.error(getApplicationContext(), "Please check internet", Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Type file name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    ArrayList<MediaEntry> list = AlbumTool.searchByDescription(getApplicationContext(), query);
                    if (list != null && !list.isEmpty()) {
                        adapter.mDataset.clear();
                        adapter.mDataset.addAll(list);
                        adapter.notifyDataSetChanged();
                        Toasty.success(getApplicationContext(), "Found :"+list.size()+" item", Toast.LENGTH_LONG).show();
                    } else {
                        Toasty.error(getApplicationContext(), "Can't find any matched", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                adapter.mDataset.clear();
                adapter.mDataset.addAll(AlbumTool.getAllListAlbum(getApplicationContext()));
                adapter.notifyDataSetChanged();
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_user) {
            mGoogleApiClient = null;
            connectApi();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    open file in google drive
     */
    public void openGDrive() {
        if (mGoogleApiClient == null) {
            connectApi();
        }
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    final ResultCallback<DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {
                        OpenFileFromGoogleDrive();
                    }
                }
            };

    /**
     * Open list of folder and file of the Google Drive
     */
    public void OpenFileFromGoogleDrive() {
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"image/jpeg", "image/png"})
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
        } catch (SendIntentException e) {
            Log.w(TAG, "Unable to send intent", e);
        }
    }
}
