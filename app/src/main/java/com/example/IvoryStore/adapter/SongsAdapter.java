package com.example.IvoryStore.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.IvoryStore.IvoryDetailsActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.IvoryStore.R;
import com.example.IvoryStore.model.Song;
import com.example.IvoryStore.model.User;

import java.util.Iterator;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

    private final String TAG = "SongsAdapter";

    private List<SongWithKey> songsList;

    private User user;


    public SongsAdapter(List<SongWithKey> songsList, User user) {

        this.songsList = songsList;
        this.user = user;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e(TAG,"onCreateViewHolder() >>");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);

        Log.e(TAG,"onCreateViewHolder() <<");
        return new SongViewHolder(parent.getContext(),itemView);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {

        Log.e(TAG,"onBindViewHolder() >> " + position);

        Song song = songsList.get(position).getSong();
        String songKey = songsList.get(position).getKey();


        StorageReference thumbRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("thumbs/"+song.getThumbImage());
        // Load the image using Glide
        Glide.with(holder.getContext())
                .using(new FirebaseImageLoader())
                .load(thumbRef)
                .into(holder.getThumbImage());

        holder.setSelectedSong(song);
        holder.setSelectedSongKey(songKey);
        holder.getName().setText(song.getName());
        holder.setSongFile(song.getFile());
        holder.getGenre().setText(song.getGenre());
        holder.getArtist().setText(song.getArtist());
     
        holder.setThumbFile(song.getThumbImage());
        if (song.getReviewsCount() >0) {
            holder.getReviewsCount().setText("("+song.getReviewsCount()+")");
            holder.getRating().setRating((float)(song.getRating() / song.getReviewsCount()));
        }
        //Check if the user already purchased the song if set the text to Play
        //If not to BUY $X
        holder.getPrice().setText("$"+song.getPrice());

        Iterator i = user.getMySongs().iterator();
        while (i.hasNext()) {
            if (i.next().equals(songKey)) {
                holder.getPrice().setTextColor(R.color.colorPrimary);
                break;
            }
        }

        Log.e(TAG,"onBindViewHolder() << "+ position);
    }


    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {


        private CardView songCardView;
        private ImageView thumbImage;
        private TextView name;
        private TextView artist;
        private TextView genre;
        private TextView price;
        private TextView reviewsCount;
        private String songFile;
        private String thumbFile;
        private Context context;
        private RatingBar rating;
        private Song selectedSong;
        private String selectedSongKey;

        public SongViewHolder(Context context, View view) {

            super(view);

            songCardView = (CardView) view.findViewById(R.id.card_view_song);
            thumbImage = (ImageView) view.findViewById(R.id.song_thumb_image);
            name = (TextView) view.findViewById(R.id.song_name);
            artist = (TextView) view.findViewById(R.id.song_reviewer_mail);
            genre = (TextView) view.findViewById(R.id.song_genre);
            price = (TextView) view.findViewById(R.id.song_price);
            reviewsCount = (TextView) view.findViewById(R.id.song_review_count);
            rating = (RatingBar) view.findViewById(R.id.song_rating);



            this.context = context;

            songCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e(TAG, "CardView.onClick() >> name=" + selectedSong.getName());

                    Context context = view.getContext();
                    Intent intent = new Intent(context, IvoryDetailsActivity.class);
                    intent.putExtra("song", selectedSong);
                    intent.putExtra("key", selectedSongKey);
                    intent.putExtra("user",user);
                    context.startActivity(intent);
                }
            });
        }

        public TextView getPrice() {
            return price;
        }

        public TextView getName() {
            return name;
        }

        public ImageView getThumbImage() {
            return thumbImage;
        }

        public void setSongFile(String file) {
            this.songFile = file;
        }

        public TextView getArtist() {
            return artist;
        }

        public TextView getGenre() {
            return genre;
        }

        public void setThumbFile(String thumbFile) {
            this.thumbFile = thumbFile;
        }

        public Context getContext() {
            return context;
        }

        public RatingBar getRating() {
            return rating;
        }

        public void setSelectedSong(Song selectedSong) {
            this.selectedSong = selectedSong;
        }

        public void setSelectedSongKey(String selectedSongKey) {
            this.selectedSongKey = selectedSongKey;
        }

        public TextView getReviewsCount() {return reviewsCount;}
    }


}