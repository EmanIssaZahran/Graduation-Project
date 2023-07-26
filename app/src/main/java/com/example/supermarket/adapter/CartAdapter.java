package com.example.supermarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.supermarket.R;
import com.example.supermarket.model.AddToCartModel;
import com.example.supermarket.ui.activity.MainUserActivity;
import com.example.supermarket.ui.activity.ProductDetailsActivity;
import com.example.supermarket.ui.fragment.user.CartFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    ArrayList<AddToCartModel> cartModelArrayList;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public CartAdapter(Context context, ArrayList<AddToCartModel> cartModelArrayList) {
        this.context = context;
        this.cartModelArrayList = cartModelArrayList;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        AddToCartModel cartModel = cartModelArrayList.get(position);

        holder.name.setText(cartModel.getProductName());
        holder.price.setText("Price : JOD " + Double.parseDouble(cartModel.getProductPrice()));
        holder.quantity.setText("Quantity : " + cartModel.getTotalQuantity());

        Glide.with(context).load(cartModel.getImage()).into(holder.image);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Cart List");
                databaseReference.child("User View").child(firebaseAuth.getCurrentUser().getUid())
                        .child("Products").child(cartModel.getProductName()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    databaseReference.child("Admin View").child(firebaseAuth.getCurrentUser().getUid())
                                            .child("Products").child(cartModel.getProductName()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(context, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                                    context.startActivity(new Intent(context, MainUserActivity.class));
                                                    final FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                                                    ft.replace(R.id.frame_container, new CartFragment(), "NewFragmentTag");
                                                    ft.commit();
                                                }
                                            });

                                }
                            }
                        });
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("Product", cartModel.getProductName());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return cartModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        ImageView image;
        ImageButton delete, edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txt_product);
            price = itemView.findViewById(R.id.txt_currency);
            quantity = itemView.findViewById(R.id.txt_quantity);
            image = itemView.findViewById(R.id.img_product);
            delete = itemView.findViewById(R.id.img_btn_delete);
            edit = itemView.findViewById(R.id.img_btn_edit);


        }
    }
}
