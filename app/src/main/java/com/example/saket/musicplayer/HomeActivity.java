package com.example.saket.musicplayer;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.saket.musicplayer.utils.Song;
import com.example.saket.musicplayer.utils.SongAdapter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class HomeActivity extends AppCompatActivity {

    private ArrayList<Song> songList;
    private ListView songView;
    String TAG;
    public MusicService mService;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TAG = "Permissions";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<>();

        if(!isStoragePermissionGranted()) {
            Toast.makeText(getApplicationContext(), "Looking for permissions!", Toast.LENGTH_LONG).show();
        }
        else {
            getSongList();
            setSongList();
        }
    }

    // Bind service when app starts.
    @Override
    protected void onStart() {
        super.onStart();
        if(mIntent == null) {
            mIntent = new Intent(this, MusicService.class);
            bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
            startService(mIntent);
        }
    }


    // Clear service binding on destruction
    @Override
    protected void onDestroy() {
        stopService(mIntent);
        unbindService(mConnection);
        mService = null;

        // Remove persistence from notification
        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mgr.cancel(MusicService.notifierId);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
        } else if(id == R.id.exit_app) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,null,null,null,null);

        // Get column IDs.
        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int path = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int duration = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        if(musicCursor !=null && musicCursor.moveToFirst()) {
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisPath = musicCursor.getString(path);
                int thisDuration = musicCursor.getInt(duration);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisPath, duration));
            } while (musicCursor.moveToNext());
        }
    }

    public void setSongList() {
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getSongTitle().compareTo(o2.getSongTitle());
            }
        });

        SongAdapter songAdapter = new SongAdapter(this,songList);
        songView.setAdapter(songAdapter);

        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), SongActivity.class);
                intent.putExtra("song_info", songList.get(i));
                startActivity(intent);
                mService.startAt(i);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            getSongList();
            setSongList();
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT > 22) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder mBinder = (MusicService.MusicBinder)iBinder;
            mService = mBinder.getService();
            mService.setPlaylist(songList);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: Disconnected.");
        }
    };
}
