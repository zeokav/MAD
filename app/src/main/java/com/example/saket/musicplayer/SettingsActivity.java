package com.example.saket.musicplayer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    Switch shuffleSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        shuffleSwitch = (Switch)findViewById(R.id.shuffle);
        shuffleSwitch.setChecked(true);
        shuffleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffleSwitch.setChecked(false);
            }
        });
    }

}
