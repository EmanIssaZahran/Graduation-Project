package com.example.supermarket.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.supermarket.adapter.CategoryAdapter;
import com.example.supermarket.databinding.FragmentHomeBinding;
import com.example.supermarket.model.CategoryModel;
import com.example.supermarket.ui.activity.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    CategoryAdapter categoryAdapter;
    RecyclerView recyclerViewCategory;
    ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCategory();


    }


    private void getCategory() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerViewCategory = binding.RVCategories;
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setLayoutManager(gridLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
        recyclerViewCategory.setAdapter(categoryAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CategoryModel categoryModel = ds.getValue(CategoryModel.class);
                    categoryModelList.add(categoryModel);

                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        categoryAdapter.setOnItemClick(position -> {
            Intent intent =  new Intent(getActivity(), HomeActivity.class);
            intent.putExtra("Category",categoryModelList.get(position).getCategory());
            getActivity().startActivity(intent);

        });
    }
}