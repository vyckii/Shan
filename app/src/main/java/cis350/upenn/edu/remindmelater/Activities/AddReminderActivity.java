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
    private Button addCameraPicture;
    private Button addGalleryPicture;
    private TextView shareWith;

    final Activity addReminderActivity = this;

    Calendar myCalendar = Calendar.getInstance();
    Calendar recurringCal = new GregorianCalendar();

    static final int REQUEST_TAKE_PHOTO = 1;
    private static int RESULT_LOAD_IMG = 2;
    String imgDecodableString;



    private ImageView imageView;
    String mCurrentPhotoPath;
    private String image;

    private String reminderText;
    private String notesText;
    private String recurringText;
    private String categoryText;
    private String locationText;
    private String shareWithText;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        image = "";

        checkIfUserIsSignedIn();

        System.out.println("--------------------------");
        System.out.println("in add reminder");
        System.out.println("--------------------------");


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
        addCameraPicture = (Button) findViewById(R.id.addCameraPic);
        addGalleryPicture = (Button) findViewById(R.id.addGalleryPic);
        shareWith = (TextView) findViewById(R.id.shareWith);


        ArrayAdapter<String> suggestedAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SUGGESTIONS);

        reminder.setAdapter(suggestedAdapter);

        addCameraPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(AddReminderActivity.this, CameraActivity.class);

//                Intent i = new Intent(AddReminderActivity.this,PictureActivity.class);
//                startActivity(i);


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
            }
        });

        addGalleryPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


                // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

//        final Context context = this;

        addReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // get user inputs
                reminderText = reminder.getText().toString().trim();
                notesText = notes.getText().toString().trim();
                recurringText = recurring.getSelectedItem().toString();
                categoryText = category.getSelectedItem().toString();
                locationText = location.getText().toString().trim();
                shareWithText = shareWith.getText().toString().trim().toLowerCase();
                checkUserExists(shareWithText);

            }
        });
//        checkUserExists("ajnandi@gmail.com");
//        checkUserExists("connorwen@gmail.com");
//        checkUserExists("fakeemail@gmail.com");
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

    private void finishAddingReminder(boolean userExists) {
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

            //boolean userExists = checkUserExists(shareWithText);
            //boolean userExists = true;

            if (shareWithText.equals("") || userExists) {

                Reminder.createReminderInDatabase(mCurrentUser, reminderText, notesText, dateToSaveToDB,
                        locationText, categoryText, recurringText, dateToRecur, image, shareWithText, context, false);

                System.out.println(mUserReference.child("image").toString());

                System.out.println("done adding reminder");
                finish();
            } else {
                Toast.makeText(addReminderActivity.getApplicationContext(), R.string.share_failed,
                        Toast.LENGTH_SHORT).show();
            }

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

//    public void startCamera(View v) {
//        Intent i = new Intent(this, CameraActivity.class);
//        startActivity(i);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

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

    //TODO separate the rest of addReminder onclicklistener into a separate function called by this function in if/else statement
    public void checkUserExists(String email) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        finishAddingReminder(true);
                    } else {
                        finishAddingReminder(false);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
