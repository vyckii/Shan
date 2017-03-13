package cis350.upenn.edu.remindmelater;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.content.Intent;
import android.widget.TextView;



/*
 * activity of main screen that pulls up reminders
 */
public class MainScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mReminderReference;
    private DatabaseReference mUserReference;

    private List<Reminder> reminders;

    private FirebaseUser mCurrentUser;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        checkIfUserIsSignedIn();

        System.out.println("--------------------------");
        System.out.println("ON CREATE");
        System.out.println("--------------------------");

        TextView addReminder = (TextView) findViewById(R.id.addReminder);

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddReminderActivity.class);
                startActivity(intent);
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

    private void getRemindersForCurrentUser(User user) {
        final ArrayList<Reminder> remindersList = new ArrayList<>();

        for (String key : user.getReminders()) {
            mReminderReference = FirebaseDatabase.getInstance()
                    .getReference("reminders").child(key);

            ValueEventListener reminderEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Reminder reminder = dataSnapshot.getValue(Reminder.class);

                    System.out.println("===============================");
                    System.out.println(reminder.toString());
                    System.out.println("===============================");

                    //TODO: HANDLE REMINDERS AND ADD THEM TO UI
                    remindersList.add(reminder);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            };
            mReminderReference.addValueEventListener(reminderEventListener);
        }
    }



    private void getUserReminderIDs() {

        System.out.println("Getting Reminder IDs");
        ValueEventListener userEventListner = new ValueEventListener() {



            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);

                System.out.println("===============================");
                System.out.println(user.toString());
                System.out.println(user.getReminders());
                System.out.println("===============================");

                getRemindersForCurrentUser(user);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                System.out.println("loadPost:onCancelled" + databaseError.toException());
                // ...
            }
        };
        mUserReference.addValueEventListener(userEventListner);


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
                    //getUserReminderIDs();

                } else {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                }
            }
        };
    }

}
