package me.fishy.testapp.app.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import me.fishy.testapp.R;
import me.fishy.testapp.app.recycler.RecyclerAdapter;
import me.fishy.testapp.common.holders.UserDataHolder;

public class PaymentsFragment extends Fragment {
    private static ArrayList<JSONObject> array = new ArrayList<>();
    private static RecyclerAdapter adapter;

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

        AppCompatActivity parent = (AppCompatActivity) getActivity();
        parent.getSupportActionBar().setTitle("$" + UserDataHolder.getInstance().getBalance());

        RecyclerView recycler =  view.findViewById(R.id.payments_recycler);

        recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recycler.setHasFixedSize(true);

        try{
            ArrayList<JSONObject> payments = UserDataHolder.getInstance().getPayments();

            for (int i = payments.size() - 1; i >= 0; i--){
                if (array.contains(payments.get(i))) continue;
                array.add(payments.get(i));
            }
        } catch (NullPointerException e){
            UserDataHolder.getInstance().setPayments(new ArrayList<>());
        }

        adapter = new RecyclerAdapter(array);
        recycler.setAdapter(adapter);
    }

    public static void addToArray(JSONObject o){
        array.add(0, o);
        adapter.notifyItemInserted(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        saveData();
    }

    @Override
    public void onPause() {
        super.onPause();

        saveData();
    }

    private void saveData(){
        System.out.println("Attempting to save data!");

        ArrayList<JSONObject> payments = UserDataHolder.getInstance().getPayments();
        try {
            System.out.println(payments.size());
        } catch (NullPointerException e){
            payments = new ArrayList<>();
        }

        for (JSONObject i : array){
            if (payments.contains(i)) continue;

            payments.add(i);
        }

        UserDataHolder.getInstance().setPayments(payments);
    }
}