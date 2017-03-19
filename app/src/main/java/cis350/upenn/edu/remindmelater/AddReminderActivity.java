package cis350.upenn.edu.remindmelater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Date;

/**
 * Created by cristinabuenahora on 2/20/17.
 */

public class AddReminderActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mUserReference;

    private FirebaseUser mCurrentUser;

    // variables for reminder input
    private Button addReminder;
    private TextView reminder;
    private TextView notes;
    private TextView date;
    private Spinner recurring;
    private Spinner category;
    private TextView location;

    final Activity addReminderActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        checkIfUserIsSignedIn();

        System.out.println("--------------------------");
        System.out.println("in add reminder");
        System.out.println("--------------------------");

        // grab reminder input
        addReminder = (Button) findViewById((R.id.addReminder));
        reminder = (TextView) findViewById(R.id.reminderName);
        notes = (TextView) findViewById(R.id.notes);
        date = (TextView) findViewById(R.id.dueDate);
        recurring = (Spinner) findViewById(R.id.recurring);
        category = (Spinner) findViewById(R.id.category);
        location = (TextView) findViewById(R.id.location);

        addReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // get user inputs
                String reminderText = reminder.getText().toString();
                String notesText = notes.getText().toString();
                String dateText = date.getText().toString();
                String recurringText = recurring.getSelectedItem().toString();
                String categoryText = category.getSelectedItem().toString();
                String locationText = location.getText().toString();

                // lol
                boolean allGood = true;

                // check if reminder name is empty
                if (reminderText.isEmpty()) {
                    allGood = false;
                    Toast.makeText(addReminderActivity.getApplicationContext(), R.string.empty_fields,
                            Toast.LENGTH_SHORT).show();
                }

                // check that date is valid
                String[] dateArr = dateText.split("/");
                if (dateArr.length != 3 || dateArr[0].length() > 2 || dateArr[1].length() > 2
                        || dateArr[2].length() > 4) {
                    allGood = false;
                    Toast.makeText(addReminderActivity.getApplicationContext(), R.string.valid_date,
                            Toast.LENGTH_SHORT).show();
                } else {
                    int month = Integer.parseInt(dateArr[0]);
                    int day = Integer.parseInt(dateArr[1]);
                    int year = Integer.parseInt(dateArr[2]);
                    Date reminderDate = new Date(month, day, year);
                    Date currDate = new Date();
                    if (!reminderDate.after(currDate)) {
                        allGood = false;
                        Toast.makeText(addReminderActivity.getApplicationContext(), R.string.valid_date,
                                Toast.LENGTH_SHORT).show();
                    }
                }

                if (allGood) {
                    // add reminder to database
                    Reminder.createReminderInDatabase(mCurrentUser, reminderText, notesText);
                    System.out.println("done adding reminder");
                }
            }
        });

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
                    mUserReference = FirebaseDatabase.getInstance().getReference("users").child(mCurrentUser.getUid());

                    System.out.println("here inside User SIgned In");

                } else {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                }

            }
        };
    }

}
