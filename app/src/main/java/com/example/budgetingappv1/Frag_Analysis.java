package com.example.budgetingappv1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Analysis#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Analysis extends Fragment {

    private SharedPreferences preferences;
    private PieChart pieChart;
    private DatabaseHelper databaseHelper;
    private ImageView graphView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Frag_Analysis() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnalysisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Analysis newInstance(String param1, String param2) {
        Frag_Analysis fragment = new Frag_Analysis();
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
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        pieChart = view.findViewById(R.id.category_pie_chart);
        databaseHelper = new DatabaseHelper(view.getContext());

        setupPieChart();
        loadPieChartData();

        return view;
    }

    private void setupPieChart(){
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Spending by Category");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
    }


    private void loadPieChartData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        Map<String, Float> cat_map = databaseHelper.getCategorySpendings();
        preferences = this.getActivity().getSharedPreferences(AccountCreation.PREFERENCES, Context.MODE_PRIVATE);
        float budget = preferences.getFloat(AccountCreation.ACCOUNT_BUDGET, 0);

        for(String name: cat_map.keySet()){
            entries.add(new PieEntry(cat_map.get(name)/budget, name));
            colors.add(databaseHelper.selectCategory(name).getColor());
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        // for animation
        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }
}