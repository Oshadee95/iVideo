package com.example.redhw.videomanagementapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // Creating new variables to store interface field values
    private EditText login_email;
    private EditText login_password;
    private Button btn_login;
    private Button btn_reg;

    private FirebaseAuth login_Auth;
    private DatabaseReference mDatabase;

    private ProgressDialog login_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_Auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        // Creates new progress dialog (loader)
        login_dialog = new ProgressDialog(this);

        // Initializing interface fields to LoginActivity variables
        login_email = findViewById(R.id.user_email);
        login_password = findViewById(R.id.user_password);
        btn_reg = findViewById(R.id.btn_register_redirect);
        btn_login = findViewById(R.id.btn_login_user);

        // Onclick login button user will be validated and processes accordingly
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Getting input field values and assigning to local variables
                String login_email_value = login_email.getText().toString().trim();
                String login_password_value = login_password.getText().toString().trim();

                // Setting process dialog message
                login_dialog.setMessage("Authenticating");
                login_dialog.show();

                // Firebase default authenticating method
                login_Auth.signInWithEmailAndPassword(login_email_value, login_password_value).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    // Authenticate user entered credentials against firebase users login
                    if(task.isSuccessful()){

                        // Getting user type and redirecting accordingly
                        FirebaseUser firebaseUser = login_Auth.getCurrentUser();
                        validateUserType(firebaseUser);

                    // In case of invalid credentials entered
                    }else{
                        // Displaying toast message to notify user of invalid credentials
                        toastMessage("Please re-check credentials");

                        // Closing progress dialog (loader)
                        login_dialog.dismiss();
                    }
                    }
                });
            }
        });





        // Onclick registration button redirect user back to register activity for new user registration
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });









    }

    //Method to set user information / get user type and redirect user accordingly
    public void validateUserType(final FirebaseUser firebaseUser){

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
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    // Method to view toast message
    public void toastMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
