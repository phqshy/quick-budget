package me.fishy.testapp.app.ui.fragment.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import me.fishy.testapp.R;
import me.fishy.testapp.common.notification.NotificationWorker;
import me.fishy.testapp.common.notification.RepeatingTypeEnum;
import me.fishy.testapp.common.holders.UserDataHolder;

public class NewScheduleFragment extends Fragment {
    private Calendar currentCalendar = Calendar.getInstance();
    private RepeatingTypeEnum repeatingType = RepeatingTypeEnum.NEVER;

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
        RadioButton neverRepeat = view.findViewById(R.id.scheduled_dontRepeat);
        RadioButton repeatDaily = view.findViewById(R.id.scheduled_repeatDaily);
        RadioButton repeatMonthly = view.findViewById(R.id.scheduled_repeatMonthly);

        neverRepeat.toggle();

        neverRepeat.setOnClickListener((l) -> repeatingType = RepeatingTypeEnum.NEVER);
        repeatDaily.setOnClickListener((l) -> repeatingType = RepeatingTypeEnum.DAILY);
        repeatMonthly.setOnClickListener((l) -> repeatingType = RepeatingTypeEnum.MONTHLY);

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
            setNotification(this.getContext(), notifReason, "You have a payment worth $" + numberAmount, currentCalendar, code);

            System.out.println("repeating type: " + repeatingType.getMode());

            try{
                JSONObject json = new JSONObject();
                json.put("title", notifReason);
                json.put("text", "You have a payment worth $" + numberAmount);
                json.put("date", currentCalendar.getTimeInMillis());
                json.put("code", code);
                json.put("repeating", repeatingType.getMode());

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
        OneTimeWorkRequest.Builder work = new OneTimeWorkRequest.Builder(NotificationWorker.class);
        Data.Builder data = new Data.Builder();
        data.putString("title", title);
        data.putString("content", content);
        work.setInputData(data.build());
        work.setInitialDelay(calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(), TimeUnit.MILLISECONDS);
        WorkManager.getInstance(context).beginUniqueWork(
                String.valueOf(code),
                ExistingWorkPolicy.REPLACE,
                work.build()
        ).enqueue();
    }

    public static void cancelNotification(Context context, int code){
        WorkManager.getInstance(context).cancelUniqueWork(String.valueOf(code));
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