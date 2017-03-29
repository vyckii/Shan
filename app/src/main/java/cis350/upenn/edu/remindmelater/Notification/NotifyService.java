package cis350.upenn.edu.remindmelater.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import cis350.upenn.edu.remindmelater.Activities.EditReminderActivity;
import cis350.upenn.edu.remindmelater.R;

/**
 * Created by AJNandi on 3/27/17.
 *
 *
 */

public class NotifyService extends Service {



    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static final String INTENT_NOTIFY = "cis350.upenn.edu.remindmelater";
    // The system notification manager
    private NotificationManager mNM;

    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // If this service was started by out AlarmTask intent then we want to show our notification
        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification(intent);

        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification(Intent intent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(intent.getStringExtra("title"))
                        .setContentText(intent.getStringExtra("notes"))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(intent.getLongExtra("time", System.currentTimeMillis()));
        Intent resultIntent = new Intent(this, EditReminderActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(EditReminderActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNM = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = intent.getIntExtra("id", 12345);
        mNM.notify(id, mBuilder.build());
        System.out.println("---------------------------");
        System.out.println("Notified!! " + id);
        System.out.println(intent.getStringExtra("title"));
        System.out.println(intent.getStringExtra("notes"));

        mBuilder.setAutoCancel(true);
    }


}
