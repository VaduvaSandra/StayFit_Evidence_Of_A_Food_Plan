package com.example.prototip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class PersonalFoodActivity extends AppCompatActivity {
     TextView recipeNameTextView;
     RecyclerView recyclerView;
     MyAdapter adapter;
     DatabaseReference personalMealsRef;
     RadioButton breakfastRadioButton;
     RadioButton lunchRadioButton;
     RadioButton dinnerRadioButton;
     RadioButton snacksRadioButton;

     List<FoodInfo> foodInfoList;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_food);

        recipeNameTextView = findViewById(R.id.titlefood);

        breakfastRadioButton = findViewById(R.id.radio_button1);
        lunchRadioButton = findViewById(R.id.radio_button2);
        dinnerRadioButton = findViewById(R.id.radio_button3);
        snacksRadioButton = findViewById(R.id.radio_button4);



        recipeNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(view);
            }
        });

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        personalMealsRef = usersRef.child(userId).child("PersonalMeals");
        foodInfoList = new ArrayList<>();
        adapter = new MyAdapter(new ArrayList<>(), new MyAdapter.OnFoodDeleteListener() {
            @Override
            public void onFoodDelete(FoodInfo foodInfo) {
                deleteFoodItem(foodInfo);
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        personalMealsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FoodInfo> foodInfoList = new ArrayList<>();
                for(DataSnapshot mealSnapshot : snapshot.getChildren()){
                    String mealId = mealSnapshot.getKey();
                    DatabaseReference mealRef = personalMealsRef.child(mealId);

                    mealRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            FoodInfo foodInfo = snapshot.getValue(FoodInfo.class);
                            if(foodInfo != null){
                                foodInfoList.add(foodInfo);
                                adapter.setFoodList(foodInfoList);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("PersonalFoodActivity", "Failed to read value", error.toException());
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PersonalFoodActivity", "Failed to read value", error.toException());
            }
        });
    }

    public void showDialog(View view){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_personal_food);

        //Initializarea elementelor din fereastra de dialog
        EditText editName = dialog.findViewById(R.id.editName);
        EditText editCalories = dialog.findViewById(R.id.editCalories);
        EditText editCarbs = dialog.findViewById(R.id.editCarbs);
        EditText editFats = dialog.findViewById(R.id.editFats);
        EditText editProteins = dialog.findViewById(R.id.editProteins);

        //Adaugam logica pentru butoanele din fereastra de dialog
        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                int calories = Integer.parseInt(editCalories.getText().toString());
                float carbs = Float.parseFloat(editCarbs.getText().toString());
                float fats = Float.parseFloat(editFats.getText().toString());
                float proteins = Float.parseFloat(editProteins.getText().toString());

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(FirebaseAuth.getInstance().getUid());
                DatabaseReference personalMealsRef = userRef.child("PersonalMeals").push();
                String mealId = personalMealsRef.getKey();

                personalMealsRef.child("foodName").setValue(name);
                personalMealsRef.child("calories").setValue(calories);
                personalMealsRef.child("carbohydrates").setValue(carbs);
                personalMealsRef.child("fat").setValue(fats);
                personalMealsRef.child("protein").setValue(proteins);

                FoodInfo foodInfo = new FoodInfo(name, calories, proteins, fats, carbs);
                personalMealsRef.setValue(foodInfo);
                dialog.dismiss();
            }
        });
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        // Afișarea ferestrei de dialog
        dialog.show();
    }
    private void deleteFoodItem(FoodInfo foodInfo){
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference personalMealsRef = usersRef.child(userId).child("PersonalMeals");

        personalMealsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mealSnapshot: snapshot.getChildren()){
                    FoodInfo mealFoodInfo = mealSnapshot.getValue(FoodInfo.class);
                    if(mealFoodInfo.getFoodName().equals(foodInfo.getFoodName())){
                        mealSnapshot.getRef().removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
            }
        });
        adapter.getFoodList().remove(foodInfo);
        adapter.notifyDataSetChanged();
    }
}