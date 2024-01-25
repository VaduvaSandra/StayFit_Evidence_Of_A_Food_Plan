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

        /**
         * Adaugă un listener pentru evenimentele de modificare a datelor în cadrul referinței personalMealsRef.
         * Acesta este utilizat pentru a actualiza lista de alimente afișată în activitatea curentă.
         */
        personalMealsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Inițializează o listă pentru a stoca informațiile despre alimente
                List<FoodInfo> foodInfoList = new ArrayList<>();
                // Parcurge toate copiii referinței personalMealsRef, fiecare reprezentând o masă personalizată
                for(DataSnapshot mealSnapshot : snapshot.getChildren()){
                    // Obține ID-ul mesei curente
                    String mealId = mealSnapshot.getKey();
                    // Construiește referința către masa curentă din personalMealsRef
                    DatabaseReference mealRef = personalMealsRef.child(mealId);

                    // Adaugă un listener pentru evenimentele de modificare a datelor în cadrul referinței mealRef
                    mealRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Obține informațiile despre aliment în formatul FoodInfo
                            FoodInfo foodInfo = snapshot.getValue(FoodInfo.class);
                            // Verifică dacă informațiile despre aliment nu sunt nule
                            if(foodInfo != null){
                                // Adaugă informațiile despre aliment în lista foodInfoList
                                foodInfoList.add(foodInfo);
                                // Actualizează lista de alimente afișată în adapter și notifică adapterul despre schimbare
                                adapter.setFoodList(foodInfoList);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Afiseaza un mesaj de eroare în cazul în care citirea datelor a eșuat
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
        // Inițializează un obiect Dialog pentru a afișa fereastra de dialog
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
                // Obține valorile introduse în câmpurile de introducere
                String name = editName.getText().toString();
                int calories = Integer.parseInt(editCalories.getText().toString());
                float carbs = Float.parseFloat(editCarbs.getText().toString());
                float fats = Float.parseFloat(editFats.getText().toString());
                float proteins = Float.parseFloat(editProteins.getText().toString());

                // Obține referința către utilizatorul curent din baza de date Firebase
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(FirebaseAuth.getInstance().getUid());
                // Creează o referință pentru alimentul personalizat în cadrul PersonalMeals
                DatabaseReference personalMealsRef = userRef.child("PersonalMeals").push();
                String mealId = personalMealsRef.getKey();

                // Setează valorile corespunzătoare câmpurilor pentru alimentul personalizat
                personalMealsRef.child("foodName").setValue(name);
                personalMealsRef.child("calories").setValue(calories);
                personalMealsRef.child("carbohydrates").setValue(carbs);
                personalMealsRef.child("fat").setValue(fats);
                personalMealsRef.child("protein").setValue(proteins);

                // Creează un obiect FoodInfo cu informațiile alimentului personalizat și îl salvează în baza de date
                FoodInfo foodInfo = new FoodInfo(name, calories, proteins, fats, carbs);
                personalMealsRef.setValue(foodInfo);
                // Închide fereastra de dialog după ce alimentul a fost adăugat
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
        // Obține referința către baza de date Firebase pentru utilizatorul curent
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference personalMealsRef = usersRef.child(userId).child("PersonalMeals");

        // Adaugă un ascultător de evenimente pentru a urmări modificările din PersonalMeals
        personalMealsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Parcurge fiecare element din PersonalMeals
                for(DataSnapshot mealSnapshot: snapshot.getChildren()){
                    // Obține un obiect FoodInfo pentru alimentul curent din listă
                    FoodInfo mealFoodInfo = mealSnapshot.getValue(FoodInfo.class);
                    // Verifică dacă alimentul curent are același nume ca și alimentul de șters
                    if(mealFoodInfo.getFoodName().equals(foodInfo.getFoodName())){
                        // Șterge alimentul din baza de date Firebase
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