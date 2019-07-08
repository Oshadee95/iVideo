package com.example.redhw.videomanagementapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.redhw.videomanagementapp.models.User;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar =  (ProgressBar) findViewById(R.id.SpinKit);
        WanderingCubes wave = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wave);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(final FirebaseUser firebaseUser) {
        if (firebaseUser != null) {

            mDatabase.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Assigning user values of the logged-in user to user model
                    User user =  dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);

                    // Redirecting user according to user type
                    if(user.Type.equalsIgnoreCase("Admin")){
                        // Redirecting user to admin home
                        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                    }else{
                        // Redirecting user to customer home
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } else {

            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}
