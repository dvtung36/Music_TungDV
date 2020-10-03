package com.example.music.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.security.Provider;

public class MusicProvider extends ContentProvider {


    private MyDatabaseHelper dbHelper;

    private static final int URI_ALL_ITEMS_CODE = 1;
    private static final int URI_ONE_ITEM_CODE = 2;
    private static final String AUTHORITY = "com.example.music.database.MusicProvider";

    // create content URIs from the authority by appending path to database table
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/MusicDB");

    // a content URI pattern matches content URIs using wildcard characters:
    // *: Matches a string of any valid characters of any length.
    // #: Matches a string of numeric characters of any length.
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "MusicDB", URI_ALL_ITEMS_CODE);
        uriMatcher.addURI(AUTHORITY, "MusicDB/#", URI_ONE_ITEM_CODE);
    }

    public MusicProvider() {
    }

    // system calls onCreate() when it starts up the provider.
    @Override
    public boolean onCreate() {
        // get access to the database helper
        dbHelper = new MyDatabaseHelper(getContext());
        return false;
    }

    //Return the MIME type corresponding to a content URI
    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                return "vnd.android.cursor.dir/vnd.com.example.music.database.MusicProvider.MusicDBs";
            case URI_ONE_ITEM_CODE:
                return "vnd.android.cursor.item/vnd.com.example.music.database.MusicProvider.MusicDBs";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                //do nothing
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        long id = db.insert(MusicDatabase.SQL_LITE_TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI + "/" + id);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MusicDatabase.SQL_LITE_TABLE);

        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                //do nothing
                break;
            case URI_ONE_ITEM_CODE:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(MusicDatabase.ID_PROVIDER + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        return cursor;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                //do nothing
                break;
            case URI_ONE_ITEM_CODE:
                String id = uri.getPathSegments().get(1);
                selection = MusicDatabase.ID_PROVIDER + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int deleteCount = db.delete(MusicDatabase.SQL_LITE_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                //do nothing
                break;
            case URI_ONE_ITEM_CODE:
                String id = uri.getPathSegments().get(1);
                selection = MusicDatabase.ID_PROVIDER + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int updateCount = db.update(MusicDatabase.SQL_LITE_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}
