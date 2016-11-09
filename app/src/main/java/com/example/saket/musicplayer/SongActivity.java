package com.example.saket.musicplayer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.saket.musicplayer.utils.Song;

import java.util.ArrayList;

public class SongActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_playing);
        Song song = getIntent().getParcelableExtra("song_info");
        mediaPlayer = new MediaPlayer();
        if(song != null) {
            try {
                mediaPlayer.setDataSource(song.getSongPath());
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.prepareAsync();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "IOError", Toast.LENGTH_LONG).show();
            }

        }
        else {
            Toast.makeText(getApplicationContext(), "There was an error!", Toast.LENGTH_LONG).show();
        }
    }
//
//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
//            mService = binder.getService();
////            mService.setList();
//            musicBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            musicBound = false;
//        }
//    };

}
