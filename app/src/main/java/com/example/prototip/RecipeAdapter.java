package com.example.prototip;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;
    private OnRecipeClickListener listener;
    private Context context;
    DatabaseReference reference;
    private List<CardView> cardViews;


    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    public void setData(List<Recipe> recipes){
        this.recipes = recipes;
        notifyDataSetChanged();
    }
    public void setCardViews(List<CardView> cardViews) {
        this.cardViews = cardViews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe,listener);


        holder.nameTextView.setText(recipe.getName());


        // încarcă imaginea din URL-ul stocat în obiectul Recipe folosind biblioteca Glide
        Glide.with(holder.imageView.getContext())
                .load(recipe.getImage())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView nameTextView;
        public OnRecipeClickListener listener;
        CardView cardView;



        public ViewHolder(@NonNull View itemView, OnRecipeClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.title);
            cardView = itemView.findViewById(R.id.card_view);
            this.listener = listener;
            //Setam clickListener pentru itemView
            itemView.setOnClickListener(this);
            //Setam clickListener pentru cardView
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                Recipe clickedRecipe = recipes.get(position);

                Intent intent = new Intent(view.getContext(),RecipeDetailsActivity.class);
                intent.putExtra("firebaseKey", clickedRecipe.getFirebaseKey());
                intent.putExtra("nume", clickedRecipe.getName());
                intent.putExtra("ingrediente", clickedRecipe.getIngredients());
                intent.putExtra("reteta", clickedRecipe.getInstructions());

                listener.onRecipeClick(clickedRecipe,position);
            }
        }

        public void bind(Recipe recipe, OnRecipeClickListener listener) {
            nameTextView.setText(recipe.getName());
            cardView.setOnClickListener(this);

        }
    }

}