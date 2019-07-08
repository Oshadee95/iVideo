package com.example.redhw.videomanagementapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redhw.videomanagementapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private Button btn_update;
    private EditText reg_username;
    private EditText reg_address;
    private EditText reg_contact;

    private FirebaseAuth rAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth login_Auth;

    private ProgressDialog rDialog;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        BottomNavigationView mBtmView;
        mBtmView = (BottomNavigationView) findViewById(R.id.navigation);
        mBtmView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBtmView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);

        login_Auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        //Registration dialog (To alert the user of code execution with a loader)
        rDialog = new ProgressDialog(this);

        //Initializing text fields and buttons to interface ids
        reg_username = findViewById(R.id.logged_usermail);
        reg_contact = findViewById(R.id.logged_contact);
        reg_address = findViewById(R.id.logged_address);
        btn_update = findViewById(R.id.btn_admin_update);

        //Onclick of registration button following will execute
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting registration username
                final String new_username = reg_username.getText().toString().trim();

                //Getting registration contact
                final String new_contact = reg_contact.getText().toString().trim();

                //Getting registration address
                final String new_address = reg_address.getText().toString().trim();

                //Loader to alert user of code execution
                rDialog.setMessage("Updating");
                rDialog.show();

                //Getting auto-generated user UID from firebase user registration
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String userUid = currentUser.getUid();

                //Inserting values to a different collection(table) for further use
                mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://movie-time-19.firebaseio.com/");

                //Inserting data into collection(table)
                //Collection name -> users
                //Record name     -> User UID
                //username(key)   -> email(value)
                mDatabase.child("users").child(userUid).child("Username").setValue(new_username);
                mDatabase.child("users").child(userUid).child("Contact").setValue(new_contact);
                mDatabase.child("users").child(userUid).child("Address").setValue(new_address);


                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                //Displaying a message to alert the user of successful registration
                Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                rDialog.dismiss();

            }
        });


        mBtmView.setOnNavigationItemSelectedListener((item)->{
            switch(item.getItemId()){

                case R.id.navigation_home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    break;

                case R.id.navigation_dashboard:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    break;


                case R.id.navigation_notifications:
                    FirebaseAuth.getInstance().signOut();

                    //Firebase registration instance
                    rAuth = FirebaseAuth.getInstance();

                    //If user is already logged in, will be redirected to HomeActivity
                    if(rAuth.getCurrentUser()==null){
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                    break;

            }
            return false;

        });



    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = login_Auth.getCurrentUser();
        setUser(firebaseUser);
    }

    //Method to set user information / get user type and redirect user accordingly
    public void setUser(final FirebaseUser firebaseUser){

        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Assigning user values of the logged-in user to user model
                User user =  dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);

                TextView logged_welcome = (TextView) findViewById(R.id.logged_welcome);
                logged_welcome.setText("Welcome "+user.Username);


                EditText  logged_username = (EditText) findViewById(R.id.logged_usermail);
                logged_username.setText(user.Username);

                EditText logged_contact = (EditText) findViewById(R.id.logged_contact);
                logged_contact.setText(user.Contact);

                EditText logged_address = (EditText) findViewById(R.id.logged_address);
                logged_address.setText(user.Address);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
