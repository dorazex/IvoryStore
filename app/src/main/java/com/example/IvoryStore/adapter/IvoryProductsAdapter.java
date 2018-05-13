package com.example.IvoryStore.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.IvoryStore.IvoryDetailsActivity;
import com.example.IvoryStore.model.IvoryProduct;
import com.example.IvoryStore.model.Review;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.IvoryStore.R;
import com.example.IvoryStore.model.User;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IvoryProductsAdapter extends RecyclerView.Adapter<IvoryProductsAdapter.IvoryProductViewHolder> {

    private final String TAG = "IvoryProductsAdapter";

    private List<IvoryProductWithKey> productsList;
    private HashMap<String, Integer> reviewsCountMap;

    private User user;

    public IvoryProductsAdapter(List<IvoryProductWithKey> productsList, User user) {

        this.productsList = productsList;
        this.user = user;
        this.reviewsCountMap = new HashMap<String, Integer>() {};
    }

    private void populateReviewsCountMap(String prodKey){
        final String productKey = prodKey;
        DatabaseReference productReviewsRef = FirebaseDatabase.getInstance().getReference(
                "Products/" + productKey +"/reviews");
        productReviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange() >> Products/" + productKey);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    reviewsCountMap.put(productKey, reviewsCountMap.get(productKey) + 1);
                }
                Log.e(TAG, "onDataChange(Review) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
            }
        });

    }

    @Override
    public IvoryProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e(TAG,"onCreateViewHolder() >>");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ivory_product, parent, false);

        Log.e(TAG,"onCreateViewHolder() <<");
        return new IvoryProductViewHolder(parent.getContext(),itemView);
    }

    @Override
    public void onBindViewHolder(final IvoryProductViewHolder holder, int position) {

        Log.e(TAG,"onBindViewHolder() >> " + position);

        final IvoryProduct ivoryProduct = productsList.get(position).getIvoryProduct();
        final String productKey = productsList.get(position).getKey();

//        populateReviewsCountMap(productKey);


        StorageReference thumbRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("product_image/"+ ivoryProduct.getImage());

        StorageReference mImageRef = thumbRef;
        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
//                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        holder.getImageView().setMinimumHeight(dm.heightPixels);
                        holder.getImageView().setMinimumWidth(dm.widthPixels);
                        holder.getImageView().setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        holder.setSelectedIvoryProduct(ivoryProduct);
        holder.setSelectedProductKey(productKey);
        holder.getName().setText(ivoryProduct.getName());
        holder.getDeathReason().setText(ivoryProduct.getDeathReason());
        holder.getElephantAge().setText(Integer.toString(ivoryProduct.getElephantAge()));
        holder.setImage(ivoryProduct.getImage());

        if (reviewsCountMap.get(productKey)==null) reviewsCountMap.put(productKey, 0);
        DatabaseReference productReviewsRef = FirebaseDatabase.getInstance().getReference("Products/" + productKey +"/reviews");
        productReviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange() >> Products/" + productKey);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    reviewsCountMap.put(productKey, reviewsCountMap.get(productKey) + 1);
                }
                if (reviewsCountMap.get(productKey) > 0) {
                    holder.getReviewsCount().setText("("+ reviewsCountMap.get(productKey)+")");
                    reviewsCountMap.put(productKey, 0);
                }

                Log.e(TAG, "onDataChange(Review) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
            }
        });

        //Check if the user already purchased the ivoryProduct if set the text to Play
        //If not to BUY $X
        holder.getOrigin().setText("$"+ ivoryProduct.getPrice());

        Iterator i = user.getProducts().iterator();
        while (i.hasNext()) {
            if (i.next().equals(productKey)) {
                holder.getOrigin().setTextColor(R.color.colorLightGrey);
                break;
            }
        }

        Log.e(TAG,"onBindViewHolder() << "+ position);
    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class IvoryProductViewHolder extends RecyclerView.ViewHolder {


        private CardView ivoryProductCardView;
        private ImageView imageView;
        private TextView name;
        private TextView elephantAge;
        private TextView deathReason;
        private TextView origin;
        private TextView reviewsCount;

        private String image;
        private Context context;
        private IvoryProduct selectedIvoryProduct;
        private String selectedProductKey;

        public IvoryProductViewHolder(Context context, View view) {

            super(view);

            ivoryProductCardView = (CardView) view.findViewById(R.id.card_view_product);
            imageView = (ImageView) view.findViewById(R.id.product_image);
            name = (TextView) view.findViewById(R.id.product_name);
            elephantAge = (TextView) view.findViewById(R.id.product_elephant_age);
            deathReason = (TextView) view.findViewById(R.id.product_death_reason);
            origin = (TextView) view.findViewById(R.id.product_origin);
            reviewsCount = (TextView) view.findViewById(R.id.product_review_count);



            this.context = context;

            ivoryProductCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e(TAG, "CardView.onClick() >> name=" + selectedIvoryProduct.getName());

                    Context context = view.getContext();
                    Intent intent = new Intent(context, IvoryDetailsActivity.class);
                    intent.putExtra("ivoryProduct", selectedIvoryProduct);
                    intent.putExtra("key", selectedProductKey);
                    intent.putExtra("user",user);
                    context.startActivity(intent);
                }
            });
        }

        public TextView getOrigin() {
            return origin;
        }

        public TextView getName() {
            return name;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getElephantAge() {
            return elephantAge;
        }

        public TextView getDeathReason() {
            return deathReason;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Context getContext() {
            return context;
        }

        public void setSelectedIvoryProduct(IvoryProduct selectedIvoryProduct) {
            this.selectedIvoryProduct = selectedIvoryProduct;
        }

        public void setSelectedProductKey(String selectedProductKey) {
            this.selectedProductKey = selectedProductKey;
        }

        public TextView getReviewsCount() {return reviewsCount;}
    }


}
