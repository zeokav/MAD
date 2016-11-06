package com.example.saket.musicplayer.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.saket.musicplayer.R;

import java.util.ArrayList;

/**
 * Created by saket on 11/6/2016.
 */
public class SongAdapter extends BaseAdapter {

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
