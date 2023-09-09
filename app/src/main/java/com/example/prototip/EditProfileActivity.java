package com.example.prototip;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EditProfileActivity extends AppCompatActivity {

    //Declarare variabile pentru elementele de interfata si referinta
    EditText editUsername;
    EditText editAge;
    EditText editWeight;
    EditText editGoal;
    EditText editDesiredWeight;
    Button saveButton;
    String nameUser;
    long ageUser;
    long weightUser;
    String goalUser;
    long desiredWeightUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtinem cheia Firebase din intent
        String key = getIntent().getStringExtra("firebaseKey");

        //Setare layout
        setContentView(R.layout.activity_edit_profile);

        //Initializare referinta la nodul Firebase
        reference = FirebaseDatabase.getInstance().getReference("users").child(key);

        //Initializare elemente interfata
        editAge = findViewById(R.id.editAge);
        editGoal = findViewById(R.id.editGoal);
        editUsername = findViewById(R.id.editName);
        editWeight = findViewById(R.id.editWeight);
        editDesiredWeight = findViewById(R.id.editdesireWeight);
        saveButton = findViewById(R.id.saveButton);
        //Afiseaza datele utilizatorului
        showData();

        //Adauga un listener de evenimente pentru butonul de salvare
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Verifica daca au fost facute modificari si afiseaza un mesaj corespunzator
                if (isUserNameChanged() || isAgeChanged() || isGoalChanged() || isWeightChanged() || isDesiredWeightChanged()){
                    Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Metoda pentru verificarea modificarilor la greutatea dorita
    private boolean isDesiredWeightChanged() {
        long newDesiredWeight = Long.parseLong(editDesiredWeight.getText().toString().trim());
        desiredWeightUser = getIntent().getLongExtra("desiredWeight", 0L);
        if (newDesiredWeight != 0 && desiredWeightUser != newDesiredWeight) {
            reference.child("desiredWeight").setValue(newDesiredWeight);
            Log.d("Ref", "Reference: " + reference);
            desiredWeightUser = newDesiredWeight;
            return true;
        } else {
            return false;
        }
    }

    //Metoda pentru verificarea modificarilor la numele utilizatorului
    private boolean isUserNameChanged() {
        String newUsername = editUsername.getText().toString().trim();
        nameUser = getIntent().getStringExtra("username");
        Log.d("Failed to read value","Valori:" +newUsername +nameUser);
        if (newUsername!= null && !nameUser.equals(newUsername)){
            reference.child("username").setValue(newUsername);
            Log.d("Username","Username: "+reference);
            nameUser = newUsername;
            return true;
        } else {
            return false;
        }
    }

    //Metoda pentru verificarea modificarilor la varsta utilizatorului
    private boolean isAgeChanged() {
        long newAge = Long.parseLong(editAge.getText().toString().trim());
        ageUser = getIntent().getLongExtra("age", 0L);
        if (newAge != 0 && ageUser != newAge) {
            reference.child("age").setValue(newAge);
            Log.d("Ref", "Reference: " + reference);
            ageUser = newAge;
            return true;
        } else {
            return false;
        }
    }

    //Metoda pentru verificarea modificarilor la obiectivul utilizatorului
    private boolean isGoalChanged() {
        String newGoal = editGoal.getText().toString().trim();
        goalUser = getIntent().getStringExtra("goal");
        if (newGoal != null && !goalUser.equals(newGoal)) {
            reference.child("goal").setValue(newGoal);
            Log.d("Ref", "Reference: " + reference);
            goalUser = newGoal;
            return true;
        } else {
            return false;
        }
    }

    //Metoda pentru verificarea modificarilor la greutatea utilizatorului
    private boolean isWeightChanged() {
        long newWeight = Long.parseLong(editWeight.getText().toString().trim());
        weightUser = getIntent().getLongExtra("weight", 0L);
        if (newWeight != 0 && weightUser != newWeight) {
            reference.child("weight").setValue(newWeight);
            Log.d("Ref", "New Weight: " + newWeight);
            weightUser = newWeight;
            return true;
        } else {
            return false;
        }
    }

    //Metoda pentru afisarea datelor utilizatorului in interfata
    public void showData(){

        Intent intent = getIntent();
        nameUser = intent.getStringExtra("username");
        ageUser = intent.getLongExtra("age", (long) 0.0);
        weightUser = intent.getLongExtra("weight", (long) 0.0);
        goalUser = intent.getStringExtra("goal");
        desiredWeightUser = intent.getLongExtra("desiredWeight", (long) 0.0);

        // Verificare valorile inițiale în logcat
        Log.d("showData", "nameUser: " + nameUser);
        Log.d("showData", "AgeUser: " + ageUser);
        Log.d("showData", "WeightUser: " + weightUser);
        Log.d("showData", "GoalUser: " + goalUser);
        Log.d("showData", "DesiredWeightUser: " + desiredWeightUser);

        // Setare text în campurile de editare
        editUsername.setText(nameUser);
        editAge.setText(String.valueOf(ageUser));
        editWeight.setText(String.valueOf(weightUser));
        editGoal.setText(goalUser);
        editDesiredWeight.setText(String.valueOf(desiredWeightUser));
    }
}