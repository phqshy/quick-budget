package me.fishy.testapp.app.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.fishy.testapp.R;
import me.fishy.testapp.app.recycler.RecyclerAdapter;

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

    @Override
    public void onViewCreated(@NonNull View view1, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view1, savedInstanceState);
        RecyclerView view = view1.findViewById(R.id.home_recycler);
        view.setLayoutManager(new LinearLayoutManager(this.getContext()));
        view.setHasFixedSize(true);
        ArrayList<JSONObject> json = new ArrayList<>();
        try {
            JSONObject jsontest = new JSONObject();
            jsontest.put("title", "test")
                    .put("text", "this is a test");
            json.add(jsontest);
            jsontest = new JSONObject();
            jsontest.put("title", "number 2")
                    .put("text", "this is the second one");
            json.add(jsontest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        view.setAdapter(new RecyclerAdapter(json, true));
    }
}