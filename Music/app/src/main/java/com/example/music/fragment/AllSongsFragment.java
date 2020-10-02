package com.example.music.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.music.ActivityMusic;
import com.example.music.R;
import com.example.music.adapter.SongAdapter;
import com.example.music.database.MusicDatabase;
import com.example.music.database.MusicProvider;
import com.example.music.model.Song;
import com.example.music.service.MediaPlaybackService;
import com.example.music.service.SongManager;

import java.util.ArrayList;
import java.util.List;

public class AllSongsFragment extends BaseSongsFragment {


    public AllSongsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void updatePopupMenu(View v, final Song song, int pos) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);             //gán menu_popup  khi click vào các option
          int id = (int) song.getmSongID();
          final Uri uri = Uri.parse(MusicProvider.CONTENT_URI + "/" + id);
          final Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                             @Override
                                             public boolean onMenuItemClick(MenuItem item) {                      //setClick cho option menu
                                                 ContentValues values = new ContentValues();
                                                 if (item.getItemId() == R.id.action_add_songs) {
                                                     values.put(MusicDatabase.ID_PROVIDER, song.getSongIDProvider());
                                                     values.put(MusicDatabase.ID, song.getmSongID());
                                                     values.put(MusicDatabase.TITLE, song.getmSongName());
                                                     values.put(MusicDatabase.ARTIST, song.getmSongAuthor());
                                                     values.put(MusicDatabase.DATA, song.getmSongArt());
                                                     values.put(MusicDatabase.DURATION, song.getmSongTime());
                                                     values.put(MusicDatabase.IS_FAVORITE, 2);
                                                     getContext().getContentResolver().insert(MusicProvider.CONTENT_URI, values);
                                                     Toast.makeText(getActivity().getApplicationContext(), "Add Favorite", Toast.LENGTH_SHORT).show();
                                                 } else if (item.getItemId() == R.id.action_remove_songs) {
                                                     values.put(MusicDatabase.IS_FAVORITE, 0);

                                                     Toast.makeText(getActivity().getApplicationContext(), "Remove Favorite", Toast.LENGTH_SHORT).show();
                                                 }

                                                 return false;

                                             }
                                         }
        );

        popup.show();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    SongManager.getSong(getContext(),mListSong);


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void updateAdapter() {
        mListSong = new ArrayList<>();
        SongManager.getSong(getContext(), mListSong);   //set List song cho activity
        mSongAdapter = new SongAdapter(getContext(), mListSong);
    }

}
