package cis350.upenn.edu.remindmelater.Notification;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.Calendar;

import cis350.upenn.edu.remindmelater.Reminder;

/**
 * Created by AJNandi on 3/27/17.
 */

public class ScheduleClient {

        private ScheduleService mBoundService;
        private Context mContext;
        private boolean mIsBound;

        public ScheduleClient(Context context) {
            mContext = context;
        }

        public void doBindService() {
            // Establish a connection with our service
            mContext.bindService(new Intent(mContext, ScheduleService.class), mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
        }

        private ServiceConnection mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with our service has been established,
                // giving us the service object we can use to interact with our service.
                mBoundService = ((ScheduleService.ServiceBinder) service).getService();
            }

            public void onServiceDisconnected(ComponentName className) {
                mBoundService = null;
            }
        };

        public void setAlarmForNotification(Reminder reminder){
            mBoundService.setAlarm(reminder);
        }

        public void doUnbindService() {
            if (mIsBound) {
                // Detach our existing connection.
                mContext.unbindService(mConnection);
                mIsBound = false;
            }
        }
}
