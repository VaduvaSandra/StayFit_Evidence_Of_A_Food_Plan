package com.example.prototip;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {

    private LineChart lineChart;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private  int dailyCalories;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    String mParam1;
    String mParam2;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
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
       View view = inflater.inflate(R.layout.fragment_statistics, container, false);

       lineChart = view.findViewById(R.id.calories_line_chart);
       lineChart.setDragEnabled(true);
       lineChart.setScaleEnabled(true);
       lineChart.setPinchZoom(true);
       lineChart.setVisibleXRangeMaximum(7);
       //Setarea butoanelor de derulare orizontala
        lineChart.setHorizontalScrollBarEnabled(true);
        lineChart.setScrollBarSize(20);
        //Setarea gestului de derulare orizontala
        lineChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                lineChart.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

       return view;
    }
    @Override
    public void onStart() {

        super.onStart();
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //Obtinem valoarea calorii_zilnice din nodul userId
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    dailyCalories = snapshot.child("calorii_zilnice").getValue(Integer.class);
                    setupChart();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
            }
        });


    }

    private void setupChart() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> entries = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.child("HistoryMeals").getChildren()){
                    String date = dataSnapshot.getKey();
                    HashMap<String, Object> mealsMap = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Object>>() {});
                    int totalCalories = 0;
                    if(mealsMap != null && mealsMap.containsKey("totalCalories")){
                        totalCalories = ((Long) mealsMap.get("totalCalories")).intValue();
                    }
                    entries.add(new Entry(convertTimestampToXValue(date), totalCalories));
                }
                LineDataSet lineDataSet = new LineDataSet(entries, "Calorii");
                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);
                lineChart.invalidate();

                // Configurare axa Y
                YAxis yAxis = lineChart.getAxisLeft();
                yAxis.setAxisMinimum(0f);
                yAxis.setAxisMaximum(dailyCalories + 500); // Valoarea maximă a axei Y
                yAxis.setGranularity(500f);

                yAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value);
                    }
                });
                // Configurare axa X
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if(index >=0 && index < entries.size()){
                            Entry entry = entries.get(index);
                            long timestamp = (long) entry.getX();
                            return convertTimestampToDate(timestamp);
                        }
                        return "";
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
            }
        };
        reference.addValueEventListener(valueEventListener);
    }

    private float convertTimestampToXValue(String timestamp) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            Date date = dateFormat.parse(timestamp);
            return (float) date.getTime();
        }catch (ParseException e ){
            e.printStackTrace();
            return 0f;
        }
    }
    private String convertTimestampToDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }

    @Override
    public void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListener);
    }


}