package com.example.saket.musicplayer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

/**
 * This class is responsible for setting the user preferences.
 * All preferences can be changed here.
 */
public class SettingsActivity extends AppCompatActivity {

    Switch shuffleSwitch;
    Switch hapticSwitch;
    Switch loopSwitch;

    SharedPreferences preferences;

    boolean isShuffle;
    boolean isHaptic;
    boolean isRepeat;

    private void setSavedPrefs() {
        preferences = getSharedPreferences(MusicService.pref_tag, 0);
        isShuffle = preferences.getBoolean("shuffle", false);
        isHaptic = preferences.getBoolean("haptic", false);
        isRepeat = preferences.getBoolean("repeat", false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        setSavedPrefs();

        final SharedPreferences.Editor editor = preferences.edit();

        shuffleSwitch = (Switch)findViewById(R.id.shuffle);
        shuffleSwitch.setChecked(isShuffle);

        hapticSwitch = (Switch)findViewById(R.id.haptic);
        hapticSwitch.setChecked(isHaptic);

        loopSwitch = (Switch) findViewById(R.id.repeat);
        loopSwitch.setChecked(isRepeat);


        /**
         * Listeners for the toggles. On changing, save in shared preferences.
         */
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

        hapticSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHaptic) {
                    hapticSwitch.setChecked(false);
                    editor.putBoolean("haptic", false);
                    isHaptic = false;
                    editor.apply();
                }
                else {
                    hapticSwitch.setChecked(true);
                    editor.putBoolean("haptic", true);
                    isHaptic = true;
                    editor.apply();
                }
            }
        });

        loopSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRepeat) {
                    loopSwitch.setChecked(false);
                    editor.putBoolean("repeat", false);
                    isRepeat = false;
                    editor.apply();
                }
                else {
                    loopSwitch.setChecked(true);
                    editor.putBoolean("repeat", true);
                    isRepeat = true;
                    editor.apply();
                }
            }
        });
    }

}
