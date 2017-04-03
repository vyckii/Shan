package cis350.upenn.edu.remindmelater.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cis350.upenn.edu.remindmelater.R;

public class ReminderActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIfUserIsSignedIn();
        setContentView(R.layout.activity_reminder);

        TextView title = (TextView) findViewById(R.id.title);
        TextView notes = (TextView) findViewById(R.id.notes);
        TextView location = (TextView) findViewById(R.id.location);
        TextView dueDate = (TextView) findViewById(R.id.dateDue);
        TextView category = (TextView) findViewById(R.id.category);

        Intent intent = getIntent();

        title.setText(intent.getStringExtra("reminderName"));
        notes.setText(intent.getStringExtra("notes"));
        location.setText(intent.getStringExtra("location"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(intent.getLongExtra("dueDate", 0));

        SimpleDateFormat format1 = new SimpleDateFormat("h:mma EEEE, d MMMM yyyy");

        String formatted = format1.format(calendar.getTime());

        String str = "Due " + formatted;


        dueDate.setText(str);
        category.setText(intent.getStringExtra("location"));


    }

    public void onCompleteButtonClicked(View v) {

        this.finish();

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

    private void checkIfUserIsSignedIn() {

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    System.out.println("onAuthStateChanged:signed_in:" + user.getUid());

                    mCurrentUser = user;

                    System.out.println("user Signed in inside Main Screen Activity");

                } else {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                }
            }
        };
    }


}
