package com.example.music.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObservable;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.music.database.MusicDatabase;
import com.example.music.database.MusicProvider;
import com.example.music.model.Song;
import com.example.music.service.MediaPlaybackService;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BaseSongsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class BaseSongsFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, View.OnClickListener, MediaPlaybackService.IUpdateUI, MediaPlaybackFragment.IUpdateAllSong,
        MediaPlaybackService.INextAndPreNotification, MediaPlaybackService.IPauseNotification, MediaPlaybackFragment.IUpdateAllSongWhenPlayMedia,
        MediaPlaybackFragment.IUpdateAllSongWhenPauseMedia, MediaPlaybackService.IUpdateAllSongWhenAutoNext {


    protected RecyclerView mRcvSong;
    protected List<Song> mListSong;
    protected SongAdapter mSongAdapter;
    protected RelativeLayout mLlBottom;
    protected ImageView mSongArt;
    protected int mCurrentPosition;
    protected Button mBtnPay;
    protected TextView mSongName, mSongAuthor, mImageID, mTextView;
    protected boolean isVertical = false;
    public MediaPlaybackService mMusicService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public abstract void updateAdapter();

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    protected boolean isFavorite = false;

    //get activity
    protected ActivityMusic getActivityMusic() {
        if (getActivity() instanceof ActivityMusic) {
            return (ActivityMusic) getActivity();
        }
        return null;
    }

    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }


    protected MediaPlaybackService getMusicService() {
        return getActivityMusic().getMusicService();
    }

    protected List<Song> getListSong() {
        return getActivityMusic().getListSong();
    }

    protected SongAdapter getSongAdapter() {
        return getActivityMusic().getSongAdapter();
    }

    public void setData() {
        mMusicService = getMusicService();
        mListSong = getListSong();
        mSongAdapter = getSongAdapter();
    }


    public static BaseSongsFragment newInstance(String param1, String param2) {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        sharedPreferences = getActivity().getSharedPreferences("DATA_CURRENT_PLAY", getActivity().MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.all_song_fragment, container, false);
        setData();
        initView(view);
        setDataBottom();
        if (mMusicService != null && mSongAdapter != null) {
            mMusicService.setINextAndPreNotification(BaseSongsFragment.this);
            mMusicService.setIPauseNotification(BaseSongsFragment.this);
            mMusicService.setIUpdateUI(BaseSongsFragment.this);
            mMusicService.setIUpdateAllSongWhenAutoNext(BaseSongsFragment.this);
            mSongAdapter.notifyDataSetChanged();

        }
        return view;
    }

    public void saveData() {

        Log.d("AllSongOk", "" + mMusicService.getCurrentStreamPosition());
        editor.remove("DATA_CURRENT");           //luu position dang phat
        editor.putInt("DATA_CURRENT", mMusicService.getCurrentPlay());
        editor.remove("DATA_CURRENT_STREAM_POSITION");
        editor.putInt("DATA_CURRENT_STREAM_POSITION", mMusicService.getCurrentStreamPosition());
        editor.commit();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
    }

    public static byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        byte[] albumArt = mediaMetadataRetriever.getEmbeddedPicture();   // chuyển đổi đường dẫn file media thành đường dẫn file Ảnh
        mediaMetadataRetriever.release();
        return albumArt;
    }

    public void setItemWhenPause(long id) {
        for (int i = 0; i < mListSong.size(); i++) {
            mListSong.get(i).setmIsPlay(false);
            mListSong.get(i).setIsPause(false);
        }
        for (int i = 0; i < mListSong.size(); i++) {
            if (mListSong.get(i).getmSongID() == id) {
                mListSong.get(i).setIsPause(true);
            }
        }

        mSongAdapter.notifyDataSetChanged();
    }

    public void setItemWhenPlay(long id) {
        for (int i = 0; i < mListSong.size(); i++) {
            mListSong.get(i).setmIsPlay(false);
            mListSong.get(i).setIsPause(false);
        }
        for (int i = 0; i < mListSong.size(); i++) {
            if (mListSong.get(i).getmSongID() == id) {
                mListSong.get(i).setmIsPlay(true);
            }
        }


        mSongAdapter.notifyDataSetChanged();
    }


    private void setDataBottom() {
        if (mMusicService != null && mListSong.size() > 0) {
            if (mMusicService.getCurrentPlay() < 0) {
                int current = sharedPreferences.getInt("DATA_CURRENT", -1);
                Log.d("ClickPlay", "" + current);
                if (current > -1) {
                    if (isVertical) {
                        mLlBottom.setVisibility(View.VISIBLE);
                        mMusicService.setCurrentPlay(current);
                    }
                    mSongName.setText(mMusicService.getListSong().get(current).getmSongName());                         //Click item RecycleView
                    mSongAuthor.setText(mMusicService.getListSong().get(current).getmSongAuthor());
                    byte[] songArt = getAlbumArt(mMusicService.getListSong().get(current).getmSongArt());
                    Glide.with(getContext()).asBitmap()
                            .error(R.drawable.ic_nct)
                            .load(songArt)
                            .into(mSongArt);
                    mBtnPay.setBackgroundResource(R.drawable.ic_subplay);
                    setItemWhenPause(mMusicService.getListSong().get(current).getmSongID());

                }
            }

            if (mMusicService.getCurrentPlay() >= 0 && mListSong.size() > 0) {     //khi chạy nhạc
                if (isVertical)
                    mLlBottom.setVisibility(View.VISIBLE);
                else mLlBottom.setVisibility(View.GONE);
                mSongName.setText(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongName());                         //Click item RecycleView
                mSongAuthor.setText(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongAuthor());
                byte[] songArt = getAlbumArt(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongArt());
                Glide.with(getContext()).asBitmap()
                        .error(R.drawable.ic_nct)
                        .load(songArt)
                        .into(mSongArt);

                if (mMusicService.isStatusPlay()) {
                    mBtnPay.setBackgroundResource(R.drawable.ic_subpause);

                    setItemWhenPlay(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongID());
                } else {
                    mBtnPay.setBackgroundResource(R.drawable.ic_subplay);
                    setItemWhenPause(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongID());

                }

            }
        }


    }
    public void inSert(Song song){
        ContentValues values = new ContentValues();
        values.put(MusicDatabase.ID_PROVIDER, song.getSongIDProvider());
        values.put(MusicDatabase.ID, song.getmSongID());
        values.put(MusicDatabase.TITLE, song.getmSongName());
        values.put(MusicDatabase.ARTIST, song.getmSongAuthor());
        values.put(MusicDatabase.DATA, song.getmSongArt());
        values.put(MusicDatabase.DURATION, song.getmSongTime());
        values.put(MusicDatabase.IS_FAVORITE, 2);
        getContext().getContentResolver().insert(MusicProvider.CONTENT_URI, values);
        Toast.makeText(getActivity().getApplicationContext(), "Added favorites list",
                Toast.LENGTH_SHORT).show();
    }

    private void initView(final View view) {
        mSongArt = view.findViewById(R.id.img_bottomArt);
        mRcvSong = view.findViewById(R.id.rcv_Song);
        mSongName = view.findViewById(R.id.tv_bottom_songName);
        mSongAuthor = view.findViewById(R.id.tv_bottom_song_author);                    //Ánh Xạ
        mLlBottom = view.findViewById(R.id.bottom);
        mBtnPay = view.findViewById(R.id.btn_play);
        mImageID = view.findViewById(R.id.tv_imageItem_pause);
        mTextView = view.findViewById(R.id.text_favorite_song);

        mBtnPay.setOnClickListener(this);
        mLlBottom.setOnClickListener(this);

        updateAdapter();

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
            mSongName.setText(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongName());                                  //Click item RecycleView
            mSongAuthor.setText(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongAuthor());
            byte[] songArt = getAlbumArt(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongArt());
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

                            final int id = (int) song.getmSongID();
                            final Uri uri = Uri.parse(MusicProvider.CONTENT_URI + "/" + id);
                            final Cursor cursor = getContext().getContentResolver().query(uri,
                                    null, null, null, null);
                            ContentValues values = new ContentValues();
                            int count= cursor.getColumnIndex(MusicDatabase.COUNT_OF_PLAY);
                            Log.d("COUNT_OF_PLAY","ok"+count);
                            values.put(MusicDatabase.COUNT_OF_PLAY,count);
                            getContext().getContentResolver().update(MusicProvider.CONTENT_URI,
                                    values,MusicDatabase.ID + "=" + id,null );
                     if(count>=3){
                        // inSert(song);
                     }
                            if (mMusicService != null) {
                                mMusicService.createChannel();
                                mMusicService.createNotification(getActivity(), song, true);
                                mMusicService.setCurrentPlay(pos);
                                mMusicService.playSong(song.getmSongArt());        //play nhac
                                mBtnPay.setBackgroundResource(R.drawable.ic_subpause);
                                setItemWhenPlay(song.getmSongID());

                            }

                            if (isVertical) {   //khi doc

                                mLlBottom.setVisibility(View.VISIBLE);
                                mSongName.setText(song.getmSongName());                                  //Click item RecycleView
                                mSongAuthor.setText(song.getmSongAuthor());
                                byte[] songArt = getAlbumArt(mListSong.get(pos).getmSongArt());
                                Glide.with(view.getContext()).asBitmap()
                                        .error(R.drawable.ic_nct)
                                        .load(songArt)
                                        .into(mSongArt);

                            } else {         //khi ngang

                                iUpdateMediaWhenAllSongClickItem.UpdateMediaWhenAllSongClickItem(pos);

                                TextView mSongNameMedia = getActivity().findViewById(R.id.tv_song_name_media);
                                TextView mSongAuthorMedia = getActivity().findViewById(R.id.tv_songauthormedia); //ánh xạ bên media
                                ImageView mArtMedia = getActivity().findViewById(R.id.tv_ArtMedia);
                                ImageView mImageBackground = getActivity().findViewById(R.id.img_background);

                                mSongAdapter.notifyDataSetChanged();
                                if (mMusicService != null) {
                                    mMusicService.setCurrentPlay(pos);
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
                                Glide.with(view.getContext()).asBitmap()
                                        .error(R.drawable.ic_nct)
                                        .load(songArt)
                                        .into(mImageBackground);
                            }
                            mCurrentPosition = pos;
                        }

                        @Override
                        public void onSongBtnClickListener(ImageButton btn, View v, Song song, int pos) {
                            updatePopupMenu(v, song, pos);
                        }
                    }
            );
        }
    }

    protected abstract void updatePopupMenu(View v, Song song, int pos);

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_play: {

                Log.d("ClickPlay", "" + mMusicService.getCurrentPlay());
                if (mMusicService.isStatusPlay()) {
                    setItemWhenPause(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongID());
                    mMusicService.pauseSong();
                    mBtnPay.setBackgroundResource(R.drawable.ic_subplay);
                    mMusicService.createChannel();
                    mMusicService.createNotification(getActivity(), mMusicService.getListSong().get(mMusicService.getCurrentPlay()), false);
                } else {
                    setItemWhenPlay(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongID());
                    if (mMusicService.isResume())
                        mMusicService.reSumSong();
                    else {
                        mMusicService.playSong(mMusicService.getListSong().get(mMusicService.getCurrentPlay()).getmSongArt());
                        int position = sharedPreferences.getInt("DATA_CURRENT_STREAM_POSITION", 0);
                        Log.d("DATA_CURRENT", "" + position);
                        mMusicService.seekTo(position);

                    }

                    mBtnPay.setBackgroundResource(R.drawable.ic_subpause);
                    mMusicService.createChannel();
                    mMusicService.createNotification(getActivity(), mMusicService.getListSong().get(mMusicService.getCurrentPlay()), true);
                }

                break;
            }
            case R.id.bottom: {
                mCurrentPosition = mMusicService.getCurrentPlay();
                Log.d("Bottom", "" + mCurrentPosition);
                Song song = mMusicService.getListSong().get(mCurrentPosition);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MediaPlaybackFragment mediaPlaybackFragment = MediaPlaybackFragment.newInstance(
                        song.getmSongName(), song.getmSongAuthor(), song.getmSongArt(), mCurrentPosition);
                mediaPlaybackFragment.setMusicService(mMusicService);

                mediaPlaybackFragment.setSongList(mListSong);
                mediaPlaybackFragment.setVertical(isVertical);

                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();  // hide action bar
                fragmentTransaction.replace(R.id.content, mediaPlaybackFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
            default: {
            }
        }
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onQueryTextChange(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSongAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void updateAllSong(int pos) {

       setItemWhenPlay(mMusicService.getListSong().get(pos).getmSongID());

        mSongAdapter.notifyDataSetChanged();
        setDataBottom();

    }

    @Override
    public void updateAllSongWhenPauseMedia(int pos) {
        setItemWhenPause(mMusicService.getListSong().get(pos).getmSongID());

    }

    @Override
    public void updateAllSongWhenPlayMedia(int pos) {
        setItemWhenPlay(mMusicService.getListSong().get(pos).getmSongID());

    }

    @Override
    public void updateNotificationWhenNextAndPre(int pos) {

        //update Notification next  and pre
      setItemWhenPlay(mMusicService.getListSong().get(pos).getmSongID());
        setDataBottom();
    }

    @Override
    public void updateNotificationWhenPause(int pos) {
        //update Notification when pause
        setItemWhenPause(mMusicService.getListSong().get(pos).getmSongID());
        setDataBottom();                                                 //update Notification next  and pre
        mSongAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateUI(int pos) { //update when auto next
        Log.d("AllSongNext", "ok ok");
        setItemWhenPause(mMusicService.getListSong().get(pos).getmSongID());                 //update  khi auto next
        setDataBottom();
        mMusicService.createChannel();
        mMusicService.createNotification(getActivity(), mMusicService.getListSong().get(pos), true);

    }

    @Override
    public void updateAllSongWhenAutoNext(int pos) {
        Log.d("AllSongFag", "okokokok");

        setItemWhenPlay(mMusicService.getListSong().get(pos).getmSongID());
        setDataBottom();

    }

    public interface IUpdateMediaWhenAllSongClickItem {
        void UpdateMediaWhenAllSongClickItem(int pos);
    }


    private BaseSongsFragment.IUpdateMediaWhenAllSongClickItem iUpdateMediaWhenAllSongClickItem;

    /* method này để đảm bảo activity sẽ giao tiếp với allSongFragment thông qua interface IUpdateMediaWhenAllSongClickItem*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseSongsFragment.IUpdateMediaWhenAllSongClickItem) {
            iUpdateMediaWhenAllSongClickItem = (BaseSongsFragment.IUpdateMediaWhenAllSongClickItem) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IUpdateMediaWhenAllSongClickItem");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        iUpdateMediaWhenAllSongClickItem = null;
    }
}