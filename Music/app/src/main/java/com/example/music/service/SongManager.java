package com.example.music.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;


import com.example.music.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SongManager {


    private List<Song> mListSong = new ArrayList<>();
    private MediaPlayer mPlayer;
    private boolean isStatusPlay = false;
    private int mCurrentPlay = -1;
    private Context mContext;
    private boolean isPause =false;

    public void setmListSong(List<Song> mListSong) {
        this.mListSong = mListSong;
    }

    public List<Song> getmListSong() {
        return mListSong;
    }


    public int getmCurrentPlay() {
        return mCurrentPlay;
    }


    public boolean isStatusPlay() {
        return isStatusPlay;
    }

    public void setmCurrentPlay(int mCurrentPlay) {
        this.mCurrentPlay = mCurrentPlay;
    }

    public List<Song> getDataMusic() {
        return mListSong;
    }

    public MediaPlayer getPlayer() {
        return mPlayer;
    }

    public void seekTo(int position) {
        mPlayer.seekTo(position);
    }
    public  int getCurrentStreamPosition(){
        if(mPlayer!=null)
        return mPlayer.getCurrentPosition();  //trả về vtri đang phát
        return 0;
    }

    public long getDuration(){
       if(mPlayer!=null)
            return mPlayer.getDuration();      //trả về vtri cuối

        return 0;
    }

    public SongManager(Context mContext) {
        this.mContext = mContext;
        initMediaPlayer();
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
                Log.d("media","complete");
                int current= getmCurrentPlay() +1;
                mPlayer.reset();
                setmCurrentPlay(current);
                String pathNext=mListSong.get(current).getmSongArt();
                setmCurrentPlay(current);
                playSong(pathNext);
                mIUpdateUI.updateUI(current);
            }
        });
    }

    public void playSong(String path) {
        mPlayer.reset();
        try {
            mPlayer.setDataSource(path);            //run media
            mPlayer.prepare();
            isStatusPlay = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        mPlayer.pause();                           //pause media
        isStatusPlay = false;
        isPause =true;
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

    public void nextSong(int pos) {
        pos++;
        if (pos > mListSong.size() - 1) {
            pos = 0;                           //next media
        }
        mCurrentPlay = pos;
        playSong(mListSong.get(pos).getmSongArt());
    }

    public void previousSong(int pos) {
        pos--;
        if (pos < 0) {
            pos = mListSong.size() - 1;
        }
        mCurrentPlay = pos;
        playSong(mListSong.get(pos).getmSongArt());
    }

    public interface IUpdateUI{
        void updateUI(int pos);
    }
    private IUpdateUI mIUpdateUI;
    public void setIUpdateUI(IUpdateUI mIUpdateUI) {
        this.mIUpdateUI = mIUpdateUI;
    }



}
