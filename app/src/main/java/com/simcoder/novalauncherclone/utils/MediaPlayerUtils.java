package com.simcoder.novalauncherclone.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class MediaPlayerUtils {

    // "mysong.mp3" in directory "raw".
    public static final String RAW_MEDIA_SAMPLE = "mysong";

    // Example Path: /sdcard/Music/mysong.mp3
    // Example Path: /storage/emulated/0/DCIM/Music/mysong.mp3
    public static final String LOCAL_MEDIA_SAMPLE = "/storage/emulated/0/Music";
    public static final String URL_MEDIA_SAMPLE = "https://raw.githubusercontent.com/o7planning/webexamples/master/_testdatas_/yodel.mp3";

    public static final String LOG_TAG = "MediaPlayerTutorial";

    // Play a media in directory RAW.
    // Media name = "mysong.mp3" ==> resName = "mysong".
    public static void playRawMedia(Context context, MediaPlayer mediaPlayer, String resName) {
        try {
// ID of video file.
            int id = MediaPlayerUtils.getRawResIdByName(context, resName);

            Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + id);
            Log.i(LOG_TAG, "Media URI: " + uri);
            Toast.makeText(context, "Select source: " + uri, Toast.LENGTH_SHORT).show();

            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error Play Raw Media: " + e.getMessage());
            Toast.makeText(context, "Error Play Raw Media: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Example Path: /sdcard/Music/mysong.mp3
    // Example Path: /storage/emulated/0/DCIM/Music/mysong.mp3
    public static void playLocalMedia(Context context, MediaPlayer mediaPlayer, String localPath) {
        try {
            Uri uri = Uri.parse("android.resource://" + localPath);
            Log.i(LOG_TAG, "Media URI: " + uri);
            Toast.makeText(context, "Select source: " + uri, Toast.LENGTH_SHORT).show();

            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error Play Local Media: " + e.getMessage());
            Toast.makeText(context, "Error Play Local Media: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // String videoURL = "https://raw.githubusercontent.com/o7planning/webexamples/master/_testdatas_/yodel.mp3";
    public static void playURLMedia(Context context, MediaPlayer mediaPlayer, String videoURL) {
        try {
            Log.i(LOG_TAG, "Media URL: " + videoURL);

            Uri uri = Uri.parse(videoURL);
            Toast.makeText(context, "Select source: " + uri, Toast.LENGTH_SHORT).show();

            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error Play URL Media: " + e.getMessage());
            Toast.makeText(context, "Error Play URL Media: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Find ID corresponding to the name of the resource (in the directory RAW).
    public static int getRawResIdByName(Context context, String resName) {
        String pkgName = context.getPackageName();
// Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName, "raw", pkgName);

        Log.i(LOG_TAG, "Res Name: " + resName + "==> Res ID = " + resID);
        return resID;
    }
}