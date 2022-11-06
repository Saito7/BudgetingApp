package com.example.budgetingappv1;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainCategory extends AppCompatActivity {

    private FloatingActionButton add_btn, close_btn;
    private SearchView searchView;
    private RecyclerView rv_categories;
    private RecyclerViewAdapter_Categories mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseHelper databaseHelper;
    private List<Data_Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_category);

        add_btn = findViewById(R.id.add_cat_btn);
        close_btn = findViewById(R.id.category_close_btn);
        rv_categories = findViewById(R.id.rv_categories);
        databaseHelper = new DatabaseHelper(MainCategory.this);
        //databaseHelper.addCategories(DatabaseHelper.CATEGORIES);
        searchView = findViewById(R.id.search_cat_bar);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // rv_categories.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rv_categories.setLayoutManager(layoutManager);

        // specify an adapter
        categories = databaseHelper.getAllCategoriesLike(null);
        mAdapter = new RecyclerViewAdapter_Categories(categories, MainCategory.this);
        rv_categories.setAdapter(mAdapter);

        // item touch helper is used for swiping left to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv_categories);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCategory.this, Add_Category.class);
                startActivity(intent);
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCategory.this, MainActivity.class);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query != null){
                    // update recycler view every time the query text changes
                    mAdapter.updateAdapter(databaseHelper.getAllCategoriesLike(query));
                }
                return true;
            }
        });
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
            // get the corresponding category from the recycler view
            int position = viewHolder.getBindingAdapterPosition();
            Data_Category deleted_category = categories.get(position);

            // delete from the database and notify adapter
            databaseHelper.deleteOne(deleted_category);
            categories.remove(position);
            mAdapter.notifyItemRemoved(position);

            Snackbar.make(rv_categories, deleted_category.getName(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // create an undo option at bottom of screen
                            // if pressed then add category back
                            databaseHelper.addOne(deleted_category);
                            categories.add(position, deleted_category);
                            mAdapter.notifyItemInserted(position);
                        }
                    }).show();

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            // create a background decor to cover recycler view when swiping
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainCategory.this, R.color.delete_pink))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addCornerRadius(actionState, 15)
                    .addSwipeLeftPadding(actionState, 4, 0, 4)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}