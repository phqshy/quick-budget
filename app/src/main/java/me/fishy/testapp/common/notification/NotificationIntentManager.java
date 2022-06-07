package me.fishy.testapp.common.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import me.fishy.testapp.R;
import me.fishy.testapp.app.ui.activity.MainActivity;
import me.fishy.testapp.app.ui.fragment.schedule.NewScheduleFragment;
import me.fishy.testapp.app.ui.fragment.schedule.ScheduledPaymentsFragment;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.post.JSONPostRequest;

public class NotificationIntentManager extends IntentService {
    public NotificationIntentManager(String name) {
        super(name);
    }

    public NotificationIntentManager() {
        super("NotificationManager");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String title = intent.getExtras().getString("title");
        String text = intent.getExtras().getString("content");

        //generating the intents for marking payments as read and cancelling the notification

        //this intent will add/subtract this much from the account
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("text", text);

        PendingIntent paymentsAddIntent = new NavDeepLinkBuilder(getApplicationContext())
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.nav_chart)
                .setDestination(R.id.paymentsAddFragment)
                .setArguments(args)
                .createPendingIntent();

        PendingIntent removeScheduledIntent = new NavDeepLinkBuilder(getApplicationContext())
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.nav_chart)
                .setDestination(R.id.scheduledPaymentsFragment)
                .setArguments(args)
                .createPendingIntent();

        Notification notification = new NotificationCompat.Builder(this, "quick-budget")
                .setSmallIcon(R.drawable.transparent_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.transparent_logo))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(text)
                .setContentTitle(title)
                .addAction(R.drawable.transparent_logo, "Complete", paymentsAddIntent)
                .addAction(R.drawable.transparent_logo, "Remove", removeScheduledIntent)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(ThreadLocalRandom.current().nextInt(), notification);

        ArrayList<JSONObject> list = new ArrayList<>();
        try{
            for (int i = 0; i < ScheduledPaymentsFragment.recyclerAdapter.jsonList.length(); i++){
                JSONObject json = ScheduledPaymentsFragment.recyclerAdapter.jsonList.getJSONObject(i);
                if (json.get("title").equals(title) && json.get("text").equals(text)){
                    //we found the corresponding json object! woo!!!
                    //this is deeply flawed lmfao
                    if (json.getInt("repeating") == RepeatingTypeEnum.NEVER.getMode()){
                        ScheduledPaymentsFragment.recyclerAdapter.jsonList.remove(i);
                        ScheduledPaymentsFragment.recyclerAdapter.setIsDirty(true);
                    } else if (json.getInt("repeating") == RepeatingTypeEnum.DAILY.getMode()){
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date(json.getLong("date")));
                        cal.add(Calendar.DATE, 1);
                        json.put("date", cal.getTimeInMillis());
                        NewScheduleFragment.setNotification(getApplicationContext(), json.getString("title"), json.getString("text"), cal, UserDataHolder.getInstance().addToNumScheduled());

                        ScheduledPaymentsFragment.recyclerAdapter.jsonList.remove(i);
                        ScheduledPaymentsFragment.recyclerAdapter.jsonList.put(json);
                        ScheduledPaymentsFragment.recyclerAdapter.setIsDirty(true);
                    } else if (json.getInt("repeating") == RepeatingTypeEnum.MONTHLY.getMode()){
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date(json.getLong("date")));
                        cal.add(Calendar.MONTH, 1);
                        json.put("date", cal.getTimeInMillis());
                        NewScheduleFragment.setNotification(getApplicationContext(), json.getString("title"), json.getString("text"), cal, UserDataHolder.getInstance().addToNumScheduled());

                        ScheduledPaymentsFragment.recyclerAdapter.jsonList.remove(i);
                        ScheduledPaymentsFragment.recyclerAdapter.jsonList.put(json);
                        ScheduledPaymentsFragment.recyclerAdapter.setIsDirty(true);
                    }
                }

                list.add(json);
            }

            UserDataHolder.getInstance().setScheduled(list);

            String json = UserDataHolder.getGson().toJson(UserDataHolder.getInstance());

            File stored = new File(getCacheDir() + "/cached_instance.txt");

            if (stored.exists()){
                PrintWriter writer2 = new PrintWriter(stored);
                writer2.close();
            }

            System.out.println("Writing stored instance to file.");
            FileWriter writer = new FileWriter(stored);
            writer.write(json);
            writer.close();

            new JSONPostRequest("https://phqsh.me/update_user")
                    .post(json)
                    .thenAccept(System.out::println);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
