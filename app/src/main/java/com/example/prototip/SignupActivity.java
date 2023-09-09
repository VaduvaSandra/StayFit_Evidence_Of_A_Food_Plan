package com.example.prototip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button nextButton;
    private TextView loginRedirectText;
    private CheckBox rememberCheckBox;

    private boolean rememberMe = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Obttinerea instantei FirebaseAuth()
        mAuth = FirebaseAuth.getInstance();
        //Initializarea elementelor de interfata
        mEmailField = findViewById(R.id.signup_email);
        mPasswordField = findViewById(R.id.signup_password);
        nextButton = findViewById(R.id.next_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        rememberCheckBox = findViewById(R.id.remember_me_checkbox);

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        rememberCheckBox.setChecked(rememberMe);
        if (rememberMe) {
            mEmailField.setText(sharedPreferences.getString("email", ""));
        }
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(SignupActivity.this,FirstActivity.class));
        }

        //Setarea unui listener pentru butonul Next
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtinerea valorilor introduse pentru email si parola
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                //Verificam daca campurile de email si parola sunt completate
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Crearea unui nou utilizator cu adresa de email și parola introduse
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    if (rememberCheckBox.isChecked()) {
                                        sharedPreferences.edit()
                                                .putBoolean("rememberMe", true)
                                                .putString("email", email)
                                                .apply();
                                    } else {
                                        sharedPreferences.edit()
                                                .clear()
                                                .apply();
                                    }
                                    //Redirectionarea catre o alta activitate
                                    startActivity(new Intent(SignupActivity.this, BasicInfo.class));
                                    finish();
                                } else {
                                    // Autentificare esuata
                                    Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        //Listener pentru textul de redirectionare catre activitatea Log In
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }
}