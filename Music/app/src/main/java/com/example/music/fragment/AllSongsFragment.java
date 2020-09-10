package com.example.music.fragment;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.music.ActivityMusic;
import com.example.music.Notifications.Notification;
import com.example.music.R;
import com.example.music.adapter.SongAdapter;
import com.example.music.model.Song;
import com.example.music.service.MediaPlaybackService;
import com.example.music.service.SongManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static android.content.Context.BIND_AUTO_CREATE;

public class AllSongsFragment extends Fragment implements View.OnClickListener, MediaPlaybackService.IUpdateUI {

    private RecyclerView mRcvSong;
    private List<Song> mListSong;
    private SongAdapter mSongAdapter;
    private RelativeLayout mLlBottom;
    private ImageView mSongArt;
    private int mCurrentPosition;
    private Button mBtnPay;
    private TextView mSongName, mSongAuthor;
    private boolean isVertical;


    public void setListSong(List<Song> mListSong) {
        this.mListSong = mListSong;
    }


    public void setMusicService(MediaPlaybackService mMusicService) {
        this.mMusicService = mMusicService;
    }

    public MediaPlaybackService mMusicService;


    public AllSongsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_song_fragment, container, false);
        initView(view);

        getDataBottom();
        mSongAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onStart() {
       // setService();
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
//
//    private void setService() {
//        Intent intent = new Intent(getActivity(), MediaPlaybackService.class);
//        getActivity().startService(intent);
//        getActivity().bindService(intent, serviceConnection, BIND_AUTO_CREATE);
//
//
//    }

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.d("AllSongFragment", "ServiceConnection");
//            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) service;
//            mMusicService = binder.getMusicService();
//
//            mMusicService.getMediaManager().setmListSong(mListSong); //put mListSong -> MediaPlaybackService
//
//            getDataBottom();
//            mSongAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mMusicService = null;
//        }
//    };

    private void getDataBottom() {
        if (mMusicService != null && mMusicService.getmCurrentPlay() >= 0) {     //khi chạy nhạc
            if (isVertical)
                mLlBottom.setVisibility(View.VISIBLE);
            else mLlBottom.setVisibility(View.GONE);

            mSongName.setText(mListSong.get(mMusicService.getmCurrentPlay()).getmSongName());                                  //Click item RecycleView
            mSongAuthor.setText(mListSong.get(mMusicService.getmCurrentPlay()).getmSongAuthor());
            byte[] songArt = getAlbumArt(mListSong.get(mMusicService.getmCurrentPlay()).getmSongArt());
            Glide.with(getContext()).asBitmap()
                    .error(R.drawable.ic_nct)
                    .load(songArt)
                    .into(mSongArt);

            if (mMusicService.isStatusPlay()) {
                mBtnPay.setBackgroundResource(R.drawable.ic_subpause);
            } else {
                mBtnPay.setBackgroundResource(R.drawable.ic_subplay);

            }

            for (int i = 0; i < mListSong.size(); i++) {
                mListSong.get(i).setmIsPlay(false);
            }
            mListSong.get(mMusicService.getmCurrentPlay()).setmIsPlay(true);

        }
    }

    private void initView(final View view) {


        mSongArt = view.findViewById(R.id.img_bottomArt);
        mRcvSong = view.findViewById(R.id.rcv_Song);
        mSongName = view.findViewById(R.id.tv_bottom_songName);
        mSongAuthor = view.findViewById(R.id.tv_bottom_song_author);                    //Ánh Xạ
        mLlBottom = view.findViewById(R.id.bottom);
        mBtnPay = view.findViewById(R.id.btn_play);

        mBtnPay.setOnClickListener(this);
        mLlBottom.setOnClickListener(this);


        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);                                     //Set Layout
        mRcvSong.setLayoutManager(manager);
        mSongAdapter = new SongAdapter(getActivity(), mListSong);
        mRcvSong.setAdapter(mSongAdapter);
        getDataBottom();
        mSongAdapter.notifyDataSetChanged();


        if (mMusicService != null&& mMusicService.isStatusPlay()) { //khi chạy nhạc
            mLlBottom.setVisibility(View.VISIBLE);
            mSongName.setText(mListSong.get(mMusicService.getmCurrentPlay()).getmSongName());                                  //Click item RecycleView
            mSongAuthor.setText(mListSong.get(mMusicService.getmCurrentPlay()).getmSongAuthor());
            byte[] songArt = getAlbumArt(mListSong.get(mMusicService.getmCurrentPlay()).getmSongArt());
            Glide.with(view.getContext()).asBitmap()
                    .error(R.drawable.ic_nct)
                    .load(songArt)
                    .into(mSongArt);
            if (!mMusicService.isStatusPlay()) {
                mBtnPay.setBackgroundResource(R.drawable.ic_subplay);
            }
        }

        mSongAdapter.setSongAdapter(
                new SongAdapter.IIClick() {
                    @Override
                    public void onItemClick(Song song, int pos) {

                        if(mMusicService!=null){
                            mMusicService.createChannel();
                            mMusicService.createNotification(getActivity(), song,true);

                        }

                        for (int i = 0; i < mListSong.size(); i++) {
                            mListSong.get(i).setmIsPlay(false);
                        }
                        mListSong.get(pos).setmIsPlay(true);

                        if (isVertical) {   //khi doc
                            if (mMusicService != null) {
                                mMusicService.playSong(song.getmSongArt());        //play nhac
                                mMusicService.setmCurrentPlay(pos);
                            }
                            mBtnPay.setBackgroundResource(R.drawable.ic_subpause);


                            mLlBottom.setVisibility(View.VISIBLE);
                            mSongName.setText(song.getmSongName());                                  //Click item RecycleView
                            mSongAuthor.setText(song.getmSongAuthor());
                            byte[] songArt = getAlbumArt(mListSong.get(pos).getmSongArt());
                            Glide.with(view.getContext()).asBitmap()
                                    .error(R.drawable.ic_nct)
                                    .load(songArt)
                                    .into(mSongArt);

                        } else {         //khi ngang

                            TextView mSongNameMedia = getActivity().findViewById(R.id.tv_song_name_media);
                            TextView mSongAuthorMedia = getActivity().findViewById(R.id.tv_songauthormedia); //ánh xạ bên media
                            ImageView mArtMedia = getActivity().findViewById(R.id.tv_ArtMedia);

                            mSongAdapter.notifyDataSetChanged();
                            if (mMusicService != null) {
                                mMusicService.playSong(song.getmSongArt());       //play nhac
                            }
                            mLlBottom.setVisibility(View.GONE);


                            mSongNameMedia.setText(song.getmSongName());
                            mSongAuthorMedia.setText(song.getmSongAuthor());
                            byte[] songArt = getAlbumArt(mListSong.get(pos).getmSongArt());         //gán dữ liệu khi xoay màn hình
                            Glide.with(view.getContext()).asBitmap()
                                    .error(R.drawable.ic_nct)
                                    .load(songArt)
                                    .into(mArtMedia);
                        }
                        mCurrentPosition = pos;

                    }

                    @Override
                    public void onSongBtnClickListener(ImageButton btn, View v, Song song, int pos) {

                        PopupMenu popup = new PopupMenu(v.getContext(), v);             //gán menu_popup  khi click vào các option
                        // Inflate the Popup using XML file.
                        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                             @Override
                                                             public boolean onMenuItemClick(MenuItem item) {                      //setClick cho option menu
                                                                 Toast.makeText(getActivity(),
                                                                         "item Click", Toast.LENGTH_SHORT).show();
                                                                 return false;

                                                             }
                                                         }
                        );
                    }
                }
        );

    }


    public static byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        byte[] albumArt = mediaMetadataRetriever.getEmbeddedPicture();   // chuyển đổi đường dẫn file media thành đường dẫn file Ảnh
        mediaMetadataRetriever.release();
        return albumArt;
    }

