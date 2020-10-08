package com.example.music.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
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
import com.example.music.model.Song;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MediaPlaybackService extends Service {
    public static final String ID_CHANNEL = "999";
    private static final CharSequence NAME_CHANNEL = "App_Music";
    private static final String MUSIC_SERVICE_ACTION_PAUSE = "music_service_action_pause";
    private static final String MUSIC_SERVICE_ACTION_PLAY = "music_service_action_play";
    private static final String MUSIC_SERVICE_ACTION_NEXT = "music_service_action_next";
    private static final String MUSIC_SERVICE_ACTION_PREV = "music_service_action_prev";
    private static final String MUSIC_SERVICE_ACTION_STOP = "music_service_action_stop";
    private static final int NOTIFICATION_ID = 10;
    public static final int REPEAT = 10;
    public static final int REPEAT_ALL = 11;
    public static final int NORMAL = 12;
    public static final int SHUFFLE = 13;
    private boolean isFist = true;

    public long getIdPlay() {
        return mIdPlay;
    }

    public void setIdPlay(long mIdPlay) {
        this.mIdPlay = mIdPlay;
    }

    private long mIdPlay;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    private boolean isFavorite=false;

    SharedPreferences sharedPreferencesCurrent;

    public boolean isFist() {
        return isFist;
    }




    public boolean isResume() {
        return isResume;
    }

    private boolean isResume = false;

    private MusicBinder mBinder = new MusicBinder();
    private MediaPlayer mPlayer;


    private boolean isStatusPlay = false;
    private NotificationManagerCompat notificationManagerCompat;


    public void setCurrentPlay(int mCurrentPlay) {
        this.mCurrentPlay = mCurrentPlay;
    }

    private int mCurrentPlay = -1;
    private int isRepeat;
    private int isShuffle;

    public int isShuffle() {
        return isShuffle;
    }

    public void setShuffle(int shuffle) {
        isShuffle = shuffle;
    }


    public int isRepeat() {
        return isRepeat;
    }

    public void setRepeat(int repeat) {
        isRepeat = repeat;
    }


    public boolean isStatusPlay() {
        return isStatusPlay;
    }

    public int getCurrentPlay() {
        return mCurrentPlay;
    }


    public void setListSong(List<Song> mListSong) {
        this.mListSong = mListSong;
    }

    public List<Song> getListSong() {
        return mListSong;
    }

    private List<Song> mListSong;


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
        initMediaPlayer();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent.getAction() == MUSIC_SERVICE_ACTION_NEXT) {
            nextSong(getCurrentPlay());
            int pos = getCurrentPlay();
            Song song = mListSong.get(pos);
            createChannel();
            createNotification(getApplicationContext(), song, true);
            iNextAndPreNotification.updateNotificationWhenNextAndPre(pos);
        }
        if (intent.getAction() == MUSIC_SERVICE_ACTION_PREV) {

            previousSong(getCurrentPlay());

            int pos1 = getCurrentPlay();
            Song song1 = mListSong.get(pos1);
            createChannel();
            createNotification(getApplicationContext(), song1, true);
            iNextAndPreNotification.updateNotificationWhenNextAndPre(pos1);

        }
        if (intent.getAction() == MUSIC_SERVICE_ACTION_PAUSE) {


            pauseSong();

            int pos = getCurrentPlay();
            Song song = mListSong.get(pos);

            createChannel();
            createNotification(getApplicationContext(), song, false);
            iPauseNotification.updateNotificationWhenPause(pos);

        }
        if (intent.getAction() == MUSIC_SERVICE_ACTION_PLAY) {


            reSumSong();

            int pos = getCurrentPlay();
            Song song = mListSong.get(pos);
            createChannel();
            createNotification(getApplicationContext(), song, true);
            iPauseNotification.updateNotificationWhenPause(pos);
        }


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        if (notificationManagerCompat != null) {
            cancelNotification();
        }

        super.onDestroy();
    }

    public void createChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID_CHANNEL, NAME_CHANNEL, NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    public void createNotification(Context context, Song song, boolean isPlaying) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");


            Intent intentNextMedia = new Intent("Next_Media");
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, intentNextMedia, 0); //getBroadcast

            Intent intent = new Intent(context, ActivityMusic.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent pauseIntent = new Intent(this, MediaPlaybackService.class).setAction(MUSIC_SERVICE_ACTION_PAUSE);
            PendingIntent pausePendingIntent = PendingIntent.getService(this,
                    0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent playIntent = new Intent(this, MediaPlaybackService.class).setAction(MUSIC_SERVICE_ACTION_PLAY);
            PendingIntent playPendingIntent = PendingIntent.getService(this,
                    0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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


            Bitmap bitmap = getAlbumArt(song.getmSongArt());
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_nct);
            }


            //set data Notification
            notification_small.setImageViewBitmap(R.id.image_music_notification, bitmap);
            notification_big.setTextViewText(R.id.tv_song_name_notification, song.getmSongName());
            notification_big.setTextViewText(R.id.tv_song_author_notification, song.getmSongAuthor());
            notification_big.setImageViewBitmap(R.id.image_music_notification, bitmap);

            //set onClick button Notification
            //  notification_small.setOnClickPendingIntent(R.id.icon_next_notification, pendingSwitchIntent);     /*BroadCart Receiver

            notification_small.setOnClickPendingIntent(R.id.icon_next_notification, nextPendingIntent);
            notification_small.setOnClickPendingIntent(R.id.icon_previous_notification, prevPendingIntent);
            notification_small.setOnClickPendingIntent(R.id.icon_play__notification_small, pausePendingIntent);
            notification_big.setOnClickPendingIntent(R.id.icon_next_notification, nextPendingIntent);
            notification_big.setOnClickPendingIntent(R.id.icon_previous_notification, prevPendingIntent);
            notification_big.setOnClickPendingIntent(R.id.icon_play__notification_small, pausePendingIntent);

            if (isPlaying) {
                notification_small.setImageViewResource(R.id.icon_play__notification_small, R.drawable.ic_pause_media);
                notification_small.setOnClickPendingIntent(R.id.icon_play__notification_small, pausePendingIntent);
                notification_big.setImageViewResource(R.id.icon_play_notification_big, R.drawable.ic_pause_media);
                notification_big.setOnClickPendingIntent(R.id.icon_play_notification_big, pausePendingIntent);
            } else {
                notification_small.setImageViewResource(R.id.icon_play__notification_small, R.drawable.ic_play_media);
                notification_small.setOnClickPendingIntent(R.id.icon_play__notification_small, playPendingIntent);
                notification_big.setImageViewResource(R.id.icon_play_notification_big, R.drawable.ic_play_media);
                notification_big.setOnClickPendingIntent(R.id.icon_play_notification_big, playPendingIntent);
            }


            //build Notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID_CHANNEL)
                    .setSmallIcon(R.drawable.ic_list_music)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notification_small)
                    .setContentIntent(contentIntent)
                    .setCustomBigContentView(notification_big);
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());


        }
    }

    public void cancelNotification() {
        if (notificationManagerCompat != null)
            notificationManagerCompat.cancel(NOTIFICATION_ID);
    }

    public static Bitmap getAlbumArt(String path) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
        if (data != null) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return null;
    }

    private void initMediaPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                Log.d("media", "complete" + mPlayer.getCurrentPosition() + " " + mPlayer.getDuration());
                if (!isFist) {
                    if (isShuffle == NORMAL) {
                        long id= mIdPlay;
                        int pos = -1;
                        for(int i=0;i<mListSong.size();i++){
                            if(mListSong.get(i).getmSongID()==id){
                                pos=i;
                            }
                        }
                        if (isRepeat == NORMAL) {


                            if (pos != mListSong.size() - 1) {

                                pos++;
                                if (pos > mListSong.size() - 1) {
                                    pos = 0;                           //next media
                                }
                                setCurrentPlay(pos);
                                String pathNext = mListSong.get(pos).getmSongArt();
                                mCurrentPlay = pos;
                                playSong(pathNext);
                                mIUpdateUI.updateUI(pos);
                                mIdPlay=mListSong.get(pos).getmSongID();
                                iUpdateAllSongWhenAutoNext.updateAllSongWhenAutoNext(pos);
                            }
                        }
                        if (isRepeat == REPEAT_ALL) {
                            pos++;
                            if (pos > mListSong.size() - 1) {
                                pos = 0;                           //next media
                            }
                            setCurrentPlay(pos);
                            mIdPlay=mListSong.get(pos).getmSongID();
                            String pathNext = mListSong.get(pos).getmSongArt();
                            mCurrentPlay = pos;
                            playSong(pathNext);
                            mIUpdateUI.updateUI(pos);
                            iUpdateAllSongWhenAutoNext.updateAllSongWhenAutoNext(pos);
                        } else {
                            setCurrentPlay(pos);
                            String pat = mListSong.get(pos).getmSongArt();
                            mCurrentPlay = pos;
                            mIdPlay=mListSong.get(pos).getmSongID();
                            playSong(pat);
                            mIUpdateUI.updateUI(pos);
                            iUpdateAllSongWhenAutoNext.updateAllSongWhenAutoNext(pos);
                        }

                    } else {
                        Random random = new Random();
                        int pos = random.nextInt(mListSong.size());
                        setCurrentPlay(pos);
                        mIdPlay=mListSong.get(pos).getmSongID();
                        String pat = mListSong.get(pos).getmSongArt();
                        mCurrentPlay = pos;
                        playSong(pat);
                        mIUpdateUI.updateUI(pos);
                        iUpdateAllSongWhenAutoNext.updateAllSongWhenAutoNext(mListSong.get(pos ).getmSongID());
                    }
                }
            }
        });
    }


    public void playSong(String path) {
        mPlayer.reset();
        try {
            mPlayer.setDataSource(path);            //run media
            mPlayer.prepare();
            isStatusPlay = true;
            isFist = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextSong(int pos) {

        pos++;
        if (pos > mListSong.size() - 1) {
            pos = 0;                           //next media
        }
        mCurrentPlay = pos;
        mIdPlay=mListSong.get(pos).getmSongID();
        playSong(mListSong.get(pos).getmSongArt());
    }


    public void pauseSong() {
        mPlayer.pause();                           //pause media
        isStatusPlay = false;
        isResume = true;

    }

    public void stop() {
        mPlayer.stop();                            //stop media
        mPlayer.reset();
        mPlayer.release();
    }

    public void reSumSong() {
        mPlayer.start();
        isStatusPlay = true;
    }


    public void previousSong(int pos) {
        int seconds = getCurrentStreamPosition() / 1000 % 60;
        if (seconds <= 3) {
            pos--;
            if (pos < 0) {
                pos = mListSong.size() - 1;
            }
        }
        mCurrentPlay = pos;
        mIdPlay=mListSong.get(pos).getmSongID();
        playSong(mListSong.get(pos).getmSongArt());
    }

    public MediaPlayer getPlayer() {
        return mPlayer;
    }

    public int getCurrentStreamPosition() {

        sharedPreferencesCurrent = getSharedPreferences("DATA_CURRENT_PLAY", MODE_PRIVATE);
        int position = sharedPreferencesCurrent.getInt("DATA_CURRENT_STREAM_POSITION", 0);
        if (isFist) return position;
        if (mPlayer != null)
            return mPlayer.getCurrentPosition();  //trả về vtri đang phát
        return 0;
    }


    public long getDuration() {
        if (mPlayer != null)
            return mPlayer.getDuration();      //trả về vtri cuối

        return 0;
    }

    public void seekTo(int position) {
        mPlayer.seekTo(position);
    }

    public interface INextAndPreNotification {
        void updateNotificationWhenNextAndPre(int pos);
    }

    public void setINextAndPreNotification(INextAndPreNotification iNextAndPreNotification) {
        this.iNextAndPreNotification = iNextAndPreNotification;
    }

    private INextAndPreNotification iNextAndPreNotification;


    public interface IPauseNotification {
        void updateNotificationWhenPause(int pos);
    }

    public void setIPauseNotification(IPauseNotification iPauseNotification) {
        this.iPauseNotification = iPauseNotification;
    }

    private IPauseNotification iPauseNotification;


    public interface IUpdateUI {
        void updateUI(int pos);
    }

    private MediaPlaybackService.IUpdateUI mIUpdateUI;

    public void setIUpdateUI(MediaPlaybackService.IUpdateUI mIUpdateUI) {
        this.mIUpdateUI = mIUpdateUI;
    }


    public interface IUpdateAllSongWhenAutoNext {
        void updateAllSongWhenAutoNext(long id);
    }

    public void setIUpdateAllSongWhenAutoNext(IUpdateAllSongWhenAutoNext iUpdateAllSongWhenAutoNext) {
        this.iUpdateAllSongWhenAutoNext = iUpdateAllSongWhenAutoNext;
    }

    private IUpdateAllSongWhenAutoNext iUpdateAllSongWhenAutoNext;


}
