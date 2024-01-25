package com.example.prototip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter pentru afișarea și manipularea listei de alimente în cadrul unui RecyclerView.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    List<FoodInfo> foodList; // Lista de alimente
    OnFoodDeleteListener deleteListener; // Listener pentru ștergerea alimentelor
    int selectedPosition; // Poziția alimentului selectat
    RadioButton selectedRadioButton;// RadioButton-ul selectat
    int totalCalories = 0;// Numărul total de calorii

    public MyAdapter(List<FoodInfo> foodList, OnFoodDeleteListener deleteListener){
        this.foodList = foodList;
        this.deleteListener = deleteListener;
    }


    public void setFoodList(List<FoodInfo> foodList) {
        this.foodList = foodList;
        notifyDataSetChanged();
    }
    public void setSelectedPosition(int position){
        selectedPosition = position;
    }


    public List<FoodInfo> getFoodList() {
        return foodList;
    }

    public interface OnFoodDeleteListener{
        void onFoodDelete(FoodInfo foodInfo);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_food_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obține informații despre alimentul de la poziția specificată
        FoodInfo foodInfo = foodList.get(position);
        // Setează textul pentru diferitele informații ale alimentului în ViewHolder
        holder.foodNameTextView.setText(foodInfo.getFoodName());
        holder.caloriesTextView.setText(String.valueOf(foodInfo.getCalories()));
        holder.carbsTextView.setText(String.valueOf(foodInfo.getCarbohydrates()));
        holder.fatTextView.setText(String.valueOf(foodInfo.getFat()));
        holder.proteinTextView.setText(String.valueOf(foodInfo.getProtein()));

        // Actualizează numărul total de calorii cu cele ale alimentului curent
        totalCalories += foodInfo.getCalories();
        

    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView caloriesTextView;
        TextView carbsTextView;
        TextView fatTextView;
        TextView proteinTextView;
        ImageButton deleteButton;
        ImageButton addButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.food_name);
            caloriesTextView = itemView.findViewById(R.id.calories_value);
            carbsTextView = itemView.findViewById(R.id.carbs_value);
            fatTextView = itemView.findViewById(R.id.fat_value);
            proteinTextView = itemView.findViewById(R.id.protein_value);
            deleteButton = itemView.findViewById(R.id.delete_button);
            addButton = itemView.findViewById(R.id.add_button);
            RadioButton radioButton1 = itemView.findViewById(R.id.radio_button1);
            RadioButton radioButton2 = itemView.findViewById(R.id.radio_button2);
            RadioButton radioButton3 = itemView.findViewById(R.id.radio_button3);
            RadioButton radioButton4 = itemView.findViewById(R.id.radio_button4);
            RadioGroup radioGroup = itemView.findViewById(R.id.meal_radio_group);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(i == radioButton1.getId()){
                        selectedRadioButton = radioButton1;
                    }else if(i == radioButton2.getId()){
                        selectedRadioButton = radioButton2;
                    }else if(i == radioButton3.getId()){
                        selectedRadioButton = radioButton3;
                    }else if(i == radioButton4.getId()){
                        selectedRadioButton = radioButton4;
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        setSelectedPosition(position);
                        FoodInfo foodInfo = foodList.get(position);
                        deleteListener.onFoodDelete(foodInfo);

                    }
                }
            });
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        FoodInfo selectedFood = foodList.get(position);
                        addSelectedFoodToHistory(selectedFood);
                    }
                }
            });
        }
    }

    private void addSelectedFoodToHistory(FoodInfo selectedFood) {
        // Obține o referință la rădăcina "users" în Firebase Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        // Obține ID-ul utilizatorului curent autentificat
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Construiește referința către istoricul de mese al utilizatorului curent
        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");

        // Obține data curentă sub formă de șir de caractere (ex. "yyyy-MM-dd")
        String currentDate = getCurrentDate();
        // Obține referința către masa curentă (breakfast, lunch, dinner, snack) în funcție de RadioButton-ul selectat
        DatabaseReference currentMealRef = historyMealsRef.child(currentDate).child(getSelectedMealKey(selectedRadioButton));


        // Adaugă alimentul selectat la istoricul mesei curente
        currentMealRef.push().setValue(selectedFood);
        // Obține referința către totalul de calorii pentru data curentă în istoricul alimentar
        DatabaseReference totalCaloriesRef = historyMealsRef.child(getCurrentDate()).child("totalCalories");
        // Ascultă o singură dată pentru a obține valoarea actuală a totalului de calorii
        totalCaloriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Obține valoarea curentă a totalului de calorii
                Integer totalCalories = snapshot.getValue(Integer.class);
                // Verifică dacă totalul de calorii există în baza de date
                if(totalCalories == null){
                    // Dacă nu există, inițializează totalul de calorii cu 0
                    totalCalories = 0;
                }
                // Adaugă numărul de calorii ale alimentului selectat la totalul de calorii
                totalCalories += selectedFood.getCalories();
                // Actualizează valoarea totalului de calorii în Firebase Realtime Database
                totalCaloriesRef.setValue(totalCalories);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
            }
        });

    }

    private String getSelectedMealKey(RadioButton selectedRadioButton) {
        int selectedId = selectedRadioButton.getId();
        if(selectedId == R.id.radio_button1){
            return "breakfast";
        }else if(selectedId == R.id.radio_button2){
            return "lunch";
        }else if(selectedId == R.id.radio_button3){
            return "dinner";
        }else if(selectedId == R.id.radio_button4){
            return "snack";
        }
        return "";
    }


    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
