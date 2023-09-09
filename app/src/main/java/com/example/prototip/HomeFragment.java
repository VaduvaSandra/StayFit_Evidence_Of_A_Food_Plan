package com.example.prototip;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private PieChart mChart;
    private TextView mLegendText;
    private static final String TAG ="HomeFragment" ;
    private TextView requiredCaloriesTextView;
    private TextView consumedCaloriesTextView;
    private PieChart pieChart;
    private int requiredCalories;
    private int consumedCalories;
    private ArrayList<PieEntry> entries = new ArrayList<>();
    private PieDataSet pieDataSet;
    private PieData pieData;
    private float totalCalories =0;
 //   private AlertDialog alertDialog;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    //Creez o metoda pentru a calcula necesarul caloric si a actualiza pie chartul cu rezeltatul corespunzator
    private void updatePieChart(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date(timestamp));



        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int weight = snapshot.child("weight").getValue(Integer.class);
                int height = snapshot.child("height").getValue(Integer.class);
                int age = snapshot.child("age").getValue(Integer.class);
                String activityLevel = snapshot.child("activityLevel").getValue(String.class);
                String obiectiv = snapshot.child("goal").getValue(String.class);
                String gen = snapshot.child("gender").getValue(String.class);


                    double bmr;
                    if (gen != null && gen.equals("radioButtonM")) {
                        bmr = (10 * weight) + (6.25 * height) - (5 * age) - 5;
                    } else {
                        bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
                    }

                    double activitateFizica;
                    switch (activityLevel) {
                        case "Sedentar":
                            activitateFizica = 1.2; // Sedentar
                            break;
                        case "Putin activ":
                            activitateFizica = 1.4; // Ușor activ
                            break;
                        case "Activ moderat":
                            activitateFizica = 1.6; // Moderat activ
                            break;
                        case "Foarte activ":
                            activitateFizica = 1.75; // Foarte activ
                            break;
                        case "Extra activ":
                            activitateFizica = 2; // Super activ
                            break;
                        default:
                            activitateFizica = 1.4;
                            break;
                    }

                    double necesarCaloric;
                    if (obiectiv.equals("Scadere in greutate")) {
                        necesarCaloric = (bmr * activitateFizica) - 500;
                    } else if (obiectiv.equals("Mentinere in greutate")) {
                        necesarCaloric = bmr * activitateFizica;
                    } else {
                        necesarCaloric = (bmr * activitateFizica) + 500;
                    }
                    // Actualizați Firebase Real Time Database cu valoarea necesarului zilnic de calorii
                    userRef.child("calorii_zilnice").setValue(necesarCaloric);
                    requiredCaloriesTextView.setText(String.valueOf(necesarCaloric));
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private AlertDialog alertDialog;


    private void initRecyclerView(View view) {
        ArrayList<MenuItem> menuList = new ArrayList<>();
        menuList.add(new MenuItem(R.drawable.breakfast, "Breakfast"));
        menuList.add(new MenuItem(R.drawable.lunch, "Lunch"));
        menuList.add(new MenuItem(R.drawable.dinner, "Dinner"));
        menuList.add(new MenuItem(R.drawable.snack, "Snacks"));

        RecyclerView recyclerView = view.findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MenuAdapter adapter = new MenuAdapter(getContext(),menuList);
        recyclerView.setAdapter(adapter);

        adapter.setOnAddButtonClickListener(new MenuAdapter.OnAddButtonClickListener() {
            @Override
            public void onAddButtonClick(int position) {
                openAddDialog(position);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        pieChart = view.findViewById(R.id.pie_chart);

        requiredCaloriesTextView = view.findViewById(R.id.tv_total_calories);
        consumedCaloriesTextView = view.findViewById(R.id.tv_consumed_calories);

        updatePieChartWithValues();

        updatePieChart();
        initRecyclerView(view);

        return view;
    }

   private void updatePieChartWithValues(){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("calorii_zilnice").exists() && snapshot.child("calorii_zilnice").getValue() != null) {
                    int requiredCalories = snapshot.child("calorii_zilnice").getValue(Integer.class);
                    float consumedCalories = HomeFragment.this.totalCalories;

                    // Obține data curentă
                    long timestamp = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String currentDate = sdf.format(new Date(timestamp));
                    Log.d("Valoare","Data"+currentDate);

                    DataSnapshot historyMealsSnapshot = snapshot.child("HistoryMeals");
                    //Verifica daca exista alimente pentru ziua curenta
                    if(historyMealsSnapshot.child(currentDate).exists()){
                        DataSnapshot totalCaloriesSnapshot = historyMealsSnapshot.child(currentDate).child("totalCalories");
                        if(totalCaloriesSnapshot.exists()){
                            float totalCalories = totalCaloriesSnapshot.getValue(Float.class);
                            consumedCalories = totalCalories;
                        }
                    }else {
                        //Daca nu exista alimente pentru ziua curenta, seteaza totalCalories la 0
                        consumedCalories = 0;
                    }

                    List<PieEntry> entries = new ArrayList<>();
                    entries.add(new PieEntry(consumedCalories, "Calorii consumate"));
                    entries.add(new PieEntry(requiredCalories, "Calorii totale"));

                    PieDataSet pieDataSet = new PieDataSet(entries, "");
                    pieDataSet.setColors(Color.RED, Color.GREEN);
                    pieDataSet.setValueTextColor(Color.BLACK);
                    pieDataSet.setValueTextSize(12f);

                    PieData pieData = new PieData(pieDataSet);
                    pieChart.setData(pieData);
                    pieChart.invalidate();
                    consumedCaloriesTextView.setText(String.valueOf(consumedCalories));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
   }

    private void openAddDialog(int position) {
        Log.d("Position pressed", "pos: "+position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //alertDialog = builder.create();
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_food, null);
        builder.setView(dialogView);

        // Găsirea elementelor de interfață utilizator din fereastra de dialog
        SearchView searchView = dialogView.findViewById(R.id.searchView);
        View addButtonView = LayoutInflater.from(getContext()).inflate(R.layout.menu_item, null);
        ImageButton addButton = addButtonView.findViewById(R.id.menu_add_button);

        Button cancelButton = dialogView.findViewById(R.id.btnCancel);
        Button adaugButton = dialogView.findViewById(R.id.btnAdd);

        AlertDialog dialog = builder.create(); //Creeaza instanta dialogului


        //Setarea functionalitatii pentru butoanele din fereastra de dialog
        adaugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foodName = searchView.getQuery().toString();
                int servingSize = 100;
               // long timestamp = System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = sdf.format(calendar.getTime());
                CalorieNinjaApiExample.searchFood(foodName, servingSize, new CalorieNinjaApiExample.Callback() {
                    @Override
                    public void onNutritionalInfoRetrieved(CalorieNinjaApiExample.NutritionalInfo info) {
                        //Salvam datele despre mancare in Firebase
                        DatabaseReference currentDayHistoryMealsRef = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(FirebaseAuth.getInstance().getUid()).child("HistoryMeals").child(currentDate);
                        String mealId = "";
                        DatabaseReference currentDayMealRef = null;
                        if(position==0){
                            mealId = currentDayHistoryMealsRef.child("breakfast").push().getKey();
                            currentDayMealRef = currentDayHistoryMealsRef.child("breakfast");
                        }else if(position ==1){
                            mealId = currentDayHistoryMealsRef.child("lunch").push().getKey();
                            currentDayMealRef = currentDayHistoryMealsRef.child("lunch");
                        }else if (position == 2){
                            mealId = currentDayHistoryMealsRef.child("dinner").push().getKey();
                            currentDayMealRef = currentDayHistoryMealsRef.child("dinner");
                        }else {
                            mealId = currentDayHistoryMealsRef.child("snack").push().getKey();
                            currentDayMealRef = currentDayHistoryMealsRef.child("snack");
                        }
                        
                         Meal meal = new Meal(foodName, info.calories, info.protein, info.fat, info.carbohydrates);
                        currentDayMealRef.child(mealId).setValue(meal);

                        //Actualizam numarul total de calorii pentru ziua curenta
                        DatabaseReference totalCaloriesRef = currentDayHistoryMealsRef.child("totalCalories");
                        totalCaloriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Integer totalCalories = snapshot.getValue(Integer.class);
                                if(totalCalories == null){
                                    totalCalories = 0;
                                }
                                totalCalories += info.calories;
                                totalCaloriesRef.setValue(totalCalories);
                                Log.d("Valoarea","Calorii Total: "+totalCalories);

                                consumedCaloriesTextView.setText(String.valueOf(totalCalories));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        String toastMessage = "Calories: " + info.calories + "\n"
                                + "Protein: " + info.protein + " g" + "\n"
                                + "Fat: " + info.fat + " g" + "\n"
                                + "Carbohydrates: " + info.carbohydrates + " g";
                        // Adaugă instrucțiuni Log.d() pentru a verifica valorile
                        Log.d("NutritionalInfo", "Calories: " + info.calories);
                        Log.d("NutritionalInfo", "Protein: " + info.protein);
                        Log.d("NutritionalInfo", "Fat: " + info.fat);
                        Log.d("NutritionalInfo", "Carbohydrates: " + info.carbohydrates);

                        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String errorMessage) {

                        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // Inchide fereastra de dialog

            }
        });
     //   AlertDialog dialog = builder.create();
        dialog.show();
    }

}

