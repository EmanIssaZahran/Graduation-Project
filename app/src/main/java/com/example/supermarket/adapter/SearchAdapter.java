package com.example.supermarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.R;
import com.example.supermarket.interfaces.OnItemClick;
import com.example.supermarket.model.ProductModel;

import java.util.ArrayList;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    OnItemClick onItemClick;

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    Context context;
    ArrayList<ProductModel> searchModel;

    public SearchAdapter(Context context, ArrayList<ProductModel> searchModel) {
        this.context = context;
        this.searchModel = searchModel;
    }


    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view, parent, false);
        return new ViewHolder(view);
    }

    public void filterList(ArrayList<ProductModel> filterList) {
        searchModel = filterList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        ProductModel productModel = searchModel.get(position);

        holder.txtSearch.setText(productModel.getProduct());

        holder.itemView.setOnClickListener(v -> {
            onItemClick.onClick(position);

        });
    }

    @Override
    public int getItemCount() {
        return searchModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSearch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSearch = itemView.findViewById(R.id.txt_search);
        }
    }
}
