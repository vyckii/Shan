package cis350.upenn.edu.remindmelater;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

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
    private Button timeButton;
    private Button dateButton;
    private Spinner recurring;
    private Button recurringUntil;
    private Spinner category;
    private TextView location;

    final Activity addReminderActivity = this;

    Calendar myCalendar = Calendar.getInstance();
    Calendar recurringCal = new GregorianCalendar();

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
        timeButton = (Button) findViewById(R.id.timeDue);
        dateButton = (Button) findViewById(R.id.dateDue);
        recurring = (Spinner) findViewById(R.id.recurring);
        recurringUntil = (Button) findViewById(R.id.recurringUntil);
        category = (Spinner) findViewById(R.id.category);
        location = (TextView) findViewById(R.id.location);

        addReminder.setOnClickListener(new View.OnClickListener() {

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
                    Toast.makeText(addReminderActivity.getApplicationContext(), R.string.empty_fields,
                            Toast.LENGTH_SHORT).show();
                }

                Long dateToSaveToDB = myCalendar.getTimeInMillis();
                Long dateToRecur = recurringCal.getTimeInMillis();


                if (allGood && mCurrentUser != null) {
                    // add reminder to database
                    System.out.println("adding reminder to db");
                    Reminder.createReminderInDatabase(mCurrentUser, reminderText, notesText, dateToSaveToDB,
                            locationText,categoryText, recurringText, dateToRecur);

                    //TODO add multiple for recurring

                    System.out.println("done adding reminder");
                    finish();
                }
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



    public void showTimePickerDialog(View v) {

        TimePickerDialog tpd = new TimePickerDialog(AddReminderActivity.this, time,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));

        tpd.show();
    }

    public void showDatePickerDialog(View v) {

        DatePickerDialog dpd = new DatePickerDialog(AddReminderActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        dpd.show();
    }

    public void showRecurringDatePickerDialog(View v) {

        DatePickerDialog dpd = new DatePickerDialog(AddReminderActivity.this, recurringDate, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        dpd.show();
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel(dateButton, myCalendar);
        }

    };

    DatePickerDialog.OnDateSetListener recurringDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            recurringCal.set(Calendar.YEAR, year);
            recurringCal.set(Calendar.MONTH, monthOfYear);
            recurringCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel(recurringUntil, recurringCal);
        }

    };

    private void updateDateLabel(Button b, Calendar c) {

        String myFormat = "EEEE, MMMM d"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        b.setText(sdf.format(c.getTime()));
    }


    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {


        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);

            updateTimeLabel();
        }

    };

    private void updateTimeLabel() {
        String myFormat = "h:mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        timeButton.setText(sdf.format(myCalendar.getTime()));
    }
}
