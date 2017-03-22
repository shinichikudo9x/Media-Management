package tools;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import entry.MediaEntry;

/**
 * Created by HuongLX on 3/12/2017.
 */

public class AlbumTool {

    public static ArrayList<MediaEntry> getAllListAlbum(Context context){
        ArrayList<MediaEntry> list = new ArrayList<>();
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE,
                MediaStore.Video.VideoColumns.ALBUM,
                MediaStore.Video.VideoColumns.ARTIST,
                MediaStore.Video.VideoColumns.DESCRIPTION,
                MediaStore.Video.VideoColumns.LATITUDE,
                MediaStore.Video.VideoColumns.LONGITUDE,
                MediaStore.Video.VideoColumns.TAGS
        };

// Return only video and image metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

//        CursorLoader cursorLoader = new CursorLoader(
//                context,
//                queryUri,
//                projection,
//                selection,
//                null, // Selection args (none).
//                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
//        );
//
//        Cursor cursor = cursorLoader.loadInBackground();
        Cursor cursor = context.getContentResolver().query(queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");  // Sort order.):
        String lastDate = "";
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            Long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
            String latitude="",longtitude="",album="",artist="",tag="",description="";
            int type = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
            if(type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                latitude = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE));
                longtitude = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE));
            } else
            if(type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO){
                latitude = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LATITUDE));
                longtitude = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LONGITUDE));
                album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ALBUM));
                artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ARTIST));
                tag = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TAGS));
                description = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DESCRIPTION));
            }

            MediaEntry media = new MediaEntry(id, path, title, dateAdded, latitude, longtitude, description, tag, album, artist, type);
            list.add(media);
        }
        cursor.close();
        return list.isEmpty()?null:list;
    }
    public static ArrayList<MediaEntry> filter(Context context, @NonNull String query){
        ArrayList<MediaEntry> list = new ArrayList<>();
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE,
                MediaStore.Video.VideoColumns.ALBUM,
                MediaStore.Video.VideoColumns.ARTIST,
                MediaStore.Video.VideoColumns.DESCRIPTION,
                MediaStore.Video.VideoColumns.LATITUDE,
                MediaStore.Video.VideoColumns.LONGITUDE,
                MediaStore.Video.VideoColumns.TAGS
        };

// Return only video and image metadata.
        String selection = "("+MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO+") AND("
                + query +")";


        Uri queryUri = MediaStore.Files.getContentUri("external");

//        CursorLoader cursorLoader = new CursorLoader(
//                context,
//                queryUri,
//                projection,
//                selection,
//                null, // Selection args (none).
//                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
//        );
//
//        Cursor cursor = cursorLoader.loadInBackground();
        Cursor cursor = context.getContentResolver().query(queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");  // Sort order.):
        String lastDate ="";
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            Long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
            String latitude="",longtitude="",album="",artist="",tag="",description="";
            int type = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
            if(type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                latitude = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE));
                longtitude = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE));
            } else
            if(type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO){
                latitude = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LATITUDE));
                longtitude = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.LONGITUDE));
                album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ALBUM));
                artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ARTIST));
                tag = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TAGS));
                description = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DESCRIPTION));
            }

            MediaEntry media = new MediaEntry(id, path, title, dateAdded, latitude, longtitude, description, tag, album, artist, type);
            list.add(media);
        }
        cursor.close();
        return list.isEmpty()?null:list;
    }
    public static int deleteById(Context context, int id){
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };

// Return only video and image metadata.
        String where = "("+MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO+") AND("
                + MediaStore.Files.FileColumns._ID +" = "+id+")";
        int idCol = context.getContentResolver().delete(queryUri,where,null);

        return idCol;
    }
}
