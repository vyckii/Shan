package cis350.upenn.edu.remindmelater;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        checkIfUserIsSignedIn();

        System.out.println("--------------------------");
        System.out.println("ON CREATE");
        System.out.println("--------------------------");

        String UID = getIntent().getStringExtra("uid");

        mReminderReference = FirebaseDatabase.getInstance().getReference("users").child(UID).child("reminders");

        Query query = mReminderReference.orderByChild("dueDate");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICKED ITEM");
                int itemPosition = mRecyclerView.getChildLayoutPosition(v);
                Reminder r = reminders.get(itemPosition);

                Intent intent = new Intent(getApplicationContext(), EditReminderActivity.class);
                intent.putExtra(r.getTitle(), "reminderName");
                intent.putExtra(r.getNotes(), "notes");
                intent.putExtra(r.getDueDate().toString(), "dueDate");
                intent.putExtra(r.getRecurring(), "recurring");
                intent.putExtra(r.getRecurringDate().toString(), "recurringUntil");
                intent.putExtra(r.getCategory(), "category");
                intent.putExtra(r.getLocation(), "location");
                startActivity(intent);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView.Adapter mAdapter = new FirebaseRecyclerAdapter<Reminder, ReminderHolder>(Reminder.class,
                R.layout.reminder_view, ReminderHolder.class, query) {

            @Override
            public void populateViewHolder(ReminderHolder reminderMessageViewHolder, Reminder reminder, int position) {
                reminderMessageViewHolder.setReminderTitle(reminder.getTitle());
                reminderMessageViewHolder.setReminderDesc(reminder.getNotes());
                reminderMessageViewHolder.setReminderTime(reminder.getDueDate());
                reminderMessageViewHolder.setReminderType(reminder.getCategory());
                reminderMessageViewHolder.setReminderLoc(reminder.getLocation());
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

                    System.out.println("user Signed in inside Main Screen Activity");

                } else {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                }
            }
        };
    }

//    private class RHolder extends RecyclerView.ViewHolder {
//
//        private final TextView reminderTitle;
//        private final TextView reminderDesc;
//        private final TextView reminderTime;
//        private final TextView reminderType;
//        private final TextView reminderLoc;
//
//        public RHolder(View itemView) {
//            super(itemView);
//
//            //TODO: Add views for this and put their IDs in place
//            reminderTitle = (TextView) itemView.findViewById(R.id.reminder_title);
//            reminderDesc = (TextView) itemView.findViewById(R.id.reminder_desc);
//            reminderTime =  (TextView) itemView.findViewById(R.id.due_date_label);
//            reminderType =  (TextView) itemView.findViewById(R.id.reminder_type);
//            reminderLoc =  (TextView) itemView.findViewById(R.id.reminder_loc);
//        }
//
//        public void setReminderTitle(String name) {
//            reminderTitle.setText(name);
//        }
//
//        public void setReminderDesc(String text) {
//            reminderDesc.setText(text);
//        }
//
//        public void setReminderTime(Long dateStr) {
//
//            System.out.println("setting date");
//
//            if (dateStr != null) {
//                SimpleDateFormat f1 = new SimpleDateFormat("hh:mm a", Locale.US);
//                SimpleDateFormat f2 = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(dateStr);
//                reminderTime.setText("" + f1.format(calendar.getTime()) + "\n" + f2.format(calendar.getTime()));
//
//            } else {
//                reminderTime.setText("No Date Set");
//            }
//        }
//
//        public void setReminderType(String text) {
//            reminderType.setText(text);
//        }
//
//        public void setReminderLoc(String text) {
//            reminderLoc.setText(text);
//        }
//
//
//        public void onClick(View view) {
//            System.out.println("clicked " + reminderTitle.getText());
//        }
//
//    }

}



