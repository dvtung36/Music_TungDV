package com.example.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.music.ActivityMusic;
import com.example.music.R;
import com.example.music.fragment.MediaPlaybackFragment;
import com.example.music.model.Song;

public class MediaPlaybackService extends Service {
    public static final String ID_CHANNEL = "999";
    private static final CharSequence NANME_CHANNEL = "App_Music";
    private static final String MUSIC_SERVICE_ACTION_PAUSE = "music_service_action_pause";
    private static final String MUSIC_SERVICE_ACTION_PLAY = "music_service_action_play";
    private static final String MUSIC_SERVICE_ACTION_NEXT = "music_service_action_next";
    private static final String MUSIC_SERVICE_ACTION_PREV = "music_service_action_prev";
    private static final String MUSIC_SERVICE_ACTION_STOP = "music_service_action_stop";

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MediaService",)
        switch (intent.getAction()) {

            case MUSIC_SERVICE_ACTION_NEXT:
                getMediaManager().nextSong(getMediaManager().getmCurrentPlay());

                int pos= getMediaManager().getmCurrentPlay()+1;
                Song song= getMediaManager().getmListSong().get(pos);
                createChannel();
                createNotification(getApplicationContext(),song,pos);
                break;
            case MUSIC_SERVICE_ACTION_PREV:
                getMediaManager().previousSong(getMediaManager().getmCurrentPlay());

                int pos1= getMediaManager().getmCurrentPlay()-1;
                Song song1= getMediaManager().getmListSong().get(pos1);
                createChannel();
                createNotification(getApplicationContext(),song1,pos1);
                break;

            default:
                Log.d("XXXX", "onStartCommand: default");
                break;
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void createChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID_CHANNEL, NANME_CHANNEL, NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    public void createNotification(Context context, Song song, int pos) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            Intent intentNextMedia = new Intent("Next_Media");
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, intentNextMedia, 0); //getBroadcast

            Intent intent = new Intent(context, ActivityMusic.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent pauseIntent = new Intent(this, MediaPlaybackService.class).setAction(MUSIC_SERVICE_ACTION_PAUSE);
            PendingIntent pausePendingIntent = PendingIntent.getService(this,
                    0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent nextIntent = new Intent(this, MediaPlaybackService.class).setAction(MUSIC_SERVICE_ACTION_NEXT);
            PendingIntent nextPendingIntent = PendingIntent.getService(this,
                    0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent prevIntent = new Intent(this, MediaPlaybackService.class).setAction(MUSIC_SERVICE_ACTION_PREV);
            PendingIntent prevPendingIntent = PendingIntent.getService(this,
                    0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent stopIntent = new Intent(this, MediaPlaybackService.class).setAction(MUSIC_SERVICE_ACTION_STOP);
            PendingIntent stopPendingIntent = PendingIntent.getService(this,
                    0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            RemoteViews notification_small = new RemoteViews(context.getPackageName(), R.layout.notifiation_small);
            RemoteViews notification_big = new RemoteViews(context.getPackageName(), R.layout.notifiation_big);


            Bitmap bitmap =  getAlbumArt(song.getmSongArt());
            if (bitmap == null)
            {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_nct);
            }


            //set data Notification
            notification_small.setImageViewBitmap(R.id.image_music_notification, bitmap);
            notification_big.setTextViewText(R.id.tv_song_name_notification, song.getmSongName());
            notification_big.setTextViewText(R.id.tv_song_author_notification, song.getmSongAuthor());
            notification_big.setImageViewBitmap(R.id.image_music_notification,bitmap);

            //set onClick button Notification
            notification_small.setOnClickPendingIntent(R.id.icon_next_notification, pendingSwitchIntent);

            notification_small.setOnClickPendingIntent(R.id.icon_next_notification, nextPendingIntent);
            notification_small.setOnClickPendingIntent(R.id.icon_previous_notification, prevPendingIntent);
            notification_big.setOnClickPendingIntent(R.id.icon_next_notification, nextPendingIntent);
            notification_big.setOnClickPendingIntent(R.id.icon_previous_notification, prevPendingIntent);

            //build Notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID_CHANNEL)
                    .setSmallIcon(R.drawable.ic_list_music)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notification_small)
                    .setContentIntent(contentIntent)
                    .setCustomBigContentView(notification_big);
            notificationManagerCompat.notify(10, builder.build());


        }
    }
    public static Bitmap getAlbumArt(String path){
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte [] data=mediaMetadataRetriever.getEmbeddedPicture();
        if(data!=null){
            return BitmapFactory.decodeByteArray(data, 0 , data.length);
        }
        return null;
    }




}
