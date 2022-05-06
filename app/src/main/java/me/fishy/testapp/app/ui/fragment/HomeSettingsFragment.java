package me.fishy.testapp.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import me.fishy.testapp.R;
import me.fishy.testapp.app.ui.activity.LoginActivity;

public class HomeSettingsFragment extends Fragment {
    private static boolean enabled = false;

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
            try {
                PrintWriter pw = new PrintWriter(getActivity().getCacheDir() + "/uuid.txt");
                pw.close();

                Intent intent = new Intent(this.getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                System.out.println("forced credential reset (aka a logout)");
                startActivity(intent);
                this.getActivity().finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean isEnabled(){
        return enabled;
    }

    public static void setEnabled(boolean value){
        enabled = value;
    }
}