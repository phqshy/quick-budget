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

public class PaymentsFragment extends Fragment {
    public PaymentsFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler =  view.findViewById(R.id.payments_recycler);

        recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recycler.setHasFixedSize(true);
        ArrayList<JSONObject> json = new ArrayList<>();

        try {
            JSONObject jsontest = new JSONObject();
            jsontest.put("title", "-$500")
                    .put("text", "Spent money on TSA");
            json.add(jsontest);
            jsontest = new JSONObject();
            jsontest.put("title", "+$1000")
                    .put("text", "Sugar daddy gave me money");
            json.add(jsontest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recycler.setAdapter(new RecyclerAdapter(json));
    }
}