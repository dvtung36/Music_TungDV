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
    public static final String ID_CHANNEL = "999";
    private static final CharSequence NANME_CHANNEL ="App_Music" ;

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

//        Log.d("XXX","aaaaaaa");
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(ID_CHANNEL,NANME_CHANNEL,NotificationManager.IMPORTANCE_LOW);
//            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            manager.createNotificationChannel(channel);
//
//
//            RemoteViews notification_small = new RemoteViews(getPackageName(), R.layout.notifiation_small);
//            RemoteViews notification_big = new RemoteViews(getPackageName(), R.layout.notifiation_big);
//
//
//            if (notification_small!=null){
//                //ImageView imageView= notification_small.setOnClickFillInIntent(R.id.iconPrevious,new Intent());
//            }
//            NotificationCompat.Builder builder= new NotificationCompat.Builder(this, ID_CHANNEL)
//                    .setSmallIcon(R.drawable.ic_list_music)
//                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                    .setCustomContentView(notification_small)
//                    .setCustomBigContentView(notification_big);
//            manager.notify(10,builder.build());
//        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }




}
