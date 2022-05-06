package me.fishy.testapp.app.ui;

import android.content.Intent;
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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;

import me.fishy.testapp.R;
import me.fishy.testapp.app.recycler.RecyclerAdapter;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.SessionGetRequest;

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

        RecyclerView view = view1.findViewById(R.id.home_recycler);
        view.setLayoutManager(new LinearLayoutManager(this.getContext()));
        view.setHasFixedSize(true);
        RecyclerAdapter adapter = new RecyclerAdapter(new ArrayList<>(), true);
        view.setAdapter(adapter);

        //WARNING- MORE HACKY CODE AHEAD
        if (LoginActivity.didAutoLogin()){
            try {
                System.out.println(LoginActivity.getName());
                System.out.println(LoginActivity.getSession());
                new SessionGetRequest("https://phqsh.me/login_session", LoginActivity.getName(), LoginActivity.getSession())
                        .get()
                        .thenAccept((s) -> {
                            System.out.println(s);
                            if (!(s.equals("Invalid session ID") || s.equals("Internal server error"))){
                                UserDataHolder.setInstance(UserDataHolder.getGson().fromJson(s, UserDataHolder.class));

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
                                    adapter.setData(json);
                                    adapter.notifyDataSetChanged();
                                });
                            } else {
                                try {
                                    PrintWriter pw = new PrintWriter(getActivity().getCacheDir() + "/uuid.txt");
                                    pw.close();

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