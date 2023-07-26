package com.example.supermarket.ui.fragment.user;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.supermarket.R;
import com.example.supermarket.adapter.OrderSummaryAdapter;
import com.example.supermarket.databinding.FragmentConfirmOrderBinding;
import com.example.supermarket.model.AddToCartModel;
import com.example.supermarket.model.Config;
import com.example.supermarket.model.OrderDetailsModel;
import com.example.supermarket.ui.activity.MainUserActivity;
import com.example.supermarket.ui.activity.PaymentDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class ConfirmOrderFragment extends Fragment {

    private static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FragmentConfirmOrderBinding binding;
    String totalPrice, deliveryFee, productsPrice, location, shopLocation, userLocation;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerViewCart;
    OrderSummaryAdapter orderSummaryAdapter;
    ArrayList<AddToCartModel> cartModelArrayList = new ArrayList<>();
    String status = "Not Paid";
    ProgressDialog progressDialog;

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConfirmOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        //start paypal service
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

        Bundle bundle = this.getArguments();
        productsPrice = bundle.getString("ProductsPrice");
        binding.txtProductsPriceNum.setText(productsPrice + "  JOD");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        retrieveUserDataProfile();
        retrieveAdminDataProfile();
        getCart();


        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartFragment fragment = new CartFragment();
                getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();

            }
        });
        binding.rbToTheAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = userLocation;
                binding.layDescribeLocation.setVisibility(View.VISIBLE);
                binding.txtShopLocation.setVisibility(View.GONE);
                binding.txtHomeLocationGps.setText(location);
            }
        });
        binding.rbReceiveFromTheSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = shopLocation;
                binding.layDescribeLocation.setVisibility(View.GONE);
                binding.txtShopLocation.setVisibility(View.VISIBLE);
                binding.txtShopLocation.setText(location + "");
            }
        });
        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.rbToTheAddress.isChecked()) {
                    location = userLocation;

                    if (binding.rbPaymentWhenReceiving.isChecked()) {
                        Toast.makeText(getActivity(), "Your order has been placed successfully", Toast.LENGTH_SHORT).show();
                        status = "Not Paid";
                        DeleteCarts(status);
                        addToOrderList(status);
                        makeNotification();
                        startActivity(new Intent(getActivity(), PaymentDetails.class).putExtra("TotalPrice", totalPrice)
                                .putExtra("status",status).putExtra("location", location));
                    } else if (binding.rbVisaMastercard.isChecked()) {
                        processPayment();
                        status = "Paid";
                        DeleteCarts(status);
                        addToOrderList(status);
                        makeNotification();
                        startActivity(new Intent(getActivity(), PaymentDetails.class).putExtra("TotalPrice", totalPrice)
                                .putExtra("status",status).putExtra("location", location));
                    }

                } else if (binding.rbReceiveFromTheSite.isChecked()) {
                    location = shopLocation;
                    if (binding.rbPaymentWhenReceiving.isChecked()) {
                        Toast.makeText(getActivity(), "Your order has been placed successfully", Toast.LENGTH_SHORT).show();
                        status = "Not Paid";
                        DeleteCarts(status);
                        addToOrderList(status);
                        makeNotification();
                        startActivity(new Intent(getActivity(), PaymentDetails.class).putExtra("TotalPrice", totalPrice)
                                .putExtra("status",status).putExtra("location", location));
                    } else if (binding.rbVisaMastercard.isChecked()) {
                        processPayment();
                        status = "Paid";
                        DeleteCarts(status);
                        addToOrderList(status);
                        makeNotification();
                    }

                }
            }
        });

    }

    private void processPayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(totalPrice)), "USD",
                "Customer", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (requestCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation == null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(getActivity(), PaymentDetails.class)
                                .putExtra("PaymentDetails",paymentDetails )
                                .putExtra("payment_amount",totalPrice ));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(getActivity(),"Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(getActivity(),"Invalid", Toast.LENGTH_SHORT).show();




    }

    private void DeleteCarts(String status) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart List");
        databaseReference.child("User View").child(firebaseAuth.getCurrentUser().getUid()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        databaseReference.child("Admin View").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                    }
                });

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CartFragment cartFragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        cartFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.frame_container, cartFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void retrieveUserDataProfile() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            userLocation = "" + ds.child("address").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void retrieveAdminDataProfile() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("accountType").equalTo("Admin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            deliveryFee = "" + ds.child("deliveryFee").getValue();
                            shopLocation = "" + ds.child("address").getValue();

                            totalPrice = String.valueOf(Double.parseDouble(productsPrice) + Double.parseDouble(deliveryFee));
                            binding.txtDeliveryFeeNum.setText(deliveryFee + "  JOD");
                            binding.txtTotalPriceNum.setText(totalPrice + "  JOD");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getCart() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewCart = binding.rvOrderList;
        recyclerViewCart.setHasFixedSize(true);
        recyclerViewCart.setLayoutManager(linearLayoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart List");
        orderSummaryAdapter = new OrderSummaryAdapter(getContext(), cartModelArrayList);
        recyclerViewCart.setAdapter(orderSummaryAdapter);

        databaseReference.child("User View").child(firebaseAuth.getCurrentUser().getUid()).child("Products").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            AddToCartModel cartModel = ds.getValue(AddToCartModel.class);
                            cartModelArrayList.add(cartModel);

                        }
                        orderSummaryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void addToOrderList(String status) {

        String saveCurrentDate, saveCurrentTime;
        Calendar calender = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,  yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss  a");
        saveCurrentTime = currentTime.format(calender.getTime());

        OrderDetailsModel orderDetailsModel = new OrderDetailsModel(firebaseAuth.getCurrentUser().getUid(), location,
                saveCurrentDate, saveCurrentTime, totalPrice, status, cartModelArrayList
        );

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order List");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child(databaseReference.push().getKey())
                .setValue(orderDetailsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Done", "Done");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Failed", "Failed");

                    }
                });

    }


    public void makeNotification() {
        String chanelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), chanelID);
        builder.setSmallIcon(R.drawable.ic_notification_dark_pink)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle("Your order has been placed successfully")
                .setContentText("Total Price " + totalPrice + " JOD was " + status.toLowerCase())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(requireContext(), MainUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "Some value to be pressed here");

        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(),
                0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {

            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(chanelID);

            if (notificationChannel == null) {
                int impotance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(chanelID, "Some description", impotance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(0, builder.build());

    }


}