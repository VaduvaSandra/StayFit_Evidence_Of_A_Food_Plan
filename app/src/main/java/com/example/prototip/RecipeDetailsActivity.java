package com.example.prototip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

//Activitatea care afiseaza detaliile unei retete, inclusiv instrctiunile detaliate
public class RecipeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        //Initializarea elementelor UI
        RecyclerView recyclerViewInstructions = findViewById(R.id.recyclerViewInstructions);
        TextView ingredientsTextView = findViewById(R.id.textViewIngredientsContent);

        //Obtinerea datelor despre reteta din internet
        Intent intent = getIntent();
        if(intent != null){
            String recipe = intent.getStringExtra("recipe");
            String ingredients = intent.getStringExtra("ingredients");

            //Verificare daca datele sunt valide
            if(recipe != null && ingredients != null){
                //Transformarea sirului de ingrediente intr-o lista si formatarea lor pentru afisare
                List<String> ingredientList = Arrays.asList(ingredients.split(", "));
                StringBuilder formattedIngredients = new StringBuilder();
                for(String ingredient : ingredientList){
                    formattedIngredients.append("\u2022 ").append(ingredient).append("\n");
                }
                ingredientsTextView.setText(formattedIngredients.toString().trim());

                // Transformarea sirului de instructiuni intr-o lista si setarea adapterului pentru RecyclerView
                List<String> stepList = Arrays.asList(recipe.split("\\."));
                StepAdapter stepAdapter = new StepAdapter(stepList);
                recyclerViewInstructions.setAdapter(stepAdapter);
                recyclerViewInstructions.setLayoutManager(new LinearLayoutManager(this));

            }
        }
    }
}