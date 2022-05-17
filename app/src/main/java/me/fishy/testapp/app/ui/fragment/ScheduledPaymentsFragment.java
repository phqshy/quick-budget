package me.fishy.testapp.app.ui.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.fishy.testapp.R;
import me.fishy.testapp.app.recycler.RecyclerAdapter;
import me.fishy.testapp.common.engines.ScheduledManager;
import me.fishy.testapp.common.holders.UserDataHolder;

public class ScheduledPaymentsFragment extends Fragment {
    public ScheduledPaymentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scheduled_payments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.scheduled_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        recycler.setAdapter(new RecyclerAdapter(new ArrayList<>()));


    }

    public void setNotification(String title, String content, Calendar calendar) {
        Intent intent = new Intent(this.getContext(), ScheduledManager.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        PendingIntent pending = PendingIntent.getBroadcast(this.getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);

    }
}