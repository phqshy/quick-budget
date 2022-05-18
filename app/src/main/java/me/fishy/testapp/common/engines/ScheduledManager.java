package me.fishy.testapp.common.engines;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduledManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startService = new Intent(context, NotificationIntentManager.class);
        startService.putExtra("title", intent.getExtras().getString("title"));
        startService.putExtra("content", intent.getExtras().getString("content"));

        context.startService(startService);
    }
}
