package cis350.upenn.edu.remindmelater;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by AJNandi on 2/8/17.
 *
 *
 */

public class Reminder {

    public List<String> userIDs;
    public String title;
    public String notes;
    public String dueDate;

    static DatabaseReference mDatabase;


    public Reminder() {

    }

    private Reminder(String uid, String title, String notes, String duedate) {

        this.userIDs = new LinkedList<>();
        this.userIDs.add(uid);
        this.title = title;
        this.notes = notes;

        this.dueDate = duedate;
    }

    public List<String> userIDs() {
        return userIDs;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public String getDueDate() {
        return dueDate;
    }


    public static void createReminderInDatabase(FirebaseUser user, String title, String notes, String duedate) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = mDatabase.child("reminders").push().getKey();


        Reminder reminder = new Reminder(user.getUid(), title, notes, duedate);


        mDatabase.child("reminders").child(uid).setValue(reminder);
        DatabaseReference reminderRef = mDatabase.child("users").child(user.getUid()).child("reminders");
        reminderRef.push().setValue(uid);

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
