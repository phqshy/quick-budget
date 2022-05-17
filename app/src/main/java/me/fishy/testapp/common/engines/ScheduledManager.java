package me.fishy.testapp.common.engines;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import me.fishy.testapp.R;

public class ScheduledManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startService = new Intent(context, NotificationIntentManager.class);
        startService.putExtra("balance", intent.getExtras().getDouble("balance"));

        System.out.println("alarm triggered");

        context.startService(startService);
    }
}
