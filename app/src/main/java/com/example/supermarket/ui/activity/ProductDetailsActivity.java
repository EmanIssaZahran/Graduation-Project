package com.example.supermarket.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.supermarket.R;
import com.example.supermarket.databinding.ActivityProductDetailsBinding;
import com.example.supermarket.model.AddToCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProductDetailsActivity extends AppCompatActivity {

    ActivityProductDetailsBinding binding;
    String product, price, image, uid;
    int quantity = 1;
    double totalPrice = 0;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);


        Bundle extra = getIntent().getExtras();
        String key = extra.getString("Product");
        getProductDetails(key);

        uid = firebaseAuth.getUid();


        binding.imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    if (quantity == 1) return;
                    quantity--;
                    totalPrice = quantity * Double.parseDouble(price);
                    binding.txtQuantityNum.setText(String.valueOf((quantity)));
                }
            }
        });
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                totalPrice = quantity * Double.parseDouble(price);
                binding.txtQuantityNum.setText(String.valueOf(quantity));
            }
        });


        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalPrice = quantity * Double.parseDouble(price);
                addedToCart();

            }
        });
    }

    private void addedToCart() {
        String saveCurrentDate, saveCurrentTime;
        Calendar calender = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,  yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss  a");
        saveCurrentTime = currentTime.format(calender.getTime());

        AddToCartModel addToCartModel = new AddToCartModel(product, price, saveCurrentDate, saveCurrentTime, String.valueOf(quantity), String.valueOf(totalPrice), image, uid);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart List");
        databaseReference.child("User View").child(firebaseAuth.getCurrentUser().getUid())
                .child("Products").child(product).setValue(addToCartModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            databaseReference.child("Admin View").child(firebaseAuth.getCurrentUser().getUid())
                                    .child("Products").child(product).setValue(addToCartModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProductDetailsActivity.this, "Added To  Cart List", Toast.LENGTH_SHORT).show();
                                                onBackPressed();

                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    public void getProductDetails(String key) {
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Product");
        ref.orderByChild("product").equalTo(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            product = "" + ds.child("product").getValue();
                            price = "" + ds.child("price").getValue();
                            String description = "" + ds.child("description").getValue();
                            image = "" + ds.child("image").getValue();

                            binding.txtTitle.setText(product);
                            binding.txtPrice.setText("" + Double.parseDouble(price));
                            binding.txtDescription.setText(description);

                            try {
                                Glide.with(ProductDetailsActivity.this).load(image).into(binding.imgProduct);
                            } catch (Exception e) {
                                binding.imgProduct.setImageResource(R.drawable.ic_add_photo_light_gray);
                            }

                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}