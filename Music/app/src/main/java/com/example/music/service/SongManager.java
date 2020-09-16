package com.example.music.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


import com.example.music.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongManager {

    /* Lấy nhạc trong db*/

    public static void getSong(Context context, List<Song> mListSong) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri songUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = musicResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songName = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songTime = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);           //Lấy Nhạc trong Local
            int songAuthor = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songArt = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                long currentId = songCursor.getLong(songID);
                String currentName = songCursor.getString(songName);
                String currentTime = songCursor.getString(songTime);
                String currentAuthor = songCursor.getString(songAuthor);
                String currentArt = songCursor.getString(songArt);
                mListSong.add(new Song(currentId, currentName, currentTime, currentAuthor, currentArt, false,false));
            } while (songCursor.moveToNext());
            for (int i = 0; i < mListSong.size(); i++) {
                for (int j = i + 1; j < mListSong.size(); j++) {
                    if (mListSong.get(i).getmSongName().compareTo(mListSong.get(j).getmSongName()) > 0) {
                        Collections.swap(mListSong, i, j);
                    }

                }
            }
        }


    }
}
