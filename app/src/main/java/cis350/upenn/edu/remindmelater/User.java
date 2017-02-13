package cis350.upenn.edu.remindmelater;

import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by AJNandi on 2/8/17.
 */

public class User {

    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String email;
    private List<Reminder> reminders;



    public User() {

    }

    public User(String firstname, String lastname, String username, String password, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.reminders = new LinkedList<>();
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }
}
