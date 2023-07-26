package com.example.supermarket.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.adapter.SearchAdapter;
import com.example.supermarket.databinding.ActivitySearchBinding;
import com.example.supermarket.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerViewSearch;
    private SearchAdapter searchAdapter;
    private ArrayList<ProductModel> productList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String data = intent.getStringExtra("key");

        recyclerViewSearch = binding.rvSearch;
        getList();
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data == "1") {
                    startActivity(new Intent(SearchActivity.this, MainUserActivity.class));
                } else {
                    onBackPressed();
                }
            }
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());


            }

        });


    }

    private void filter(String text) {
        ArrayList<ProductModel> filterList = new ArrayList<>();
        for (ProductModel productModel : productList) {
            if (productModel.getProduct().toLowerCase().contains(text.toLowerCase())) {
                filterList.add(productModel);
            }
        }
        searchAdapter.filterList(filterList);

        if (!text.isEmpty()) {
            binding.rvSearch.setVisibility(View.VISIBLE);
        } else {
            binding.rvSearch.setVisibility(View.GONE);
        }

        searchAdapter.setOnItemClick(position -> {
            Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
            intent.putExtra("Product", filterList.get(position).getProduct());
            startActivity(intent);
        });




    }

    private void getList() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setLayoutManager(linearLayoutManager);
        searchAdapter = new SearchAdapter(this, productList);
        recyclerViewSearch.setAdapter(searchAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    ProductModel productModel = ds.getValue(ProductModel.class);
                    productList.add(productModel);
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchAdapter.setOnItemClick(position -> {
            Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
            intent.putExtra("Product", productList.get(position).getProduct());
            startActivity(intent);
        });

    }
}