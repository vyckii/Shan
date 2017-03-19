package cis350.upenn.edu.remindmelater;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    static DatabaseReference mDatabase;


    public Reminder() {

    }

    private Reminder(String uid, String title, String notes, Long duedate) {

        this.userIDs = new HashMap<>();
        this.userIDs.put(uid, true);
        this.title = title;
        this.notes = notes;

        this.dueDate = duedate;
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


    public static void createReminderInDatabase(FirebaseUser user, String title, String notes, Long duedate) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = mDatabase.child("reminders").push().getKey();


        Reminder reminder = new Reminder(user.getUid(), title, notes, duedate);


        mDatabase.child("users").child(user.getUid()).child("reminders").child(uid).setValue(reminder);

        DatabaseReference reminderRef = mDatabase.child("reminders");
        reminderRef.child(uid).setValue(user.getUid());

    }

    @Override
    public String toString() {
        return "Reminder{" +
                "userIDs=" + userIDs +
                ", title='" + title + '\'' +
                ", notes='" + notes + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }
}
