package com.example.supermarket.ui.fragment.user;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.supermarket.R;
import com.example.supermarket.adapter.CartAdapter;
import com.example.supermarket.databinding.FragmentCartBinding;
import com.example.supermarket.model.AddToCartModel;

import com.example.supermarket.model.OrderDetailsModel;
import com.example.supermarket.ui.activity.MainUserActivity;
import com.example.supermarket.ui.fragment.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    CartAdapter cartAdapter;
    RecyclerView recyclerViewCart;
    ArrayList<AddToCartModel> addToCartModels = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String price;
    String status = null;
    double totalPrice = 0.0;
    double roundedTotalPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getStatus();


        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle bundle = new Bundle();
                bundle.putString("ProductsPrice", roundedTotalPrice + "");

                ConfirmOrderFragment fragment = new ConfirmOrderFragment();
                fragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();

            }
        });

        binding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainUserActivity.class));
//                final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.frame_container, new HomeFragment(), "NewFragmentTag");
//                ft.commit();
            }
        });

        binding.txtTotalPrice.setText("JOD " + totalPrice);


    }

    private void getCart() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewCart = binding.RVCart;
        recyclerViewCart.setHasFixedSize(true);
        recyclerViewCart.setLayoutManager(linearLayoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart List");
        cartAdapter = new CartAdapter(getContext(), addToCartModels);
        recyclerViewCart.setAdapter(cartAdapter);

        databaseReference.child("User View").child(firebaseAuth.getCurrentUser().getUid()).child("Products")

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            AddToCartModel cartModel = ds.getValue(AddToCartModel.class);
                            addToCartModels.add(cartModel);

                            price = cartModel.getTotalPrice();
                            totalPrice = totalPrice + Double.parseDouble(price);
                            DecimalFormat df = new DecimalFormat("#.###");
                            roundedTotalPrice = Double.parseDouble(df.format(totalPrice));

                            if (cartAdapter.getItemCount() > 0) {
                                binding.layNotEmpty.setVisibility(View.VISIBLE);
                                binding.layEmpty.setVisibility(View.GONE);
                            }
                            binding.txtTotalPrice.setText("JOD " + roundedTotalPrice);
                        }
                        cartAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void getStatus() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order List");
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            OrderDetailsModel model = ds.getValue(OrderDetailsModel.class);
                            status = model.getStatus();
                        }

                        if (snapshot.getValue() == null) {
                            if (status == ("Not Paid")) {
                                binding.layEmpty.setVisibility(View.GONE);
                                binding.layNotEmpty.setVisibility(View.GONE);
                                binding.layWaiting.setVisibility(View.VISIBLE);

                            } else if (status == ("Paid")) {
                                binding.layWaiting.setVisibility(View.GONE);
                                binding.layEmpty.setVisibility(View.VISIBLE);
                                getCart();
                            } else {

                                getCart();
                            }
                        } else {
                            if (status.equals("Not Paid")) {
                                binding.layEmpty.setVisibility(View.GONE);
                                binding.layNotEmpty.setVisibility(View.GONE);
                                binding.layWaiting.setVisibility(View.VISIBLE);

                            } else if (status.equals("Paid")) {
                                binding.layWaiting.setVisibility(View.GONE);
                                binding.layEmpty.setVisibility(View.VISIBLE);
                                getCart();
                            } else {

                                getCart();
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

}