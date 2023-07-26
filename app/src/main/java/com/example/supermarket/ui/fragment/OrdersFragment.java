package com.example.supermarket.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supermarket.R;
import com.example.supermarket.adapter.CategoryAdapter;
import com.example.supermarket.adapter.OrderDetailsAdapter;
import com.example.supermarket.databinding.FragmentOrdersBinding;
import com.example.supermarket.model.AddToCartModel;
import com.example.supermarket.model.CategoryModel;
import com.example.supermarket.model.OrderDetailsModel;
import com.example.supermarket.model.ProductModel;
import com.example.supermarket.ui.activity.HomeActivity;
import com.example.supermarket.ui.activity.OrderDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class OrdersFragment extends Fragment {
    private FragmentOrdersBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserType();

    }


    private void checkUserType() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String userType = "" + ds.child("accountType").getValue();

                            if (userType.equals("User")) {
                                userOrderList();
                            } else if (userType.equals("Admin")) {
                                adminOrderList();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void adminOrderList() {
        binding.rvOrderView.setVisibility(View.VISIBLE);

        ArrayList<OrderDetailsModel> orderDetails = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvOrderView.setHasFixedSize(true);
        binding.rvOrderView.setLayoutManager(linearLayoutManager);
        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(getActivity(), orderDetails);
        binding.rvOrderView.setAdapter(orderDetailsAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order List");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String uid = ds.getKey();


                            databaseReference.child(uid)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Log.d("ghgjkhl","hgf " + uid);

                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                OrderDetailsModel model = ds.getValue(OrderDetailsModel.class);
                                                orderDetails.add(model);

                                            }
                                            orderDetailsAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        orderDetailsAdapter.setOnItemClick(position -> {
            Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
            intent.putExtra("Uid", orderDetails.get(position).getUid());
            intent.putExtra("Time",orderDetails.get(position).getCurrentTime());
            startActivity(intent);
        });
    }


    private void userOrderList() {
        ArrayList<OrderDetailsModel> orderDetails = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvOrderView.setHasFixedSize(true);
        binding.rvOrderView.setLayoutManager(linearLayoutManager);
        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(getActivity(), orderDetails);
        binding.rvOrderView.setAdapter(orderDetailsAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order List");
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            OrderDetailsModel model = ds.getValue(OrderDetailsModel.class);
                            orderDetails.add(model);

                            if (orderDetailsAdapter.getItemCount() > 0){
                                binding.rvOrderView.setVisibility(View.VISIBLE);
                            }else {
                                binding.rvOrderView.setVisibility(View.GONE);
                                binding.txtNoOrder.setVisibility(View.VISIBLE);

                            }

                        }
                        orderDetailsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        orderDetailsAdapter.setOnItemClick(position -> {

        });
    }

}

