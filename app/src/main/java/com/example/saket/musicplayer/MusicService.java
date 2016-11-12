package com.example.saket.musicplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.saket.musicplayer.utils.Song;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by saket on 10/20/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    public static final int notifierId = 1;
    private MediaPlayer mPlayer;
    private ArrayList<Song> playList;
    private int currPosn;
    private final IBinder mBind = new MusicBinder();
    private boolean isPaused = false;
    private boolean isSetup = false;
    public static final String pref_tag = "PLAYERPREFS";

    public void onCreate() {
        super.onCreate();
        currPosn = 0;
        mPlayer = new MediaPlayer();

        // Initialise player
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.stop();
        mPlayer.release();
        return true;
    }

    public void setPlaylist(ArrayList<Song> playList) {
        this.playList = playList;
    }

    public void startAt(int position) {
        currPosn = position;
        startSong();
        isSetup = true;
    }

    public void startSong() {
        mPlayer.reset();
        Song toPlay = playList.get(currPosn);
        Uri path = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, toPlay.getSongId());

        try {
            mPlayer.setDataSource(getApplicationContext(), path);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "IO Error", Toast.LENGTH_SHORT).show();
            return;
        }

        mPlayer.prepareAsync();
        NotificationCompat.Builder notifier =  makeNotification(toPlay, true);
        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mgr.notify(notifierId, notifier.build());
    }

    public NotificationCompat.Builder makeNotification(Song toPlay, boolean persistent) {
        NotificationCompat.Builder notifier = new NotificationCompat.Builder(this);
        notifier.setContentText(toPlay.getSongArtist());
        notifier.setContentTitle(toPlay.getSongTitle());
        notifier.setSmallIcon(R.drawable.notificon);
        notifier.setOngoing(persistent);

        Intent onSelect = new Intent(this, SongActivity.class);
        onSelect.putExtra("song_info", toPlay);

        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, onSelect, PendingIntent.FLAG_UPDATE_CURRENT);
        notifier.setContentIntent(resultIntent);
        return notifier;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void pauseSong() {
        mPlayer.pause();
        NotificationCompat.Builder notifier = makeNotification(playList.get(currPosn), false);
        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mgr.notify(notifierId, notifier.build());
        isPaused = true;
    }

    public void stopSong() {
        mPlayer.stop();
        isSetup = false;
    }

    public void nextSong() {
        SharedPreferences pref = getSharedPreferences(pref_tag, 0);
        boolean is_shuffle = pref.getBoolean("shuffle", false);
        if(is_shuffle) {
            Random r = new Random();
            currPosn = r.nextInt(playList.size());
        }
        else {
            currPosn = (currPosn + 1)%playList.size();
        }
        startAt(currPosn);
    }

    public void prevSong() {
        currPosn -= 1;
        if(currPosn < 0) {
            currPosn = playList.size() - 1;
        }
        startAt(currPosn);
    }

    public void resumeSong() {
        if(isSetup) {
            NotificationCompat.Builder notifier =  makeNotification(playList.get(currPosn), true);
            NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            mgr.notify(notifierId, notifier.build());
            mPlayer.start();
            isPaused = false;
        }
        else {
            startAt(currPosn);
        }

    }

    // Event handlers

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    // Binder Class

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
