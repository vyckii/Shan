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
    // The date selected for the alarm
    private final Calendar date;
    // The android system alarm manager
    private final AlarmManager am;
    // Your context to retrieve the alarm manager from
    private final Context context;
    // Reminder for alarm
    private final Reminder reminder;

    public AlarmTask(Context context, Calendar date, Reminder reminder) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
        this.reminder = reminder;
    }

    @Override
    public void run() {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
    }
}
