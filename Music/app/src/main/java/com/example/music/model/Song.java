package com.example.music.model;

public class Song {
    private long mSongID;

    public long getSongIDProvider() {
        return mSongIDProvider;
    }

    public void setSongIDProvider(long mSongIDProvider) {
        this.mSongIDProvider = mSongIDProvider;
    }

    private long mSongIDProvider;
    private String mSongName;
    private String mSongTime;
    private String mSongAuthor;
    private String mSongArt;
    private boolean mIsPlay;

    public long getmSongIDProvider() {
        return mSongIDProvider;
    }

    public void setmSongIDProvider(long mSongIDProvider) {
        this.mSongIDProvider = mSongIDProvider;
    }

    public int getCountOfPlay() {
        return mCountOfPlay;
    }

    public void setCountOfPlay(int mCountOfPlay) {
        this.mCountOfPlay = mCountOfPlay;
    }

    public void setmIsPause(boolean mIsPause) {
        this.mIsPause = mIsPause;
    }

    private int mCountOfPlay;



    private boolean mIsPause;

    public Song(long mSongID,long mSongIDProvider, String mSongName, String mSongTime, String mSongAuthor, String mSongArt, boolean mIsPlay,boolean mIsPause,int mCountOfPlay) {
        this.mSongID = mSongID;
        this.mSongIDProvider=mSongIDProvider;
        this.mSongName = mSongName;
        this.mSongTime = mSongTime;
        this.mSongAuthor = mSongAuthor;
        this.mSongArt = mSongArt;
        this.mIsPlay = mIsPlay;
        this.mIsPause=mIsPause;
        this.mCountOfPlay=mCountOfPlay;
    }


    public void setIsPause(boolean mIsPause) {
        this.mIsPause = mIsPause;
    }

    public boolean ismIsPause() {
        return mIsPause;
    }



    public boolean ismIsPlay() {
        return mIsPlay;
    }

    public void setmIsPlay(boolean mIsPlay) {
        this.mIsPlay = mIsPlay;
    }

    public long getmSongID() {
        return mSongID;
    }

    public void setmSongID(long mSongID) {
        this.mSongID = mSongID;
    }

    public String getmSongName() {
        return mSongName;
    }

    public void setmSongName(String mSongName) {
        this.mSongName = mSongName;
    }

    public String getmSongTime() {
        return mSongTime;
    }

    public void setmSongTime(String mSongTime) {
        this.mSongTime = mSongTime;
    }

    public String getmSongAuthor() {
        return mSongAuthor;
    }

    public void setmSongAuthor(String mSongAuthor) {
        this.mSongAuthor = mSongAuthor;
    }

    public String getmSongArt() {
        return mSongArt;
    }

    public void setmSongArt(String mSongArt) {
        this.mSongArt = mSongArt;
    }


}
