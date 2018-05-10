package com.example.IvoryStore.adapter;

import com.example.IvoryStore.model.Song;


public class SongWithKey {
    private String key;
    private Song song;

    public SongWithKey(String key, Song song) {
        this.key = key;
        this.song = song;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
