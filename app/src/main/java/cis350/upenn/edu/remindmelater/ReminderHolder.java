package cis350.upenn.edu.remindmelater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by AJNandi on 3/15/17.
 *
 * This class maps the reminder data to its respective text views etc
 *
 */

public class ReminderHolder extends RecyclerView.ViewHolder {

    private final TextView reminderTitle;
    private final TextView reminderDesc;

    public ReminderHolder(View itemView) {
        super(itemView);
        //TODO: Add views for this and put their IDs in place
        reminderTitle = (TextView) itemView.findViewById(1);
        reminderDesc = (TextView) itemView.findViewById(1);
    }

    public void setReminderTitle(String name) {
        reminderTitle.setText(name);
    }

    public void setReminderDesc(String text) {
        reminderDesc.setText(text);
    }


}
