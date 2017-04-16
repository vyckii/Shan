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
    public String image;
    public String uid;
    public String shareWith;
    public boolean isComplete;

    static DatabaseReference mDatabase;
    static ScheduleClient scheduleClient;


    public Reminder() {

    }

    private Reminder(String uid, String title, String notes, Long dueDate, String location, String category,
                     String recurring, Long recurringDate, String image, String shareWith, boolean isComplete) {

        this.userIDs = new HashMap<>();
        this.userIDs.put(uid, true);
        this.title = title;
        this.notes = notes;
        this.location = location;
        this.category = category;
        this.recurring = recurring;
        this.dueDate = dueDate;
        this.recurringDate = recurringDate;
        this.image = image;
        this.isComplete = isComplete;
        this.shareWith = shareWith;
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

    public String getImage() { return image; }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete() {
        isComplete = true;
    }

    public String getShareWith() { return shareWith; }


    public static void createReminderInDatabase(FirebaseUser user, String title, String notes, Long duedate,
                                                String location, String category, String recurring, Long recurringDate,
                                                String image, String shareWith, Context context, boolean isComplete) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        long delta = 0;
        long dueDate = duedate;

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
            Reminder reminder = new Reminder(user.getUid(), title, notes, duedate, location, category, recurring, recurringDate, image, shareWith, false);
            reminder.setUid(uid);
            mDatabase.child("users").child(user.getUid()).child("reminders").child(uid).setValue(reminder);

            duedate += delta;
        }


        if (!shareWith.equals("")) {

            System.out.println("SHARING REMINDER WOO");

                final String userEmail = shareWith;
                final String rShareWith = user.getEmail();
                final String rTitle = title;
                final String rNotes = notes;
                final Long rDueDate = dueDate;
                final String rLocation = location;
                final String rCategory = category;
                final String rRecurring = recurring;
                final Long rRecurringDate = recurringDate;
                final String rImage = image;
                final Long rDelta = delta;


                // get user to share with
                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            System.out.println(d.child("email").getValue());
                            System.out.println(d.child("email").getValue().equals(userEmail));

                            if (d.child("email").getValue().equals(userEmail)) {
                                for (Long i = rDueDate; i <= rRecurringDate; i += rDelta) {

                                    String userUid = d.getKey();
                                    System.out.println("~~~~~~~~~~~~~~~~~");
                                    System.out.println("userid " + userUid);

                                    String uid = mDatabase.child("reminders").push().getKey();
                                    Reminder reminder = new Reminder(userUid, rTitle, rNotes, i, rLocation, rCategory, rRecurring, rRecurringDate, rImage, rShareWith, false);
                                    reminder.setUid(uid);
                                    mDatabase.child("users").child(userUid).child("reminders").child(uid).setValue(reminder);

                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("Firebase error finding reminder to delete");
                    }
                });
        }
    }


    public static void updateReminderInDatabase(final FirebaseUser user, String oldTitle, long OldDate, String title, String notes, Long dueDate,
                                                String location, String category, String recurring, Long recurringDate, String image, final String shareWith, final boolean isComplete) {

        final String rOldTitle = oldTitle;
        final long rOldDate = OldDate;
        final FirebaseUser rUser = user;
        final String rUserEmail = user.getEmail();
        final String rTitle = title;
        final String rNotes = notes;
        final Long rDueDate = dueDate;
        final String rLocation = location;
        final String rCategory = category;
        final String rRecurring = recurring;
        final Long rRecurringDate = recurringDate;
        final String rImage = image;
        final String rShareWith = shareWith;
        final boolean rIsComplete = isComplete;

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

        mDatabase.child("users").child(user.getUid()).child("reminders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.child("title").getValue().equals(rOldTitle) && Long.parseLong(d.child("dueDate").getValue().toString()) == (rOldDate)) {
                        d.getRef().removeValue();
                        System.out.println("deleted");
                        String uid = mDatabase.child("reminders").push().getKey();
                        Reminder reminder = new Reminder(rUser.getUid(), rTitle, rNotes, rDueDate, rLocation, rCategory, rRecurring, rRecurringDate, rImage, rShareWith, rIsComplete);
                        reminder.setUid(uid);
                        mDatabase.child("users").child(rUser.getUid()).child("reminders").child(uid).setValue(reminder);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Firebase error finding reminder to delete");
            }
        });


        if (!shareWith.equals("")) {
            // get user to share with

            System.out.println("UPDATING SHARED REMINDER");

            mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        if (d.child("email").getValue().equals(rShareWith)) {

                            System.out.println("updating reminder for : " + rShareWith);

                            final String userUid = d.getKey();
                            System.out.println(userUid);

                            mDatabase.child("users").child(userUid).child("reminders").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        if (d.child("title").getValue().equals(rOldTitle) && Long.parseLong(d.child("dueDate").getValue().toString()) == (rOldDate)) {
                                            d.getRef().removeValue();
                                            System.out.println("deleted");
                                            String uid = mDatabase.child("reminders").push().getKey();
                                            Reminder reminder = new Reminder(userUid, rTitle, rNotes, rDueDate, rLocation, rCategory, rRecurring, rRecurringDate, rImage, rUserEmail, rIsComplete);
                                            reminder.setUid(uid);
                                            mDatabase.child("users").child(userUid).child("reminders").child(uid).setValue(reminder);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("Firebase error finding reminder to delete");
                                }
                            });


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Firebase error finding reminder to delete");
                }
            });
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
                ", image='" + image + '\'' +
                '}';
    }

    public static void setScheduleClient(ScheduleClient scheduleClient) {
        Reminder.scheduleClient = scheduleClient;
    }

}
