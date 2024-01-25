package com.example.prototip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;

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

public class BreakfastActivity extends AppCompatActivity {

    private ImageView datetimeIcon;
    private RecyclerView recyclerView;
    private FoodItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakfast);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initializarea adaptorului cu o lista goala și un listener pentru stergerea alimentelor
        adapter = new FoodItemAdapter(new ArrayList<>(), new FoodItemAdapter.OnFoodDeleteListener() {
            @Override
            public void onFoodDelete(FoodInfo foodInfo) {
                deleteFoodItem(foodInfo);
            }
        });
        recyclerView.setAdapter(adapter);
        datetimeIcon = findViewById(R.id.datetime_icon);
        // Setarea unui listener pentru iconita de data pentru a afisa dialogul de selecție a datei
        datetimeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
// Referința către nodul principal "users"
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

// Obținem ID-ul utilizatorului curent
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

// Construim calea către nodul "HistoryMeals" pentru utilizatorul curent
        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");

// Construim calea către nodul "breakfast" în functie de timestamp-ul curent
        String currentTimestamp = getCurrentTimestamp();
        DatabaseReference breakfastRef = historyMealsRef.child(currentTimestamp).child("breakfast");


        breakfastRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Creăm o listă pentru a stoca obiecte FoodInfo
                List<FoodInfo> foodInfoList = new ArrayList<>();
                // Iterăm prin fiecare masă din breakfastRef
                for(DataSnapshot mealSnapshot : snapshot.getChildren()){
                    String mealId = mealSnapshot.getKey();
                    DatabaseReference mealRef = breakfastRef.child(mealId);

                    // Adăugăm un ValueEventListener pentru fiecare masă individuală
                    mealRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Obținem obiectul FoodInfo din snapshot
                            FoodInfo foodInfo = snapshot.getValue(FoodInfo.class);
                            // Verificăm dacă obiectul FoodInfo nu este nul
                            if(foodInfo != null){
                                // Adăugăm obiectul FoodInfo la listă
                                foodInfoList.add(foodInfo);
                                // Setăm lista de alimente actualizată în adaptor și notificăm schimbarea
                                adapter.setFoodList(foodInfoList);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Afișăm un mesaj de eroare dacă preluarea datelor nu reușește
                            Log.e("BreakfastActivity", "Failed to read value.", error.toException());

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Afișăm un mesaj de eroare dacă preluarea datelor nu reușește
                Log.e("BreakfastActivity", "Failed to read value.", error.toException());

            }
        });
        // Afișăm timestamp-ul după blocul ValueEventListener
        Log.d("Timestamp", currentTimestamp);
    }

    private void deleteFoodItem(FoodInfo foodInfo) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");
        DatabaseReference currentTimestampRef = historyMealsRef.child(getCurrentTimestamp());
        DatabaseReference totalCaloriesRef = currentTimestampRef.child("totalCalories");
        DatabaseReference breakfastRef = currentTimestampRef.child("breakfast");

        breakfastRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot breakfastSnapshot) {
                deleteFoodItemFromBreakfast(breakfastSnapshot, foodInfo, totalCaloriesRef);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aceasta metoda a fost lăsată goală deoarece nu este necesar să gestionăm evenimentele onCancelled.
            }
        });

        adapter.getFoodList().remove(foodInfo);
        adapter.notifyDataSetChanged();
    }

    private void deleteFoodItemFromBreakfast(DataSnapshot breakfastSnapshot, FoodInfo foodInfo, DatabaseReference totalCaloriesRef) {
        for (DataSnapshot mealSnapshot : breakfastSnapshot.getChildren()) {
            FoodInfo mealFoodInfo = mealSnapshot.getValue(FoodInfo.class);
            if (mealFoodInfo != null && mealFoodInfo.getFoodName().equals(foodInfo.getFoodName())) {
                mealSnapshot.getRef().removeValue();
                updateTotalCaloriesAfterDeletion(totalCaloriesRef, foodInfo.getCalories());
                break;
            }
        }
    }

    private void updateTotalCaloriesAfterDeletion(DatabaseReference totalCaloriesRef, int deletedCalories) {
        totalCaloriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer currentValue = snapshot.getValue(Integer.class);
                    if (currentValue != null) {
                        int newValue = currentValue - deletedCalories;
                        totalCaloriesRef.setValue(newValue);
                        Log.d("Valoare", "Total calories: " + newValue);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aceasta metoda a fost lăsată goală deoarece nu este necesar să gestionăm evenimentele onCancelled.
            }
        });
    }


    /**
     * Afișează un dialog de selectare a datei și acționează în consecință la selectarea unei date.
     */
    private void showDatePickerDialog() {
        // Obținem data și ora curentă
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Creăm un obiect DatePickerDialog pentru a permite utilizatorului să selecteze o dată
        DatePickerDialog datePickerDialog = new DatePickerDialog(BreakfastActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                //Convertim data selectata in timestamp
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year,month,dayOfMonth);
                String selectedTimestamp = getTimestampFromDate(selectedDate.getTime());
                // Actualizăm RecyclerView cu datele corespunzătoare datei selectate
                updateRecyclerView(selectedTimestamp);

            }
        },year,month,day);
        // Afișăm dialogul de selectare a datei
        datePickerDialog.show();
    }
    /**
     * Actualizează RecyclerView-ul cu alimentele din micul dejun pentru data selectată.
     * @param selectedTimestamp Timestamp-ul corespunzător datei selectate.
     */
    private void updateRecyclerView(String selectedTimestamp) {
        // Obținem referința către nodul "users" în Firebase Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        // Obținem identificatorul unic al utilizatorului curent (UID)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Obținem referința la "HistoryMeals" și "breakfast" sub nodul utilizatorului curent pentru data selectată
        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");
        DatabaseReference breakfastRef = historyMealsRef.child(selectedTimestamp).child("breakfast");
        // Adăugăm un ValueEventListener pentru a prelua datele despre micul dejun
        breakfastRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Inițializăm o listă pentru a stoca obiecte FoodInfo
                List<FoodInfo> foodInfoList = new ArrayList<>();
                // Iterăm prin fiecare masă din micul dejun
                for(DataSnapshot mealSnapshot : snapshot.getChildren()) {
                    // Obținem ID-ul mesei și referința către nodul mesei
                    String mealId = mealSnapshot.getKey();
                    DatabaseReference mealRef = breakfastRef.child(mealId);

                    // Adăugăm un ValueEventListener pentru fiecare masă individuală
                    mealRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Obținem obiectul FoodInfo din snapshot
                            FoodInfo foodInfo = snapshot.getValue(FoodInfo.class);
                            // Verificăm dacă obiectul FoodInfo nu este nul
                            if (foodInfo != null) {
                                // Adăugăm obiectul FoodInfo la listă
                                foodInfoList.add(foodInfo);
                                // Setăm lista de alimente actualizată în adaptor și notificăm schimbarea
                                adapter.setFoodList(foodInfoList);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
            }
        });

    }

    /**
     * Converteste un obiect de tip Date într-un șir de caractere (timestamp) folosind un format specific.
     * @param time Obiectul de tip Date care trebuie convertit.
     * @return Un șir de caractere reprezentând timestamp-ul obținut din obiectul Date.
     */
    private String getTimestampFromDate(Date time) {
        // Creăm un obiect SimpleDateFormat pentru a formata data într-un șir de caractere
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Returnăm timestamp-ul formatat
        return dateFormat.format(time);
    }
    /**
     * Obține timestamp-ul curent sub formă de șir de caractere.
     * @return Șirul de caractere reprezentând timestamp-ul curent.
     */
    public String getCurrentTimestamp(){
        // Obținem data și ora curentă
        Date currentDate = Calendar.getInstance().getTime();
        // Creăm un obiect SimpleDateFormat pentru a formata data într-un șir de caractere
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Returnăm timestamp-ul formatat pentru data și ora curentă
        return dateFormat.format(currentDate);
    }
}