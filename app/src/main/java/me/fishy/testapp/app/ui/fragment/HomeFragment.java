package me.fishy.testapp.app.ui.fragment;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.fishy.testapp.R;
import me.fishy.testapp.app.recycler.RecyclerAdapter;
import me.fishy.testapp.app.ui.activity.LoginActivity;
import me.fishy.testapp.app.ui.fragment.schedule.NewScheduleFragment;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.get.SessionGetRequest;

public class HomeFragment extends Fragment {
    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    //keep collapsed, disgusting code
    //we catch up on synchronization here lmao
    @Override
    public void onViewCreated(@NonNull View view1, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view1, savedInstanceState);

        if (HomeSettingsFragment.isEnabled()) HomeSettingsFragment.setEnabled(false);

        RecyclerView view = view1.findViewById(R.id.home_recycler);
        view.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RecyclerAdapter adapter = new RecyclerAdapter(new ArrayList<>(), true);
        view.setAdapter(adapter);

        //WARNING- MORE HACKY CODE AHEAD
        if (LoginActivity.didAutoLogin()){
            try {
                File f = new File(getActivity().getCacheDir() + "/cached_instance.txt");

                if (f.exists()){
                    String cached = "";
                    List<String> lines = Files.readAllLines(Paths.get(getActivity().getCacheDir() + "/cached_instance.txt"), Charset.defaultCharset());

                    for (String s : lines){
                        cached = cached.concat(s);
                    }

                    if (!cached.equals("null")){
                        UserDataHolder.setInstance(UserDataHolder.getGson().fromJson(cached, UserDataHolder.class));

                        ArrayList<JSONObject> initJSON = new ArrayList<>();
                        JSONObject json1 = new JSONObject();
                        json1.put("title", "Your balance")
                                .put("text", "$" + UserDataHolder.getInstance().getBalance());
                        initJSON.add(json1);
                        adapter.setData(initJSON);
                        adapter.notifyDataSetChanged();
                    }


                }

                System.out.println(LoginActivity.getName());
                System.out.println(LoginActivity.getSession());

                new SessionGetRequest("https://phqsh.me/login_session", LoginActivity.getName(), LoginActivity.getSession())
                        .get()
                        .thenAccept((s) -> {
                            System.out.println(s);
                            if (!(s.equals("Invalid session ID") || s.equals("Internal server error"))){
                                UserDataHolder.setInstance(UserDataHolder.getGson().fromJson(s, UserDataHolder.class));

                                if (UserDataHolder.getInstance().getScheduled() == null){
                                    UserDataHolder.getInstance().setScheduled(new ArrayList<>());
                                }

                                //register notifs
                                for (JSONObject j : UserDataHolder.getInstance().getScheduled()){
                                    try {
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(new Date(j.getLong("date")));
                                        if (cal.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()){
                                            UserDataHolder.getInstance().getScheduled().remove(j);
                                            continue;
                                        }
                                        NewScheduleFragment.setNotification(this.getContext(), j.getString("title"), j.getString("text"), cal, j.getInt("code"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //init home recycler view
                                ArrayList<JSONObject> json = new ArrayList<>();
                                try {
                                    JSONObject jsontest = new JSONObject();
                                    jsontest.put("title", "Your balance")
                                            .put("text", "$" + UserDataHolder.getInstance().getBalance());
                                    json.add(jsontest);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                System.out.println(json);
                                getActivity().runOnUiThread(() -> {
                                    adapter.replaceData(json);
                                    adapter.notifyDataSetChanged();
                                });
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
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            ArrayList<JSONObject> json = new ArrayList<>();
            try {
                JSONObject jsontest = new JSONObject();
                jsontest.put("title", "Your balance")
                        .put("text", "$" + UserDataHolder.getInstance().getBalance());
                json.add(jsontest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter.setData(json);
        }


    }
}