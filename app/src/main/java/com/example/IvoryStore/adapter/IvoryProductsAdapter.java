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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.IvoryStore.IvoryDetailsActivity;
import com.example.IvoryStore.model.IvoryProduct;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.IvoryStore.R;
import com.example.IvoryStore.model.User;

import java.util.Iterator;
import java.util.List;

public class IvoryProductsAdapter extends RecyclerView.Adapter<IvoryProductsAdapter.IvoryProductViewHolder> {

    private final String TAG = "IvoryProductsAdapter";

    private List<IvoryProductWithKey> productsList;

    private User user;

    public IvoryProductsAdapter(List<IvoryProductWithKey> productsList, User user) {

        this.productsList = productsList;
        this.user = user;
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
    public void onBindViewHolder(IvoryProductViewHolder holder, int position) {

        Log.e(TAG,"onBindViewHolder() >> " + position);

        IvoryProduct ivoryProduct = productsList.get(position).getIvoryProduct();
        String productKey = productsList.get(position).getKey();


        StorageReference thumbRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("product_image/"+ ivoryProduct.getImage());
        // Load the imageView using Glide
        Glide.with(holder.getContext())
                .using(new FirebaseImageLoader())
                .load(thumbRef)
                .into(holder.getImageView());

        holder.setSelectedIvoryProduct(ivoryProduct);
        holder.setSelectedProductKey(productKey);
        holder.getName().setText(ivoryProduct.getName());
        holder.getDeathReason().setText(ivoryProduct.getDeathReason());
        holder.getElephantAge().setText(ivoryProduct.getElephantAge());
     
        holder.setImage(ivoryProduct.getImage());
        if (ivoryProduct.getReviewsCount() >0) {
            holder.getReviewsCount().setText("("+ ivoryProduct.getReviewsCount()+")");
        }
        //Check if the user already purchased the ivoryProduct if set the text to Play
        //If not to BUY $X
        holder.getOrigin().setText("$"+ ivoryProduct.getPrice());

        Iterator i = user.getUserProducts().iterator();
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
            elephantAge = (TextView) view.findViewById(R.id.product_reviewer_mail);
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
                    intent.putExtra("product", selectedIvoryProduct);
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
