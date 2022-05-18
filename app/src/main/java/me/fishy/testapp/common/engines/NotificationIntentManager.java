package me.fishy.testapp.common.engines;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import me.fishy.testapp.R;
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

        Notification notification = new NotificationCompat.Builder(this, "quick-budget")
                .setSmallIcon(R.drawable.transparent_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.transparent_logo))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(text)
                .setContentTitle(title)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(ThreadLocalRandom.current().nextInt(), notification);

        ArrayList<JSONObject> list = new ArrayList<>();
        try{
            for (int i = 0; i < ScheduledPaymentsFragment.recyclerAdapter.jsonList.length(); i++){
                JSONObject json = ScheduledPaymentsFragment.recyclerAdapter.jsonList.getJSONObject(i);
                if (json.get("title").equals(title) && json.get("text").equals(text)){
                    ScheduledPaymentsFragment.recyclerAdapter.jsonList.remove(i);
                    ScheduledPaymentsFragment.recyclerAdapter.setIsDirty(true);
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
