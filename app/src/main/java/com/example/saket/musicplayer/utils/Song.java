package com.example.saket.musicplayer.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that stores data about a song.
 */

public class Song implements Parcelable {
    private long id;
    private String title, artist, path;
    private int duration;

    // Parcelable for transferring song information through intent object.
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(title);
        out.writeString(artist);
        out.writeString(path);
        out.writeInt(duration);
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel parcel) {
            return new Song(parcel);
        }

        @Override
        public Song[] newArray(int i) {
            return new Song[i];
        }
    };

    private Song(Parcel parcel) {
        id = parcel.readLong();
        title = parcel.readString();
        artist = parcel.readString();
        path = parcel.readString();
        duration = parcel.readInt();
    }

    public Song(long songID,String songTitle, String songArtist, String songPath, int songDuration) {
        id = songID;
        artist = songArtist;
        title = songTitle;
        path = songPath;
        duration = songDuration;
    }

    /**
     * Returns ID of the song.
     * @return id
     */
    public long getSongId() {
        return id;
    }

    /**
     * Returns artist of the song
     * @return artist name
     */
    public String getSongArtist() {
        return artist;
    }

    /**
     * Returns name of song
     * @return Song name
     */
    public String getSongTitle() {
        return title;
    }

    /**
     * Returns path of song
     * @return Song path
     */
    public String getSongPath() {
        return path;
    }

    /**
     * Returns duration of current song
     * @return Song duration
     */
    public int getDuration() {
        return duration;
    }

}
