package com.example.redhw.videomanagementapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.redhw.videomanagementapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email;
    private EditText reg_password;
    private EditText reg_address;
    private EditText reg_contact;
    private Button btn_login;
    private Button btn_reg;

    private FirebaseAuth rAuth;
    private ProgressDialog rDialog;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase registration instance
        rAuth = FirebaseAuth.getInstance();

        //If user is already logged in, will be redirected to HomeActivity
        if(rAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        //Registration dialog (To alert the user of code execution with a loader)
        rDialog = new ProgressDialog(this);

        //Initializing text fields and buttons to interface ids
        reg_email = findViewById(R.id.new_user_email);
        reg_password = findViewById(R.id.new_user_password);
        reg_contact = findViewById(R.id.new_user_contact);
        reg_address = findViewById(R.id.new_user_address);
        btn_reg = findViewById(R.id.btn_register_user);
        btn_login = findViewById(R.id.btn_login_redirect);


        //Onclick of registration button following will execute
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting registration email
                final String new_email = reg_email.getText().toString().trim();

                //Getting registration password
                final String new_password = reg_password.getText().toString().trim();

                //Getting registration username
                final String new_username = "U-MT";

                //Getting registration contact
                final String new_contact = reg_contact.getText().toString().trim();

                //Getting registration address
                final String new_address = reg_address.getText().toString().trim();

                //Getting registration type
                final String new_type = "Customer";


                //Loader to alert user of code execution
                rDialog.setMessage("Registering");
                rDialog.show();

                //Registering new user using default firebase authentication method
                rAuth.createUserWithEmailAndPassword(new_email, new_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //If registration is successful following will execute
                        if(task.isSuccessful()){

                            //Getting auto-generated user UID from firebase user registration
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            final String userUid = currentUser.getUid();

                            //Inserting values to a different collection(table) for further use
                            mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://movie-time-19.firebaseio.com/");

                            //Inserting data into collection(table)
                            //Collection name -> users
                            //Record name     -> User UID
                            //username(key)   -> email(value)
                            mDatabase.child("users").child(userUid).child("Email").setValue(new_email);
                            mDatabase.child("users").child(userUid).child("Username").setValue(new_username);
                            mDatabase.child("users").child(userUid).child("Contact").setValue(new_contact);
                            mDatabase.child("users").child(userUid).child("Address").setValue(new_address);
                            mDatabase.child("users").child(userUid).child("Type").setValue(new_type);


                            //Setting view to HomeActivity after inserting data to collection
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                            //Displaying a message to alert the user of successful registration
                            Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                            rDialog.dismiss();

                        //In case of registration error following will execute
                        }else{

                            //Displaying a message to alert the user of registration error
                            Toast.makeText(getApplicationContext(), "Error in Registering", Toast.LENGTH_SHORT).show();
                            rDialog.dismiss();
                        }
                    }
                });
            }
        });

        //Redirecting user to login page if created an account already
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }
}
