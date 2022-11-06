package com.example.budgetingappv1;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Transactions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Transactions extends Fragment {

    private SearchView searchView;
    private RecyclerView rv_transactions;
    private RecyclerViewAdapter_Transactions mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseHelper databaseHelper;
    private List<Data_Transaction> transactions;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Frag_Transactions() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Transactions newInstance(String param1, String param2) {
        Frag_Transactions fragment = new Frag_Transactions();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        rv_transactions = view.findViewById(R.id.rv_transactionslist);
        databaseHelper = new DatabaseHelper(view.getContext());
        //databaseHelper.addCategories(DatabaseHelper.CATEGORIES);
        searchView = view.findViewById(R.id.search_bar);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // rv_transactions.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        rv_transactions.setLayoutManager(layoutManager);

        // specify an adapter
        transactions = databaseHelper.getAllTransactionsLike(null);
        mAdapter = new RecyclerViewAdapter_Transactions(transactions);
        rv_transactions.setAdapter(mAdapter);

        // item touch helper is used for swiping left to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv_transactions);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query != null){
                    // update recycler view every time the query text changes
                    mAdapter.updateAdapter(databaseHelper.getAllTransactionsLike(query));
                }
                return true;
            }
        });

        return view;
    }

    // creates an item touch helper used to detect touch actions
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        // this is only required when we want to rearrange the rows
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // direction is swipe to the left
            // get the corresponding transaction from the recycler view
            int position = viewHolder.getBindingAdapterPosition();
            Data_Transaction deleted_transaction = transactions.get(position);

            // delete from the database and notify adapter
            databaseHelper.deleteOne(deleted_transaction);
            transactions.remove(position);
            mAdapter.notifyItemRemoved(position);

            Snackbar.make(rv_transactions, deleted_transaction.getTitle(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // create an undo option at bottom of screen
                            // if pressed then add transaction back
                            databaseHelper.addOne(deleted_transaction);
                            transactions.add(position, deleted_transaction);
                            mAdapter.notifyItemInserted(position);
                        }
                    }).show();

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            // create a background decor to cover recycler view when swiping
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getView().getContext(), R.color.delete_pink))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addCornerRadius(actionState, 15)
                    .addSwipeLeftPadding(actionState, 4, 0, 4)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}