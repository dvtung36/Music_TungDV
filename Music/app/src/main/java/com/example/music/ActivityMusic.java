package com.example.music;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
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

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.adapter.SongAdapter;
import com.example.music.fragment.AllSongsFragment;
import com.example.music.fragment.MediaPlaybackFragment;
import com.example.music.model.Song;
import com.example.music.service.MediaPlaybackService;
import com.example.music.service.SongManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ActivityMusic extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 5;
    public boolean isVertical;
    public MediaPlaybackService mMusicService;

    public MediaPlaybackService getMusicService() {
        return mMusicService;
    }

    public List<Song> getListSong() {
        return mListSong;
    }

    private List<Song> mListSong;

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

        }
    }

    @Override
    protected void onStart() {
        setService();
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMusicService != null) {
            unbindService(serviceConnection);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMusicService!=null) {
            mMusicService.cancelNotification();
          //  unbindService(serviceConnection);
        }
    }

    private void setService() {
        Intent intent = new Intent(this, MediaPlaybackService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) service;
            mMusicService = binder.getMusicService();
            mMusicService.setListSong(mListSong);
            getFragment();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicService = null;
        }
    };

    public void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.full);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name,
                R.string.app_name);

        if (drawer != null) {
            drawer.addDrawerListener(toggle);

        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        }
        mListSong = new ArrayList<>();
        SongManager.getSong(this, mListSong);   //set List song cho activity

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_MEDIA) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getFragment();                        //  Override  onRequestPermissionsResult() để nhận lại cuộc gọi (check permission)\
                Toast.makeText(this, "Permission Granted !", Toast.LENGTH_SHORT).show();
            } else {
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.full);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }


    public void getFragment() {
//        if (findViewById(R.id.contentAllSongs) != null)
//            isVertical = true;
//        else
//            isVertical = false;

        int mOrientation=getResources().getConfiguration().orientation;

        if(mOrientation== Configuration.ORIENTATION_PORTRAIT){
            isVertical = true;
        }
        else
            isVertical = false;

        FragmentManager manager = getSupportFragmentManager();

        if (isVertical) {
            AllSongsFragment allSongsFragment = new AllSongsFragment();
            allSongsFragment.setMusicService(mMusicService);
            allSongsFragment.setVertical(isVertical);
            allSongsFragment.setListSong(mListSong);
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.content, allSongsFragment);               //get fragment AllSongsFragment vào activity main
            fragmentTransaction.commit();

        } else {
            AllSongsFragment allSongsFragment= new AllSongsFragment();
            allSongsFragment.setMusicService(mMusicService);
            allSongsFragment.setVertical(!isVertical);
            allSongsFragment.setListSong(mListSong);
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.content,allSongsFragment);
            fragmentTransaction.commit();
            MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
            mediaPlaybackFragment.setVertical(!isVertical);
            mediaPlaybackFragment.setMusicService(mMusicService);
            mediaPlaybackFragment.setSongList(mListSong);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.full);
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_camera:
                // Handle the camera import action (for now display a toast).
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this, "Click1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_gallery:
                // Handle the gallery action (for now display a toast).
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this, "Click2", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_slideshow:
                // Handle the slideshow action (for now display a toast).
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this, "Click3", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_send:
                // Handle the send action (for now display a toast).
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this, "Click4", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
}