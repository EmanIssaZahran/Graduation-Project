package com.example.supermarket.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.supermarket.R;
import com.example.supermarket.databinding.ActivityChooseUserBinding;
import com.example.supermarket.ui.activity.admin.RegisterAdminActivity;
import com.example.supermarket.ui.activity.user.RegisterUserActivity;

public class ChooseUser extends AppCompatActivity {

    private ActivityChooseUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imgAdmin.setBorderColor(0);
                binding.imgUser.setBorderColor(getResources().getColor(R.color.colorPrimary));
                binding.imgUser.setBorderWidth(5);
                binding.cardUser.setActivated(true);
                binding.cardAdmin.setActivated(false);


            }
        });
        binding.imgAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imgUser.setBorderColor(0);
                binding.imgAdmin.setBorderColor(getResources().getColor(R.color.colorPrimary));
                binding.imgAdmin.setBorderWidth(5);
                binding.cardUser.setActivated(false);
                binding.cardAdmin.setActivated(true);




            }
        });

        binding.btnChooseUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cardUser.isActivated()){
                    startActivity(new Intent(ChooseUser.this, RegisterUserActivity.class));
                } else if (binding.cardAdmin.isActivated()) {
                    final EditText editText = new EditText(ChooseUser.this);
                    editText.setHint("Enter admin code");
                    editText.setPadding(100, 100, 100, 20);
                    editText.setTextSize(16);


                    AlertDialog.Builder builder = new AlertDialog.Builder(ChooseUser.this);
                    builder.setTitle("Register as admin");
                    builder.setCancelable(false);
                    builder.setView(editText);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = editText.getText().toString();

                            if (text.equals("31815007031")) {
                                // open register admin activity
                                startActivity(new Intent(ChooseUser.this, RegisterAdminActivity.class));
                                finish();
                            }

                        }
                    });
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_rect01);
                    dialog.show();

                }
            }
        });
    }
}