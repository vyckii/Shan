package cis350.upenn.edu.remindmelater.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import cis350.upenn.edu.remindmelater.R;
import cis350.upenn.edu.remindmelater.Reminder;

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
    private AutoCompleteTextView reminder;
    private TextView notes;
    private Button timeButton;
    private Button dateButton;
    private Spinner recurring;
    private Button recurringUntil;
    private Spinner category;
    private TextView location;
    private Button addPicture;

    final Activity addReminderActivity = this;

    Calendar myCalendar = Calendar.getInstance();
    Calendar recurringCal = new GregorianCalendar();

    static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView imageView;
    String mCurrentPhotoPath;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        image = "";

        checkIfUserIsSignedIn();

        System.out.println("--------------------------");
        System.out.println("in add reminder");
        System.out.println("--------------------------");
        checkUserExists("ajnandi@gmail.com");
        checkUserExists("connorwen@gmail.com");
        checkUserExists("fakeemail@gmail.com");

        imageView = (ImageView) this.findViewById(R.id.imageView1);
        imageView.setImageDrawable(null);

        // grab reminder input
        addReminder = (Button) findViewById((R.id.addReminder));
        reminder = (AutoCompleteTextView) findViewById(R.id.reminderName);
        notes = (TextView) findViewById(R.id.notes);
        timeButton = (Button) findViewById(R.id.timeDue);
        dateButton = (Button) findViewById(R.id.dateDue);
        recurring = (Spinner) findViewById(R.id.recurring);
        recurringUntil = (Button) findViewById(R.id.recurringUntil);
        category = (Spinner) findViewById(R.id.category);
        location = (TextView) findViewById(R.id.location);
        addPicture = (Button) findViewById(R.id.addPicture);


        ArrayAdapter<String> suggestedAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SUGGESTIONS);

        reminder.setAdapter(suggestedAdapter);




        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(AddReminderActivity.this, CameraActivity.class);
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (i.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        System.out.println("oh no!");
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(AddReminderActivity.this, "com.example.android.fileprovider", photoFile);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(i, REQUEST_TAKE_PHOTO);
                    }
                }

                //startActivityForResult(i, REQUEST_TAKE_PHOTO);
            }
        });

        final Context context = this;

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
                            locationText, categoryText, recurringText, dateToRecur, image, context, false);

                    System.out.println(mUserReference.child("image").toString());

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

    public void startCamera(View v) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
            System.out.println("got image");
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 1250;
        int targetH = 700;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
        encodeBitmapAndSaveToFirebase(bitmap);
    }

    //TODO how to get reference to current user in firebase
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private static final String[] SUGGESTIONS = new String[] {
            "Call", "Text", "Pay", "Buy", "Go to", "Get lunch with", "Get dinner with"
    };

    public boolean checkUserExists(String email) {
        final boolean[] exists = new boolean[1];
        final String emailAdd = email;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot data: dataSnapshot.getChildren()) {
                    //if (data.child(emailAdd).exists()) {
                    if (dataSnapshot.exists()) {
                        exists[0] = true;
                    } else {
                        exists[0] = false;
                    }
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        System.out.println(email + " " + exists[0]);
        return exists[0];
    }
}
