package cis350.upenn.edu.remindmelater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

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
        setContentView(R.layout.activity_register);

        etfirstname = (EditText) findViewById(R.id.FirstName);
        etlastname = (EditText) findViewById(R.id.LastName);
        etusername = (EditText) findViewById(R.id.UserName);
        etpassword = (EditText) findViewById(R.id.Password);
        etemail = (EditText) findViewById(R.id.Email);

        etregister = (Button) findViewById(R.id.RegisterButton);
        etregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = etfirstname.getText().toString();
                String lastname = etlastname.getText().toString();
                String username = etusername.getText().toString();
                String email = etemail.getText().toString();
                String password = etpassword.getText().toString();
                User user = new User(firstname, lastname, username, email);
                User.createNewUser(this, mAuth, firstname, lastname, username, email, password);

            }
        });
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
}
