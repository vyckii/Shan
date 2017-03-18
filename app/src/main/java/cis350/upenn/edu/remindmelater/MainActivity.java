package cis350.upenn.edu.remindmelater;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

/**
 * Created by stephaniefei on 2/12/17.
 */
public class MainActivity extends AppCompatActivity {

    Button createAcctButton;
    Button logInButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase Authentication check
        checkIfUserIsSignedIn();

        setContentView(R.layout.activity_main);
        createAcctButton = (Button) findViewById(R.id.createAcct);
        createAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        logInButton = (Button) findViewById(R.id.logIn);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                System.out.println("String here");
            }
        });

        //Example on how to authenticate a new user

//        createNewUser("testEmail@gmail.com", "password");

        //Example on how to sign in a user
//        signInUser("testksdjflskdjf@gmail.com", "dddoooffff");


        checkIfUserIsSignedIn();


    }

    public void registerScreen() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * User Authentication Setup Function
     *
     * Checks if the user is already signed in. If the user is not signed in, they should
     * be prompted to log in or create an account.
     */
    private void checkIfUserIsSignedIn() {

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    System.out.println("onAuthStateChanged:signed_in:" + user.getUid());

                    currentUser = user;



                } else {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                }

            }};


    }







}
