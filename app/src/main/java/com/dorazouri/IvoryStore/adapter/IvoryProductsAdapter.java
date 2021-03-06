package com.dorazouri.IvoryStore.adapter;

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

import com.dorazouri.IvoryStore.IvoryDetailsActivity;
import com.dorazouri.IvoryStore.model.IvoryProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.dorazouri.IvoryStore.R;
import com.dorazouri.IvoryStore.model.User;

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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ivory_product, parent, false);
        return new IvoryProductViewHolder(parent.getContext(),itemView);
    }

    @Override
    public void onBindViewHolder(final IvoryProductViewHolder holder, int position) {
        final IvoryProduct ivoryProduct = productsList.get(position).getIvoryProduct();
        final String productKey = productsList.get(position).getKey();

        Log.d(TAG,String.format("onBindViewHolder called for product %s in position %s",
                productKey, position));

        StorageReference imageRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("product_image/"+ ivoryProduct.getImage());

        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        DisplayMetrics dm = new DisplayMetrics();
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
        holder.getNameTextView().setText(ivoryProduct.getName());
        holder.getOriginContinentTextView().setText(ivoryProduct.getOriginContinent());
        holder.getElephantAgeTextView().setText("Age in death: " + Integer.toString(ivoryProduct.getElephantAge()));

        if (ivoryProduct.getReviews() != null) {
            int reviewsCount = ivoryProduct.getReviews().size();
            if (reviewsCount > 0) {
                holder.getReviewsCountTextView().setText("(" + reviewsCount + " reviews)");
            }
        }

        //Check if the user already purchased the ivoryProduct if set the text to demonstrate
        //If not to BUY $X
        holder.getPriceTextView().setText("$"+ ivoryProduct.getPrice());

        Iterator i = user.getProducts().iterator();
        for (String pKey:
                user.getProducts()) {
            if (pKey.equals(productKey)){
                holder.getPriceTextView().setText("Owned");
                break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class IvoryProductViewHolder extends RecyclerView.ViewHolder {


        private CardView ivoryProductCardView;
        private ImageView imageView;
        private TextView nameTextView;
        private TextView elephantAgeTextView;
        private TextView originContinentTextView;
        private TextView priceTextView;
        private TextView reviewsCountTextView;

        private Context context;
        private IvoryProduct selectedIvoryProduct;
        private String selectedProductKey;

        private IvoryProductViewHolder(Context context, View view) {

            super(view);

            ivoryProductCardView = (CardView) view.findViewById(R.id.cardViewProduct);
            imageView = (ImageView) view.findViewById(R.id.productImage);
            nameTextView = (TextView) view.findViewById(R.id.productName);
            elephantAgeTextView = (TextView) view.findViewById(R.id.productElephantAge);
            originContinentTextView = (TextView) view.findViewById(R.id.productOriginContinent);
            priceTextView = (TextView) view.findViewById(R.id.productPrice);
            reviewsCountTextView = (TextView) view.findViewById(R.id.productReviewCount);

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

        private TextView getPriceTextView() {
            return priceTextView;
        }

        private TextView getNameTextView() {
            return nameTextView;
        }

        private ImageView getImageView() {
            return imageView;
        }

        private TextView getElephantAgeTextView() {
            return elephantAgeTextView;
        }

        private TextView getOriginContinentTextView() {
            return originContinentTextView;
        }

        public Context getContext() {
            return context;
        }

        private void setSelectedIvoryProduct(IvoryProduct selectedIvoryProduct) {
            this.selectedIvoryProduct = selectedIvoryProduct;
        }

        private void setSelectedProductKey(String selectedProductKey) {
            this.selectedProductKey = selectedProductKey;
        }

        private TextView getReviewsCountTextView() {return reviewsCountTextView;}
    }

}
