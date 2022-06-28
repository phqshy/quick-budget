package me.fishy.testapp.app.ui.fragment.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import me.fishy.testapp.R;
import me.fishy.testapp.app.recycler.ScheduledRecyclerAdapter;
import me.fishy.testapp.app.ui.activity.LoginActivity;
import me.fishy.testapp.app.ui.activity.MainActivity;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.get.SessionGetRequest;

public class ScheduledPaymentsFragment extends Fragment {
    public static ScheduledRecyclerAdapter recyclerAdapter = null;

    public ScheduledPaymentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.menuMode = 3;
        System.out.println("called scheduled");
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

        try{
            if (recyclerAdapter == null){
                recyclerAdapter = new ScheduledRecyclerAdapter(UserDataHolder.getInstance().getScheduled(), false);
            }
        } catch (NullPointerException e){

        }

        recycler.setAdapter(recyclerAdapter);
        if (recyclerAdapter.isDirty()){
            ArrayList<JSONObject> newScheduled = new ArrayList<>();
            for (int i = 0; i < recyclerAdapter.jsonList.length(); i++){
                try {
                    newScheduled.add(recyclerAdapter.jsonList.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            UserDataHolder.getInstance().setScheduled(newScheduled);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    public static ArrayList<JSONObject> loadCachedInstance(){
        try{
            String cached = "";
            List<String> lines = Files.readAllLines(Paths.get(MainActivity.cacheDirectory + "/cached_instance.txt"), Charset.defaultCharset());

            for (String s : lines){
                cached = cached.concat(s);
            }
            UserDataHolder.setInstance(UserDataHolder.getGson().fromJson(cached, UserDataHolder.class));

            if (UserDataHolder.getInstance().getScheduled() == null){
                UserDataHolder.getInstance().setScheduled(new ArrayList<>());
            }

            return UserDataHolder.getInstance().getScheduled();
        } catch (IOException | NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.toolbar_payment, menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            ArrayList<JSONObject> json = new ArrayList<>();
            for (int i = 0; i < recyclerAdapter.jsonList.length(); i++){
                json.add(recyclerAdapter.jsonList.getJSONObject(i));
            }
            UserDataHolder.getInstance().setScheduled(json);
        } catch (Exception e){
            Toast.makeText(this.getContext(), "Caught exception while writing to file", Toast.LENGTH_LONG).show();
        }
    }

    public static void clearData(){
        recyclerAdapter = null;
    }

    private void getSession(){
        try{
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