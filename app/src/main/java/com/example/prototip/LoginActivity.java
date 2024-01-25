package com.example.prototip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail;
    EditText loginPassword;
    Button loginButton;
    FirebaseAuth auth;
    TextView forgotPassword;
    CheckBox rememberCheckBox;

    private  boolean rememberMe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setează conținutul vizual al activității la aspectul ecranului de autentificare
        setContentView(R.layout.activity_login);
        // Inițializează elementele vizuale din aspect
        initializeViews();
        // Obține o instanță a FirebaseAuth pentru gestionarea autentificării
        auth = FirebaseAuth.getInstance();

        // Recuperează preferințele partajate pentru informațiile de autentificare
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        // Recuperează valoarea "rememberMe" din preferințele partajate
        rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        // Setează starea casetei rememberMe în funcție de valoarea recuperată
        rememberCheckBox.setChecked(rememberMe);
        // Dacă sunt îndeplinite condițiile pentru autentificare automată, efectuează autentificarea automată
        if (shouldAutoLogin()) {
            autoLogin();
        }

        // Configurează ascultătorul de clic pentru butonul de autentificare
        setupLoginButton();
        // Configurează ascultătorul de clic pentru butonul "Ai uitat parola"
        setupForgotPasswordButton();
    }
    private void initializeViews() {
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.forgetpasswordRedirectText);
        rememberCheckBox = findViewById(R.id.remember_me_checkbox);
    }
    // Verifică dacă trebuie să efectueze autentificarea automată
    private boolean shouldAutoLogin() {
        return rememberMe && auth.getCurrentUser() != null;
    }
    // Efectuează autentificarea automată și navighează către activitatea principală
    private void autoLogin() {
        startActivity(new Intent(LoginActivity.this, FirstActivity.class));
        finish();
    }
    // Configurează ascultătorul de clic pentru butonul de autentificare
    private void setupLoginButton() {
        loginButton.setOnClickListener(view -> {
            // Obține adresa de email și parola introduse de utilizator
            String email = loginEmail.getText().toString();
            String pass = loginPassword.getText().toString();

            // Verifică dacă adresa de email și parola sunt valide
            if (isValidEmail(email) && isValidPassword(pass)) {
                // Încearcă să efectueze autentificarea cu adresa de email și parola introduse
                logInWithEmailAndPassword(email, pass);
            } else {
                // Tratează cazul în care adresa de email sau parola nu sunt valide
                handleInvalidInput(email, pass);
            }
        });
    }
    // Verifică dacă adresa de email este validă
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Verifică dacă parola este validă
    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password);
    }

    // Metoda pentru autentificarea utilizatorului cu adresa de email și parola
    private void logInWithEmailAndPassword(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Autentificare cu succes
                        handleLogInSuccess(Objects.requireNonNull(task.getResult()).getUser());
                    } else {
                        // Autentificare nereușită
                        handleSignInFailure(task.getException());
                    }
                });
    }

    // Metoda pentru gestionarea acțiunilor după autentificare reușită
    private void handleLogInSuccess(FirebaseUser user) {
        Toast.makeText(LoginActivity.this, "Te-ai conectat cu succes!", Toast.LENGTH_SHORT).show();
        Log.d("LoginActivity", "User ID: " + user.getUid());
        // Gestionare opțiune Remember Me
        handleRememberMeCheckbox();
        // Redirecționare către activitatea principală
        startActivity(new Intent(LoginActivity.this, FirstActivity.class));
        finish();
    }

    // Metoda pentru gestionarea erorilor în cazul unei autentificări nereușite
    private void handleSignInFailure(@NonNull Exception e) {
        // Construirea unui mesaj de eroare
        String errorMessage = "Conectarea a esuat! Motiv: " + e.getMessage();
        // Afișarea mesajului de eroare utilizatorului
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        // Înregistrarea detaliilor despre eroare în consolă
        e.printStackTrace();
        Log.e("LoginActivity", "Motiv: " + errorMessage);

        // Verificarea tipului de excepție pentru a obține codul specific Firebase Auth (dacă există)
        if (e instanceof FirebaseAuthException) {
            FirebaseAuthException firebaseAuthException = (FirebaseAuthException) e;
            String errorCode = firebaseAuthException.getErrorCode();
            Log.e("LoginActivity", "Firebase Auth Error Code: " + errorCode);
        }
    }

    // Metoda pentru gestionarea opțiunii "Remember Me" din caseta de bifare
    private void handleRememberMeCheckbox() {
        CheckBox rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);
        if (rememberMeCheckBox.isChecked()) {
            // Salvare preferințe dacă opțiunea "Remember Me" este bifată
            saveRememberMePreference();
        } else {
            // Ștergere preferințe dacă opțiunea "Remember Me" nu este bifată
            clearRememberMePreference();
        }
    }

    // Metoda pentru salvarea preferințelor "Remember Me" în memoria locală
    private void saveRememberMePreference() {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Salvare starea "Remember Me" și ID-ul utilizatorului autentificat
        editor.putBoolean("rememberMe", true);
        editor.putString("loggedInUserId", auth.getCurrentUser().getUid());
        editor.apply();
    }

    // Metoda pentru ștergerea preferințelor "Remember Me" din memoria locală
    private void clearRememberMePreference() {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Dezactivare opțiune "Remember Me"
        editor.putBoolean("rememberMe", false);
        editor.apply();
    }

    // Metoda pentru gestionarea cazurilor în care datele introduse pentru autentificare sunt invalide
    private void handleInvalidInput(String email, String password) {
        if (email.isEmpty()) {
            loginEmail.setError("Campurile necompletate nu sunt permise!");
        } else if (!isValidEmail(email)) {
            loginEmail.setError("Introduceti o adresa de email corecta!");
        } else {
            loginPassword.setError("Campurile necompletate nu sunt permise!");
        }
    }

    // Metoda pentru configurarea butonului de uitare a parolei
    private void setupForgotPasswordButton() {
        forgotPassword.setOnClickListener(view -> showForgotPasswordDialog());
    }

    // Metoda pentru afișarea ferestrei de dialog pentru resetarea parolei
    private void showForgotPasswordDialog() {
        // Construirea ferestrei de dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
        EditText emailBox = dialogView.findViewById(R.id.emailBox);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Setarea acțiunilor pentru butoanele din fereastra de dialog
        dialogView.findViewById(R.id.btnReset).setOnClickListener(view -> resetPassword(emailBox.getText().toString(), dialog));
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(view -> dialog.dismiss());

        // Setarea fundalului transparent al ferestrei de dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        // Afișarea ferestrei de dialog
        dialog.show();
    }

    // Metoda pentru resetarea parolei folosind adresa de email furnizată
    private void resetPassword(String userEmail, AlertDialog dialog) {
        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            // Validare dacă adresa de email este goală sau nu este validă
            Toast.makeText(LoginActivity.this, "Introduceti adresa de email!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trimite un email pentru resetarea parolei către adresa specificată
        auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> handleResetPasswordResult(task, dialog));
    }

    // Metoda pentru gestionarea rezultatului procesului de resetare a parolei
    private void handleResetPasswordResult(Task<Void> task, AlertDialog dialog) {
        if (task.isSuccessful()) {
            // Resetarea parolei a fost realizată cu succes
            Toast.makeText(LoginActivity.this, "Verificati-va email-ul", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else {
            // Resetarea parolei a eșuat
            Toast.makeText(LoginActivity.this, "Nu se poate trimite email", Toast.LENGTH_SHORT).show();
        }
    }
}
