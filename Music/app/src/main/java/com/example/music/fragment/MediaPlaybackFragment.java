package com.example.music.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.music.ActivityMusic;
import com.example.music.R;
import com.example.music.adapter.SongAdapter;
import com.example.music.model.Song;
import com.example.music.service.MediaPlaybackService;
import com.example.music.service.SongManager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class MediaPlaybackFragment extends Fragment implements View.OnClickListener, MediaPlaybackService.IUpdateUI,
        MediaPlaybackService.INextAndPreNotification, MediaPlaybackService.IPauseNotification /*,AllSongsFragment.IUpdateMediaWhenAllSongClickItem*/ {

    private TextView mSongName, mSongAuthor;
    public boolean isVertical;
    // TODO: Rename parameter arguments, choose names that match
    private static final String SONG_NAME = "name";
    private static final String SONG_ARTIST = "author";
    private static final String SONG_ART = "art";
    private static final String CURRENT_POSITION = "currentPosition";
    // TODO: Rename and change types of parameters
    private ImageButton mButtonReturnAllSong;
    private String mSongNameMedia;
    private String mSongAuthorMedia;
    private String mSongArtMedia;
    private int mCurrentPosition;
    private ImageView mArtMedia;
    private ImageView mBackground;
    private TextView mPlayTime, mEndTime;
    private ImageButton mPlayMedia, mPreMedia, mNextMedia, mLikeMedia, mDisLikeMedia, mMenu;

    private MediaPlaybackService mMusicService;
    private SeekBar mSeeBar;
    private List<Song> mSongList = new ArrayList<>();
    private View view;
    private UpdateSeekBarThread mUpdateSeekBarThread;

    public void setSongList(List<Song> mSongList) {
        this.mSongList = mSongList;
    }

    public void setMusicService(MediaPlaybackService mMusicService) {
        this.mMusicService = mMusicService;
    }

    private MediaPlaybackService getMusicService() {
        return getActivityMusic().getMusicService();
    }

    private List<Song> getListSong() {
        return getActivityMusic().getListSong();
    }

    private SongAdapter getSongAdapter() {
        return getActivityMusic().getSongAdapter();
    }


    public MediaPlaybackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Media", mSongList.size() + "    onCreateMedia   " + mMusicService);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSongNameMedia = getArguments().getString(SONG_NAME);
            mSongAuthorMedia = getArguments().getString(SONG_ARTIST); // khởi tạo fragment
            mSongArtMedia = getArguments().getString(SONG_ART);
            mCurrentPosition = getArguments().getInt(CURRENT_POSITION);

        }

        mUpdateSeekBarThread = new UpdateSeekBarThread();
        mUpdateSeekBarThread.start();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.media_play_back_fragment, container, false);

        setData();
        initView();

        if (mMusicService != null) {
            mMusicService.setIUpdateUI(MediaPlaybackFragment.this);
            mMusicService.setINextAndPreNotification(MediaPlaybackFragment.this);
            mMusicService.setIPauseNotification(MediaPlaybackFragment.this);
            setDataTop();
        }
        setDataTop();

        return view;

    }

    public void setData() {
        mMusicService = getMusicService();
        mSongList = getListSong();
    }


    public void initView() {
        mSongName = view.findViewById(R.id.tv_song_name_media);
        mSongAuthor = view.findViewById(R.id.tv_songauthormedia);
        mArtMedia = view.findViewById(R.id.tv_ArtMedia);
        mBackground = view.findViewById(R.id.img_background);
        mPlayMedia = view.findViewById(R.id.btn_play_media);
        mPreMedia = view.findViewById(R.id.btn_pre_media);
        mNextMedia = view.findViewById(R.id.btn_next_media);
        mButtonReturnAllSong = view.findViewById(R.id.btn_show_list);
        mPlayTime = view.findViewById(R.id.play_time);
        mEndTime = view.findViewById(R.id.end_time);
        mSeeBar = view.findViewById(R.id.media_seekBar);
        mLikeMedia = view.findViewById(R.id.btn_like_media);
        mDisLikeMedia = view.findViewById(R.id.btn_dislike_media);
        mMenu = view.findViewById(R.id.btn_menu_media);

        mPlayMedia.setOnClickListener(this);
        mNextMedia.setOnClickListener(this);
        mPreMedia.setOnClickListener(this);
        mButtonReturnAllSong.setOnClickListener(this);
        mLikeMedia.setOnClickListener(this);
        mDisLikeMedia.setOnClickListener(this);
        mMenu.setOnClickListener(this);

        if (isVertical) {
            mBackground.setScaleType(ImageView.ScaleType.FIT_XY);
            if (mMusicService != null && mSongList.size() > 0) {
                update();
            }

        } else {
            //set khi xoay màn hình
            mBackground.setScaleType(ImageView.ScaleType.FIT_CENTER);

        }
        mSeeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mMusicService != null && b) {
                    mMusicService.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void update() {                          //update khi all song click ll_bottom  ==>media
        mSongName.setText(mSongNameMedia);
        mSongAuthor.setText(mSongAuthorMedia);
        byte[] Art = getAlbumArt(mSongArtMedia);
        Glide.with(view.getContext()).asBitmap()
                .load(Art)
                .error(R.drawable.ic_nct)
                .into(mArtMedia);
        Glide.with(view.getContext()).asBitmap()
                .load(Art)
                .error(R.drawable.ic_nct)
                .into(mBackground);
        if (mMusicService.isStatusPlay()) {
            mPlayMedia.setBackgroundResource(R.drawable.ic_pause_media);
        } else mPlayMedia.setBackgroundResource(R.drawable.ic_play_media);

        mPlayTime.setText(formattedTime(String.valueOf(mMusicService.getCurrentStreamPosition())));
        mEndTime.setText(formattedTime(mSongList.get(mMusicService.getmCurrentPlay()).getmSongTime()));


    }

    public static MediaPlaybackFragment newInstance(String songName, String songArtist, String songArt, int mCurrentPosition) {
        MediaPlaybackFragment fragment = new MediaPlaybackFragment();
        Bundle args = new Bundle();
        args.putString(SONG_NAME, songName);
        args.putString(SONG_ARTIST, songArtist);            //request dữ liệu khi khởi tạo
        args.putString(SONG_ART, songArt);
        args.putInt(CURRENT_POSITION, mCurrentPosition);
        fragment.setArguments(args);
        return fragment;

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
    public void onDestroy() {
        if (mMusicService != null) mUpdateSeekBarThread.exit();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onDestroy();
    }

    //get activity
    private ActivityMusic getActivityMusic() {
        if (getActivity() instanceof ActivityMusic) {
            return (ActivityMusic) getActivity();
        }
        return null;
    }

    private void setDataTop() {
        if (mMusicService != null && mSongList.size() > 0 && mMusicService.getmCurrentPlay() >= 0) {
            int current = mMusicService.getmCurrentPlay();
            mSongName.setText(mSongList.get(current).getmSongName());
            mSongAuthor.setText(mSongList.get(current).getmSongAuthor());
            mEndTime.setText(formattedTime(mSongList.get(current).getmSongTime()));
            byte[] Art = getAlbumArt(mSongList.get(current).getmSongArt());
            Glide.with(view.getContext()).asBitmap()                                 //set dữ liệu hiển thị đồng bộ khi play đang chạy
                    .load(Art)
                    .error(R.drawable.ic_nct)
                    .into(mArtMedia);
            Glide.with(view.getContext()).asBitmap()
                    .load(Art)
                    .error(R.drawable.ic_nct)
                    .into(mBackground);

            if (mMusicService.isStatusPlay()) {
                mPlayMedia.setBackgroundResource(R.drawable.ic_pause_media);
            } else mPlayMedia.setBackgroundResource(R.drawable.ic_play_media);
        }
        updateUI();

    }

    private void updateUI() {
        if (mMusicService != null) {
            mUpdateSeekBarThread.updateSeekBar();
        }
    }

    public static byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        byte[] albumArt = mediaMetadataRetriever.getEmbeddedPicture();     //chuyển đổi đường dẫn file media thành đường dẫn file Ảnh
        mediaMetadataRetriever.release();
        return albumArt;
    }

    public void setVertical(boolean vertical) {  //set ngang dọc
        isVertical = vertical;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play_media:

                if (mMusicService.getmCurrentPlay() > 0) {
                    if (mMusicService.isStatusPlay()) {


                        mMusicService.pauseSong();
                        mPlayMedia.setBackgroundResource(R.drawable.ic_play_media);

                        mMusicService.createChannel();
                        mMusicService.createNotification(getActivity(), mSongList.get(mMusicService.getmCurrentPlay()), false);

                        if (!isVertical) {
                            iUpdateAllSongWhenPauseMedia.updateAllSongWhenPauseMedia(mMusicService.getmCurrentPlay());
                        }

                    } else {
                        mMusicService.reSumSong();
                        if (!isVertical) {
                            iUpdateAllSongWhenPlayMedia.updateAllSongWhenPlayMedia(mMusicService.getmCurrentPlay());
                        }
                        mPlayMedia.setBackgroundResource(R.drawable.ic_pause_media);

                        mMusicService.createChannel();
                        mMusicService.createNotification(getActivity(), mSongList.get(mMusicService.getmCurrentPlay()), true);
                    }
                }

                break;


            case R.id.btn_next_media:                            //buton điều hướng bên media
                if (mMusicService.getmCurrentPlay() >= 0) {

                    int pos = mMusicService.getmCurrentPlay();
                    Log.d("MediaNext", "" + pos);
                    mMusicService.nextSong(pos);
                    mCurrentPosition = mMusicService.getmCurrentPlay();
                    Song song = mSongList.get(mCurrentPosition);
                    mSongNameMedia = song.getmSongName();
                    mSongAuthorMedia = song.getmSongAuthor();
                    mSongArtMedia = song.getmSongArt();
                    setDataTop();
                    mMusicService.createChannel();
                    mMusicService.createNotification(getActivity(), song, true);
                    if (!isVertical) {
                        iUpdateAllSong.updateAllSong(mMusicService.getmCurrentPlay());
                    }
                }

                break;

            case R.id.btn_pre_media:
                if (mMusicService.getmCurrentPlay() >= 0) {
                    mMusicService.previousSong(mMusicService.getmCurrentPlay());
                    mCurrentPosition = mMusicService.getmCurrentPlay();
                    Song song1 = mSongList.get(mCurrentPosition);
                    mSongNameMedia = song1.getmSongName();
                    mSongAuthorMedia = song1.getmSongAuthor();
                    mSongArtMedia = song1.getmSongArt();
                    setDataTop();
                    mMusicService.createChannel();
                    mMusicService.createNotification(getActivity(), song1, true);
                    if (!isVertical) {
                        iUpdateAllSong.updateAllSong(mMusicService.getmCurrentPlay());
                    }
                }

                break;

            case R.id.btn_show_list:
                if (isVertical) {
                    getFragmentManager().popBackStack();
                }
                break;

            case R.id.btn_like_media:
                Toast.makeText(mMusicService, "Added to favorites list", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_dislike_media:
                Toast.makeText(mMusicService, "Added dislike list", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_menu_media:
                Toast.makeText(mMusicService, "Menu chưa làm", Toast.LENGTH_SHORT).show();

            default:
                break;


        }
    }

    @Override
    public void updateUI(int pos) {
        setDataTop();                                //override , update UI mediaFragment auto khi next bài
        mMusicService.createChannel();
        mMusicService.createNotification(getActivity(), mSongList.get(pos), true);
    }

    @Override
    public void updateNotificationWhenNextAndPre(int pos) {

        if (mMusicService != null) {

            setDataTop(); //update khi Notification next Pre

        }
    }

    @Override
    public void updateNotificationWhenPause(int pos) {
        //update khi Notification when pause

        if (!mMusicService.isStatusPlay()) {

            mPlayMedia.setBackgroundResource(R.drawable.ic_play_media);

        } else {
            mPlayMedia.setBackgroundResource(R.drawable.ic_pause_media);
        }


    }
/*

    @Override
    public void UpdateMediaWhenAllSongClickItem(int pos) {

    }
*/

    public class UpdateSeekBarThread extends Thread {
        private Handler handler;

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            handler = new Handler();
            Looper.loop();
        }

        public void updateSeekBar() {
            if (mMusicService != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mMusicService.getmCurrentPlay() >= 0) {
                            while (mMusicService.getPlayer() != null) {
                                try {
                                    long current = -1;
                                    try {
                                        current = mMusicService.getCurrentStreamPosition();
                                    } catch (IllegalStateException e) {
//                                    e.printStackTrace();
                                    }
                                    if (getActivity() != null && mSongList.size() > 0) {
                                        final long finalCurrent = current;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mSeeBar.setMax((int) (mMusicService.getDuration()));
                                                mSeeBar.setProgress((int) (finalCurrent));
                                                mPlayTime.setText(formattedTime(String.valueOf(finalCurrent)));
                                                mEndTime.setText(formattedTime(mSongList.get(mMusicService.getmCurrentPlay()).getmSongTime()));
                                            }
                                        });
                                    }
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        }

        public void exit() {
            handler.getLooper().quit();
        }
    }

    private String formattedTime(String time) {
        long duration = Long.parseLong(time);
        int minutes = (int) (duration / 1000 / 60);
        int seconds = (int) ((duration / 1000) % 60);
        if (seconds < 10) {
            String seconds2 = "0" + seconds;
            return minutes + ":" + seconds2;
        }
        return minutes + ":" + seconds;
    }

    public interface IUpdateAllSong {
        void updateAllSong(int pos);
    }

    public void setIUpdateAllSong(IUpdateAllSong iUpdateAllSong) {
        this.iUpdateAllSong = iUpdateAllSong;
    }

    private IUpdateAllSong iUpdateAllSong;

    public interface IUpdateAllSongWhenPauseMedia {
        void updateAllSongWhenPauseMedia(int pos);
    }

    public void setIUpdateAllSongWhenPauseMedia(IUpdateAllSongWhenPauseMedia iUpdateAllSongWhenPauseMedia) {
        this.iUpdateAllSongWhenPauseMedia = iUpdateAllSongWhenPauseMedia;
    }

    private IUpdateAllSongWhenPauseMedia iUpdateAllSongWhenPauseMedia;


    public interface IUpdateAllSongWhenPlayMedia {
        void updateAllSongWhenPlayMedia(int pos);
    }

    public void setIUpdateAllSongWhenPlayMedia(IUpdateAllSongWhenPlayMedia iUpdateAllSongWhenPlayMedia) {
        this.iUpdateAllSongWhenPlayMedia = iUpdateAllSongWhenPlayMedia;
    }

    private IUpdateAllSongWhenPlayMedia iUpdateAllSongWhenPlayMedia;

    /*update media when click item in allSongFragment when ngang */
    public void updateMediaWhenClickItem(int pos) {

        Log.d("xxx", "updateMediaWhenClickItem" + pos);
        setDataTop();
        mPlayMedia.setBackgroundResource(R.drawable.ic_pause_media);


    }


}