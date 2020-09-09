package com.example.music.Notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.music.ActivityMusic;
import com.example.music.R;
import com.example.music.model.Song;

import java.util.List;

public class Notification {
//    public static void setListSong(List<Song> mListSong) {
//        Notification.mListSong = mListSong;
//    }
//
//    public static List<Song> mListSong;
//
//    private static TextView mSongNameNotification;
//
//    public static final String ID_CHANNEL = "999";
//    public static Bitmap readBitmapFile(String path){
//        return BitmapFactory.decodeFile(path);
//    }

//
//    public static void createNotification(Context context, Song song, int pos) {
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(context);
//            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context,"tag");
//
//            Intent intentNextMedia = new Intent("Next_Media");
//            intentNextMedia.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, intentNextMedia, 0);
//
//            Intent intent = new Intent(context, ActivityMusic.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            RemoteViews notification_small = new RemoteViews(context.getPackageName(), R.layout.notifiation_small);
//            RemoteViews notification_big = new RemoteViews(context.getPackageName(), R.layout.notifiation_big);
//
//            notification_small.setOnClickPendingIntent(R.id.icon_next_notification, pendingSwitchIntent);
//
//
//
//            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_app);
//
//            notification_small.setImageViewBitmap(R.id.image_music_notification, readBitmapFile(song.getmSongArt()));
//            notification_big.setTextViewText(R.id.tv_song_name_notification,song.getmSongName());
//            notification_big.setTextViewText(R.id.tv_song_author_notification, song.getmSongAuthor());
//
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID_CHANNEL)
//                    .setSmallIcon(R.drawable.ic_list_music)
//                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                    .setCustomContentView(notification_small)
//                    .setContentIntent(contentIntent)
//                    .setCustomBigContentView(notification_big);
//            notificationManagerCompat.notify(10, builder.build());
//            Toast.makeText(context, ""+mListSong.size(), Toast.LENGTH_SHORT).show();
//
//
//        }
//    }
}
