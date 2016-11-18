package com.example.saket.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by saket on 11/18/2016.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent homeScreen = new Intent(SplashActivity.this, HomeActivity.class);

        SharedPreferences prefs = getSharedPreferences("PLAYERPREFS", 0);
        boolean disableSplash = prefs.getBoolean("splash", false);
        if(disableSplash) {
            startActivity(homeScreen);
            finish();
        }
        else {
            TextView title = (TextView)findViewById(R.id.splash_title);
            ImageView icon = (ImageView)findViewById(R.id.splash_icon);
            Animation splashAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_anim);
            Animation splashAnimDelayed = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_anim_delayed);

            title.startAnimation(splashAnim);
            icon.startAnimation(splashAnimDelayed);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(homeScreen);
                    finish();
                }
            }, 1500);
        }
    }
}
