package com.example.prototip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.ViewHolder> {
    private List<FoodInfo> foodList;
    private OnFoodDeleteListener deleteListener;
    int selectedPosition;

    // Constructor pentru initializarea adapterului cu lista de alimente si un ascultator pentru stergerea alimentelor
    public FoodItemAdapter(List<FoodInfo> foodList, OnFoodDeleteListener deleteListener) {
        this.foodList = foodList;
        this.deleteListener = deleteListener;
    }


    //Metoda pentru a seta lista de alimente din adapter
    public void setFoodList(List<FoodInfo> foodList) {
        this.foodList = foodList;
    }

    //Metoda pentru a seta pozitia selectata in adaptere
    public void setSelectedPosition(int position){
        selectedPosition = position;
    }

    //Metoda pentru a obtine lista de alimente din adapter
    public List<FoodInfo> getFoodList() {
        return foodList;
    }

    //Interfata pentru ascultatorul evenimentului de stergere a alimentului
    public interface OnFoodDeleteListener{
        void onFoodDelete(FoodInfo foodInfo);
    }

    //Metoda pentru crearea unui nou element de vizualizare in lista
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new ViewHolder(itemView);
    }

    //Metoda pentru legarea datelor la un element de vizualizare existent in lista
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodInfo foodInfo = foodList.get(position);
        holder.foodNameTextView.setText(foodInfo.getFoodName());
        holder.caloriesValueTextView.setText(String.valueOf(foodInfo.getCalories()));
        holder.proteinValueTextView.setText(String.valueOf(foodInfo.getProtein()));
        holder.fatValueTextView.setText(String.valueOf(foodInfo.getFat()));
        holder.carbsValueTextView.setText(String.valueOf(foodInfo.getCarbohydrates()));
    }

    //Metoda pentru a obtine numarul total de elemente din lista
    @Override
    public int getItemCount() {
        return foodList.size();
    }

    //Clasa interna ViewHolder pentru a reprezenta elementul de vizualizare al unui aliment in RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView caloriesValueTextView;
        TextView proteinValueTextView;
        TextView fatValueTextView;
        TextView carbsValueTextView;
        ImageButton deleteButton;

        //Constructor pentru initializarea elementelor de vizualizare dintr-un element de lista
        public ViewHolder(View itemView){
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.food_name);
            caloriesValueTextView=itemView.findViewById(R.id.calories_value);
            proteinValueTextView=itemView.findViewById(R.id.protein_value);
            fatValueTextView=itemView.findViewById(R.id.fat_value);
            carbsValueTextView=itemView.findViewById(R.id.carbs_value);
            deleteButton = itemView.findViewById(R.id.delete_button);

            //Setare listener pentru butonul de stergere a alimentului
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        setSelectedPosition(position);
                        FoodInfo foodInfo = foodList.get(position);
                        deleteListener.onFoodDelete(foodInfo);
                        // Referinte catre baza de date Firebase pentru utilizatori si istoricul meselor
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");

                        // Referință către totalul caloric pentru data curentă
                        DatabaseReference totalCaloriesRef = historyMealsRef.child(getCurrentDate()).child("totalCalories");

                        //Actualizare total calorii dupa stergerea alimentului
                        totalCaloriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Integer totalCalories = snapshot.getValue(Integer.class);
                                if(totalCalories == null){
                                    totalCalories = 0;
                                }
                                totalCalories -= foodInfo.getCalories();
                                totalCaloriesRef.setValue(totalCalories);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
                            }
                        });
                    }
                }
            });
        }
    }

    //Metoda pentru a obtine data curenta sub forma de sir de caractere
    private String getCurrentDate() {
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
