package com.example.supermarket.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.supermarket.R;
import com.example.supermarket.databinding.ActivityMainUserBinding;
import com.example.supermarket.ui.fragment.AboutUsFragment;
import com.example.supermarket.ui.fragment.admin.AddCategoryFragment;
import com.example.supermarket.ui.fragment.admin.AddProductFragment;
import com.example.supermarket.ui.fragment.admin.DeleteCategoryOrProductFragment;
import com.example.supermarket.ui.fragment.user.CartFragment;
import com.example.supermarket.ui.fragment.HomeFragment;
import com.example.supermarket.ui.fragment.OrdersFragment;
import com.example.supermarket.ui.fragment.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private ActivityMainUserBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    FirebaseUser user;

    public static Context contextOfApplication;

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        contextOfApplication = getApplicationContext();

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);


        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //-------------------------- Change NavigationDrawer toggle button icon ---------------------
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_dark_pink, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        //-----------------------------------------------

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        ActionBar actionBar = getSupportActionBar();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            assert actionBar != null;
            binding.fragmentName.setText("Home");
        }

        binding.imgBtnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fragmentName.setText("Cart");
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new CartFragment()).commit();
            }
        });
        binding.imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUserActivity.this,SearchActivity.class);
                intent.putExtra("key","1");
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                binding.fragmentName.setText("Home");
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_profile) {
                binding.fragmentName.setText("My Profile");
                replaceFragment(new ProfileFragment());
            } else if (id == R.id.nav_order) {
                binding.fragmentName.setText("Orders");
                replaceFragment(new OrdersFragment());
            } else if (id == R.id.nav_about_us) {
                binding.fragmentName.setText("About Us");
                replaceFragment(new AboutUsFragment());
            } else if (id == R.id.nav_category) {
                binding.fragmentName.setText("Add Category");
                replaceFragment(new AddCategoryFragment());
            } else if (id == R.id.nav_product) {
                binding.fragmentName.setText("Add Product");
                replaceFragment(new AddProductFragment());
            } else if (id == R.id.nav_cart) {
                binding.fragmentName.setText("Cart");
                replaceFragment(new CartFragment());
            } else if (id == R.id.nav_remove) {
                binding.fragmentName.setText("Delete Category/Product     ");
                replaceFragment(new DeleteCategoryOrProductFragment());
            } else if (id == R.id.nav_logout) {
                makeMeOffline();
            }
            drawerLayout.closeDrawer(GravityCompat.START);


            return true;
        });


    }

    private void replaceFragment(Fragment fragment) {
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment, fragment.getClass().getSimpleName()).commit();
        } catch (Exception e) {

        }
    }

    private void makeMeOffline() {
        progressDialog.setMessage("Logging Out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                // update successfully
                firebaseAuth.signOut();
                checkUser();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed updating
                progressDialog.dismiss();
                Toast.makeText(MainUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser() {
        user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        } else {
            checkUserType();

        }
    }

    private void hideItem() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_category).setVisible(false);
        nav_Menu.findItem(R.id.nav_product).setVisible(false);
        nav_Menu.findItem(R.id.nav_remove).setVisible(false);
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

                                hideItem();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}