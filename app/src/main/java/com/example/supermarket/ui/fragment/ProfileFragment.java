package com.example.supermarket.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.supermarket.R;
import com.example.supermarket.databinding.FragmentProfileBinding;
import com.example.supermarket.model.ProfileModel;
import com.example.supermarket.ui.activity.MainUserActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Locale;
import java.lang.String;

public class ProfileFragment extends Fragment implements LocationListener {
    private FragmentProfileBinding binding;
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Uri image_uri;
    private double latitude = 0.0, longitude = 0.0;
    private LocationManager locationManager;
    private ProgressDialog progressDialog;
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);

        checkUser();
        retrieveData();

        binding.imgBtnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect current location
                if (checkLocationPermission()) {

                    //already allowed
                    detectLocation();

                } else {
                    //not allowed, request
                    requestLocationPermission();
                }
            }
        });
        binding.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnUpdate.setEnabled(false);
                progressDialog.show();
                checkUserType();
            }
        });
    }

    private String fullName, shopName, phoneNumber, deliveryFee, address, email;

    private void inputAdminData() {
        //input data
        fullName = binding.edtNameAdmin.getText().toString().trim();
        shopName = binding.edtShopName.getText().toString().trim();
        phoneNumber = binding.edtPhone.getText().toString().trim();
        deliveryFee = binding.edtDeliveryFee.getText().toString().trim();
        address = binding.edtAddress.getText().toString().trim();
        email = binding.edtEmailRegister.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(fullName)) {
            binding.edtNameAdmin.setError("Enter Name...");
            return;
        }
        if (TextUtils.isEmpty(shopName)) {
            binding.edtShopName.setError("Enter Shop Name...");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.edtPhone.setError("Enter Phone Number...");
            return;
        }
        if (TextUtils.isEmpty(deliveryFee)) {
            binding.edtDeliveryFee.setError("Enter Delivery Fee...");
            return;
        }
        if (latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(getContext(), "Please click GPS button to detect location...", Toast.LENGTH_SHORT).show();
//            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmailRegister.setError("Invalid email pattern...");
            return;
        }

        updateAdminData();
    }

    private void updateAdminData() {
        progressDialog.setMessage("Updating Account Info...");

        String timestamp = "" + System.currentTimeMillis();

        if (image_uri == null) {
            //save info without image

            //setup data to save
            ProfileModel profileModel = new ProfileModel(firebaseAuth.getUid(), fullName, phoneNumber, address, email
                    , "Admin", timestamp, "true", shopName, deliveryFee);


            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            ref.child(firebaseAuth.getUid()).setValue(profileModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //db updated
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "The data has been updated successfully", Toast.LENGTH_SHORT).show();
                            binding.btnUpdate.setEnabled(true);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed updating db
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Data updating failed", Toast.LENGTH_SHORT).show();
                            binding.btnUpdate.setEnabled(true);
                        }
                    });
        } else {
            //save info with image

            //name and path of image
            String filePathAndName = "profile_images/" + "" + firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get uri of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                //setup data to save

                                ProfileModel profileModel = new ProfileModel(firebaseAuth.getUid(), fullName, phoneNumber, address, email
                                        , downloadImageUri.toString(), "Admin", timestamp, "true", shopName, deliveryFee);

                                //save to db
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                                ref.child(firebaseAuth.getUid()).setValue(profileModel)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void avoid) {
                                                //db updated
                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "The data has been updated successfully", Toast.LENGTH_SHORT).show();
                                                binding.btnUpdate.setEnabled(true);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed updating db
                                                progressDialog.dismiss();

                                                Toast.makeText(getContext(), "Data updating failed", Toast.LENGTH_SHORT).show();
                                                binding.btnUpdate.setEnabled(true);

                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            binding.btnUpdate.setEnabled(true);
                        }
                    });
        }
    }

    private void inputUserData() {
        //input data
        fullName = binding.edtNameAdmin.getText().toString().trim();
        phoneNumber = binding.edtPhone.getText().toString().trim();
        address = binding.edtAddress.getText().toString().trim();
        email = binding.edtEmailRegister.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(fullName)) {
            binding.edtNameAdmin.setError("Enter Name...");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.edtPhone.setError("Enter Phone Number...");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmailRegister.setError("Invalid email pattern...");
            return;
        }

        updateUserData();
    }

    private void updateUserData() {
        progressDialog.setMessage("Updating Account Info...");

        String timestamp = "" + System.currentTimeMillis();

        if (image_uri == null) {
            //save info without image

            //setup data to save
            ProfileModel profileModel = new ProfileModel(firebaseAuth.getUid(), fullName, phoneNumber, address
                    , email, "User", timestamp, "true");


            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            ref.child(firebaseAuth.getUid()).setValue(profileModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //db updated
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "The data has been updated successfully", Toast.LENGTH_SHORT).show();
                            binding.btnUpdate.setEnabled(true);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed updating db
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Data updating failed", Toast.LENGTH_SHORT).show();
                            binding.btnUpdate.setEnabled(true);
                        }
                    });
        } else {
            //save info with image

            //name and path of image
            String filePathAndName = "profile_images/" + "" + firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get uri of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                //setup data to save

                                ProfileModel profileModel = new ProfileModel(firebaseAuth.getUid(), fullName, phoneNumber, address
                                        , email, downloadImageUri.toString(), "User", timestamp, "true");

                                //save to db
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                                ref.child(firebaseAuth.getUid()).setValue(profileModel)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void avoid) {
                                                //db updated
                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "The data has been updated successfully", Toast.LENGTH_SHORT).show();
                                                binding.btnUpdate.setEnabled(true);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed updating db
                                                progressDialog.dismiss();

                                                Toast.makeText(getContext(), "Data updating failed", Toast.LENGTH_SHORT).show();
                                                binding.btnUpdate.setEnabled(true);


                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            binding.btnUpdate.setEnabled(true);

                        }
                    });
        }
    }

    private void checkUser() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String userType = "" + ds.child("accountType").getValue();
                            if (userType.equals("User")) {
                                binding.edtShopName.setVisibility(View.GONE);
                                binding.edtDeliveryFee.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkUserType() {
        user = firebaseAuth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userType = snapshot.child("accountType").getValue(String.class);

                    if (userType.equals("Admin")) {

                        inputAdminData();

                    } else {
                        inputUserData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveData() {
        progressDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String userType = "" + ds.child("accountType").getValue();
                            String image = "" + ds.child("image").getValue(String.class);
                            String name = "" + ds.child("fullName").getValue();
                            String addresses = "" + ds.child("address").getValue();
                            binding.edtNameAdmin.setText(name);
                            binding.edtAddress.setText(addresses);
                            binding.edtPhone.setText("" + ds.child("phone").getValue());
                            binding.edtEmailRegister.setText("" + ds.child("email").getValue());

                            if (userType.equals("Admin")) {
                                binding.edtShopName.setText("" + ds.child("shopName").getValue());
                                binding.edtDeliveryFee.setText("" + ds.child("deliveryFee").getValue());
                            }

                            if (!image.equals("null")) {
                                Glide.with(requireContext()).load(image).into(binding.profileImg);
                            } else {
                                binding.profileImg.setImageResource(R.drawable.ic_person_light_gray);
                            }

                        }
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showImagePickDialog() {
        //options to display in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle clicks
                        if (which == 0) {
                            //camera clicked
                            if (checkCameraPermission()) {
                                //camera permission allowed
                                pickFromCamera();
                            } else {
                                //not allowed, request
                                requestCameraPermission();
                            }
                        } else {
                            //gallery clicked
                            if (checkStoragePermission()) {
                                //storage permission allowed
                                pickFromGallery();
                            } else {
                                //not allowed, request

                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Context applicationContext = MainUserActivity.getContextOfApplication();
        image_uri = applicationContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(getContext(), "Please wait...", Toast.LENGTH_LONG).show();


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void findAddress() {
        //find address, country, state, city

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // complete address
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();

            binding.edtAddress.setText(address);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermission() {

        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), locationPermissions, LOCATION_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        findAddress();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //gps/location disabled
        Toast.makeText(getContext(), "Please turn on location...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("say yes", requestCode + "");

        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        //permission allowed
                        detectLocation();
                    } else {
                        //permission denied
                        Toast.makeText(getContext(), "Location permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] != PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //permission allowed
                        pickFromCamera();
                    } else {
                        //permission denied
                        Toast.makeText(getContext(), "Camera permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] != PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        //permission allowed
                        pickFromGallery();
                    } else {
                        //permission denied
                        Toast.makeText(getContext(), "Storage permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                //get picked image
                image_uri = data.getData();
                //set to imageview
                binding.profileImg.setImageURI(image_uri);

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview
                binding.profileImg.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
