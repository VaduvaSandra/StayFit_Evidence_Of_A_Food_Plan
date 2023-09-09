package com.example.prototip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipesFragment extends Fragment {

    RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private DatabaseReference databaseReference;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    String mParam1;
    String mParam2;

    public RecipesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoodFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipesFragment newInstance(String param1, String param2) {
        RecipesFragment fragment = new RecipesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Incarcam aspectul pentru acest fragment
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        // Obținem referința la RecyclerView din fișierul XML
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        // Setăm layout manager-ul pentru RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference("Retete");
        adapter = new RecipeAdapter(new ArrayList<>(), new RecipeAdapter.OnRecipeClickListener() {
            @Override
            public void onRecipeClick(Recipe recipe, int position) {
                int recipeIndex = position +1;
                DatabaseReference recipeRef = databaseReference.child("" + recipeIndex); // Accesarea subnodului specific în funcție de index (de exemplu, reteta_1)
                Log.d("Reteta","" +recipeRef);
                recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String ingredients = snapshot.child("ingrediente").getValue(String.class);
                            String recipe = snapshot.child("reteta").getValue(String.class);
                            Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
                            intent.putExtra("recipe", recipe);
                            intent.putExtra("ingredients", ingredients);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
                    }
                });

            }
        });
        recyclerView.setAdapter(adapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> recipes = new ArrayList<>();
                List<CardView> cardViews = new ArrayList<>();
                //Iteram prin fiecare copil al snapshotului(retetele din baza de date)
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //Extragem datele retetei copil
                    String nume = dataSnapshot.child("nume").getValue(String.class);
                    String imagine =dataSnapshot.child("imagine").getValue(String.class);

                    //Cream un nou obiect Recipe din datele extrase
                    Recipe recipe = new Recipe(nume,imagine);
                    recipe.setSnapshot(snapshot);
                    recipes.add(recipe);

                    // Creăm un nou card pentru reteta și setăm datele din obiectul Recipe
                    CardView cardView = new CardView(getContext());
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View itemView = inflater.inflate(R.layout.list_item, container, false);
                    cardView.addView(itemView);

                    TextView titleTextView = itemView.findViewById(R.id.title);
                    titleTextView.setText(recipe.getName());

                    ImageView imageView = itemView.findViewById(R.id.imageView);
                    Glide.with(getContext())
                            .load(recipe.getImage())
                            .into(imageView);
                    cardViews.add(cardView);
                }
                // Actualizăm adapterul RecyclerView cu noile retete
                adapter.setData(recipes);
                adapter.setCardViews(cardViews);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
            }
        });
        return view;
    }
}


