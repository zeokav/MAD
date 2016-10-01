package com.example.saket.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class Song {

    /*
        Class for a specific song, hold information about the song.
     */

    private long id;
    private String title,artist;

    public Song(long songID,String songTitle, String songArtist) {
        id=songID;
        artist=songArtist;
        title=songTitle;
    }


    // Getters
    public long getSongId() {
        return id;
    }
    public String getSongArtist() {
        return artist;
    }
    public String getSongTitle() {
        return title;
    }
}



class SongAdapter extends BaseAdapter {

    /*
        List of songs on device plugged inside an adapter
     */

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public  SongAdapter(Context c, ArrayList<Song> theSongs) {
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }



    /*
        Getters
     */
    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLay= (LinearLayout) songInf.inflate(R.layout.song, parent,false);
        TextView songView = (TextView)songLay.findViewById(R.id.songTitle);
        TextView artistView = (TextView)songLay.findViewById(R.id.songArtist);
        Song currSong = songs.get(position);
        songView.setText(currSong.getSongTitle());
        artistView.setText(currSong.getSongArtist());
        songLay.setTag(position);
        return songLay;
    }
}


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Song> songList;
    private ListView songView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // If permissions are denied, show toast and exit out of the activity.
        if(!isStoragePermissionGranted()) {
            Toast.makeText(getApplicationContext(), "Could not get permissions", Toast.LENGTH_LONG).show();
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        songView = (ListView) findViewById(R.id.song_list);
        songList = new ArrayList<>();

        getSongList();

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
                Toast.makeText(getApplicationContext(), "Selected: " + i, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SongActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            Log.d("Menu", "Clicked");
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
        } else if(id == R.id.exit_app) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,null,null,null,null);
        //get columns
        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        if(musicCursor !=null && musicCursor.moveToFirst()) {
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            } while (musicCursor.moveToNext());
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT > 22) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG, "Permission is granted");
            return true;
        }
    }
}
