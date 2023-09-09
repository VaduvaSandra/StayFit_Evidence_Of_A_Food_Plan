package com.example.prototip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.prototip.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    TextView profileUserName, profileVarsta, profileObiectiv, profileGreutate;
    TextView titleUsername;
    Button editProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        profileUserName = findViewById(R.id.profileName);
        profileVarsta = findViewById(R.id.profileAge);
        profileObiectiv = findViewById(R.id.profileObiectiv);
        profileGreutate = findViewById(R.id.profileGreutate);
        titleUsername = findViewById(R.id.titleUserName);
        editProfile = findViewById(R.id.editButton);

        showAllUserData();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });
    }
    public void showAllUserData(){

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
            }
        });
    }
}