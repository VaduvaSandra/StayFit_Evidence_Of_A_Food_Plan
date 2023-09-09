package com.example.prototip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BasicInfo extends AppCompatActivity {

    Button letsgoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        //Initializare buton
        letsgoButton = findViewById(R.id.signup_button);

        //Setare listener pentru buton
        letsgoButton.setOnClickListener(view -> {

            //Extragerea valorilor introduse de utilizator
            int age = Integer.parseInt(((EditText) findViewById(R.id.editTextAge)).getText().toString().trim());
            int genderId = ((RadioGroup) findViewById(R.id.radioGroupGender)).getCheckedRadioButtonId();
            RadioButton genderRadioButton = findViewById(genderId);
            String gender = genderRadioButton.getText().toString().trim();
            int height = Integer.parseInt(((EditText) findViewById(R.id.editTextHeightCm)).getText().toString().trim());
            int weight = Integer.parseInt(((EditText) findViewById(R.id.editTextWeight)).getText().toString().trim());
            String activity = ((Spinner) findViewById(R.id.spinnerActivityLevel)).getSelectedItem().toString().trim();
            String goal = ((Spinner) findViewById(R.id.spinnerObiectiv)).getSelectedItem().toString().trim();
            int desiredWeight = Integer.parseInt(((EditText) findViewById(R.id.editTextWeightt)).getText().toString().trim());
            String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString().trim();

            //Initializarea referintei catre baza de date si autentificator
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            //Obtinerea Id-ului utilizatorului curent
            String userId = mAuth.getCurrentUser().getUid();
            //Crearea unui obiect UserInfo cu datele introduse
            UserInfo user = new UserInfo(age, gender, height, weight, activity, goal, desiredWeight, username);


            //Salveaza datele in baza de date Firebase
            reference.child("users").child(userId).setValue(user).addOnSuccessListener(unused -> {
                //Succes: Se afiseaza un mesaj si se navigheaza la urmatoarea activitate
                Toast.makeText(BasicInfo.this, "Utilizator creat cu succes!", Toast.LENGTH_SHORT).show();
                reference.child("users").child(userId).child("MealsHistory");

                Intent intent = new Intent(BasicInfo.this, FirstActivity.class);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e ->
                    //Esec: Afiseaza un mesaj de eroare
                    Toast.makeText(BasicInfo.this,"Eroare la crearea utilizatorului:"+e.getMessage(),Toast.LENGTH_SHORT).show());
        });
    }

}