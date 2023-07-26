package com.example.supermarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.R;
import com.example.supermarket.model.AddToCartModel;

import java.util.ArrayList;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {

    Context context;
    ArrayList<AddToCartModel> orderSummary;

    public OrderSummaryAdapter(Context context, ArrayList<AddToCartModel> orderSummary) {
        this.context = context;
        this.orderSummary = orderSummary;
    }

    @NonNull
    @Override
    public OrderSummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_summary_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderSummaryAdapter.ViewHolder holder, int position) {
        AddToCartModel cartModel = orderSummary.get(position);

        holder.productName.setText(cartModel.getProductName());
        holder.productQuantity.setText(cartModel.getTotalQuantity());
        holder.productPrice.setText(Double.parseDouble(cartModel.getProductPrice()) + " JOD");

    }

    @Override
    public int getItemCount() {
        return orderSummary.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productQuantity, productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.txt_product_name);
            productQuantity = itemView.findViewById(R.id.txt_product_quantity);
            productPrice = itemView.findViewById(R.id.txt_product_price);

        }
    }
}
