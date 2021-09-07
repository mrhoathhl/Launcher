package com.simcoder.novalauncherclone.fragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.simcoder.novalauncherclone.R;
import com.simcoder.novalauncherclone.interfaces.Communicator;
import com.simcoder.novalauncherclone.manager.ResourceManager;
import com.simcoder.novalauncherclone.model.SongModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MediaFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    RelativeLayout rlMiniPlayer;
    TextView nameSong;
    TextView singer;
    TextView lengthSong;
    TextView timePlayed;
    Communicator comm;

    private TextView textCurrentPosition;
    private Button playBtn;
    private Button previousBtn;
    private Button nextBtn;
    private ImageView avaSong;

    private SeekBar seekBar;
    private Handler threadHandler = new Handler();
    private MediaPlayer mediaPlayer;
    ArrayList<String> songNames = new ArrayList<>();
    private String mediaPath = Environment.getExternalStorageDirectory().getPath() + "/Music/";
    private List<String> songs = new ArrayList<String>();

    private Intent playIntent;
    AsyncPlay asyncPlay;
    int new_progress;
    boolean skip_progress_updates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        comm = (Communicator) getActivity();
        textCurrentPosition = view.findViewById(R.id.timePlayed);
        lengthSong = view.findViewById(R.id.lengthSong);
        timePlayed = view.findViewById(R.id.timePlayed);
        nameSong = view.findViewById(R.id.nameSong);
        singer = view.findViewById(R.id.singer);
        playBtn = view.findViewById(R.id.playBtn);
        previousBtn = view.findViewById(R.id.previousBtn);
        nextBtn = view.findViewById(R.id.nextBtn);
        seekBar = view.findViewById(R.id.seekBar);
        avaSong = view.findViewById(R.id.avaSong);
        assert comm != null;
        if (comm.isSongExist()) {
            avaSong.setOnClickListener(this);
            playBtn.setOnClickListener(this);
            previousBtn.setOnClickListener(this);
            nextBtn.setOnClickListener(this);
            seekBar.setOnSeekBarChangeListener(this);
            if (comm.is_playing()) {
                playBtn.setBackgroundResource(R.drawable.ic_pause_24dp);
            } else {
                playBtn.setBackgroundResource(R.drawable.play);
            }
            updateTags();
        } else {
            new AsyncContentResolve().execute();
        }
        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avaSong:
                comm.goToPlayer();
                break;
            case R.id.playBtn:
//                if (ResourceManager.getInstance().songFiles != null && ResourceManager.getInstance().songFiles.size() > 0) {
                comm.song_operations(R.id.playBtn);//sync 2 button in miniplayer and mainplayer is the same status
                updatePlayBtnState();
                updateTags();
//                    updateAlbumArt();
//                }
                break;
            case R.id.previousBtn:
//                if (ResourceManager.getInstance().songFiles != null && ResourceManager.getInstance().songFiles.size() > 0) {
                comm.song_operations(R.id.previousBtn);//sync 2 button in miniplayer and mainplayer is the same status
                updatePlayBtnState();
                updateTags();
//                updateAlbumArt();
//                }
                break;
            case R.id.nextBtn:
//                if (ResourceManager.getInstance().songFiles != null && ResourceManager.getInstance().songFiles.size() > 0) {
                comm.song_operations(R.id.nextBtn);//sync 2 button in miniplayer and mainplayer is the same status
                updatePlayBtnState();
                updateTags();
//                updateAlbumArt();
//                }
                break;
        }
        seekBar.setProgress(comm.get_elapsed());
        updateTimers(comm.get_elapsed());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            new_progress = i;
            updateTimers(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        skip_progress_updates = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        comm.set_progress(new_progress);
        skip_progress_updates = false;
    }

    @Override
    public void onResume() {
        asyncPlay = new AsyncPlay();
        asyncPlay.execute();
        try {
            if (comm != null && !comm.is_playing()) {
                updateTimers(comm.get_elapsed());
                playBtn.setBackgroundResource(R.drawable.play);
            }
        } catch (Exception e) {
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //define and scan music from device
    private void fetch_list() {
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        //initialization a object song
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int displayNameColumn = musicCursor.getColumnIndex
                    (String.valueOf(MediaStore.Audio.Media.DISPLAY_NAME));
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisFileName = musicCursor.getString(displayNameColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String thisDuration = musicCursor.getString(durationColumn);
                // Just for the heck of it, showing off the loading bar.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Filter out songs less than 5 seconds in length.
                if (thisDuration != null && Integer.valueOf(thisDuration) > 5000) {
                    ResourceManager.getInstance().songFiles.add(new SongModel(thisId, thisTitle, thisFileName, thisArtist, thisAlbum, thisDuration));
                }
            }
            while (musicCursor.moveToNext());
        }
    }

    private class AsyncContentResolve extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        //after scan music in device it will transfer data to
        //main activity
        @Override
        protected void onPostExecute(Void aVoid) {
            // Write results to Internal Storage for future usage.
            //write_data();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fetch_list();
            return null;
        }
    }

    //do in parallel with main thread
    //detect when user do their action in seekbar
    //this will listen and change value with value user done
    public class AsyncPlay extends AsyncTask<Void, Void, Void> {
        //on start this function will set max for seekbar
        @Override
        protected void onPreExecute() {
//            seekBar.setMax(comm.get_duration());
        }

        //in background this function will check state of seekbar and
        //state of song is play or not
        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                if (skip_progress_updates) {
                    continue;
                }
                // Revert to original colors on playing.
                //update current time of song was play

                try {
                    if (comm.is_playing()) {
                        seekBar.setProgress(comm.get_elapsed());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTimers(comm.get_elapsed());
                            }
                        });
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception ignored) {
                }
            }
        }
    }

    //get current time
    private void updateTimers(int progress) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        timePlayed.setText(" " + dateFormat.format(progress) + " ");
    }


    //update avatar of song, if song have not avatar it will replace with
    //default avatar
    public void updateAlbumArt() {
    }

    //update info of song
    //name song
    //name artist
    //time duration
    public void updateTags() {
        nameSong.setText(comm.get_title());
        singer.setText(comm.get_artist());
        lengthSong.setText(comm.get_song_list().get(comm.get_song_id()).getDuration());
        byte[] bytes = comm.get_album_art();
        if (bytes == null)
            avaSong.setImageDrawable(getResources().getDrawable(R.drawable.splash));
        else
            avaSong.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    public void updatePlayBtnState() {
        if (comm.is_playing()) {
            playBtn.setBackgroundResource(R.drawable.ic_pause_24dp);
        } else {
            playBtn.setBackgroundResource(R.drawable.play);
        }
    }

    //set max of seekbar by the lenght of song
    public void setMaxDuration(int duration) {
        seekBar.setMax(duration);
    }
}