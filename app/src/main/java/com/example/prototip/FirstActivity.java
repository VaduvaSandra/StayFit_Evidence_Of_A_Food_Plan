package com.example.prototip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;

import com.example.prototip.databinding.ActivityFirstBinding;

public class FirstActivity extends AppCompatActivity {

    // Declarație a obiectului de tip binding
    ActivityFirstBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializarea binding-ului utilizand clasa de binding auto-generata
        binding = ActivityFirstBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Inlocuirea fragmentului initial cu HomeFragment
        replaceFragment(new HomeFragment());

        //Setarea fundalului pentru bottomNavigationView la null pentru un stil personalizat
        binding.bottomNavigationView.setBackground(null);

        //Setarea listener-ului pentru selectia de elemente in bottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            //Schimbarea fragmentelor in functie de elementul selectat
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.food:
                    replaceFragment(new RecipesFragment());
                    break;
                case R.id.statistics:
                    replaceFragment(new StatisticsFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                default:
                    break;
            }
            return true; // Returneaza true pentru a indica faptul ca evenimentul de selectie al elementului este gestionat
        });
        //Setarea listener-ului de click pentru Floating Action Button
        binding.fab.setOnClickListener(view -> {
            //Pornirea PersonalFoodActivity la apasarea fab
            Intent intent = new Intent(FirstActivity.this, PersonalFoodActivity.class);
            startActivity(intent);
        });
    }
        //Metoda pentru inlocuirea fragmentului curent cu unul nou
        private void replaceFragment (Fragment fragment){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();

        }
}
