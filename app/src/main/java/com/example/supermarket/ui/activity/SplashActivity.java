package com.example.supermarket.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.supermarket.R;
import com.example.supermarket.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make fullscreen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.rotate_animation);
        binding.imgLogo.startAnimation(animation);

        // start login activity after 1sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
//                    user not logged in,  start login activity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    //user is logger in, check user type
                    startActivity(new Intent(SplashActivity.this, MainUserActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

//    private void checkUserType() {
//        //if user is admin, start admin main screen
//        //if user is buyer(user), start user main screen
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            String accountType = "" + ds.child("accountType").getValue();
//                            if (accountType.equals("Admin")) {
//                                //user is admin
//                                startActivity(new Intent(SplashActivity.this, MainAdminActivity.class));
//                                finish();
//                            } else {
//                                //user is buyer(user)
//                                startActivity(new Intent(SplashActivity.this, MainUserActivity.class));
//                                finish();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

}