package com.example.supermarket.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.supermarket.R;
import com.example.supermarket.databinding.ActivityLoginBinding;
import com.example.supermarket.ui.activity.user.RegisterUserActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
   private ActivityLoginBinding binding;
   private FirebaseAuth firebaseAuth;
   private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class));
                                startActivity(new Intent(LoginActivity.this, ChooseUser.class));

            }
        });

        binding.txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnLogin.setEnabled(false);
                loginUser();
            }
        });
    }
    private String  email, password;
    private void loginUser() {
        email = binding.edtEmailLogin.getText().toString().trim();
        password = binding.edtPasswordLogin.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.edtEmailLogin.setError("Invalid email pattern...");
            return;
        }
        if (TextUtils.isEmpty(password)){
            binding.edtPasswordLogin.setError("Enter Password...");
            return;
        }

        progressDialog.setMessage("Logging In...");
        binding.pbCategory.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //logged in successfully
                        Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.rotate_animation);
                        binding.imgHello.startAnimation(animation);

                        makeMeOnline();
                        startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
                        finish();
                        binding.btnLogin.setEnabled(true);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed logging in
                        binding.pbCategory.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.btnLogin.setEnabled(true);

                    }
                });

    }

    private void makeMeOnline() {
        //after logging in, make user online
        progressDialog.setMessage("Checking User...");
        binding.pbCategory.setVisibility(View.VISIBLE);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("online","true");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        // update successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void checkUserType(String type) {
//        //if user is admin, start admin main screen
//        //if user is buyer(user), start user main screen
//                            if (type=="Admin"){
//                                progressDialog.dismiss();
//                                //user is admin
//                                startActivity(new Intent(LoginActivity.this, MainAdminActivity.class));
//                                finish();
//                            }
//                            else {
//                                //user is buyer(user)
//                                progressDialog.dismiss();
//                                startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
//                                finish();
//                            }
//
//
//    }
}