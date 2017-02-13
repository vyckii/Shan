package cis350.upenn.edu.remindmelater;

import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by AJNandi on 2/8/17.
 */

public class User {

    private String email;
    private List<Reminder> reminders;



    public User() {

    }

    public User(String email) {
        this.email = email;
        this.reminders = new LinkedList<>();
    }

    public String getEmail() {
        return email;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }
}
