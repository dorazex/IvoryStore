package com.example.IvoryStore.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Song implements Parcelable {

    private String name;
    private String artist;
    private String genre;
    private String file;
    private String thumbImage;
    private int price;
    private int rating;
    private int reviewsCount;
    private Map<String,Review> reviews;

    public Song(String name, String artist, String genre, String file, String thumbImage, int price, int rating,int reviewsCount, Map<String,Review> reviews) {
        this.name = name;
        this.artist = artist;
        this.genre = genre;
        this.file = file;
        this.thumbImage = thumbImage;
        this.price = price;
        this.rating = rating;
        this.reviewsCount = reviewsCount;
        this.reviews = reviews;
    }

    public Song() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genere) {
        this.genre = genere;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getReviewsCount() { return reviewsCount; }

    public void incrementReviewCount() { reviewsCount++;}

    public void incrementRating(int newRating) { rating+=newRating;}

    public Map<String, Review> getReviews() { return reviews; }

    public void setReviews(Map<String, Review> reviews) { this.reviews = reviews; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(artist);
        parcel.writeString(genre);
        parcel.writeString(file);
        parcel.writeString(thumbImage);
        parcel.writeInt(price);
        parcel.writeInt(rating);
        parcel.writeInt(reviewsCount);
    }

    private Song(Parcel in){
        this.name = in.readString();
        this.artist = in.readString();
        this.genre = in.readString();
        this.file = in.readString();
        this.thumbImage = in.readString();
        this.price = in.readInt();
        this.rating = in.readInt();
        this.reviewsCount = in.readInt();

    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };


}