//    public void getSong() {
//        ContentResolver musicResolver = getActivity().getContentResolver();
//        Uri songUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Cursor songCursor = musicResolver.query(songUri, null, null, null, null);
//
//        if (songCursor != null && songCursor.moveToFirst()) {
//            int songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
//            int songName = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//            int songTime = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);           //Lấy Nhạc trong Local
//            int songAuthor = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//            int songArt = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//            do {
//                long currentId = songCursor.getLong(songID);
//                String currentName = songCursor.getString(songName);
//                String currentTime = songCursor.getString(songTime);
//                String currentAuthor = songCursor.getString(songAuthor);
//                String currentArt = songCursor.getString(songArt);
//                mListSong.add(new Song(currentId, currentName, currentTime, currentAuthor, currentArt, false));
//            } while (songCursor.moveToNext());
//            songSort(mListSong);
//        }
//    }
//
//    public void songSort(List<Song> list) {
//        for (int i = 0; i < list.size(); i++) {
//            for (int j = i + 1; j < list.size(); j++) {
//                if (list.get(i).getmSongName().compareTo(list.get(j).getmSongName()) > 0) {
//                    Collections.swap(list, i, j);
//                }
//
//            }
//        }
//
//    }

    @Override

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play: {
                if (mMusicService.isStatusPlay()) {
                    mMusicService.pauseSong();
                    mBtnPay.setBackgroundResource(R.drawable.ic_subplay);
                    mMusicService.createChannel();
                    mMusicService.createNotification(getActivity(), mListSong.get(mMusicService.getmCurrentPlay()), false);
                } else {
                    mMusicService.reSumSong();
                    mBtnPay.setBackgroundResource(R.drawable.ic_subpause);
                    mMusicService.createChannel();
                    mMusicService.createNotification(getActivity(), mListSong.get(mMusicService.getmCurrentPlay()),true);
                }

                break;
            }
            case R.id.bottom: {
                mCurrentPosition = mMusicService.getmCurrentPlay();
                Song song = mListSong.get(mCurrentPosition);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MediaPlaybackFragment mediaPlaybackFragment = MediaPlaybackFragment.newInstance(          //
                        song.getmSongName(), song.getmSongAuthor(), song.getmSongArt(), mCurrentPosition);
                mediaPlaybackFragment.setMusicService(mMusicService);
                mediaPlaybackFragment.setSongList(mListSong);
                mediaPlaybackFragment.setVertical(isVertical);
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();  // hide action bar
                fragmentTransaction.replace(R.id.content, mediaPlaybackFragment);    // get fragment MediaPlayBackFragment vào activity main
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
            default: {
            }
        }
    }

    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }

    @Override
    public void updateUI(int pos) {
        //update
    }
}
