package com.example.prototip;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.ViewHolder> {
    private List<FoodInfo> foodList;
    private OnFoodDeleteListener deleteListener;
    private int selectedPosition;

    public FoodItemAdapter(List<FoodInfo> foodList, OnFoodDeleteListener deleteListener) {
        this.foodList = foodList;
        this.deleteListener = deleteListener;
    }


    public void setFoodList(List<FoodInfo> foodList) {
        this.foodList = foodList;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodInfo foodInfo = foodList.get(position);
        holder.foodNameTextView.setText(foodInfo.getFoodName());
        holder.caloriesValueTextView.setText(String.valueOf(foodInfo.getCalories()));
        holder.proteinValueTextView.setText(String.valueOf(foodInfo.getProtein()));
        holder.fatValueTextView.setText(String.valueOf(foodInfo.getFat()));
        holder.carbsValueTextView.setText(String.valueOf(foodInfo.getCarbohydrates()));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodNameTextView;
        public TextView caloriesValueTextView;
        public TextView proteinValueTextView;
        public TextView fatValueTextView;
        public TextView carbsValueTextView;
        public ImageButton deleteButton;

        public ViewHolder(View itemView){
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.food_name);
            caloriesValueTextView=itemView.findViewById(R.id.calories_value);
            proteinValueTextView=itemView.findViewById(R.id.protein_value);
            fatValueTextView=itemView.findViewById(R.id.fat_value);
            carbsValueTextView=itemView.findViewById(R.id.carbs_value);
            deleteButton = itemView.findViewById(R.id.delete_button);
            
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                 //   int position = getAbsoluteAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        setSelectedPosition(position);
                        FoodInfo foodInfo = foodList.get(position);
                        deleteListener.onFoodDelete(foodInfo);
                     //   deleteFoodFromDatabase(foodInfo.getFoodName());
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference historyMealsRef = usersRef.child(userId).child("HistoryMeals");

                        DatabaseReference totalCaloriesRef = historyMealsRef.child(getCurrentDate()).child("totalCalories");
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

                            }
                        });
                    }
                }
            });
        }
    }

    private String getCurrentDate() {
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
