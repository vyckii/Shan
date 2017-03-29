package cis350.upenn.edu.remindmelater.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import cis350.upenn.edu.remindmelater.Reminder;

/**
 * Created by AJNandi on 3/27/17.
 */


public class AlarmTask implements Runnable {
    private final AlarmManager am;
    private final Context context;
    private final Reminder reminder;

    private static int id = 0;

    public AlarmTask(Context context, Reminder reminder) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.reminder = reminder;
    }

    @Override
    public void run() {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("notes", reminder.getNotes());
        intent.putExtra("time", reminder.getDueDate());
        intent.putExtra("id", id);

        intent.putExtra("reminderName", reminder.getTitle());
        intent.putExtra("dueDate", reminder.getDueDate());
        intent.putExtra("recurring", reminder.getRecurring());
        intent.putExtra("recurringUntil", reminder.getRecurringDate());
        intent.putExtra("category", reminder.getCategory());
        intent.putExtra("location", reminder.getLocation());


        PendingIntent pendingIntent = PendingIntent.getService(context, id, intent, 0);
        am.set(AlarmManager.RTC, reminder.getDueDate(), pendingIntent);
        System.out.println("Alarm set for : " + id + ": " + reminder.getTitle());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(reminder.getDueDate());
        System.out.println(cal.getTime());
        id++;

    }
}
