package com.example.budgetingappv1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Budget#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Budget extends Fragment {

    private RecyclerView rv_cat_bdgts;
    private RecyclerViewAdapter_CategoriesBudget mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseHelper databaseHelper;
    private Map<String, Float> cat_map;
    private ArcProgress arcProgress;
    private TextView total_spent_text, budget_text;
    private SharedPreferences preferences;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Frag_Budget() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BudgetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Budget newInstance(String param1, String param2) {
        Frag_Budget fragment = new Frag_Budget();
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
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        total_spent_text = view.findViewById(R.id.total_spent_text);
        budget_text = view.findViewById(R.id.budget_text);
        arcProgress = view.findViewById(R.id.budget_arc_progress);
        rv_cat_bdgts = view.findViewById(R.id.rv_cat_bdgts);
        databaseHelper = new DatabaseHelper(view.getContext());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // rv_cat_bdgts.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        rv_cat_bdgts.setLayoutManager(layoutManager);

        // specify an adapter
        cat_map = databaseHelper.getCategorySpendings();
        mAdapter = new RecyclerViewAdapter_CategoriesBudget(cat_map, view.getContext());
        rv_cat_bdgts.setAdapter(mAdapter);

        preferences = this.getActivity().getSharedPreferences(AccountCreation.PREFERENCES, Context.MODE_PRIVATE);
        float budget = preferences.getFloat(AccountCreation.ACCOUNT_BUDGET, 0);

        float total_spent = 0;
        for(float value: cat_map.values()){
            total_spent += value;
        }

        total_spent_text.setText(String.valueOf(total_spent));
        budget_text.setText(String.valueOf(budget));
        int progress = (int) ((total_spent/budget) * 100);
        if(progress <= 50){
            arcProgress.setFinishedStrokeColor(Color.GREEN);
        }
        else if((50 < progress) && (progress <= 80)){
            arcProgress.setFinishedStrokeColor(Color.YELLOW);
        }
        else{
            arcProgress.setFinishedStrokeColor(Color.RED);
            if(progress > 100){
                progress = 100;
            }
        }
        arcProgress.setProgress(progress);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        float budget = preferences.getFloat(AccountCreation.ACCOUNT_BUDGET, 0);

        float total_spent = 0;
        for(float value: cat_map.values()){
            total_spent += value;
        }

        total_spent_text.setText(String.valueOf(total_spent));
        budget_text.setText(String.valueOf(budget));
        int progress = (int) ((total_spent/budget) * 100);
        if(progress <= 50){
            arcProgress.setFinishedStrokeColor(Color.GREEN);
        }
        else if((50 < progress) && (progress <= 80)){
            arcProgress.setFinishedStrokeColor(Color.YELLOW);
        }
        else{
            arcProgress.setFinishedStrokeColor(Color.RED);
            if(progress > 100){
                progress = 100;
            }
        }
        arcProgress.setProgress(progress);
    }
}