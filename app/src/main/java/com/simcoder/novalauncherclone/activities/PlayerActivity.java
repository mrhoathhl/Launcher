package com.simcoder.novalauncherclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.simcoder.novalauncherclone.R;
import com.simcoder.novalauncherclone.interfaces.Communicator;
import com.simcoder.novalauncherclone.model.SongModel;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity  {

    ArrayList<SongModel> songFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }

}