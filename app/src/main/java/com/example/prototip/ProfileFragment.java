package com.example.prototip;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import com.example.prototip.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private TextView textViewUserName, textViewVarsta, textViewObiectiv, textViewGreutate;
    TextView TextViewtitleUsername;
    Button ButtoneditProfile;
    private Button logoutButton;

    private ValueEventListener valueEventListener;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextViewtitleUsername = view.findViewById(R.id.titleUserName);
        textViewUserName = view.findViewById(R.id.profileName);
        textViewVarsta = view.findViewById(R.id.profileAge);
        textViewObiectiv = view.findViewById(R.id.profileObiectiv);
        textViewGreutate = view.findViewById(R.id.profileGreutate);
        ButtoneditProfile = view.findViewById(R.id.editButton);

        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        ButtoneditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });


        return view;
}

    private void showLogoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Log Out");
        builder.setMessage("Sunteti sigur ca vreti sa va deconectati?");
        builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Redirectionare pe pagina de LogIn
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("Nu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateData() {

        String userUsername = textViewUserName.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDataBase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    DataSnapshot childSnapshot = snapshot.getChildren().iterator().next();
                    String username = childSnapshot.child("username").getValue(String.class);
                    Long age = childSnapshot.child("age").getValue(Long.class);
                    Long weight = childSnapshot.child("weight").getValue(Long.class);
                    String goal = childSnapshot.child("goal").getValue(String.class);
                    Long desiredWeight = childSnapshot.child("desiredWeight").getValue(Long.class);

                    Log.d( "Failed to read value.", "Snapshot: " +snapshot.toString() );



                    Intent intent = new Intent(getActivity(),EditProfileActivity.class);
                    DatabaseReference ref = childSnapshot.getRef();

                    intent.putExtra("firebaseKey", ref.getKey());

                    intent.putExtra("username", username);
                    intent.putExtra("age", age);
                    intent.putExtra("weight", weight);
                    intent.putExtra("desiredWeight", desiredWeight);
                    intent.putExtra("goal", goal);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Obțin o referință la nodul de bază de date "users".
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String username = snapshot.child("username").getValue(String.class);
                Long age = snapshot.child("age").getValue(Long.class);
                String goal = snapshot.child("goal").getValue(String.class);
                Long weight = snapshot.child("weight").getValue(Long.class);
                String titleUsername = snapshot.child("username").getValue(String.class);


                textViewUserName.setText(username);
                TextViewtitleUsername.setText(titleUsername);
                textViewVarsta.setText(String.valueOf(age));
                textViewGreutate.setText(String.valueOf(weight));
                textViewObiectiv.setText(goal);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());

            }

        });
    }
}