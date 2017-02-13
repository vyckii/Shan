package com.example.shoryamantry.register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText firstname = (EditText) findViewById(R.id.FirstName);
        final EditText lastname = (EditText) findViewById(R.id.LastName);
        final EditText username = (EditText) findViewById(R.id.UserName);
        final EditText password = (EditText) findViewById(R.id.Password);
        final EditText email = (EditText) findViewById(R.id.Email);

        final Button register = (Button) findViewById(R.id.RegisterButton);
    }
}
