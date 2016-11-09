package com.example.saket.musicplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.example.saket.musicplayer.utils.Song;

public class SongActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_playing);
        Song song = getIntent().getParcelableExtra("song_info");

    }

}
