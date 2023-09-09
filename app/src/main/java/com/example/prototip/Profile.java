package com.example.prototip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Activitatea reprezentând profilul utilizatorului.
 */

public class Profile extends AppCompatActivity {

    //Elementrele de interfata ale profilului
    TextView profileUserName;
    TextView profileVarsta;
    TextView profileObiectiv;
    TextView profileGreutate;
    TextView titleUsername;
    Button editProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        //Initializarea elementelor de interfata
        profileUserName = findViewById(R.id.profileName);
        profileVarsta = findViewById(R.id.profileAge);
        profileObiectiv = findViewById(R.id.profileObiectiv);
        profileGreutate = findViewById(R.id.profileGreutate);
        titleUsername = findViewById(R.id.titleUserName);
        editProfile = findViewById(R.id.editButton);

        //Afisarea datelor utilizatorului
        showAllUserData();

        //Setarea unui ascultator de eveniment pentru butonul de editare
        editProfile.setOnClickListener(new View.OnClickListener() {
            //Transmiterea datelor utilizatorului pentru editare
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });
    }
    /**
     * Metodă pentru afișarea datelor utilizatorului curent în interfața grafică.
     */
    public void showAllUserData(){

        //Actualizarea elementelor de interfata cu datele utilizatorului
        Intent intent = getIntent();
        String usernameUser = intent.getStringExtra("username");
        String varstaUser = intent.getStringExtra("age");
        String greutateUser = intent.getStringExtra("weight");
        String obiectivUser = intent.getStringExtra("goal");
        titleUsername.setText(usernameUser);
        profileUserName.setText(usernameUser);
        profileVarsta.setText(varstaUser);
        profileGreutate.setText(greutateUser);
        profileObiectiv.setText(obiectivUser);
    }
    /**
     * Metodă pentru transmiterea datelor utilizatorului către activitatea de editare a profilului.
     */
    public void passUserData(){
        String userUsername = profileUserName.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Utilizatori");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                    String varstaFromDB = snapshot.child(userUsername).child("age").getValue(String.class);
                    String greutateFromDB = snapshot.child(userUsername).child("weight").getValue(String.class);
                    String obiectivFromDB = snapshot.child(userUsername).child("goal").getValue(String.class);

                    // Transmiterea datelor utilizatorului catre activitatea principala pentru editare
                    Intent intent = new Intent(Profile.this, MainActivity.class);
                    intent.putExtra("username", usernameFromDB);
                    intent.putExtra("age", varstaFromDB);
                    intent.putExtra("weight", greutateFromDB);
                    intent.putExtra("goal", obiectivFromDB);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aceasta metoda a fost lasata goala deoarece nu este necesar sa gestionam evenimentele onCancelled.
            }
        });
    }
}