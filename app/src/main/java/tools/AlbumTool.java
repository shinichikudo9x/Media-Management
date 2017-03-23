package tools;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import entry.MediaEntry;

/**
 * Created by HuongLX on 3/12/2017.
 */

public class AlbumTool {
    public static Cursor cursor;
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
        cursor= context.getContentResolver().query(queryUri,
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
    private static ArrayList<MediaEntry> filter(Context context, @NonNull String where){
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
                + where +")";


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
    public static int setMetadata(Context context, int id, int type, String title, Long dateAdded, String description, String tag, String album, String artist){
        Uri queryUri = MediaStore.Files.getContentUri("external");

        String[] projection = {
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Video.VideoColumns.ALBUM,
                MediaStore.Video.VideoColumns.ARTIST,
                MediaStore.Video.VideoColumns.DESCRIPTION,
                MediaStore.Video.VideoColumns.TAGS
        };

        ContentValues values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATE_ADDED,dateAdded);
        values.put(MediaStore.Files.FileColumns.TITLE,title);
        values.put(MediaStore.Video.VideoColumns.ALBUM,album);
        values.put(MediaStore.Video.VideoColumns.ARTIST,artist);
        values.put(MediaStore.Video.VideoColumns.DESCRIPTION,description);
        values.put(MediaStore.Video.VideoColumns.TAGS,tag);
        String where = "("+MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO+") AND("
                + MediaStore.Files.FileColumns._ID +" = ?)";

        return context.getContentResolver().update(queryUri,values,where,new String[]{String.valueOf(id)});
    }
    public static ArrayList<MediaEntry> searchByDescription(Context context, String description){
        String where = MediaStore.Files.FileColumns.TITLE + " like '%"+description +"%'";
        return filter(context,where);
    }
    public static ArrayList<Integer> getListIndexDirectory(Context context){
        ArrayList<Integer> list = new ArrayList<>();
        String[] projection = {
                "DISTINCT "+ MediaStore.Files.FileColumns.PARENT
        };

// Return only video and image metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


        Uri queryUri = MediaStore.Files.getContentUri("external");

        Cursor cursor = context.getContentResolver().query(queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.PARENT + " ASC");  // Sort order.):
        while (cursor.moveToNext()){
            list.add(cursor.getInt(0));
        }
        return list;
    }
    public static HashMap<String,Integer> getListDirectory(Context context){
        HashMap<String,Integer> list = new HashMap<>();
        String[] projection = {
                "DISTINCT "+ MediaStore.Files.FileColumns.PARENT,
                MediaStore.Files.FileColumns.DATA
        };

// Return only video and image metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


        Uri queryUri = MediaStore.Files.getContentUri("external");

        Cursor cursor = context.getContentResolver().query(queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.PARENT + " ASC");
        int index=0;// Sort order.):
        List<Integer> indexs = getListIndexDirectory(context);
        while (cursor.moveToNext()){
            if(cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.PARENT)) == indexs.get(index)){
                File f = new File(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                list.put(f.getParentFile().getName(),indexs.get(index));
                index++;
                if(index>=indexs.size()) break;
            }
        }
        return list;
    }
    public static ArrayList<MediaEntry> getByDirectory(Context context, int index){
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
                + MediaStore.Files.FileColumns.PARENT +" = "+index+")";

        Uri queryUri = MediaStore.Files.getContentUri("external");

        cursor= context.getContentResolver().query(queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");  // Sort order.):
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
}
