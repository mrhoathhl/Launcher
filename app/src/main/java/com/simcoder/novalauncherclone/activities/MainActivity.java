package com.simcoder.novalauncherclone.activities;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.simcoder.novalauncherclone.R;
import com.simcoder.novalauncherclone.fragment.MediaFragment;
import com.simcoder.novalauncherclone.interfaces.Communicator;
import com.simcoder.novalauncherclone.manager.ResourceManager;
import com.simcoder.novalauncherclone.model.SongModel;
import com.simcoder.novalauncherclone.services.MusicService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Communicator {

    private Intent playIntent;
    ArrayList<SongModel> songFiles;
    MediaFragment miniPlayerFragment;
    FragmentManager manager;
    private MusicService musicSvc;
    private SongCompletedListener songCompletedListener;
    static MainActivity mSelf;
    boolean isLoadSong = false;

    public static MainActivity self() {
        return mSelf;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        Locale locale = new Locale("vi");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (savedInstanceState == null) {
            miniPlayerFragment = new MediaFragment();
//            songFiles = (ArrayList<SongModel>) getIntent().getSerializableExtra("songs");
            songFiles = ResourceManager.getInstance().songFiles;
            Collections.sort(ResourceManager.getInstance().songFiles);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isLoadSong && songFiles != null && songFiles.size() > 0 && playIntent == null) {
            isLoadSong = true;
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        if (songFiles != null && songFiles.size() > 0) {
//            show_list();
        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSvc = binder.getService();
            //pass list of song to service
            musicSvc.setList(songFiles);
            musicSvc.setSong(0);
//            show_list();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    //whenever user click song, next song or previous song this function
    //will update info of song
    private void updateSongInfo() {
        if (songFiles != null && songFiles.size() > 0) {
            if (miniPlayerFragment != null && miniPlayerFragment.isVisible()) {
                miniPlayerFragment.updateTags();
            }
        }
    }

    //show list song and miniplayer
    public void show_list() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.mediaFragment, miniPlayerFragment, null)
                .commit();
    }

    //get state to set play or pause when user click
    private void togglePlayPause() {
        musicSvc.togglePlayPause();
    }

    @Override
    public void playback_mode(int id, boolean status) {

    }

    @Override
    public void song_operations(int id) {
        switch (id) {
            case R.id.playBtn:
                togglePlayPause();
                break;
            case R.id.nextBtn:
                musicSvc.nextSong();
                updateSongInfo();
                break;
            case R.id.previousBtn:
                musicSvc.prevSong();
                updateSongInfo();
                break;
        }
    }

    @Override
    public void open_song(int position) {

    }

    @Override
    public ArrayList<SongModel> get_song_list() {
        return songFiles;
    }

    @Override
    public void set_progress(int i) {
        musicSvc.seekTo(i);
    }

    @Override
    public void set_volume(float vol) {

    }

    @Override
    public void goToPlayer() {

    }

    @Override
    public String get_artist() {
        return songFiles.get(musicSvc.playingIndex()).getArtist();
    }

    @Override
    public String get_album() {
        return songFiles.get(musicSvc.playingIndex()).getAlbum();
    }

    @Override
    public String get_title() {
        return songFiles.get(musicSvc.playingIndex()).getTitle();
    }

    @Override
    public byte[] get_album_art() {
        return songFiles.get(musicSvc.playingIndex()).getAlbum_Art(getBaseContext());
    }

    @Override
    public byte[] getSpecialArt(int i) {
        return songFiles.get(i).getAlbum_Art(getBaseContext());
    }

    @Override
    public int get_song_id() {
        return musicSvc.playingIndex();
    }

    @Override
    public int get_duration() {
        return musicSvc.getDuration();
    }

    @Override
    public int get_elapsed() {
        return musicSvc.getElapsed();
    }

    @Override
    public boolean is_playing() {
        return musicSvc.isPlaying();
    }

    @Override
    public boolean isSongExist() {
        return songFiles != null && songFiles.size() > 0;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (songCompletedListener == null) {
            songCompletedListener = new SongCompletedListener();
        }
        IntentFilter intentFilter = new IntentFilter("Refresh the Song Info");
        //register with system that they was listen event changed
        registerReceiver(songCompletedListener, intentFilter);
        updateSongInfo();
    } //if the song is pause this function will pause all other if

    // it involve with song complete listener
    @Override
    protected void onPause() {
        if (songCompletedListener != null) unregisterReceiver(songCompletedListener);
        super.onPause();
    }

    //create class to receive info of another source
    //this function will receive info of service send to it through broadcastreceiver
    private class SongCompletedListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Refresh the Song Info")) {
                updateSongInfo();
            }
        }
    }
}
