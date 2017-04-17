package cis350.upenn.edu.remindmelater.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

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
    private Button addCameraPic;
    private Button addGalleryPic;
    private TextView shareWith;
    private Button completeReminder;
    private ImageView imageView;
    private byte[] imageAsBytes;

    private String reminderText;
    private String notesText;
    private String recurringText;
    private String categoryText;
    private String locationText;
    private String shareWithText;
    private String reminderName;
    private String dueDateStr;


    Calendar myCalendar = Calendar.getInstance();
    Calendar recurringCal = new GregorianCalendar();

    final Activity editReminderActivity = this;

    static final int REQUEST_TAKE_PHOTO = 1;
    private static int RESULT_LOAD_IMG = 2;
    String imgDecodableString;
    private String mCurrentPhotoPath;
    private String image;

    private boolean exists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);
        image = "";

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

        imageView = (ImageView) findViewById(R.id.eImageView1);
        imageView.setImageDrawable(null);

        addCameraPic = (Button) findViewById(R.id.eAddCameraPic);
        addGalleryPic = (Button) findViewById(R.id.eAddGalleryPic);


        shareWith = (TextView) findViewById(R.id.eShareWith);

        completeReminder = (Button) findViewById(R.id.ecompleteReminder);

        addCameraPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        Uri photoURI = FileProvider.getUriForFile(EditReminderActivity.this, "com.example.android.fileprovider", photoFile);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(i, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });

        addGalleryPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        // set reminder details on screen
        Intent intent = getIntent();
        reminderName = intent.getStringExtra("reminderName");
        reminder.setText(reminderName);
        notes.setText(intent.getStringExtra("notes"));
        imageAsBytes = intent.getByteArrayExtra("imageBytes");
        //image = intent.getStringExtra("image");
        if (imageAsBytes != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            //Bitmap bitmap = decodeFromFirebaseBase64(image);
            imageView.setImageBitmap(bmp);
        }

        dueDateStr = intent.getStringExtra("dueDate");

        SimpleDateFormat f1 = new SimpleDateFormat("hh:mm a", Locale.US);
        SimpleDateFormat f2 = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
        myCalendar = Calendar.getInstance();
        try {
            myCalendar.setTimeInMillis(Long.parseLong(dueDateStr));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            myCalendar.setTimeInMillis(System.currentTimeMillis());
        }
        timeButton.setText(f1.format(myCalendar.getTime()));
        dateButton.setText(f2.format(myCalendar.getTime()));

        int selected = 0;
        recurringText = intent.getStringExtra("recurring");
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
        categoryText = intent.getStringExtra("recurring");
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
        final String reminderShareWith = intent.getStringExtra("shareWith");
        shareWith.setText(reminderShareWith);

        saveReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // get user inputs
                reminderText = reminder.getText().toString();
                notesText = notes.getText().toString();
                recurringText = recurring.getSelectedItem().toString();
                categoryText = category.getSelectedItem().toString();
                locationText = location.getText().toString();
                shareWithText = shareWith.getText().toString().trim().toLowerCase();
                checkUserExists(shareWithText);
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

        completeReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get user inputs
                String reminderText = reminder.getText().toString();
                String notesText = notes.getText().toString();
                String recurringText = recurring.getSelectedItem().toString();
                String categoryText = category.getSelectedItem().toString();
                String locationText = location.getText().toString();
                String shareWithText = shareWith.getText().toString();

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

                Reminder.updateReminderInDatabase(mCurrentUser,reminderName, Long.parseLong(dueDateStr), reminderText, notesText, dateToSaveToDB,
                        locationText,categoryText, recurringText, dateToRecur, image, shareWithText, true);

                finish();
            }
        });

    }

    private void finishEditingReminder(boolean userExists) {
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

            if (shareWithText.equals("") || userExists) {

                Reminder.updateReminderInDatabase(mCurrentUser,reminderName, Long.parseLong(dueDateStr), reminderText, notesText, dateToSaveToDB,
                        locationText,categoryText, recurringText, dateToRecur, image, shareWithText, false);

                finish();
            } else {
                Toast.makeText(editReminderActivity.getApplicationContext(), R.string.share_failed,
                        Toast.LENGTH_SHORT).show();
            }
        }
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

    public static Bitmap decodeFromFirebaseBase64(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            // when an Image is taken

            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                setPic();
                System.out.println("got image");
            }

            // When an Image is picked

            else if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data


//                setPic();
//                System.out.println("got image fuck yea");

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
//                ImageView imgView = (ImageView) findViewById(R.id.imageView1); // should probably rename imageview into picture or some shit
                // Set the Image in ImageView after decoding the String


                // Get the dimensions of the View
                int targetW = 1250;
                int targetH = 700;

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imgDecodableString, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString, bmOptions);
                imageView.setImageBitmap(bitmap);
                encodeBitmapAndSaveToFirebase(bitmap);

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

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

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    //TODO if any changes to add reminder function, also need to be made here
    public void checkUserExists(String email) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    finishEditingReminder(true);
                } else {
                    finishEditingReminder(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
