package com.example.supermarket.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.supermarket.R;
import com.example.supermarket.databinding.ActivityPaymentDetailsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class PaymentDetails extends AppCompatActivity {
    private ActivityPaymentDetailsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String address = intent.getStringExtra("location");
        String totalPrice = intent.getStringExtra("TotalPrice");
        String status = intent.getStringExtra("status");

        LocalTime currentTime = LocalTime.now();
        int hoursToAdd = 1;
        LocalTime updatedTime = currentTime.plusHours(hoursToAdd);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = updatedTime.format(dtf);

        binding.txtTotalPrice.setText("  Total Price  :    "  + totalPrice);
        binding.txtStatus.setText("  Status  :    "  + status);
        binding.txtDeliveryLocation.setText("  Address  :    "  + address);
        binding.txtTime.setText("  Time to Receiving   :    "  +formattedTime );


    }

}