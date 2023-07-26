package com.example.supermarket.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.supermarket.R;
import com.example.supermarket.adapter.ProductAdapter;
import com.example.supermarket.databinding.ActivityHomeBinding;
import com.example.supermarket.model.ProductModel;
import com.example.supermarket.ui.fragment.HomeFragment;
import com.example.supermarket.ui.fragment.user.CartFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerViewProduct;
    DatabaseReference databaseReference;
    ProductAdapter productAdapter;
    ProgressDialog progressDialog;
    ArrayList<ProductModel> productModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        Bundle extra = getIntent().getExtras();
        String key = extra.getString("Category");

        recyclerViewProduct = binding.RVProduct;
        linearLayoutManager = new LinearLayoutManager(this);

        getProduct(key);

        binding.txtProduct.setText(key);
        binding.imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }


    private void getProduct(String key) {

        recyclerViewProduct.setHasFixedSize(true);
        recyclerViewProduct.setLayoutManager(linearLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
        productAdapter = new ProductAdapter(this, productModelArrayList);
        recyclerViewProduct.setAdapter(productAdapter);


        databaseReference.orderByChild("category").equalTo(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ProductModel productModel = ds.getValue(ProductModel.class);
                            productModelArrayList.add(productModel);
                        }
                        productAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        productAdapter.setOnItemClick(position -> {
            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
            intent.putExtra("Product", productModelArrayList.get(position).getProduct());
            startActivity(intent);
        });


    }

}