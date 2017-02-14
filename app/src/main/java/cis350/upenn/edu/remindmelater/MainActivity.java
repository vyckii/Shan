package cis350.upenn.edu.remindmelater;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by stephaniefei on 2/12/17.
 */

public class MainActivity extends AppCompatActivity {

    Button createAcctButton;
    Button logInButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase Authentication setup function
        setupAuth();

        //Firebase database setup
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_main);
        createAcctButton = (Button) findViewById(R.id.createAcct);
        boolean create = false;
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
                //trigger create login screen
                System.out.println("login");
            }
        });

//        if (SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
////            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_main);
//            createAcctButton = (Button) findViewById(R.id.createAcct);
//            createAcctButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //trigger create account screen
//                    System.out.println("create");
//                }
//            });
//            logInButton = (Button) findViewById(R.id.logIn);
//            logInButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //trigger create login screen
//                    System.out.println("login");
//                }
//            });
//        }
//        else {
//            //go to main screen
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.home_screen);
//        }






        //Example on how to authenticate a new user
//        createNewUser("testEmail@gmail.com", "password");

        //Example on how to sign in a user
//        signInUser("testksdjflskdjf@gmail.com", "dddoooffff");


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
    private void setupAuth() {


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

            }
        };
    }


    /**
     * Creates a new user using Firebase authentication
     * @param email
     * @param password
     */
    public void createNewUser(final String email, String password) {


        //Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //Add user to database
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                            User user = new User();
//                            mDatabase.child("users").child(currentUser.getUid()).setValue(user);
                        }


                    }
                });



    }

    /**
     * Signs in a user that already has an account using Firebase authentication
     * @param email
     * @param password
     */
    public void signInUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            System.out.println("signInWithEmail:failed" + task.getException());
                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });



    }





}
