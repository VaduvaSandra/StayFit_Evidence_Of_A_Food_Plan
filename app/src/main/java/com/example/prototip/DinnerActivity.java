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
import android.widget.TextView;

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

public class DinnerActivity extends AppCompatActivity {
    ImageView datetimeIcon;// ImageView pentru pictograma datei și orei
    RecyclerView recyclerView;// RecyclerView pentru afișarea elementelor de mâncare
    FoodItemAdapter adapter;// Adaptor pentru popularea RecyclerView cu elemente de mâncare
    TextView selectedDateText;// TextView pentru afișarea datei selectate
    ValueEventListener valueEventListener;// ValueEventListener pentru Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setarea aspectului pentru activitate
        setContentView(R.layout.activity_dinner);
        // Inițializarea RecyclerView și setarea managerului său de aspect
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Inițializarea adaptorului cu o listă goală și un ascultător pentru ștergerea elementelor de mâncare
        adapter = new FoodItemAdapter(new ArrayList<>(), new FoodItemAdapter.OnFoodDeleteListener() {
            @Override
            public void onFoodDelete(FoodInfo foodInfo) {
                deleteFoodItem(foodInfo);
            }
        });
        // Setarea adaptorului pentru RecyclerView
        recyclerView.setAdapter(adapter);
        // Inițializarea datetimeIcon și setarea unui OnClickListener pentru a arăta dialogul selectorului de dată
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
        DatabaseReference dinnerRef = historyMealsRef.child(currentTimestamp).child("dinner");

        // Adăugarea unui listener pentru a asculta modificările în cadrul nodului "dinnerRef" în Firebase Realtime Database
        dinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Inițializarea unei liste pentru a stoca informațiile despre elementele de mâncare
                List<FoodInfo> foodInfoList = new ArrayList<>();
                // Iterarea prin fiecare sub-nod al nodului "dinnerRef"
                for(DataSnapshot mealSnapshot : snapshot.getChildren()){
                    // Obținerea ID-ului unic al fiecărei mese
                    String mealId = mealSnapshot.getKey();
                    // Obținerea unei referințe către sub-nodul specific al fiecărei mese
                    DatabaseReference mealRef = dinnerRef.child(mealId);

                    // Adăugarea unui listener pentru a asculta modificările în cadrul sub-nodului specific al fiecărei mese
                    mealRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Obținerea informațiilor despre mâncare din snapshot
                            FoodInfo foodInfo = snapshot.getValue(FoodInfo.class);
                            // Verificarea dacă informațiile despre mâncare nu sunt nule
                            if(foodInfo != null){
                                // Adăugarea informațiilor despre mâncare în lista foodInfoList
                                foodInfoList.add(foodInfo);
                                // Actualizarea listei de alimente din adaptor și notificarea adaptorului despre modificări
                                adapter.setFoodList(foodInfoList);
                                adapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Tratarea cazului în care există erori în citirea valorilor din bază de date
                            Log.e("Dinner","Failed to read value", error.toException());

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Tratarea cazului în care există erori în citirea valorilor din nodul "dinnerRef"
                Log.e("Dinner","Failed to read value", error.toException());

            }
        });
        Log.d("Timestamp", currentTimestamp);

    }

    // Funcție pentru a obține timestamp-ul curent sub formă de șir de caractere
    private String getCurrentTimestamp() {
        // Obținerea datei și orei curente
        Date currentDate = Calendar.getInstance().getTime();
        // Crearea unui obiect SimpleDateFormat pentru a formata data într-un șir de caractere specific
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Returnarea timestamp-ului curent sub formă de șir de caractere
        return dateFormat.format(currentDate);
    }

    // Funcție pentru afișarea unui dialog de selectare a datei
    private void showDatePickerDialog() {
        // Obținerea instanței actuale a Calendarului pentru data curentă
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Crearea unui obiect DatePickerDialog pentru a permite utilizatorului să selecteze data
        DatePickerDialog datePickerDialog = new DatePickerDialog(DinnerActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                //Convertim data selectata in timestamp
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year,month,dayOfMonth);
                String selectedTimestamp = getTimestampFromDate(selectedDate.getTime());

                // Actualizarea afișajului cu datele corespunzătoare datei selectate
                updateRecyclerView(selectedTimestamp);
            }
        },year, month, day);
        // Afișarea dialogului de selectare a datei
        datePickerDialog.show();
    }

    private void updateRecyclerView(String selectedTimestamp) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");
        DatabaseReference dinnerRef = historyMealsRef.child(selectedTimestamp).child("dinner");
        String mealId = dinnerRef.push().getKey();

        // Adăugare unui ascultător de evenimente pentru nodul "dinner" din baza de date
        dinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Inițializare lista pentru a stoca informațiile despre alimente
                List<FoodInfo> foodInfoList = new ArrayList<>();
                // Iterare prin fiecare copil al nodului "dinner"
                for (DataSnapshot mealSnapshot : snapshot.getChildren()){
                    // Obținerea identificatorului unic al fiecărui copil (meal)
                    String mealId = mealSnapshot.getKey();
                    // Obținerea referinței către sub-nodul specific al fiecărui "meal"
                    DatabaseReference mealRef = dinnerRef.child(mealId);

                    // Adăugare unui ascultător de evenimente pentru fiecare "meal" în parte
                    mealRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Obținerea obiectului FoodInfo din cadrul "meal"
                            FoodInfo foodInfo = snapshot.getValue(FoodInfo.class);
                            // Verificare dacă obiectul FoodInfo este diferit de null
                            if(foodInfo != null){
                                // Adăugare obiect FoodInfo la lista de informații despre alimente
                                foodInfoList.add(foodInfo);
                                // Actualizare lista de alimente din adaptor și notificare că a avut loc o schimbare
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

    private void deleteFoodItem(FoodInfo foodInfo) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");
        DatabaseReference dinnerRef = historyMealsRef.child(getCurrentTimestamp()).child("dinner");

        dinnerRef.addValueEventListener(new ValueEventListener() {
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