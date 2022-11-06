package com.example.budgetingappv1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

public class RecyclerViewAdapter_CategoriesBudget extends RecyclerView.Adapter<RecyclerViewAdapter_CategoriesBudget.MyViewHolder> {

    Map<String, Float> categories_map;
    Context context;

    public RecyclerViewAdapter_CategoriesBudget(Map<String, Float> categories, Context context) {
        this.categories_map = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_cat_budget, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String[] cat_names = categories_map.keySet().toArray(new String[categories_map.size()]);
        Data_Category category = databaseHelper.selectCategory(cat_names[position]);
        holder.tv_cat_name_bdgt.setText(category.getName());
        holder.tv_cat_tot_spendings.setText(categories_map.get(cat_names[position]).toString() + "/" + category.getBudget());
        holder.iv_transaction_category.setColorFilter(category.getColor());
    }

    @Override
    public int getItemCount() {
        return categories_map.size();
    }

    // this is a reference to the small one line layout
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_cat_name_bdgt;
        TextView tv_cat_tot_spendings;
        ImageView iv_transaction_category;
        ConstraintLayout parent_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_cat_name_bdgt = itemView.findViewById(R.id.tv_cat_name_bdgt);
            tv_cat_tot_spendings = itemView.findViewById(R.id.tv_cat_tot_spendings);
            iv_transaction_category = itemView.findViewById(R.id.iv_transaction_category);
            parent_layout = itemView.findViewById(R.id.oneLineCatBdgtLayout);
        }
    }

    public void updateAdapter(Map<String, Float> categories){
        categories_map.clear();
        categories_map.putAll(categories);
        notifyDataSetChanged();
    }
}
