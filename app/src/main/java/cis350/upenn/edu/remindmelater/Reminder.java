package cis350.upenn.edu.remindmelater;

/**
 * Created by AJNandi on 2/8/17.
 *
 *
 */

public class Reminder {

    public User author;
    public String title;
    public String notes;


    public Reminder() {

    }

    public Reminder(User author, String title, String notes) {
        this.author = author;
        this.title = title;
        this.notes = notes;
    }

}
