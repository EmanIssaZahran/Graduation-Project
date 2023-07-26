package com.example.supermarket.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.supermarket.R;
import com.example.supermarket.databinding.ActivityOrderDetailsBinding;
import com.example.supermarket.model.OrderDetailsModel;
import com.example.supermarket.ui.fragment.OrdersFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderDetailsActivity extends AppCompatActivity {

    ActivityOrderDetailsBinding binding;
    String uid;
    String time;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        Bundle extra = getIntent().getExtras();
        uid = extra.getString("Uid");
        time = extra.getString("Time");

        statusList();
        orderDetail();
        getStatus();

//        binding.imgViewBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.popBackStackImmediate("previous_fragment", 0);
//
//                Fragment fragment = new OrdersFragment();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.replace(R.id.frame_container, fragment, "previous_fragment");
//                transaction.addToBackStack("previous_fragment");
//                transaction.commit();
//            }
//        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Saving Status");
                String selectedItem = (String) binding.spState.getSelectedItem();
                String key = binding.txtTxt.getText().toString().trim();
                saveStatus(selectedItem, key);
            }
        });

    }

    private void saveStatus(String selectedItem, String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order List");
        databaseReference.child(uid).child(key).child("status").setValue(selectedItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(OrderDetailsActivity.this, "Changed status to " + selectedItem + " successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getStatus() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order List");
        databaseReference.child(uid).orderByChild("currentTime").equalTo(time).addValueEventListener(new ValueEventListener() {
            String key;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    key = ds.getKey();
                    binding.txtTxt.setText(key + "");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void orderDetail() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = "" + ds.child("fullName").getValue();
                    String phone = "" + ds.child("phone").getValue();
//
                    binding.txtNameData.setText(name + "");
                    binding.txtPhoneData.setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Order List");
        reference.child(uid).orderByChild("currentTime").equalTo(time).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String currentTime = "" + ds.child("currentTime").getValue();
                    String currentDate = "" + ds.child("currentDate").getValue();
                    String Address = "" + ds.child("location").getValue();
                    String price = "" + ds.child("totalPrice").getValue();
                    String state = "" + ds.child("status").getValue();

                    binding.txtTimeData.setText(currentTime);
                    binding.txtDateData.setText(currentDate);
                    binding.txtDv.setText(Address);
                    binding.txtTotalPriceData.setText(price);

                    if (state.equals("Paid")) {
                        binding.spState.setSelection(0);
                    } else if (state.equals("Not Paid")) {
                        binding.spState.setSelection(1);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void statusList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        binding.spState.setAdapter(adapter);
        adapter.clear();
        adapter.add("Paid");
        adapter.add("Not Paid");
    }
}