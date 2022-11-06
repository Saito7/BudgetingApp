package com.example.budgetingappv1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter_Groceries extends RecyclerView.Adapter<RecyclerViewAdapter_Groceries.MyViewHolder> {

    List<String> groceries_list;

    public RecyclerViewAdapter_Groceries(List<String> groceries_list) {
        this.groceries_list = groceries_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_grocery, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.grocery_name.setText(groceries_list.get(position));
    }

    @Override
    public int getItemCount() {
        return groceries_list.size();
    }

    // this is a reference to the small one line layout
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView grocery_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            grocery_name = itemView.findViewById(R.id.grocery_name);
        }
    }

    public void updateAdapter(List<String> groceries_list){
        groceries_list.clear();
        groceries_list.addAll(groceries_list);
        notifyDataSetChanged();
    }
}
