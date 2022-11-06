package com.example.budgetingappv1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter_Categories extends RecyclerView.Adapter<RecyclerViewAdapter_Categories.MyViewHolder> {

    List<Data_Category> categories_list;
    Context context;

    public RecyclerViewAdapter_Categories(List<Data_Category> categories, Context context) {
        this.categories_list = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_category, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        Data_Category category = categories_list.get(position);
        holder.tv_category_name.setText(category.getName());
        holder.iv_transaction_category.setColorFilter(category.getColor());

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send the control to the edit transaction activity
                Intent intent = new Intent(context, Add_Category.class);
                // send id of transaction that was clicked on
                intent.putExtra("name", categories_list.get(position).getName());
                context.startActivity(intent);
            }
        });

        // need to install glide dependency
        // for setting images
        // Glide.with(this.context).load("url of image").into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return categories_list.size();
    }

    // this is a reference to the small one line layout
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_category_name;
        ImageView iv_transaction_category;
        ConstraintLayout parent_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_category_name);
            iv_transaction_category = itemView.findViewById(R.id.iv_transaction_category);
            parent_layout = itemView.findViewById(R.id.oneLineCategoryLayout);
        }
    }

    public void updateAdapter(List<Data_Category> new_category_list){
        categories_list.clear();
        categories_list.addAll(new_category_list);
        notifyDataSetChanged();
    }
}
