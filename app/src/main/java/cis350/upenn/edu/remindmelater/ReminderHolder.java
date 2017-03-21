package cis350.upenn.edu.remindmelater;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by AJNandi on 3/15/17.
 *
 * This class maps the reminder data to its respective text views etc
 *
 */

public class ReminderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView reminderTitle;
    private final TextView reminderDesc;
    private final TextView reminderTime;
    private final TextView reminderType;
    private final TextView reminderLoc;

    public ReminderHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);

        //TODO: Add views for this and put their IDs in place
        reminderTitle = (TextView) itemView.findViewById(R.id.reminder_title);
        reminderDesc = (TextView) itemView.findViewById(R.id.reminder_desc);
        reminderTime =  (TextView) itemView.findViewById(R.id.due_date_label);
        reminderType =  (TextView) itemView.findViewById(R.id.reminder_type);
        reminderLoc =  (TextView) itemView.findViewById(R.id.reminder_loc);


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

    public void setReminderType(String text) {
        reminderType.setText(text);
    }

    public void setReminderLoc(String text) {
        reminderLoc.setText(text);
    }


    //TODO need to call activity to start EditReminderActivity
    public void onClick(View view) {
        System.out.println("clicked " + reminderTitle.getText());
    }

}
