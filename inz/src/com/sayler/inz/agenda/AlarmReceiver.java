package com.sayler.inz.agenda;

import java.sql.SQLException;

import com.sayler.inz.Launch;
import com.sayler.inz.R;
import com.sayler.inz.data.AgendaDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Agenda;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "AlarmReceiver";
	private AgendaDataProvider agendaDataProvider;

	public void onReceive(Context context, Intent intent) {

		int agendaId = intent.getIntExtra(AgendaFragment.AGENDA_ID, 0);
		if (agendaId != 0) {

			DaoHelper.setOpenHelper(context, DBSqliteOpenHelper.class);
			agendaDataProvider = new AgendaDataProvider();

			try {
				Agenda agenda = agendaDataProvider.get(agendaId);
				if (agenda != null) {
					Log.d(TAG, "AlarmReceiver " + agenda.getId());
					agendaDataProvider.delete(agenda);

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context)
							.setSmallIcon(R.drawable.access_alarms)
							.setContentTitle("Agenda!")
							.setContentText(
									context.getString(R.string.app_name))
							.setAutoCancel(true);

					Intent resultIntent = new Intent(context, Launch.class);
					resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

					TaskStackBuilder stackBuilder = TaskStackBuilder
							.create(context);
					// Adds the back stack for the Intent (but not the Intent
					// itself)
					stackBuilder.addParentStack(Launch.class);
					// Adds the Intent that starts the Activity to the top of
					// the
					// stack
					stackBuilder.addNextIntent(resultIntent);
					PendingIntent resultPendingIntent = stackBuilder
							.getPendingIntent(0,
									PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent(resultPendingIntent);
					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					// mId allows you to update the notification later on.
					mNotificationManager.notify(-1, mBuilder.build());

					PowerManager pm = (PowerManager) context
							.getSystemService(Context.POWER_SERVICE);
					PowerManager.WakeLock wl = pm.newWakeLock(
							PowerManager.SCREEN_BRIGHT_WAKE_LOCK
									| PowerManager.ACQUIRE_CAUSES_WAKEUP,
							"wakeup");
					wl.acquire();

					Vibrator vib = (Vibrator) context
							.getSystemService(Context.VIBRATOR_SERVICE);
					vib.vibrate(500);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}