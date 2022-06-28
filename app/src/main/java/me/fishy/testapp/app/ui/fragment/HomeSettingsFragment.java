package me.fishy.testapp.app.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import me.fishy.testapp.R;
import me.fishy.testapp.app.ui.activity.LoginActivity;
import me.fishy.testapp.app.ui.fragment.payments.PaymentsFragment;
import me.fishy.testapp.app.ui.fragment.schedule.NewScheduleFragment;
import me.fishy.testapp.app.ui.fragment.schedule.ScheduledPaymentsFragment;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.post.JSONPostRequest;

public class HomeSettingsFragment extends Fragment {
    private static boolean enabled = false;
    public static boolean buttoned = false;


    public HomeSettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enabled = true;

        view.findViewById(R.id.logout_button).setOnClickListener((l) -> {
            buttoned = true;
            try {
                destroyNotifications();
                closeSession();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.privacy_settings).setOnClickListener((l) -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://phqsh.me/privacy"));
            startActivity(i);
        });
    }

    public static boolean isEnabled(){
        return enabled;
    }

    public static void setEnabled(boolean value){
        enabled = value;
    }

    private void closeSession() throws IOException{
        PrintWriter pw = new PrintWriter(getActivity().getCacheDir() + "/uuid.txt");
        pw.close();

        String json = UserDataHolder.getGson().toJson(UserDataHolder.getInstance());

        UserDataHolder.setInstance(null);

        File stored = new File(getActivity().getCacheDir() + "/cached_instance.txt");

        if (stored.exists()){
            PrintWriter writer2 = new PrintWriter(stored);
            writer2.close();
        }

        System.out.println("Writing stored instance to file.");
        FileWriter writer = new FileWriter(stored);
        writer.write(json);
        writer.close();

        System.out.println("Writing current time in mills to file. called from main");
        File mils = new File(getActivity().getFilesDir() + "/timeinms.txt");

        if (mils.exists()){
            PrintWriter pw2 = new PrintWriter(stored);
            pw2.close();
        }

        FileWriter writer2 = new FileWriter(mils);
        writer2.write(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        writer2.close();

        new JSONPostRequest("https://phqsh.me/update_user")
                .post(json)
                .thenAccept(System.out::println);

        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        System.out.println("forced credential reset (aka a logout)");
        PaymentsFragment.clearData();
        ScheduledPaymentsFragment.clearData();
        startActivity(intent);
        this.getActivity().finish();
    }

    private void destroyNotifications() throws JSONException {
        ArrayList<JSONObject> notifications = UserDataHolder.getInstance().getScheduled();
        for (JSONObject j : notifications){
            NewScheduleFragment.cancelNotification(this.getContext(), j.getInt("code"));
        }
    }
}