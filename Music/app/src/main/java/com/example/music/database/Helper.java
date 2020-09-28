package com.example.music.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.music.model.Song;

public class Helper {
    public static final String TAG = "ActivityMusic";

    public static void getAllSongs(Context context) {
        int pos = 0;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA
        };
        String[] projectionDB = {
                MusicDatabase.ID,
                MusicDatabase.ID_PROVIDER,
                MusicDatabase.TITLE,
                MusicDatabase.ARTIST,
                MusicDatabase.DURATION,
                MusicDatabase.DATA,
                MusicDatabase.IS_FAVORITE,
                MusicDatabase.COUNT_OF_PLAY
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                int trackNumber = cursor.getInt(1);
                String duration = cursor.getString(2);
                String title = cursor.getString(3);
                String artistName = cursor.getString(4);
                String composer = cursor.getString(5);
                String albumName = cursor.getString(6);
                String data = cursor.getString(7);

             //   Song song = new Song(pos, id, title, artistName, data, duration);
                Cursor cursorDB = context.getContentResolver().query(MusicProvider.CONTENT_URI, projectionDB, null, null, null);
                if (cursorDB.moveToPosition(pos)) {
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MusicDatabase.ID_PROVIDER, id);
                    values.put(MusicDatabase.TITLE, title);
                    values.put(MusicDatabase.ARTIST, artistName);
                    values.put(MusicDatabase.DURATION, duration);
                    values.put(MusicDatabase.DATA, data);
                    values.put(MusicDatabase.IS_FAVORITE, 0);
                    values.put(MusicDatabase.COUNT_OF_PLAY, 0);
                    // insert a record
                    Log.d(TAG, "getAllSongs: " + id + " " + data);
                    context.getContentResolver().insert(MusicProvider.CONTENT_URI, values);
                }
                pos++;
            }
            cursor.close();
        }
    }
}
