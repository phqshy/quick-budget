package me.fishy.testapp.app.ui.fragment.schedule;

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

import java.util.ArrayList;

import me.fishy.testapp.R;
import me.fishy.testapp.app.recycler.ScheduledRecyclerAdapter;
import me.fishy.testapp.common.holders.UserDataHolder;

public class ScheduledPaymentsFragment extends Fragment {
    public static ScheduledRecyclerAdapter recyclerAdapter = new ScheduledRecyclerAdapter(UserDataHolder.getInstance().getScheduled(), false);

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
}