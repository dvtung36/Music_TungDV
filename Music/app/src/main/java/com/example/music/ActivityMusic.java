package com.example.music;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.adapter.SongAdapter;
import com.example.music.fragment.AllSongsFragment;
import com.example.music.fragment.MediaPlaybackFragment;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

public class ActivityMusic extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 5;
    private Toolbar mToolbar;
    public boolean isVertical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initView();

        if (savedInstanceState != null) {
//            savedInstanceState.getChar(sahd)
        }
        /* check permission*/
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);

        } else {
            getFragment();
        }
    }

    public void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_MEDIA) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getFragment();                       //  Override  onRequestPermissionsResult() để nhận lại cuộc gọi (check permission)\
                Toast.makeText(this, "Permission Granted !", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Permission Denied !", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);      // create button search in action bar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:                                  //set click for button search in action bar
                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        getSupportActionBar().show();                //setActionBar
        super.onBackPressed();
    }


    public void getFragment() {


        if (findViewById(R.id.contentAllSongs) != null)
            isVertical = true;
        else
            isVertical = false;

        FragmentManager manager = getSupportFragmentManager();

        if (isVertical) {
            AllSongsFragment allSongsFragment = new AllSongsFragment();
            allSongsFragment.setVertical(isVertical);
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.content, allSongsFragment);               //get fragment AllSongsFragment vào activity main
            fragmentTransaction.commit();
        } else {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.content, new AllSongsFragment());
            fragmentTransaction.commit();
            MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
            mediaPlaybackFragment.setVertical(isVertical);
            FragmentTransaction mPlayFragment = manager.beginTransaction();
            mPlayFragment.replace(R.id.fragment_media, new MediaPlaybackFragment());
            mPlayFragment.commit();

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // truoc xoay luu du lieu
//        outState.putInt();
    }
}