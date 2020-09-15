package com.example.music.fragment;


import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.fragment.app.Fragment;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.music.ActivityMusic;
import com.example.music.R;
import com.example.music.adapter.SongAdapter;
import com.example.music.model.Song;
import com.example.music.service.MediaPlaybackService;

import java.util.List;

public class AllSongsFragment extends Fragment implements View.OnClickListener, MediaPlaybackService.IUpdateUI,MediaPlaybackFragment.IUpdateAllSong,
        MediaPlaybackService.INextAndPreNotification,MediaPlaybackService.IPauseNotification

{

    private RecyclerView mRcvSong;
    private List<Song> mListSong;
    private SongAdapter mSongAdapter;
    private RelativeLayout mLlBottom;
    private ImageView mSongArt;
    private int mCurrentPosition;
    private Button mBtnPay;
    private TextView mSongName, mSongAuthor;
    private boolean isVertical = false;
    public MediaPlaybackService mMusicService;


    public void setListSong(List<Song> mListSong) {
        this.mListSong = mListSong;
    }


    public void setMusicService(MediaPlaybackService mMusicService) {
        this.mMusicService = mMusicService;
    }

    public void setSongAdapter(SongAdapter mSongAdapter) {
        this.mSongAdapter = mSongAdapter;
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

    //get activity
    private ActivityMusic getActivityMusic() {
        if (getActivity() instanceof ActivityMusic) {
            return (ActivityMusic) getActivity();
        }
        return null;
    }


    public AllSongsFragment() {
    }

    public void setData() {
        mMusicService = getMusicService();
        mListSong = getListSong();
        mSongAdapter = getSongAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_song_fragment, container, false);
        setData();
        initView(view);
        setDataBottom();
        if (mMusicService!=null&&mSongAdapter != null) {
            mMusicService.setINextAndPreNotification(AllSongsFragment.this);
            mMusicService.setIPauseNotification(AllSongsFragment.this);
            mSongAdapter.notifyDataSetChanged();

        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //     Log.d("AllSongFragment", mListSong.size()+"ServiceConnection"+ mMusicService);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setDataBottom() {

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
        //  mSongAdapter = new SongAdapter(getActivity(), mListSong);
        mRcvSong.setAdapter(mSongAdapter);
        setDataBottom();
        if (mSongAdapter != null) {
            mSongAdapter.notifyDataSetChanged();
        }


        if (mMusicService != null && mMusicService.isStatusPlay()) { //khi chạy nhạc
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
        if (mSongAdapter != null) {
            mSongAdapter.setSongAdapter(
                    new SongAdapter.IIClick() {
                        @Override
                        public void onItemClick(Song song, int pos) {

                            if (mMusicService != null) {
                                mMusicService.createChannel();
                                mMusicService.createNotification(getActivity(), song, true);

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


    }


    public static byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        byte[] albumArt = mediaMetadataRetriever.getEmbeddedPicture();   // chuyển đổi đường dẫn file media thành đường dẫn file Ảnh
        mediaMetadataRetriever.release();
        return albumArt;
    }


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
                    mMusicService.createNotification(getActivity(), mListSong.get(mMusicService.getmCurrentPlay()), true);
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

    @Override
    public void updateAllSong(int pos) {
        for (int i = 0; i < mListSong.size(); i++) {
            mListSong.get(i).setmIsPlay(false);
        }
        mListSong.get(pos).setmIsPlay(true);                            //update khi media next

        mSongAdapter.notifyDataSetChanged();

    }

    @Override
    public void updateNotificationWhenNextAndPre(int pos) {

        //update Notification next  and pre
        for (int i = 0; i < mListSong.size(); i++) {
            mListSong.get(i).setmIsPlay(false);
        }
        mListSong.get(pos).setmIsPlay(true);

        mSongAdapter.notifyDataSetChanged();
        setDataBottom();
    }

    @Override
    public void updateNotificationWhenPause(int pos) {
                                                                           //update Notification when pause
        setDataBottom();                                                 //update Notification next  and pre
        mSongAdapter.notifyDataSetChanged();
    }
}
