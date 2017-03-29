package cis350.upenn.edu.remindmelater.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import cis350.upenn.edu.remindmelater.Notification.FirebaseNotificationHandler;
import cis350.upenn.edu.remindmelater.Notification.ScheduleClient;
import cis350.upenn.edu.remindmelater.R;
import cis350.upenn.edu.remindmelater.Reminder;
import cis350.upenn.edu.remindmelater.ReminderHolder;
import cis350.upenn.edu.remindmelater.User;


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
    private String uid;

    private RecyclerView mRecyclerView;
    private ScheduleClient scheduleClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        checkIfUserIsSignedIn();

        System.out.println("--------------------------");
        System.out.println("ON CREATE");
        System.out.println("--------------------------");

        scheduleClient = new ScheduleClient(this);
        Reminder.setScheduleClient(scheduleClient);
        scheduleClient.doBindService();


        uid = getIntent().getStringExtra("uid");

        mReminderReference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("reminders");

        Query query = mReminderReference.orderByChild("dueDate");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView.Adapter mAdapter = new FirebaseRecyclerAdapter<Reminder, ReminderHolder>(Reminder.class,
                R.layout.reminder_view, ReminderHolder.class, query) {

            @Override
            public void populateViewHolder(ReminderHolder reminderMessageViewHolder, Reminder reminder, int position) {



                System.out.println("-------------------------------");
                reminder.toString();
                reminderMessageViewHolder.setReminderTitle(reminder.getTitle());
                reminderMessageViewHolder.setReminderDesc(reminder.getNotes());
                reminderMessageViewHolder.setReminderTime(reminder.getDueDate());
                reminderMessageViewHolder.setReminderType(reminder.getCategory());
                reminderMessageViewHolder.setReminderLoc(reminder.getLocation());
                reminderMessageViewHolder.setReminderRec(reminder.getRecurring());
                if (reminder.getRecurringDate() != null) {
                    reminderMessageViewHolder.setReminderRecDate(reminder.getRecurringDate().toString());
                }
            }

        };

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

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

        if(scheduleClient != null) {
            scheduleClient.doUnbindService();
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
                    FirebaseNotificationHandler.addReminderNotifications(scheduleClient, uid);

                    System.out.println("user Signed in inside Main Screen Activity");

                } else {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                }
            }
        };
    }



}



