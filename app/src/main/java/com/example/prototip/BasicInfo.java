package com.example.prototip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;

import android.service.autofill.UserData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;


public class BasicInfo extends AppCompatActivity {

    private EditText inaltime, varsta, greutate, greutateDorita, username;
    Spinner activitate, obiectiv, obiectivSaptamanal;
    private RadioButton radioButtonMale, radioButtonFemale;
    private RadioGroup radioButtonGroup;
    Button letsgoButton;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private static final String TAG = "BasicInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        username = findViewById(R.id.editTextUsername);
        inaltime = findViewById(R.id.editTextHeightCm);
        varsta = findViewById(R.id.editTextAge);
        greutate = findViewById(R.id.editTextWeight);
        greutateDorita = findViewById(R.id.editTextWeightt);
        activitate = findViewById(R.id.spinnerActivityLevel);
        obiectiv = findViewById(R.id.spinnerObiectiv);
        obiectivSaptamanal = findViewById(R.id.spinnerWeeklyGoal);
        radioButtonGroup = findViewById(R.id.radioGroupGender);
        radioButtonMale = findViewById(R.id.radioButtonGenderMale);
        radioButtonFemale =findViewById(R.id.radioButtonGenderFemale);
        letsgoButton = findViewById(R.id.signup_button);

        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        letsgoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int age = Integer.parseInt(varsta.getText().toString().trim());
                int genderId = radioButtonGroup.getCheckedRadioButtonId();
                RadioButton genderRadioButton = findViewById(genderId);
                String gender = genderRadioButton.getText().toString().trim();
                int height = Integer.parseInt(inaltime.getText().toString().trim());
                int weight = Integer.parseInt(greutate.getText().toString().trim());
                String activity = activitate.getSelectedItem().toString().trim();
                String goal = obiectiv.getSelectedItem().toString().trim();
                int desiredWeight = Integer.parseInt(greutateDorita.getText().toString().trim());
                String Username = username.getText().toString().trim();

                String userId = mAuth.getCurrentUser().getUid();
                UserInfo user = new UserInfo(age, gender, height, weight, activity, goal, desiredWeight, Username);
                reference.child("users").child(userId).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(BasicInfo.this, "Utilizator creat cu succes!", Toast.LENGTH_SHORT).show();
                        reference.child("users").child(userId).child("MealsHistory");

                        Intent intent = new Intent(BasicInfo.this, FirstActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BasicInfo.this, "Eroare la crearea utilizatorului: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

}