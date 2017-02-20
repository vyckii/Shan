package cis350.upenn.edu.remindmelater;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.design.widget.TextInputEditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private Button login;
    private TextView signupLink;
    private TextView retrievePWLink;
    private TextInputEditText email;
    private TextInputEditText password;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIfUserIsSignedIn();
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);
        retrievePWLink = (TextView) findViewById(R.id.link_retrievePW);
        email = (TextInputEditText) findViewById(R.id.input_email);
        password = (TextInputEditText) findViewById(R.id.input_password);

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        // for later, to retrieve password
//        retrievePWLink.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // do something
//            }
//        });

        final Activity loginActivity = this;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                System.out.println(emailText);
                System.out.println(passwordText);

                if (emailText.isEmpty() || passwordText.isEmpty() ) {
                    Toast.makeText(loginActivity.getApplicationContext(), R.string.empty_fields,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    User.signInUser(loginActivity, mAuth, emailText, passwordText);
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
            }
        };
    }

}
