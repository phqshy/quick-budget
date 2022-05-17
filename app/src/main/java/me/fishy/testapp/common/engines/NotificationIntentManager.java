package me.fishy.testapp.common.engines;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.ThreadLocalRandom;

import me.fishy.testapp.R;

public class NotificationIntentManager extends IntentService {
    public NotificationIntentManager(String name) {
        super(name);
    }

    public NotificationIntentManager() {
        super("NotificationManager");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        double balance = intent.getExtras().getDouble("balance");
        System.out.println(balance);

        Notification notification = new NotificationCompat.Builder(this, "quick-budget")
                .setSmallIcon(R.drawable.transparent_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.transparent_logo))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText("Your current balance is: $" + String.valueOf(balance))
                .setContentTitle("Balance updated!")
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(ThreadLocalRandom.current().nextInt(), notification);
    }
}
