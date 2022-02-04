package com.sagittariusk2.dinchari;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String channel = "DINCHARI_NOTIFICATION_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationID = intent.getIntExtra("notificationID", 0);
        String id = intent.getStringExtra("taskID");
        String cat = intent.getStringExtra("taskCategory");
        String desc = intent.getStringExtra("taskDesc");
        String name = intent.getStringExtra("taskName");
        // TODO : 2. Add notification sound
        Intent i = new Intent(context, TaskViewShowActivity.class);
        i.putExtra("date", intent.getStringExtra("date"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.icon_din_chari)
                .setContentTitle(name)
                .setContentText(desc)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationID, builder.build());

    }
}
