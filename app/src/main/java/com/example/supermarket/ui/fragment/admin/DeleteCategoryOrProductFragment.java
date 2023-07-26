package com.example.supermarket.ui.fragment.admin;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.supermarket.R;
import com.example.supermarket.databinding.FragmentDeleteCategoryOrProductBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteCategoryOrProductFragment extends Fragment {

    FragmentDeleteCategoryOrProductBinding binding;
    ArrayAdapter<String> adapterItems;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeleteCategoryOrProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        putCategoryProduct();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.spChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) binding.spChoose.getSelectedItem();
                if (selectedItem != null) {
                    if (selectedItem == "Category") {
                        progressDialog.show();
                        putCategoryList();
                        binding.txtChooseCategory.setVisibility(View.VISIBLE);
                        binding.spChooseCategory.setVisibility(View.VISIBLE);
                        binding.txtChooseProduct.setVisibility(View.GONE);
                        binding.spChooseProduct.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    } else if (selectedItem == "Product") {
                        progressDialog.show();
                        putProductList();
                        binding.txtChooseCategory.setVisibility(View.GONE);
                        binding.spChooseCategory.setVisibility(View.GONE);
                        binding.txtChooseProduct.setVisibility(View.VISIBLE);
                        binding.spChooseProduct.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    } else {
                        progressDialog.show();
                        binding.txtChooseProduct.setVisibility(View.GONE);
                        binding.spChooseProduct.setVisibility(View.GONE);
                        binding.txtChooseCategory.setVisibility(View.GONE);
                        binding.spChooseCategory.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnDelete.setEnabled(false);
                String selectedItem = (String) binding.spChoose.getSelectedItem();
                if (selectedItem == "Category") {
                    deleteCategory();
                } else if (selectedItem == "Product") {
                    deleteProduct();
                } else {
                    Toast.makeText(getActivity(), "Choose Category OR Product ", Toast.LENGTH_SHORT).show();
                    binding.btnDelete.setEnabled(true);
                }
            }
        });


    }

    private void deleteCategory() {
        progressDialog.show();
        String selectedCategory = (String) binding.spChooseCategory.getSelectedItem();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");
        databaseReference.orderByChild("category").equalTo(selectedCategory)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            DatabaseReference childRef = ds.getRef();

                            childRef.removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Category Removed", Toast.LENGTH_SHORT).show();
                                            binding.spChoose.setSelection(0);
                                            progressDialog.dismiss();
                                            binding.btnDelete.setEnabled(true);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            binding.btnDelete.setEnabled(true);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        binding.btnDelete.setEnabled(true);
                    }
                });
        progressDialog.dismiss();
    }

    private void deleteProduct() {
        progressDialog.show();
        String selectedProduct = (String) binding.spChooseProduct.getSelectedItem();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
        databaseReference.orderByChild("product").equalTo(selectedProduct)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            DatabaseReference childRef = ds.getRef();

                            childRef.removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Product Removed", Toast.LENGTH_SHORT).show();
                                            binding.spChoose.setSelection(0);
                                            progressDialog.dismiss();
                                            binding.btnDelete.setEnabled(true);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            binding.btnDelete.setEnabled(true);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        binding.btnDelete.setEnabled(true);
                    }
                });
    }

    private void putProductList() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
        adapterItems = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item);

        binding.spChooseProduct.setAdapter(adapterItems);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapterItems.clear();
                adapterItems.add("Select Product");

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String product = ds.child("product").getValue(String.class);
                    adapterItems.add(product);
                }
                adapterItems.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putCategoryList() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");
        adapterItems = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item);

        binding.spChooseCategory.setAdapter(adapterItems);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapterItems.clear();
                adapterItems.add("Select Category");

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String category = ds.child("category").getValue(String.class);
                    adapterItems.add(category);
                }
                adapterItems.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void putCategoryProduct() {
        adapterItems = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        binding.spChoose.setAdapter(adapterItems);
        adapterItems.add("Select");
        adapterItems.add("Category");
        adapterItems.add("Product");
    }


}