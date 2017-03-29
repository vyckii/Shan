package cis350.upenn.edu.remindmelater.Notification;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cis350.upenn.edu.remindmelater.Reminder;

/**
 * Created by AJNandi on 3/29/17.
 */

public class FirebaseNotificationHandler {

    private static boolean hasSetNotifications = false;

    public static void addReminderNotifications(final ScheduleClient scheduleClient, String uid) {

        if (hasSetNotifications) {
            return;
        }

        DatabaseReference remindersRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("reminders");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                System.out.println("onChildAdded:" + dataSnapshot.getKey());

                Reminder reminder = dataSnapshot.getValue(Reminder.class);
                if (!reminder.isComplete()) {
                    scheduleClient.setAlarmForNotification(reminder);
                    System.out.println("Set notification for: " + reminder.getTitle());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                System.out.println("onChildChanged:" + dataSnapshot.getKey());

                Reminder reminder = dataSnapshot.getValue(Reminder.class);
                if (!reminder.isComplete()) {
                    scheduleClient.setAlarmForNotification(reminder);
                    System.out.println("Set notification for: " + reminder.getTitle());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("onChildRemoved:" + dataSnapshot.getKey());

                Reminder reminder = dataSnapshot.getValue(Reminder.class);
                if (!reminder.isComplete()) {
                    scheduleClient.setAlarmForNotification(reminder);
                    System.out.println("Set notification for: " + reminder.getTitle());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                System.out.println("onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Reminder movedComment = dataSnapshot.getValue(Reminder.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("postComments:onCancelled "+ databaseError.toException());
                System.out.println("Failed to load comments.");
            }
        };
        remindersRef.addChildEventListener(childEventListener);
        hasSetNotifications = true;

    }

}
