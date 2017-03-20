package cis350.upenn.edu.remindmelater;

import android.text.format.DateUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
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
    public String location;
    public String category;
    public String recurring;
    public Long recurringDate;

    static DatabaseReference mDatabase;


    public Reminder() {

    }

    private Reminder(String uid, String title, String notes, Long duedate, String location, String category,
                     String recurring, Long recurringDate) {

        this.userIDs = new HashMap<>();
        this.userIDs.put(uid, true);
        this.title = title;
        this.notes = notes;
        this.location = location;
        this.category = category;
        this.recurring = recurring;

        this.dueDate = duedate;
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


    public static void createReminderInDatabase(FirebaseUser user, String title, String notes, Long duedate,
                                                String location, String category, String recurring, Long recurringDate) {

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
            mDatabase.child("users").child(user.getUid()).child("reminders").child(uid).setValue(reminder);

            duedate += delta;
        }

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
