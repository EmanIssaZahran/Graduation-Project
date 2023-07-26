package com.example.supermarket.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.R;
import com.example.supermarket.interfaces.OnItemClick;
import com.example.supermarket.model.AddToCartModel;
import com.example.supermarket.model.OrderDetailsModel;
import com.example.supermarket.model.ProfileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    OnItemClick onItemClick;

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    Context context;
    ArrayList<OrderDetailsModel> orderDetails;

    public OrderDetailsAdapter(Context context, ArrayList<OrderDetailsModel> orderDetails) {
        this.context = context;
        this.orderDetails = orderDetails;
    }

    @NonNull
    @Override
    public OrderDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderDetailsAdapter.ViewHolder holder, int position) {


        OrderDetailsModel orderDetailsModel = orderDetails.get(position);

        holder.date.setText(orderDetailsModel.getCurrentDate());
        holder.time.setText(orderDetailsModel.getCurrentTime());
        holder.price.setText(orderDetailsModel.getTotalPrice() + " JOD");
        holder.status.setText(orderDetailsModel.getStatus());

        OrderSummaryAdapter orderSummaryAdapter = new OrderSummaryAdapter(context, orderDetailsModel.getCartList());
        holder.rvv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvv.setAdapter(orderSummaryAdapter);

        holder.itemView.setOnClickListener(v -> {

            onItemClick.onClick(position);
        });


    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, time, price, status;
        RecyclerView rvv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.txt_Date);
            time = itemView.findViewById(R.id.txt_Time);
            price = itemView.findViewById(R.id.txt_total_price);
            status = itemView.findViewById(R.id.txt_state);

            rvv = itemView.findViewById(R.id.rv_products_view);

        }
    }

}