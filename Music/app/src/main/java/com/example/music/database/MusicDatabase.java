package com.example.music.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MusicDatabase {
    public static final String ID = "_id";
    public static final String ID_PROVIDER = "is_provider";
    public static final String TITLE = "song_title";
    public static final String ARTIST = "song_artist";
    public static final String DATA = "song_data";
    public static final String DURATION = "song_duration";
    public static final String IS_FAVORITE = "is_favorite";
    public static final String COUNT_OF_PLAY = "count_of_play";

    private static final String LOG_TAG = "MusicDB";
    public static final String SQL_LITE_TABLE = "MusicDB";


    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQL_LITE_TABLE + " (" +
                    ID + " integer PRIMARY KEY autoincrement," +
                    ID_PROVIDER + "," +
                    TITLE + "," +
                    ARTIST + "," +
                    DURATION + "," +
                    DATA + "," +
                    IS_FAVORITE + "," +
                    COUNT_OF_PLAY + ");" ;

    public static void onCreate(SQLiteDatabase db) {
        Log.w(LOG_TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + SQL_LITE_TABLE);
        onCreate(db);
    }

}
