package com.example.prototip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    private TextView ingredientsTextView;
    DatabaseReference reference;
    private RecyclerView recyclerViewInstructions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        recyclerViewInstructions = findViewById(R.id.recyclerViewInstructions);
        ingredientsTextView = findViewById(R.id.textViewIngredientsContent);

        Intent intent = getIntent();
        if(intent != null){
            String recipe = intent.getStringExtra("recipe");
            String ingredients = intent.getStringExtra("ingredients");
            if(recipe != null && ingredients != null){
                List<String> ingredientList = Arrays.asList(ingredients.split(", "));
                StringBuilder formattedIngredients = new StringBuilder();
                for(String ingredient : ingredientList){
                    formattedIngredients.append("\u2022 ").append(ingredient).append("\n");
                }
                ingredientsTextView.setText(formattedIngredients.toString().trim());

                List<String> stepList = Arrays.asList(recipe.split("\\."));
                StepAdapter stepAdapter = new StepAdapter(stepList);
                recyclerViewInstructions.setAdapter(stepAdapter);
                recyclerViewInstructions.setLayoutManager(new LinearLayoutManager(this));

            }
        }
    }
}