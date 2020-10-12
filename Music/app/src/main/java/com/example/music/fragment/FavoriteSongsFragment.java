package com.example.music.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.music.ActivityMusic;
import com.example.music.R;
import com.example.music.database.MusicDatabase;
import com.example.music.database.MusicProvider;
import com.example.music.model.Song;
import com.example.music.service.SongManager;


public class FavoriteSongsFragment extends BaseSongsFragment {

    public FavoriteSongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    protected void updatePopupMenu(View v, Song song, int pos) {
        final int id = (int) song.getmSongID();
        final Uri uri = Uri.parse(MusicProvider.CONTENT_URI + "/" + id);
        final Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);

        /*gán menu_popup  khi click vào các option */
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.menu_popup_favorite, popup.getMenu());

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_remove_songs) {

                    if (cursor != null) {
                        getContext().getContentResolver().delete(MusicProvider.CONTENT_URI, MusicDatabase.ID + "=" + id, null);
                        Log.d("ClickDelete", "" + mListSong.size());
                        update();
                        Toast.makeText(getActivity().getApplicationContext(), "Removed favorites list", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }

        });
    }

    public void update() {
        mListSong = SongManager.getFavorAllSongs(getContext());
        Log.d("ClickDelete", "onCreate: " + mListSong.size());
        mSongAdapter.setListSong(mListSong);
        if (mListSong.size() <= 0) {
            mTextView.setText(R.string.favorite_null);
            mTextView.setVisibility(View.VISIBLE);
        } else mTextView.setVisibility(View.INVISIBLE);
        mSongAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    protected ActivityMusic getActivityMusic() {
        if (getActivity() instanceof ActivityMusic) {
            return (ActivityMusic) getActivity();
        }
        return null;
    }


    @Override
    public void updateAdapter() {
        mListSong = SongManager.getFavorAllSongs(getContext());
        Log.d("SDd", "onCreate: " + mListSong.size());

        mSongAdapter.setMusicService(mMusicService);
        if (mListSong.size() <= 0) {
            mTextView.setText(R.string.favorite_null);
            mTextView.setVisibility(View.VISIBLE);
        } else mTextView.setVisibility(View.INVISIBLE);

    }
}