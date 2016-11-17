package com.example.saket.musicplayer;

import android.app.SearchManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.saket.musicplayer.utils.Song;

public class SongActivity extends AppCompatActivity {

    public MusicService mService;
    private Intent mIntent;
    private ImageButton pauseBtn;
    private TextView songText, artistText, titleText;
    private SeekBar seekBar;
    Song song;
    ActionBar header;
    boolean isAlive;
    Vibrator vibrator;
    boolean isHaptic;
    private SharedPreferences options;

    /**
     * Handler to update the UI elements depending on the worker thread.
     */
    final Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onSongFinish();
            super.handleMessage(msg);
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {

        /**
         * When service connected, set seek bar and start the thread.
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder mBinder = (MusicService.MusicBinder)iBinder;
            mService = mBinder.getService();
            pauseBtn = (ImageButton)findViewById(R.id.pause);
            seekBar.setMax(mService.getDuration()/1000);
            isAlive = true;
            CounterThread cThread = new CounterThread();
            Thread counter = new Thread(cThread);
            counter.start();
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


    /**
     * Worker thread to maintain timer and to swithce to next song on finish
     */
    private class CounterThread implements Runnable {
        @Override
        public void run() {
            while(isAlive) {
                try {
                    if(!mService.getSongName().equals(song.getSongTitle())) {
                        Message msg = uiHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putParcelable("song_info", mService.getSong());
                        msg.setData(b);
                        uiHandler.sendMessage(msg);
                    }
                    seekBar.setProgress(mService.getCurrPosn()/1000);
                    Thread.sleep(1000);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        isAlive = false;
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_playing);

        options = getSharedPreferences("PLAYERPREFS", 0);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        isHaptic = options.getBoolean("haptic", false);

        song = getIntent().getParcelableExtra("song_info");
        songText = (TextView) findViewById(R.id.songname);
        artistText = (TextView) findViewById(R.id.artistname);
        songText.setText(song.getSongTitle());
        artistText.setText(song.getSongArtist());
        header = getSupportActionBar();
        seekBar = (SeekBar)findViewById(R.id.seekBar);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Do nothing
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            /**
             * Seek to last position
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mService.seek(progress*1000);
            }
        });

        assert header != null;
        header.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        header.setCustomView(R.layout.head_layout);
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
        isAlive = false;
        super.onDestroy();
    }

    /**
     * Pause music, or play if paused. Change the drawable depending on the state.
     */
    public void pause(View v) {
        if(isHaptic) {
            vibrator.vibrate(100);
        }
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

    @Override
    protected void onRestart() {
        seekBar.setMax(mService.getDuration()/1000);
        super.onRestart();
    }

    /**
     * Change the UI elements after song changed.
     */
    public void onSongFinish() {
        pauseBtn.setImageResource(R.drawable.ic_pause_white_36dp);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert header != null;
        header.setTitle(mService.getSongName());

        songText.setText(mService.getSongName());
        artistText.setText(mService.getArtistName());
        titleText.setText(mService.getSongName());
        seekBar.setMax(mService.getDuration()/1000);
    }

    /**
     * Go to the next song
     */
    public void next(View v) {
        if(isHaptic) {
            vibrator.vibrate(100);
        }
        mService.nextSong();
        pauseBtn.setImageResource(R.drawable.ic_pause_white_36dp);

        assert header != null;
        header.setTitle(mService.getSongName());

        song = mService.getSong();
        songText.setText(song.getSongTitle());
        artistText.setText(song.getSongArtist());
        titleText.setText(song.getSongTitle());
        seekBar.setMax(mService.getDuration()/1000);
    }

    /**
     * Go to the previous song
     */
    public void prev(View v) {
        if(isHaptic) {
            vibrator.vibrate(100);
        }
        mService.prevSong();
        pauseBtn.setImageResource(R.drawable.ic_pause_white_36dp);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mService.getSongName());

        song = mService.getSong();
        songText.setText(song.getSongTitle());
        artistText.setText(song.getSongArtist());
        titleText.setText(song.getSongTitle());
        seekBar.setMax(mService.getDuration()/1000);
    }

    public void search(View v) {
        try{
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, song.getSongTitle());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
