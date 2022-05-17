package me.fishy.testapp.app.ui.fragment;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ThreadLocalRandom;

import me.fishy.testapp.R;
import me.fishy.testapp.common.engines.ScheduledManager;
import me.fishy.testapp.common.holders.UserDataHolder;

public class PaymentsAddFragment extends Fragment {
    private static boolean isEnabled = false;

    public PaymentsAddFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payments_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isEnabled = true;

        view.findViewById(R.id.create_payment_button).setOnClickListener((l) -> {
            try {
                EditText amount = view.findViewById(R.id.payments_amount);
                EditText reason = view.findViewById(R.id.payments_reason);

                if (amount.getText().length() <= 0 || reason.getText().length() <= 0) {
                    Toast.makeText(getContext(), "Fill in all of the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject json = new JSONObject();
                double numberAmount = Double.parseDouble(amount.getText().toString());
                numberAmount = numberAmount * 100;
                int filler = (int) numberAmount;
                numberAmount = (double) filler / 100;

                json.put("title", "$" + numberAmount);
                json.put("text", reason.getText().toString());

                PaymentsFragment.addToArray(json);
                UserDataHolder.getInstance().addToBalance(numberAmount);

                isEnabled = false;

                NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                navHostFragment.getNavController().navigate(R.id.action_paymentsAddFragment_to_paymentsFragment);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean isIsEnabled() {
        return isEnabled;
    }
}