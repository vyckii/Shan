package cis350.upenn.edu.remindmelater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AJNandi on 3/15/17.
 *
 * This class maps the reminder data to its respective text views etc
 *
 */

public class ReminderHolder extends RecyclerView.ViewHolder {

    private final TextView reminderTitle;
    private final TextView reminderDesc;
    private final TextView reminderTime;

    public ReminderHolder(View itemView) {
        super(itemView);
        //TODO: Add views for this and put their IDs in place
        reminderTitle = (TextView) itemView.findViewById(R.id.reminder_title);
        reminderDesc = (TextView) itemView.findViewById(R.id.reminder_desc);
        reminderTime =  (TextView) itemView.findViewById(R.id.due_date_label);
    }

    public void setReminderTitle(String name) {
        reminderTitle.setText(name);
    }

    public void setReminderDesc(String text) {
        reminderDesc.setText(text);
    }

    public void setReminderTime(Long dateStr) {

        System.out.println("setting date");

        if (dateStr != null) {
            SimpleDateFormat f1 = new SimpleDateFormat("hh:mm a", Locale.US);
            SimpleDateFormat f2 = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateStr);
            reminderTime.setText("" + f1.format(calendar.getTime()) + "\n" + f2.format(calendar.getTime()));

        } else {
            reminderTime.setText("No Date Set");
        }







    }


}
