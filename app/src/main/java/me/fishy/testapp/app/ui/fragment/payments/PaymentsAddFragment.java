package me.fishy.testapp.app.ui.fragment.payments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;

import me.fishy.testapp.R;
import me.fishy.testapp.app.ui.activity.LoginActivity;
import me.fishy.testapp.app.ui.activity.MainActivity;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.get.SessionGetRequest;

public class PaymentsAddFragment extends Fragment {
    private static boolean isEnabled = false;

    private String title = null;
    private String text = null;

    public PaymentsAddFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            title = getArguments().getString("title");
            text = getArguments().getString("text").split("\\$")[1];

            if (title != null && text != null) {
                getSession();
            }
        } catch (NullPointerException e) {
            System.out.println("its null lmao");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payments_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText amount = view.findViewById(R.id.payments_amount);
        EditText reason = view.findViewById(R.id.payments_reason);

        isEnabled = true;
        if (title != null) {
            reason.setText(title);
        }
        if (text != null) {
            amount.setText(text);
        }

        view.findViewById(R.id.create_payment_button).setOnClickListener((l) -> {
            try {
                if (amount.getText().length() <= 0 || reason.getText().length() <= 0) {
                    Toast.makeText(getContext(), "Fill in all of the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject json = new JSONObject();
                double numberAmount = Double.parseDouble(amount.getText().toString());
                numberAmount = numberAmount * 100;
                int filler = (int) numberAmount;
                numberAmount = (double) filler / 100;

                if (numberAmount + UserDataHolder.getInstance().getMonthlyPayments() > UserDataHolder.getInstance().getTargetMonthlyPayments()) {
                    Toast.makeText(this.getContext(), "You are now over your target spending!", Toast.LENGTH_LONG).show();
                    json.put("title", "$" + numberAmount);
                    json.put("text", reason.getText().toString());

                    PaymentsFragment.addToArray(json);
                    UserDataHolder.getInstance().addToBalance(numberAmount);
                    UserDataHolder.getInstance().addToMonthlyPayments(numberAmount);

                    isEnabled = false;

                    title = null;
                    text = null;

                    NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    navHostFragment.getNavController().navigate(R.id.action_paymentsAddFragment_to_paymentsFragment);
                } else{
                    json.put("title", "$" + numberAmount);
                    json.put("text", reason.getText().toString());

                    PaymentsFragment.addToArray(json);
                    UserDataHolder.getInstance().addToBalance(numberAmount);
                    UserDataHolder.getInstance().addToMonthlyPayments(numberAmount);

                    isEnabled = false;

                    title = null;
                    text = null;

                    NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    navHostFragment.getNavController().navigate(R.id.action_paymentsAddFragment_to_paymentsFragment);
                }
        } catch(JSONException e){
            e.printStackTrace();
        }

        });
}

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_payment, menu);
    }

    public static boolean isIsEnabled() {
        return isEnabled;
    }

    private void getSession() {
        try {
            new SessionGetRequest("https://phqsh.me/login_session", LoginActivity.getName(), LoginActivity.getSession())
                    .get()
                    .thenAccept((s) -> {
                        System.out.println(s);
                        if (!(s.equals("Invalid session ID") || s.equals("Internal server error"))) {
                            UserDataHolder.setInstance(UserDataHolder.getGson().fromJson(s, UserDataHolder.class));

                            if (UserDataHolder.getInstance().getScheduled() == null) {
                                UserDataHolder.getInstance().setScheduled(new ArrayList<>());
                            }

                        } else {
                            try {
                                PrintWriter pw = new PrintWriter(getActivity().getCacheDir() + "/uuid.txt");
                                pw.close();

                                UserDataHolder.setInstance(null);

                                Intent intent = new Intent(this.getActivity(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                System.out.println("invalid credentials, moving to log in");
                                startActivity(intent);
                                this.getActivity().finish();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}