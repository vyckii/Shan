package cis350.upenn.edu.remindmelater;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by AJNandi on 3/27/17.
 */

public class Notification {

    private static int idCount = 0;

    public static void triggerNotification(Activity activity) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setWhen(System.currentTimeMillis());
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(activity, EditReminderActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(EditReminderActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(getID(), mBuilder.build());

        mBuilder.setAutoCancel(true);


    }

    public static int getID() {
        return idCount++;
    }

}
