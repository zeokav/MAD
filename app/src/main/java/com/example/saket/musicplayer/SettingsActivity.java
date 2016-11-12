package com.example.saket.musicplayer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    Switch shuffleSwitch;
    SharedPreferences preferences;
    boolean isShuffle;

    private void setSavedPrefs() {
        preferences = getSharedPreferences(MusicService.pref_tag, 0);
        isShuffle = preferences.getBoolean("shuffle", false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        setSavedPrefs();

        final SharedPreferences.Editor editor = preferences.edit();

        shuffleSwitch = (Switch)findViewById(R.id.shuffle);
        shuffleSwitch.setChecked(isShuffle);

        shuffleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShuffle) {
                    shuffleSwitch.setChecked(false);
                    editor.putBoolean("shuffle", false);
                    isShuffle = false;
                    editor.apply();
                }
                else {
                    shuffleSwitch.setChecked(true);
                    editor.putBoolean("shuffle", true);
                    isShuffle = true;
                    editor.apply();
                }
            }
        });
    }

}
