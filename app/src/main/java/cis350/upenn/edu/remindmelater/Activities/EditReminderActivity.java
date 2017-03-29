package cis350.upenn.edu.remindmelater.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import cis350.upenn.edu.remindmelater.R;
import cis350.upenn.edu.remindmelater.Reminder;

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
    private Button deleteReminder;
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
        saveReminder = (Button) findViewById(R.id.saveReminder);
        deleteReminder = (Button) findViewById(R.id.deleteReminder);
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
        final String reminderName = intent.getStringExtra("reminderName");
        reminder.setText(reminderName);
        notes.setText(intent.getStringExtra("notes"));

        final String dueDateStr = intent.getStringExtra("dueDate");

        SimpleDateFormat f1 = new SimpleDateFormat("hh:mm a", Locale.US);
        SimpleDateFormat f2 = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
        myCalendar = Calendar.getInstance();
        myCalendar.setTimeInMillis(Long.parseLong(dueDateStr));
        timeButton.setText(f1.format(myCalendar.getTime()));
        dateButton.setText(f2.format(myCalendar.getTime()));

        int selected = 0;
        String recurringText = intent.getStringExtra("recurring");
        System.out.println("RECURRING TEXT = " + recurringText);
        if (recurringText == null) {

        } else if (recurringText.equals("Once")) {
            selected = 0;
        } else if (recurringText.equals("Daily")) {
            selected = 1;
        } else if (recurringText.equals("Weekly")) {
            selected = 2;
        } else if (recurringText.equals("Yearly")) {
            selected = 3;
        }
        recurring.setSelection(selected);

        final String recDateStr = intent.getStringExtra("recurringUntil");
        recurringCal.setTimeInMillis(Long.parseLong(recDateStr));
        recurringUntil.setText(f2.format(recurringCal.getTime()));

        selected = 0;
        String categoryText = intent.getStringExtra("recurring");
        if (categoryText == null) {

        } else if (categoryText.equals("School")) {
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
                    Reminder.updateReminderInDatabase(mCurrentUser,reminderName, reminderText, notesText, dateToSaveToDB,
                            locationText,categoryText, recurringText, dateToRecur);

                    //TODO add multiple for recurring

                    System.out.println("done adding reminder");
                    finish();
                }
            }
        });


        deleteReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reminder.deleteReminderFromDatabase(mCurrentUser, reminderName, dueDateStr);

                Toast.makeText(editReminderActivity.getApplicationContext(), "Deleted reminder",
                        Toast.LENGTH_SHORT).show();

                finish();
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

                    System.out.println("here inside User Signed In");

                } else {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                }

            }
        };
    }

    public void showTimePickerDialog(View v) {

        TimePickerDialog tpd = new TimePickerDialog(EditReminderActivity.this, time,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));

        tpd.show();
    }

    public void showDatePickerDialog(View v) {

        DatePickerDialog dpd = new DatePickerDialog(EditReminderActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        dpd.show();
    }

    public void showRecurringDatePickerDialog(View v) {

        DatePickerDialog dpd = new DatePickerDialog(EditReminderActivity.this, recurringDate, myCalendar
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

    public void startCamera(View v) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }


}
