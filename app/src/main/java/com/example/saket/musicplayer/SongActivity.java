package com.example.saket.musicplayer;

import android.support.v7.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.saket.musicplayer.utils.Song;

public class SongActivity extends AppCompatActivity {

    public MusicService mService;
    private Intent mIntent;
    private ImageButton pauseBtn;
    private TextView songText, artistText, titleText;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder mBinder = (MusicService.MusicBinder)iBinder;
            mService = mBinder.getService();
            pauseBtn = (ImageButton)findViewById(R.id.pause);
            if(mService.isPaused()) {
                pauseBtn.setImageResource(R.drawable.ic_play_arrow_white_36dp);
            }
            else {
                pauseBtn.setImageResource(R.drawable.ic_pause_white_36dp);
            }
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
        songText = (TextView) findViewById(R.id.songname);
        artistText = (TextView) findViewById(R.id.artistname);
        songText.setText(song.getSongTitle());
        artistText.setText(song.getSongArtist());

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.head_layout);
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText(song.getSongTitle());

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
        pauseBtn = (ImageButton)findViewById(R.id.pause);
        if(mService.isPaused()) {
            pauseBtn.setImageResource(R.drawable.ic_pause_white_36dp);
            mService.resumeSong();
        }
        else {
            pauseBtn.setImageResource(R.drawable.ic_play_arrow_white_36dp);
            mService.pauseSong();
        }
    }

    public void next(View v) {
        mService.nextSong();
        pauseBtn.setImageResource(R.drawable.ic_pause_white_36dp);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mService.getSongName());

        songText.setText(mService.getSongName());
        artistText.setText(mService.getArtistName());
        titleText.setText(mService.getSongName());
    }

    public void prev(View v) {
        mService.prevSong();
        pauseBtn.setImageResource(R.drawable.ic_pause_white_36dp);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mService.getSongName());

        songText.setText(mService.getSongName());
        artistText.setText(mService.getArtistName());
        titleText.setText(mService.getSongName());
    }

}
