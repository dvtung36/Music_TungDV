package com.example.music.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


import com.example.music.database.MusicDatabase;
import com.example.music.database.MusicProvider;
import com.example.music.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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
                mListSong.add(new Song(currentId, 0,currentName, currentTime, currentAuthor, currentArt, false,false));
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

    public static List<Song> getFavorAllSongs(Context context) {
        // get data in SQL lite
        List<Song> songListFavor = new ArrayList<>();
        int posFavor = 0;
        Uri uri =  Uri.parse(String.valueOf(MusicProvider.CONTENT_URI));;
        String[] projection = {
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
                int id_provider = cursor.getInt(1);
                String title = cursor.getString(2);
                String artistName = cursor.getString(3);
                String duration = cursor.getString(4);
                String data = cursor.getString(5);
                int is_fravorite = cursor.getInt(6);
                String count_of_play = cursor.getString(7);
                if (is_fravorite == 2 ) {
                    Song song = new Song(id,id_provider,title, duration,artistName,data,false,false);
                    songListFavor.add(song);
                }
                posFavor++;
            }
            cursor.close();
        }
        return  songListFavor;
    }
}
