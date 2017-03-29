package cis350.upenn.edu.remindmelater;

import android.content.Context;
import android.text.format.DateUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import cis350.upenn.edu.remindmelater.Notification.ScheduleClient;

/**
 * Created by AJNandi on 2/8/17.
 *
 *
 */

public class Reminder {

    public Map<String, Boolean> userIDs;
    public String title;
    public String notes;
    public Long dueDate;
    public String location;
    public String category;
    public String recurring;
    public Long recurringDate;
    public String uid;

    static DatabaseReference mDatabase;
    static ScheduleClient scheduleClient;


    public Reminder() {

    }

    private Reminder(String uid, String title, String notes, Long dueDate, String location, String category,
                     String recurring, Long recurringDate) {

        this.userIDs = new HashMap<>();
        this.userIDs.put(uid, true);
        this.title = title;
        this.notes = notes;
        this.location = location;
        this.category = category;
        this.recurring = recurring;
        this.dueDate = dueDate;
        this.recurringDate = recurringDate;
    }

    public Map<String, Boolean> userIDs() {
        return userIDs;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public String getRecurring() { return recurring; }

    public Long getRecurringDate() { return recurringDate; }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public static void createReminderInDatabase(FirebaseUser user, String title, String notes, Long duedate,
                                                String location, String category, String recurring, Long recurringDate, Context context) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        long delta = 0;

        switch (recurring) {
            case "Once":
                recurringDate = duedate;
                delta = recurringDate;
                break;
            case "Daily":
                delta = DateUtils.DAY_IN_MILLIS;
                break;
            case "Weekly":
                delta = DateUtils.WEEK_IN_MILLIS;
                break;
            case "Yearly":
                delta = DateUtils.YEAR_IN_MILLIS;
                break;
            default:
                delta = recurringDate;
                break;

        }

        for (Long i = duedate; i <= recurringDate; i += delta) {
            String uid = mDatabase.child("reminders").push().getKey();
            Reminder reminder = new Reminder(user.getUid(), title, notes, duedate, location, category, recurring, recurringDate);
            reminder.setUid(uid);
            mDatabase.child("users").child(user.getUid()).child("reminders").child(uid).setValue(reminder);




            duedate += delta;


        }
    }


    public static void updateReminderInDatabase(FirebaseUser user, String oldTitle, String title, String notes, Long dueDate,
                                                String location, String category, String recurring, Long recurringDate) {

        final String rOldTitle = oldTitle;
        final FirebaseUser rUser = user;
        final String rTitle = title;
        final String rNotes = notes;
        final Long rDueDate = dueDate;
        final String rLocation = location;
        final String rCategory = category;
        final String rRecurring = recurring;
        final Long rRecurringDate = recurringDate;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        long delta = 0;
        switch (recurring) {
            case "Once":
                recurringDate = dueDate;
                delta = recurringDate;
                break;
            case "Daily":
                delta = DateUtils.DAY_IN_MILLIS;
                break;
            case "Weekly":
                delta = DateUtils.WEEK_IN_MILLIS;
                break;
            case "Yearly":
                delta = DateUtils.YEAR_IN_MILLIS;
                break;
            default:
                delta = recurringDate;
                break;

        }

        for (Long i = dueDate; i <= recurringDate; i += delta) {

            mDatabase.child("users").child(user.getUid()).child("reminders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        if (d.child("title").getValue().equals(rOldTitle)) {
                            d.getRef().removeValue();
                            System.out.println("deleted");
                            String uid = mDatabase.child("reminders").push().getKey();
                            Reminder reminder = new Reminder(rUser.getUid(), rTitle, rNotes, rDueDate, rLocation, rCategory, rRecurring, rRecurringDate);
                            reminder.setUid(uid);
                            mDatabase.child("users").child(rUser.getUid()).child("reminders").child(uid).setValue(reminder);


                            //TODO: Add notification for reminders updated

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Firebase error finding reminder to delete");
                }
            });

            dueDate += delta;
        }
    }


    public static void deleteReminderFromDatabase(FirebaseUser user, String title, String date) {

        final String rTitle = title;
        final String rDate = date;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).child("reminders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.child("title").getValue().equals(rTitle) && d.child("dueDate").getValue().toString().equals(rDate)) {
                        d.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Firebase error finding reminder to delete");
            }
        });
    }


    @Override
    public String toString() {
        return "Reminder{" +
                "userIDs=" + userIDs +
                ", title='" + title + '\'' +
                ", notes='" + notes + '\'' +
                ", dueDate=" + dueDate + '\'' +
                ", location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", recurring='" + recurring + '\'' +
                ", recurringUntil='" + recurringDate + '\'' +
                '}';
    }

    public static void setScheduleClient(ScheduleClient scheduleClient) {
        Reminder.scheduleClient = scheduleClient;
    }

}
