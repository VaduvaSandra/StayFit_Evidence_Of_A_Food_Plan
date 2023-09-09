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

public class SnacksActivity extends AppCompatActivity {
    ImageView datetimeIcon;
    DatabaseReference reference;
    RecyclerView recyclerView;
    FoodItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snacks);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FoodItemAdapter(new ArrayList<>(), new FoodItemAdapter.OnFoodDeleteListener() {
            @Override
            public void onFoodDelete(FoodInfo foodInfo) {
                deleteFoodItem(foodInfo);
            }
        });
        recyclerView.setAdapter(adapter);
        datetimeIcon = findViewById(R.id.datetime_icon);
        datetimeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
// Referința către nodul principal "users"
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

// Obțineți ID-ul utilizatorului curent
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

// Construiți calea către nodul "HistoryMeals" pentru utilizatorul curent
        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");

// Construiți calea către nodul "breakfast" în funcție de timestamp-ul curent
        String currentTimestamp = getCurrentTimestamp();
        DatabaseReference snacksRef = historyMealsRef.child(currentTimestamp).child("snack");

        snacksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FoodInfo> foodInfoList = new ArrayList<>();
                for(DataSnapshot mealSnapshot : snapshot.getChildren()){
                    String mealId = mealSnapshot.getKey();
                    DatabaseReference mealRef = snacksRef.child(mealId);

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
                            Log.e("SnacksActivity","Failed to read value", error.toException());

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SnacksActivity","Failed to read value", error.toException());

            }
        });
        Log.d("Timestamp", currentTimestamp);

    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(SnacksActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                //Convertim data selectata in timestamp
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year,month,dayOfMonth);
                String selectedTimestamp = getTimestampFromDate(selectedDate.getTime());

                updateRecyclerView(selectedTimestamp);

            }
        },year,month,day);
        datePickerDialog.show();
    }

    private void updateRecyclerView(String selectedTimestamp) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");
        DatabaseReference snacksRef = historyMealsRef.child(selectedTimestamp).child("snack");
        String mealId = snacksRef.push().getKey();

        snacksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FoodInfo> foodInfoList = new ArrayList<>();
                for(DataSnapshot mealSnapshot : snapshot.getChildren()){
                    String mealId = mealSnapshot.getKey();
                    DatabaseReference mealRef = snacksRef.child(mealId);

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

    private String getTimestampFromDate(Date time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(time);
    }

    private String getCurrentTimestamp() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    private void deleteFoodItem(FoodInfo foodInfo) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");
        DatabaseReference snacksRef = historyMealsRef.child(getCurrentTimestamp()).child("snack");

        snacksRef.addValueEventListener(new ValueEventListener() {
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