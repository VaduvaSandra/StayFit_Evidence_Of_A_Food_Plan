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

import java.security.Key;

public class EditProfileActivity extends AppCompatActivity {

    EditText editUsername, editAge, editWeight, editGoal, editDesiredWeight;
    Button saveButton;
    String nameUser;
    long AgeUser;
    long WeightUser;
    String GoalUser;
    long DesiredWeightUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String Key = getIntent().getStringExtra("firebaseKey");
        setContentView(R.layout.activity_edit_profile);
        reference = FirebaseDatabase.getInstance().getReference("users").child(Key);
        editAge = findViewById(R.id.editAge);
        editGoal = findViewById(R.id.editGoal);
        editUsername = findViewById(R.id.editName);
        editWeight = findViewById(R.id.editWeight);
        editDesiredWeight = findViewById(R.id.editdesireWeight);
        saveButton = findViewById(R.id.saveButton);
        showData();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserNameChanged() || isAgeChanged() || isGoalChanged() || isWeightChanged() || isDesiredWeightChanged()){
                    Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isDesiredWeightChanged() {
        long newDesiredWeight = Long.parseLong(editDesiredWeight.getText().toString().trim());
        DesiredWeightUser = getIntent().getLongExtra("desiredWeight",(long) 0.0);
     //   Log.d( "Failed to read value.", "Valori: " +newDesiredWeight + DesiredWeightUser);
        if (newDesiredWeight != 0 && !(DesiredWeightUser == newDesiredWeight)){
            reference.child("desiredWeight").setValue(newDesiredWeight);
            Log.d("Ref","Reference: "+reference);
            DesiredWeightUser = newDesiredWeight;
            return true;
        }else{
            return false;
        }
    }

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

    private boolean isAgeChanged() {
        long newAge = Long.parseLong(editAge.getText().toString().trim());
        AgeUser = getIntent().getLongExtra("age",(long) 0.0);
        if (newAge != 0 && !(AgeUser == newAge)){
            reference.child("age").setValue(newAge);
            Log.d("Ref","Reference: "+reference);
            AgeUser = newAge;
            return true;
        } else {
            return false;
        }
    }

    private boolean isGoalChanged() {
        String newGoal = editGoal.getText().toString().trim();
        GoalUser = getIntent().getStringExtra("goal");
        if (newGoal != null && !GoalUser.equals(newGoal)){
            reference.child("goal").setValue(newGoal);
            Log.d("Ref","Reference: "+reference);
            GoalUser = newGoal;
            return true;
        } else {
            return false;
        }
    }

    private boolean isWeightChanged() {
        long newWeight = Long.parseLong(editWeight.getText().toString().trim());
        WeightUser = getIntent().getLongExtra("weight",(long) 0.0);
        if (newWeight != 0 && !(WeightUser == newWeight)){
            reference.child("weight").setValue(newWeight);
            Log.d("Ref","New Weight: "+newWeight);
            WeightUser = newWeight;
            return true;
        } else {
            return false;
        }
    }

    public void showData(){

        Intent intent = getIntent();
        nameUser = intent.getStringExtra("username");
        AgeUser = intent.getLongExtra("age", (long) 0.0);
        WeightUser = intent.getLongExtra("weight", (long) 0.0);
        GoalUser = intent.getStringExtra("goal");
        DesiredWeightUser = intent.getLongExtra("desiredWeight", (long) 0.0);

        // Verificare valorile inițiale în logcat
        Log.d("showData", "nameUser: " + nameUser);
        Log.d("showData", "AgeUser: " + AgeUser);
        Log.d("showData", "WeightUser: " + WeightUser);
        Log.d("showData", "GoalUser: " + GoalUser);
        Log.d("showData", "DesiredWeightUser: " + DesiredWeightUser);

        editUsername.setText(nameUser);
        editAge.setText(String.valueOf(AgeUser));
        editWeight.setText(String.valueOf(WeightUser));
        editGoal.setText(GoalUser);
        editDesiredWeight.setText(String.valueOf(DesiredWeightUser));
    }
}