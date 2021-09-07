package com.simcoder.novalauncherclone.interfaces;

import com.simcoder.novalauncherclone.model.SongModel;

import java.util.ArrayList;

public interface Communicator {
    void playback_mode(int id, boolean status);

    void song_operations(int id);

    void open_song(int position);

    ArrayList<SongModel> get_song_list();

    void set_progress(int i);

    void set_volume(float vol);

    void goToPlayer();

    String get_artist();

    String get_album();

    String get_title();

    byte[] get_album_art();

    byte[] getSpecialArt(int i);

    int get_song_id();

    int get_duration();

    int get_elapsed();

    boolean is_playing();

    boolean isSongExist();
}