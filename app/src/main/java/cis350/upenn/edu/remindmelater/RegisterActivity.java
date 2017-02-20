package cis350.upenn.edu.remindmelater;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private Button etregister;
    private EditText etfirstname;
    private EditText etlastname;
    private EditText etusername;
    private EditText etpassword;
    private EditText etemail;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        checkIfUserIsSignedIn();
        setContentView(R.layout.activity_register);

        etfirstname = (EditText) findViewById(R.id.FirstName);
        etlastname = (EditText) findViewById(R.id.LastName);
//        etusername = (EditText) findViewById(R.id.UserName);
        etpassword = (EditText) findViewById(R.id.Password);
        etemail = (EditText) findViewById(R.id.Email);

        etregister = (Button) findViewById(R.id.RegisterButton);

        final Activity registerActivity = this;


        etregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = etfirstname.getText().toString().trim();
                String lastname = etlastname.getText().toString().trim();
//                String username = etusername.getText().toString().trim();
                String email = etemail.getText().toString().trim();
                String password = etpassword.getText().toString();

                if(firstname.isEmpty() || lastname.isEmpty()  || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(registerActivity.getApplicationContext(), "Please fill out everything.", Toast.LENGTH_SHORT).show();
                }
                else {
                    System.out.println("in here");
                    User.createNewUser(registerActivity, mAuth, firstname, lastname, email, password);
                    System.out.println("done creating new user");


                    Intent myIntent = new Intent(v.getContext(), MainScreenActivity.class);
                    startActivityForResult(myIntent, 0);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("here");
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("what about here");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void checkIfUserIsSignedIn() {

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    System.out.println("onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                }

            }};


    }
}
