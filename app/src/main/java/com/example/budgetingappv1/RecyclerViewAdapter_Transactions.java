package com.example.budgetingappv1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter_Transactions extends RecyclerView.Adapter<RecyclerViewAdapter_Transactions.MyViewHolder> {

    List<Data_Transaction> transactions_list;
    List<String> groceries_list = new ArrayList<>();

    public RecyclerViewAdapter_Transactions(List<Data_Transaction> transaction_list) {
        this.transactions_list = transaction_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        // set data on screen to be displayed on card view
        Data_Transaction transaction = transactions_list.get(position);
        holder.tv_transaction_title.setText(transaction.getTitle());
        holder.tv_transaction_recipient.setText(transaction.getRecipient());
        holder.tv_transaction_amount.setText(String.valueOf(transaction.getAmount()));
        holder.iv_transaction_cat_icon.setImageBitmap(Add_Category_Icon_Utils.getImage(transaction.getCategoryIcon(holder.itemView.getContext())));

        // set adapter for recycler view that displays each grocery item
        if(transaction.isExpandable()) {
            RecyclerViewAdapter_Groceries groceries_adapter = new RecyclerViewAdapter_Groceries(groceries_list);
            holder.rv_groceries_list.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.rv_groceries_list.setAdapter(groceries_adapter);
        }

        if(transaction.isExpandable()) {
            holder.ib_transaction_dropdown.setVisibility(View.VISIBLE);
            holder.ib_transaction_dropdown.setOnClickListener(view -> {
                if(!transaction.isExpanded()) {
                    holder.rv_groceries_list.setVisibility(View.VISIBLE);
                    holder.ib_transaction_dropdown.setImageResource(R.drawable.ic_outline_arrow_circle_up_24);
                    transaction.setExpanded(true);
                }else{
                    holder.rv_groceries_list.setVisibility(View.GONE);
                    holder.ib_transaction_dropdown.setImageResource(R.drawable.ic_outline_arrow_circle_down_24);
                    transaction.setExpanded(false);
                }
                notifyItemChanged(position);
            });
        }
        else{
            holder.ib_transaction_dropdown.setVisibility(View.GONE);
        }

//        try {
//            holder.iv_transaction_cat_icon.setBackgroundColor(transaction.getCategoryColour(context));
//        }
//        catch(Exception e){
//            holder.iv_transaction_cat_icon.setBackgroundColor(Color.WHITE);
//        }

        holder.parent_layout.setOnClickListener(view -> {
            // send the control to the edit transaction activity
            Intent intent = new Intent(holder.itemView.getContext(), Add_Transaction.class);
            // send id of transaction that was clicked on
            intent.putExtra("id", transactions_list.get(position).getId());
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return transactions_list.size();
    }

    // this is a reference to the small one line layout
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_transaction_title;
        TextView tv_transaction_recipient;
        TextView tv_transaction_amount;
        CardView parent_layout;
        ImageView iv_transaction_cat_icon;
        ImageButton ib_transaction_dropdown;
        RecyclerView rv_groceries_list;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_transaction_title = itemView.findViewById(R.id.tv_transaction_title);
            tv_transaction_recipient = itemView.findViewById(R.id.tv_transaction_recipient);
            tv_transaction_amount = itemView.findViewById(R.id.tv_transaction_amount);
            iv_transaction_cat_icon = itemView.findViewById(R.id.iv_transaction_cat_icon);
            ib_transaction_dropdown = itemView.findViewById(R.id.ib_transaction_dropdown);
            rv_groceries_list = itemView.findViewById(R.id.rv_groceries_list);
            parent_layout = itemView.findViewById(R.id.cv_transaction_item);
        }
    }

    public void updateAdapter(List<Data_Transaction> new_transaction_list){
        transactions_list.clear();
        transactions_list.addAll(new_transaction_list);
        notifyDataSetChanged();
    }
}
