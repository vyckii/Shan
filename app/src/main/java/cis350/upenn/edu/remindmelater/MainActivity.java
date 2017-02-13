package cis350.upenn.edu.remindmelater;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by stephaniefei on 2/12/17.
 */

public class MainActivity extends AppCompatActivity {

    Button createAcctButton;
    Button logInButton;

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
    }



}
