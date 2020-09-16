package com.example.music.model;

public class Song {
    private long mSongID;
    private String mSongName;
    private String mSongTime;
    private String mSongAuthor;
    private String mSongArt;
    private boolean mIsPlay;



    private boolean mIsPause;

    public Song(long mSongID, String mSongName, String mSongTime, String mSongAuthor, String mSongArt, boolean mIsPlay,boolean mIsPause) {
        this.mSongID = mSongID;
        this.mSongName = mSongName;
        this.mSongTime = mSongTime;
        this.mSongAuthor = mSongAuthor;
        this.mSongArt = mSongArt;
        this.mIsPlay = mIsPlay;
        this.mIsPause=mIsPause;
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
