package com.example.music.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.music.R;
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
        PopupMenu popup = new PopupMenu(v.getContext(), v);             //gán menu_popup  khi click vào các option
        // Inflate the Popup using XML file.
        popup.getMenuInflater().inflate(R.menu.menu_popup_favorite, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                             @Override
                                             public boolean onMenuItemClick(MenuItem item) {                      //setClick cho option menu
                                                 Toast.makeText(getActivity(),
                                                         "item Click", Toast.LENGTH_SHORT).show();
                                                 return false;

                                             }
                                         }
        );
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
}