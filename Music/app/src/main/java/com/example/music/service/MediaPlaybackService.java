package com.example.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.music.ActivityMusic;
import com.example.music.R;
import com.example.music.fragment.MediaPlaybackFragment;

public class MediaPlaybackService extends Service {

    private SongManager mSongManager;
    private MusicBinder mBinder = new MusicBinder();

    public SongManager getMediaManager() {
        return mSongManager;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MusicBinder extends Binder {
        public MediaPlaybackService getMusicService() {
            return MediaPlaybackService.this;
        }
    }

    @Override

    public void onCreate() {
        super.onCreate();
        mSongManager = new SongManager(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }




}