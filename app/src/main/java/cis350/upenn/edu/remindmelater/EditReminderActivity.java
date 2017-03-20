package cis350.upenn.edu.remindmelater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by cristinabuenahora on 2/20/17.
 */

public class EditReminderActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mUserReference;

    private FirebaseUser mCurrentUser;

    // variables for reminder input
    private Button saveReminder;
    private TextView reminder;
    private TextView notes;
    private Button timeButton;
    private Button dateButton;
    private Spinner recurring;
    private Button recurringUntil;
    private Spinner category;
    private TextView location;

    Calendar myCalendar = Calendar.getInstance();
    Calendar recurringCal = new GregorianCalendar();

    final Activity editReminderActivity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        checkIfUserIsSignedIn();

        System.out.println("--------------------------");
        System.out.println("in edit reminder");
        System.out.println("--------------------------");

        // grab reminder fields
        saveReminder = (Button) findViewById((R.id.saveReminder));
        reminder = (TextView) findViewById(R.id.eReminderName);
        notes = (TextView) findViewById(R.id.eNotes);
        timeButton = (Button) findViewById(R.id.eTimeDue);
        dateButton = (Button) findViewById(R.id.eDateDue);
        recurring = (Spinner) findViewById(R.id.eRecurring);
        recurringUntil = (Button) findViewById(R.id.eRecurringUntil);
        category = (Spinner) findViewById(R.id.eCategory);
        location = (TextView) findViewById(R.id.eLocation);

        // set reminder details on screen
        Intent intent = getIntent();
        reminder.setText(intent.getStringExtra("reminderName"));
        notes.setText(intent.getStringExtra("notes"));
        // TODO: check this
        timeButton.setHint(intent.getStringExtra("timeButton"));
        dateButton.setHint(intent.getStringExtra("dateButton"));

        int selected = 0;
        String recurringText = intent.getStringExtra("recurring");
        if (recurringText.equals("Once")) {
            selected = 0;
        } else if (recurringText.equals("Daily")) {
            selected = 1;
        } else if (recurringText.equals("Weekly")) {
            selected = 2;
        } else if (recurringText.equals("Yearly")) {
            selected = 3;
        }
        recurring.setSelection(selected);

        // TODO: check this
        recurringUntil.setText(intent.getStringExtra("recurringUntil"));
        selected = 0;
        String categoryText = intent.getStringExtra("recurring");
        if (categoryText.equals("School")) {
            selected = 0;
        } else if (categoryText.equals("Work")) {
            selected = 1;
        } else if (categoryText.equals("Extracurricular")) {
            selected = 2;
        } else if (categoryText.equals("Personal")) {
            selected = 3;
        } else if (categoryText.equals("Other")) {
            selected = 4;
        }
        category.setSelection(selected);

        location.setText(intent.getStringExtra("location"));


        saveReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // get user inputs
                String reminderText = reminder.getText().toString();
                String notesText = notes.getText().toString();
                String recurringText = recurring.getSelectedItem().toString();
                String categoryText = category.getSelectedItem().toString();
                String locationText = location.getText().toString();

                // lol
                boolean allGood = true;

                // check if reminder name is empty
                if (reminderText.isEmpty()) {
                    allGood = false;
                    Toast.makeText(editReminderActivity.getApplicationContext(), R.string.empty_fields,
                            Toast.LENGTH_SHORT).show();
                }

                Long dateToSaveToDB = myCalendar.getTimeInMillis();
                Long dateToRecur = recurringCal.getTimeInMillis();


                if (allGood && mCurrentUser != null) {
                    // add reminder to database
                    System.out.println("adding reminder to db");

                    //TODO: edit reminder, not create reminder
                    Reminder.createReminderInDatabase(mCurrentUser, reminderText, notesText, dateToSaveToDB,
                            locationText,categoryText, recurringText, dateToRecur);

                    //TODO add multiple for recurring

                    System.out.println("done adding reminder");
                    finish();
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
