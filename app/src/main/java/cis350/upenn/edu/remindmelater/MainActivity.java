package cis350.upenn.edu.remindmelater;


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

/**
 * Created by stephaniefei on 2/12/17.
 */

public class MainActivity extends AppCompatActivity {

    Button createAcctButton;
    Button logInButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createAcctButton = (Button) findViewById(R.id.createAcct);
        createAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //trigger create account screen
                System.out.println("create");
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

        if (SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            createAcctButton = (Button) findViewById(R.id.createAcct);
            createAcctButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //trigger create account screen
                    System.out.println("create");
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
        }
        else {
            //go to main screen
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_screen);
        }




        setupAuth();

//        createNewUser("testksdjflskdjf@gmail.com", "dddoooffff");
//        signInUser("testksdjflskdjf@gmail.com", "dddoooffff");


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

    public void createNewUser(String email, String password) {

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
                        }


                    }
                });

    }

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
