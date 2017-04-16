package cis350.upenn.edu.remindmelater.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import cis350.upenn.edu.remindmelater.Notification.FirebaseNotificationHandler;
import cis350.upenn.edu.remindmelater.Notification.ScheduleClient;
import cis350.upenn.edu.remindmelater.R;
import cis350.upenn.edu.remindmelater.Reminder;
import cis350.upenn.edu.remindmelater.ReminderHolder;

public class NavigationMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mReminderReference;
    private DatabaseReference mUserReference;


    private FirebaseUser mCurrentUser;
    private String uid;

    private RecyclerView mRecyclerView;
    private ScheduleClient scheduleClient;

    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Reminders");

        setContentView(R.layout.activity_navigation_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddReminderActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // --------- Main Screen Activity Stuff below ---------

        scheduleClient = new ScheduleClient(this);
        Reminder.setScheduleClient(scheduleClient);
        scheduleClient.doBindService();

        checkIfUserIsSignedIn();

        uid = getIntent().getStringExtra("uid");

        mReminderReference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("reminders");

        Query query = mReminderReference.orderByChild("isComplete").startAt(false).endAt(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = getAdapter(query);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.scheduled_category) {

            Query query = mReminderReference.orderByChild("isComplete").startAt(false).endAt(false);
            mAdapter = getAdapter(query);

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            setTitle("Scheduled");



        } else if (id == R.id.complete_category) {

            Query query = mReminderReference.orderByChild("isComplete").startAt(true).endAt(true);
            mAdapter = getAdapter(query);

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            setTitle("Completed");


        } else if (id == R.id.school_category) {


            Query query = mReminderReference.orderByChild("category").startAt("School").endAt("School");
            mAdapter = getAdapter(query);

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            setTitle("School");

        } else if (id == R.id.work_category) {

            Query query = mReminderReference.orderByChild("category").startAt("Work").endAt("Work");
            mAdapter = getAdapter(query);

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            setTitle("Work");

        } else if (id == R.id.extracurricular_category) {

            Query query = mReminderReference.orderByChild("category").startAt("Extracurricular").endAt("Extracurricular");
            mAdapter = getAdapter(query);

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            setTitle("Extracurricular");

        } else if (id == R.id.personal_category) {

            Query query = mReminderReference.orderByChild("category").startAt("Personal").endAt("Personal");
            mAdapter = getAdapter(query);

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            setTitle("Personal");

        } else if (id == R.id.other_category) {

            Query query = mReminderReference.orderByChild("category").startAt("Other").endAt("Other");
            mAdapter = getAdapter(query);

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            setTitle("Other");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private FirebaseRecyclerAdapter<Reminder, ReminderHolder> getAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Reminder, ReminderHolder>(Reminder.class,
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
                reminderMessageViewHolder.setReminderImage(reminder.getImage());
                reminderMessageViewHolder.setReminderShareWith(reminder.getShareWith());
                if (reminder.getRecurringDate() != null) {
                    reminderMessageViewHolder.setReminderRecDate(reminder.getRecurringDate().toString());
                }
            }

        };
    }
}
