package com.example.music.fragment;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.music.ActivityMusic;
import com.example.music.R;
import com.example.music.adapter.SongAdapter;
import com.example.music.database.MusicDatabase;
import com.example.music.database.MusicProvider;
import com.example.music.model.Song;
import com.example.music.service.MediaPlaybackService;
import com.example.music.service.SongManager;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


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
    private Button mMediaRepeatButton, mMediaShuffleButton;
    private MediaPlaybackService mMusicService;
    private SeekBar mSeeBar;
    private List<Song> mSongList = new ArrayList<>();
    private View view;
    private UpdateSeekBarThread mUpdateSeekBarThread;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private List<Song> mSongListFavorite = new ArrayList<>();
    private boolean isSongFavorite = false;
    private boolean isSongDislike = false;

    SharedPreferences sharedPreferencesCurrent;
    SharedPreferences.Editor editorCurrent;


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


    public MediaPlaybackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Media", mSongList.size() + "    onCreateMedia   " + mMusicService);
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("DATA_PLAY_MEDIA", getActivity().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sharedPreferencesCurrent = getActivity().getSharedPreferences("DATA_CURRENT_PLAY", getActivity().MODE_PRIVATE);
        editorCurrent = sharedPreferences.edit();


        if (getArguments() != null) {
            mSongNameMedia = getArguments().getString(SONG_NAME);
            mSongAuthorMedia = getArguments().getString(SONG_ARTIST); // khởi tạo fragment
            mSongArtMedia = getArguments().getString(SONG_ART);

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
        checkSongFavorite();

        if (isSongFavorite) {
            mLikeMedia.setBackgroundResource(R.drawable.ic_liked);
            isSongFavorite=true;
        } else {
            mLikeMedia.setBackgroundResource(R.drawable.ic_like);
            isSongFavorite=false;
        }
        mDisLikeMedia.setBackgroundResource(R.drawable.ic_dislike);


        if (mMusicService != null) {
            mMusicService.setIUpdateUI(MediaPlaybackFragment.this);
            mMusicService.setINextAndPreNotification(MediaPlaybackFragment.this);
            mMusicService.setIPauseNotification(MediaPlaybackFragment.this);
            setDataTop();
        }

        return view;

    }

    public void setData() {
        mMusicService = getMusicService();
        mSongList = getListSong();
        mSongListFavorite = SongManager.getFavorAllSongs(getContext());
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
        mMediaRepeatButton = view.findViewById(R.id.btn_media_repeat);
        mMediaShuffleButton = view.findViewById(R.id.btn_media_shuffle);

        mPlayMedia.setOnClickListener(this);
        mNextMedia.setOnClickListener(this);
        mPreMedia.setOnClickListener(this);
        mButtonReturnAllSong.setOnClickListener(this);
        mLikeMedia.setOnClickListener(this);
        mDisLikeMedia.setOnClickListener(this);
        mMenu.setOnClickListener(this);
        mMediaRepeatButton.setOnClickListener(this);
        mMediaShuffleButton.setOnClickListener(this);

        if (isVertical) {
            mBackground.setScaleType(ImageView.ScaleType.FIT_XY);
            if (mMusicService != null && mSongList.size() > 0) {
                //  update();
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

    public void checkSongFavorite() {
        if (mMusicService != null) {
            long idPlay = mMusicService.getIdPlay();
            for (int i = 0; i < mSongListFavorite.size(); i++) {
                if (mSongListFavorite.get(i).getmSongID() == idPlay) {
                    isSongFavorite = true;
                }
            }
        }

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

        if (mMusicService != null) {
            if (mMusicService.getCurrentPlay() < 0) {   //chi set khi xoay man hình luc mới vào app
                int current = sharedPreferencesCurrent.getInt("DATA_CURRENT", -1);
                if (current !=-1) {
                    mSongName.setText(mSongList.get(current).getmSongName());
                    mSongAuthor.setText(mSongList.get(current).getmSongAuthor());
                    mEndTime.setText(formattedTime(mSongList.get(current).getmSongTime()));
                    Bitmap Art = getAlbumArt(mSongList.get(current).getmSongArt());
                    Glide.with(view.getContext()).asBitmap()
                            .load(Art)
                            .error(R.drawable.ic_nct)
                            .into(mArtMedia);
                    Glide.with(view.getContext()).asBitmap()
                            .load(Art)
                            .error(R.drawable.ic_nct)
                            .into(mBackground);

                    mPlayMedia.setBackgroundResource(R.drawable.ic_play_media);
                    mMusicService.setCurrentPlay(current);

                    int position = sharedPreferencesCurrent.getInt("DATA_CURRENT_STREAM_POSITION", 0);
                    Log.d("isFirstSetProgress", "" + position);
                    mSeeBar.setMax(Integer.parseInt(mSongList.get(current).getmSongTime()));
                    mSeeBar.setProgress(position);
                    mPlayTime.setText(formattedTime(String.valueOf(position)));
                }
            }
            else {                // khi đã chơi nhạc
                int current = 0;
                long IdPlay = mMusicService.getIdPlay();
                Log.d("SetDataTop", "ID Play : : " + IdPlay);
                for (int i = 0; i < mSongList.size(); i++) {
                    if (mSongList.get(i).getmSongID() == IdPlay) {
                        current = i;
                        mMusicService.setCurrentPlay(i);
                        mMusicService.setIdPlay(IdPlay);
                    }
                }
                Log.d("SetDataTop", "Media : Current: " + current);
                mSongName.setText(mSongList.get(current).getmSongName());
                mSongAuthor.setText(mSongList.get(current).getmSongAuthor());
                mEndTime.setText(formattedTime(mSongList.get(current).getmSongTime()));
               Bitmap Art = getAlbumArt(mSongList.get(current).getmSongArt());
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
                } else {
                    mPlayMedia.setBackgroundResource(R.drawable.ic_play_media);
                }

                int position = sharedPreferencesCurrent.getInt("DATA_CURRENT_STREAM_POSITION", 0);
                Log.d("isFirstSetProgress", "" + position);
                mSeeBar.setMax(Integer.valueOf(mSongList.get(mMusicService.getCurrentPlay()).getmSongTime()));
                mSeeBar.setProgress(position);
                if (mMusicService.isFist()) {
                    mPlayTime.setText(formattedTime(String.valueOf(position)));
                }

            }
            int repeat = mMusicService.isRepeat();
            if (repeat == MediaPlaybackService.REPEAT) {
                mMediaRepeatButton.setBackgroundResource(R.drawable.ic_baseline_repeat_one_24);
            } else if (repeat == MediaPlaybackService.REPEAT_ALL) {
                mMediaRepeatButton.setBackgroundResource(R.drawable.ic_baseline_repeat_all);
            } else {
                mMediaRepeatButton.setBackgroundResource(R.drawable.ic_baseline_repeat_24);
            }

            int shuffle = mMusicService.isShuffle();
            if (shuffle != MediaPlaybackService.SHUFFLE) {
                mMediaShuffleButton.setBackgroundResource(R.drawable.ic_shuffle);
            } else {
                mMediaShuffleButton.setBackgroundResource(R.drawable.ic_baseline_shuffle_25);
            }
            if (!mMusicService.isFist()) {
                updateUI();
            }
        }
    }

    private void updateUI() {
        if (mMusicService != null) {
            mUpdateSeekBarThread.updateSeekBar();
        }
    }

    public static Bitmap getAlbumArt(String path) {
        File file = new File(path);
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {
            metadataRetriever.setDataSource(file.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            Log.e("TAG", "parseAlbum: ", e);
        }
        byte[] albumData = metadataRetriever.getEmbeddedPicture();
        if (albumData != null) {
            return BitmapFactory.decodeByteArray(albumData, 0, albumData.length);
        }
        return null;
    }

    public void setVertical(boolean vertical) {  //set ngang dọc
        isVertical = vertical;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play_media:
                int pos = -1;
                for (int i = 0; i < mSongList.size(); i++) {
                    if (mSongList.get(i).getmSongID() == mMusicService.getIdPlay()) {
                        pos = i;
                    }
                }
                if (pos >= 0) {
                    if (mMusicService.isStatusPlay()) {
                        mMusicService.pauseSong();
                        mPlayMedia.setBackgroundResource(R.drawable.ic_play_media);
                        mMusicService.createChannel();
                        mMusicService.createNotification(getActivity(), mSongList.get(pos), false);

                        if (!isVertical) {
                            iUpdateAllSongWhenPauseMedia.updateAllSongWhenPauseMedia(pos);
                        }

                    } else {
                        if (mMusicService.isResume())
                            mMusicService.reSumSong();
                        else {
                            mMusicService.playSong(mSongList.get(pos).getmSongArt());
                            int position = sharedPreferencesCurrent.getInt("DATA_CURRENT_STREAM_POSITION", 0);
                            mMusicService.seekTo(position);
                        }
                        if (!isVertical) {
                            iUpdateAllSongWhenPlayMedia.updateAllSongWhenPlayMedia(pos);
                        }
                        mPlayMedia.setBackgroundResource(R.drawable.ic_pause_media);

                        mMusicService.createChannel();
                        mMusicService.createNotification(getActivity(), mSongList.get(pos), true);
                    }
                    updateUI();
                }

                break;


            case R.id.btn_next_media:                           
                mDisLikeMedia.setBackgroundResource(R.drawable.ic_dislike);
                if (mMusicService.getCurrentPlay() >= 0) {
                    int pos1 = -1;
                    for (int i = 0; i < mSongList.size(); i++) {
                        if (mSongList.get(i).getmSongID() == mMusicService.getIdPlay()) {
                            pos1 = i;
                        }
                    }
                    Log.d("MediaNext", "" + pos1);
                    mMusicService.nextSong(pos1);
                    mCurrentPosition = mMusicService.getCurrentPlay();
                    Song song = mSongList.get(mCurrentPosition);
                    mSongNameMedia = song.getmSongName();
                    mSongAuthorMedia = song.getmSongAuthor();
                    mSongArtMedia = song.getmSongArt();
                    setDataTop();
                    mMusicService.createChannel();
                    mMusicService.createNotification(getActivity(), song, true);
                    if (!isVertical) {
                        iUpdateAllSong.updateAllSong(mCurrentPosition);
                    }
                }

                break;

            case R.id.btn_pre_media:
                mDisLikeMedia.setBackgroundResource(R.drawable.ic_dislike);
                if (mMusicService.getCurrentPlay() >= 0) {

                    int pos2 = -1;
                    for (int i = 0; i < mSongList.size(); i++) {
                        if (mSongList.get(i).getmSongID() == mMusicService.getIdPlay()) {
                            pos2 = i;
                        }
                    }
                      mMusicService.previousSong(pos2);
                    mCurrentPosition = mMusicService.getCurrentPlay();
                    Song song1 = mSongList.get(mCurrentPosition);
                    mSongNameMedia = song1.getmSongName();
                    mSongAuthorMedia = song1.getmSongAuthor();
                    mSongArtMedia = song1.getmSongArt();
                    setDataTop();
                    mMusicService.createChannel();
                    mMusicService.createNotification(getActivity(), song1, true);
                    if (!isVertical) {
                        iUpdateAllSong.updateAllSong(mCurrentPosition);
                    }
                }

                break;

            case R.id.btn_show_list:
                if (isVertical) {
                    getFragmentManager().popBackStack();
                }
                break;

            case R.id.btn_like_media:
                if (isSongFavorite) {
                    isSongFavorite = false;
                    mLikeMedia.setBackgroundResource(R.drawable.ic_like);
                    Song song1 = mMusicService.getListSong().get(mMusicService.getCurrentPlay());
                    final int id = (int) song1.getmSongID();
                    getContext().getContentResolver().delete(MusicProvider.CONTENT_URI, MusicDatabase.ID + "=" + id, null);
                    Toast.makeText(getActivity().getApplicationContext(), "Removed favorites list", Toast.LENGTH_SHORT).show();

                } else {
                    isSongFavorite = true;
                    mLikeMedia.setBackgroundResource(R.drawable.ic_liked);
                    ContentValues values = new ContentValues();
                    Song song = mMusicService.getListSong().get(mMusicService.getCurrentPlay());
                    values.put(MusicDatabase.ID_PROVIDER, song.getSongIDProvider());
                    values.put(MusicDatabase.ID, song.getmSongID());
                    values.put(MusicDatabase.TITLE, song.getmSongName());
                    values.put(MusicDatabase.ARTIST, song.getmSongAuthor());
                    values.put(MusicDatabase.DATA, song.getmSongArt());
                    values.put(MusicDatabase.DURATION, song.getmSongTime());
                    values.put(MusicDatabase.IS_FAVORITE, 2);
                    getContext().getContentResolver().insert(MusicProvider.CONTENT_URI, values);
                    Toast.makeText(mMusicService, "Added favorites list", Toast.LENGTH_SHORT).show();

                }
                if (isSongDislike) {
                    mDisLikeMedia.setBackgroundResource(R.drawable.ic_dislike);
                }

                break;

            case R.id.btn_dislike_media:

                if (isSongFavorite) {
                    isSongFavorite = false;
                    isSongDislike = true;
                    mLikeMedia.setBackgroundResource(R.drawable.ic_like);
                    mDisLikeMedia.setBackgroundResource(R.drawable.ic_disliked);
                    Song song1 = mMusicService.getListSong().get(mMusicService.getCurrentPlay());
                    final int id = (int) song1.getmSongID();
                    getContext().getContentResolver().delete(MusicProvider.CONTENT_URI, MusicDatabase.ID + "=" + id, null);
                    Toast.makeText(getActivity().getApplicationContext(), "Removed favorites list", Toast.LENGTH_SHORT).show();
                } else {
                    isSongDislike = true;
                    mDisLikeMedia.setBackgroundResource(R.drawable.ic_disliked);
                }


                break;

            case R.id.btn_menu_media:
                Toast.makeText(mMusicService, "Click", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_media_repeat:
                int repeat = mMusicService.isRepeat();
                if (repeat == MediaPlaybackService.REPEAT) {
                    mMusicService.setRepeat(MediaPlaybackService.NORMAL);
                    editor.remove("DATA_REPEAT");
                    editor.putInt("DATA_REPEAT", MediaPlaybackService.NORMAL);
                    editor.commit();
                    mMediaRepeatButton.setBackgroundResource(R.drawable.ic_baseline_repeat_24);
                } else if (repeat == MediaPlaybackService.REPEAT_ALL) {
                    mMusicService.setRepeat(MediaPlaybackService.REPEAT);
                    mMediaRepeatButton.setBackgroundResource(R.drawable.ic_baseline_repeat_one_24);
                    editor.remove("DATA_REPEAT");
                    editor.putInt("DATA_REPEAT", MediaPlaybackService.REPEAT);
                    editor.commit();
                } else {
                    mMusicService.setRepeat(MediaPlaybackService.REPEAT_ALL);
                    mMediaRepeatButton.setBackgroundResource(R.drawable.ic_baseline_repeat_all);
                    editor.remove("DATA_REPEAT");
                    editor.putInt("DATA_REPEAT", MediaPlaybackService.REPEAT_ALL);
                    editor.commit();
                }
                break;

            case R.id.btn_media_shuffle:
                int shuffle = mMusicService.isShuffle();
                if (shuffle == MediaPlaybackService.SHUFFLE) {
                    mMusicService.setShuffle(MediaPlaybackService.NORMAL);
                    mMediaShuffleButton.setBackgroundResource(R.drawable.ic_shuffle);
                    editor.remove("DATA_SHUFFLE");
                    editor.putInt("DATA_SHUFFLE", MediaPlaybackService.NORMAL);
                    editor.commit();
                } else {
                    mMusicService.setShuffle(MediaPlaybackService.SHUFFLE);
                    mMediaShuffleButton.setBackgroundResource(R.drawable.ic_baseline_shuffle_25);
                    editor.remove("DATA_SHUFFLE");
                    editor.putInt("DATA_SHUFFLE", MediaPlaybackService.SHUFFLE);
                    editor.commit();

                }
                break;

            default:
                break;

        }
    }

    @Override
    public void updateUI(int pos) {
        setDataTop();   //override , update UI mediaFragment auto khi next bài
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
                        if (mMusicService.getCurrentPlay() >= 0) {
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
                                                mEndTime.setText(formattedTime(mSongList.get(mMusicService.getCurrentPlay()).getmSongTime()));
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