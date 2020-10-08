package com.example.music;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import com.example.music.fragment.BaseSongsFragment;
import com.example.music.fragment.FavoriteSongsFragment;
import com.example.music.fragment.MediaPlaybackFragment;
import com.example.music.model.Song;
import com.example.music.service.MediaPlaybackService;
import com.example.music.service.SongManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ActivityMusic extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSongsFragment.IUpdateMediaWhenAllSongClickItem {

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 5;
    public boolean isVertical;
    public MediaPlaybackService mMusicService;
    private int Repeat = 11, Shuffle = 12;
    MediaPlaybackFragment mMediaPlaybackFragment;
    FragmentManager manager = getSupportFragmentManager();
    private BaseSongsFragment baseSongsFragment;
    private boolean isFavorite = false;

    public static final int REPEAT_ALL = 11;
    public static final int NORMAL = 12;
    int permissionCheck;

    public MediaPlaybackService getMusicService() {
        return mMusicService;
    }


    public List<Song> getListSong() {
        return mListSong;
    }

    private List<Song> mListSong;


    public SongAdapter getSongAdapter() {
        return mSongAdapter;
    }

    private SongAdapter mSongAdapter;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ActivityCheck", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initView();
        sharedPreferences = getSharedPreferences("DATA_PLAY_MEDIA", MODE_PRIVATE);

        Repeat = sharedPreferences.getInt("DATA_REPEAT", REPEAT_ALL);
        Shuffle = sharedPreferences.getInt("DATA_SHUFFLE", NORMAL);


        if (savedInstanceState != null) {
            Repeat = savedInstanceState.getInt("REPEAT");
            Shuffle = savedInstanceState.getInt("SHUFFLE");
            isFavorite = savedInstanceState.getBoolean("Favorite");
        }

        /* check permission*/
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "" + "check permissionCheck");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);

        } else {
            Log.d("TAG", "serviceConnection" + "khi dã co quyen ok");
            getData();
//            getFragment();
        }

    }

    public void getData() {
        mListSong = new ArrayList<>();
        SongManager.getSong(this, mListSong);   //set List song cho activity
        mSongAdapter = new SongAdapter(this, mListSong);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_MEDIA) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //  Override  onRequestPermissionsResult() để nhận lại cuộc gọi (check permission)\
                getData();
                mMusicService.setListSong(mListSong);
                mMusicService.setRepeat(Repeat);
                mMusicService.setShuffle(Shuffle);
                getFragment();
                Toast.makeText(this, "Permission Granted !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied !", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setService();
        getSupportActionBar().show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMusicService != null) {
            unbindService(serviceConnection);
        }
        baseSongsFragment.saveData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMusicService != null) {
            mMusicService.cancelNotification();

        }
        Log.d("ActivityOnDestroy", "onDestroy");

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    private void setService() {
        Intent playIntent = new Intent(ActivityMusic.this, MediaPlaybackService.class);
        playIntent.setAction("");
        startService(playIntent);
        bindService(playIntent, serviceConnection, BIND_AUTO_CREATE);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) service;
            mMusicService = binder.getMusicService();
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                mMusicService.setListSong(mListSong);
                mMusicService.setRepeat(Repeat);
                mMusicService.setShuffle(Shuffle);
                Log.d("ActivityCheck", "service  ở onServiceConnected= " + mMusicService);
                getFragment();
            }
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
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);        //show button navigationView
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        }

    }


    @Override
    public void onBackPressed() {
        getSupportActionBar().show();                //setActionBar
        super.onBackPressed();

    }

    public void getFragment() {
        Log.d("sdhgfchgsdf", "getFragment: ");
        int mOrientation = getResources().getConfiguration().orientation;

        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            isVertical = true;
        }
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            isVertical = false;
        }
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {


            mMusicService.setIUpdateUI(baseSongsFragment);
            mMusicService.setIUpdateAllSongWhenAutoNext(baseSongsFragment);

        }

        if (isVertical) {

            if (!isFavorite) {
                Log.d("isFavorite", "" + isFavorite);
                baseSongsFragment = new AllSongsFragment();
                getSupportActionBar().setTitle("Music");
            } else {
                Log.d("isFavorite", "done" + isFavorite);
                baseSongsFragment = new FavoriteSongsFragment();
                getSupportActionBar().setTitle("Favorite Songs");
            }

            baseSongsFragment.setVertical(isVertical);
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.content, baseSongsFragment);               //get fragment AllSongsFragment vào activity main
            fragmentTransaction.commit();

        } else {
            if (isFavorite) {
                baseSongsFragment = new FavoriteSongsFragment();
                getSupportActionBar().setTitle("Favorite Songs");
            } else {
                baseSongsFragment = new AllSongsFragment();
                getSupportActionBar().setTitle("Music");

            }

            baseSongsFragment.setVertical(isVertical);
            mMusicService.setIUpdateUI(baseSongsFragment);
            mMusicService.setIUpdateUI(baseSongsFragment);

            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.content, baseSongsFragment);               //get fragment AllSongsFragment vào activity main
            fragmentTransaction.commit();

            mMediaPlaybackFragment = new MediaPlaybackFragment();
            mMediaPlaybackFragment.setIUpdateAllSong(baseSongsFragment);
            mMediaPlaybackFragment.setIUpdateAllSongWhenPauseMedia(baseSongsFragment);
            mMediaPlaybackFragment.setIUpdateAllSongWhenPlayMedia(baseSongsFragment);

            mMediaPlaybackFragment.setVertical(isVertical);
            FragmentTransaction mPlayFragment = manager.beginTransaction();
            mPlayFragment.replace(R.id.fragment_media, mMediaPlaybackFragment);
            mPlayFragment.commit();

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // truoc xoay luu du lieu
        outState.putInt("REPEAT", mMusicService.isRepeat());
        outState.putInt("SHUFFLE", mMusicService.isShuffle());
        outState.putBoolean("Favorite", isFavorite);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.full);

        switch (item.getItemId()) {
            case R.id.nav_listen_now:
                isFavorite = false;
                drawer.closeDrawer(GravityCompat.START);
                getSupportActionBar().setTitle("Music");
                getSupportActionBar().show();
                Log.d("ActivityCheck", "doc");
                baseSongsFragment = new AllSongsFragment();
                baseSongsFragment.setVertical(isVertical);
                baseSongsFragment.setFavorite(isFavorite);
                getMusicService().setIsFavorite(isFavorite);
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.replace(R.id.content, baseSongsFragment);
                fragmentTransaction.commit();


                Toast.makeText(this, "listen now", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_favorite_songs:
                // Handle the gallery action (for now display a toast).
                drawer.closeDrawer(GravityCompat.START);
                isFavorite = true;
                Log.d("ActivityCheck", "doc");
                getSupportActionBar().setTitle("Favorite Songs");
                getSupportActionBar().show();
                baseSongsFragment = new FavoriteSongsFragment();
                baseSongsFragment.setVertical(isVertical);
                baseSongsFragment.setFavorite(isFavorite);
                getMusicService().setIsFavorite(isFavorite);
                FragmentTransaction fragmentTransaction1 = manager.beginTransaction();
                fragmentTransaction1.replace(R.id.content, baseSongsFragment);               //get fragment AllSongsFragment vào activity main
                fragmentTransaction1.commit();
                Toast.makeText(this, "favorite songs", Toast.LENGTH_SHORT).show();


                return true;
            case R.id.nav_setting:
                // Handle the slideshow action (for now display a toast).
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_help:
                // Handle the send action (for now display a toast).
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    //call back media
    @Override
    public void UpdateMediaWhenAllSongClickItem(int pos) {
        if (!isVertical) {
            mMediaPlaybackFragment.updateMediaWhenClickItem(pos);
        }

    }
}