package me.fishy.testapp.app.ui.fragment.schedule;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import me.fishy.testapp.R;
import me.fishy.testapp.app.recycler.ScheduledRecyclerAdapter;
import me.fishy.testapp.common.engines.ScheduledManager;
import me.fishy.testapp.common.holders.UserDataHolder;

public class NewScheduleFragment extends Fragment {
    private Calendar currentCalendar = Calendar.getInstance();

    public NewScheduleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText dateText = view.findViewById(R.id.scheduled_add_calendar_date);
        EditText timeText = view.findViewById(R.id.scheduled_add_calendar_time);
        EditText amount = view.findViewById(R.id.scheduled_add_amount);
        EditText reason = view.findViewById(R.id.scheduled_add_reason);
        Button createButton = view.findViewById(R.id.scheduled_add_create_button);

        DatePickerDialog.OnDateSetListener date = (view1, year, month, day) -> {
            currentCalendar.set(Calendar.YEAR, year);
            currentCalendar.set(Calendar.MONTH,month);
            currentCalendar.set(Calendar.DAY_OF_MONTH,day);
            updateDate(dateText, currentCalendar);
        };
        TimePickerDialog.OnTimeSetListener time = (timePicker, i, i1) -> {
            currentCalendar.set(Calendar.HOUR_OF_DAY, i);
            currentCalendar.set(Calendar.MINUTE, i1);
            updateTime(timeText, currentCalendar);
        };
        dateText.setOnClickListener(view12 -> new DatePickerDialog(getContext(), date, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH)).show());
        timeText.setOnClickListener(view12 -> new TimePickerDialog(getContext(), time, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), false).show());
        createButton.setOnClickListener((v) -> {
            double numberAmount = Double.parseDouble(amount.getText().toString());
            numberAmount = numberAmount * 100;
            int filler = (int) numberAmount;
            numberAmount = (double) filler / 100;

            String notifReason = reason.getText().toString();

            currentCalendar.set(Calendar.SECOND, 0);
            int code = UserDataHolder.getInstance().addToNumScheduled();
            setNotification(this.getContext(), "Payment: " + notifReason, "You have a payment due worth $" + numberAmount, currentCalendar, code);

            try{
                JSONObject json = new JSONObject();
                json.put("title", "Payment: " + notifReason);
                json.put("text", "You have a payment due worth $" + numberAmount);
                json.put("date", currentCalendar.getTime());
                json.put("code", code);

                UserDataHolder.getInstance().addToScheduled(json);
                ScheduledPaymentsFragment.recyclerAdapter.replaceData(UserDataHolder.getInstance().getScheduled());
                ScheduledPaymentsFragment.recyclerAdapter.notifyDataSetChanged();
                currentCalendar.setTime(Calendar.getInstance().getTime());
            } catch(JSONException e){
                e.printStackTrace();
            }

            Toast.makeText(getActivity(), "Creating scheduled payment", Toast.LENGTH_LONG).show();

            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

            navHostFragment.getNavController().navigate(R.id.action_newScheduleFragment_to_scheduledPaymentsFragment);
        });
    }

    public static void setNotification(Context context, String title, String content, Calendar calendar, int code) {
        Intent intent = new Intent(context, ScheduledManager.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        PendingIntent pending = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);

    }

    public static void cancelNotification(Context context, String title, String content, int code){
        Intent intent = new Intent(context, ScheduledManager.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        PendingIntent pending = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        pending.cancel();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pending);
    }

    private void updateDate(EditText editText, Calendar calendar){
        String format="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(format, Locale.US);
        editText.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateTime(EditText editText, Calendar calendar){
        String format = "hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        String time;
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 12){
            time = "PM";
        } else {
            time = "AM";
        }
        editText.setText(dateFormat.format(calendar.getTime()) + " " + time);
    }
}