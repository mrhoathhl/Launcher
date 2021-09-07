package com.simcoder.novalauncherclone.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.simcoder.novalauncherclone.R;
import com.simcoder.novalauncherclone.manager.ResourceManager;
import com.simcoder.novalauncherclone.model.SongModel;

import java.util.ArrayList;

public class SplashActivity extends Activity {

    int numFilesFound = 0;
    ProgressBar pbLoading;
    TextView tvFound;
    ArrayList<SongModel> songFiles;
    private static final int MY_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        songFiles = new ArrayList<SongModel>();
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        tvFound = (TextView) findViewById(R.id.tvFound);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent main = new Intent(getBaseContext(), MainActivity.class);
            startActivity(main);
        } else {
            checkPermission();
        }
    }

    //stop scan if this activity was end by user
    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }


    //define and scan music from device
    private void fetch_list() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbLoading.setMax(musicCursor.getCount());
            }
        });

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
                    numFilesFound++;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvFound.setText("Found " + numFilesFound + " songs...");
                        pbLoading.setProgress(numFilesFound);
                    }
                });
            }
            while (musicCursor.moveToNext());
            //display progress bar while scan music
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvFound.setText("Found " + numFilesFound + " files...");
                    pbLoading.setProgress(musicCursor.getCount());
                }
            });
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
            Intent main = new Intent(getBaseContext(), MainActivity.class);
            startActivity(main);
            System.out.println("SPlash");
            finish();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fetch_list();
            return null;
        }
    }

    //check permission to ask user can scan device or not
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
        } else {
            new AsyncContentResolve().execute();
        }
    }

    //do after get permission from user
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        new AsyncContentResolve().execute();
                    }
                } else {
                    Toast.makeText(this, "No Permisstion Granted", Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }
        }
    }

}
