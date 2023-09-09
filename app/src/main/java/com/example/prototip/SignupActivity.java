package com.example.prototip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Initializarea elementelor de interfata
        EditText mEmailField = findViewById(R.id.signup_email);
        EditText mPasswordField = findViewById(R.id.signup_password);
        Button nextButton = findViewById(R.id.next_button);
        TextView loginRedirectText = findViewById(R.id.loginRedirectText);
        CheckBox rememberCheckBox = findViewById(R.id.remember_me_checkbox);

        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);

        rememberCheckBox.setChecked(rememberMe);
        if (rememberMe) {
            mEmailField.setText(sharedPreferences.getString("email", ""));
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(SignupActivity.this,FirstActivity.class));
        }

    //Setarea unui listener pt butonul next
    nextButton.setOnClickListener(v -> {

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
            .addOnCompleteListener(SignupActivity.this, task -> {
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
            });
});
        //Listener pentru textul de redirectionare catre activitatea Log In
        loginRedirectText.setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}