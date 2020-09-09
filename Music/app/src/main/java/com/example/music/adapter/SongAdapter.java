package com.example.music.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.model.Song;
import com.example.music.service.MediaPlaybackService;

import java.util.List;

import es.claucookie.miniequalizerlibrary.EqualizerView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private Context mContext;
    private List<Song> mListSong;
    private IIClick mListener;

    public SongAdapter(Context mContext, List<Song> mListSong) {
        this.mContext = mContext;
        this.mListSong = mListSong;
    }

    public void setSongAdapter(IIClick mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mListSong.get(position).ismIsPlay()) return 1;   /*trả về viewType cho onCreateViewHolder*/
        return 0;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.item_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongAdapter.ViewHolder holder, final int position) {
        holder.binData(mListSong.get(position), position);


    }
    @Override
    public int getItemCount() {
        return mListSong.size();
    }

    private String getDuration(String time) {
        long duration = Long.parseLong(time);
        int minutes = (int) (duration / 1000 / 60);
        int seconds = (int) ((duration / 1000) % 60);
        if(seconds<10)
        {
            String seconds2="0"+seconds;
            return minutes + ":" + seconds2;
        }
        return minutes + ":" + seconds;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mSongName;
        private TextView mSongTime;
        private  TextView mSongID;
        private ImageView mImageID;
        private ImageButton mOption;
        private EqualizerView equalizer;

        public ViewHolder(@NonNull View itemView) {   // ánh xạ  dữ liệu
            super(itemView);
            mSongName = itemView.findViewById(R.id.tv_songName);
            mSongTime = itemView.findViewById(R.id.tv_songTime);
            mSongID = itemView.findViewById(R.id.tv_songID);
            mOption = itemView.findViewById(R.id.ib_option);
         //   mImageID= itemView.findViewById(R.id.tv_imageID);
            equalizer = (EqualizerView) itemView.findViewById(R.id.tv_imageID);


        }

        public void binData(final Song song, final int pos) {

            mSongID.setText(String.valueOf(pos + 1));        //set dữ liệu cho từng item
            mSongName.setText(song.getmSongName() + "");
            mSongTime.setText(getDuration(song.getmSongTime()));
            if(song.ismIsPlay())
            {
                equalizer.animateBars();
                mSongID.setVisibility(View.INVISIBLE);
                equalizer.setVisibility(View.VISIBLE);
                mSongName.setTypeface(null, Typeface.BOLD);
            }
            else   equalizer.stopBars();


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(song, pos);
                }
            });
            mOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onSongBtnClickListener(mOption, view, song , pos);
                    }
                }
            });
        }
    }

    public interface IIClick {
        void onItemClick(Song song, int pos);
        void onSongBtnClickListener(ImageButton btn, View v, Song song, int pos);
    }

}