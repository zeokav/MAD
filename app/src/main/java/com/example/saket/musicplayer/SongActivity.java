package com.example.saket.musicplayer;

import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.saket.musicplayer.utils.Song;

public class SongActivity extends AppCompatActivity {

    public MusicService mService;
    private Intent mIntent;
    private boolean isPaused;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder mBinder = (MusicService.MusicBinder)iBinder;
            mService = mBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_playing);
        Song song = getIntent().getParcelableExtra("song_info");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mIntent == null) {
            mIntent = new Intent(this, MusicService.class);
            bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
            startService(mIntent);
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    public void pause(View v) {
        if(mService.isPaused()) {
            mService.resumeSong();
        }
        else {
            mService.pauseSong();
        }
    }

    public void stop(View v) {
        mService.stopSong();
    }

    public void next(View v) {
        mService.nextSong();
    }

    public void prev(View v) {
        mService.prevSong();
    }

}
