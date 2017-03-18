package cis350.upenn.edu.remindmelater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public void setReminderTime(String dateStr) {

        if (dateStr != null) {
            SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
            SimpleDateFormat f2 = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
            SimpleDateFormat f3 = new SimpleDateFormat("hh:mm a", Locale.US);
            Date date;
            try {
                date = f1.parse(dateStr);
                reminderTime.setText("" + f3.format(date.getTime()) + "\n" + f2.format(date.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                reminderTime.setText("No Date Set");
            }
        } else {
            reminderTime.setText("No Date Set");
        }







    }


}
