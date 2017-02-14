package cis350.upenn.edu.remindmelater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private Button etregister;
    private EditText etfirstname;
    private EditText etlastname;
    private EditText etusername;
    private EditText etpassword;
    private EditText etemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etfirstname = (EditText) findViewById(R.id.FirstName);
        etlastname = (EditText) findViewById(R.id.LastName);
        etusername = (EditText) findViewById(R.id.UserName);
        etpassword = (EditText) findViewById(R.id.Password);
        etemail = (EditText) findViewById(R.id.Email);

        Button register = (Button) findViewById(R.id.RegisterButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = etfirstname.getText().toString();
                String lastname = etlastname.getText().toString();
                String username = etusername.getText().toString();
                String email = etemail.getText().toString();
                String password = etpassword.getText().toString();
                //MainActivity.createNewUser(firstname, lastname, username, email, password);
            }
        });
    }
}
