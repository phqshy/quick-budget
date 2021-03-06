package me.fishy.testapp.app.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.fishy.testapp.R;
import me.fishy.testapp.common.engines.OnSwipeTouchEngine;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    public JSONArray jsonList = new JSONArray();
    protected final boolean shouldSwipe;

    public RecyclerAdapter(ArrayList<JSONObject> json){
        for (JSONObject i : json){
            jsonList.put(i);
        }

        shouldSwipe = false;
    }

    public RecyclerAdapter(ArrayList<JSONObject> json, boolean shouldSwipe){
        for (JSONObject i : json){
            jsonList.put(i);
        }

        this.shouldSwipe = shouldSwipe;
    }

    public void setData(ArrayList<JSONObject> array){
        for (JSONObject i : array){
            jsonList.put(i);

        }
    }

    public void replaceData(ArrayList<JSONObject> array){
        jsonList = new JSONArray();
        for (JSONObject i : array){
            jsonList.put(i);

        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.boxview_layout, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject pos = jsonList.getJSONObject(position);
            holder.setText(pos.getString("text"));
            holder.setTitle(pos.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView text;
        private final View view;

        public ViewHolder(View v) {
            super(v);
            this.view = v;
            this.title = view.findViewById(R.id.titleBox);
            this.text = view.findViewById(R.id.textBox);
            if (shouldSwipe){
                this.view.setOnTouchListener(new OnSwipeTouchEngine(view.getContext()){
                    @Override
                    public void onSwipeRight(){
                        jsonList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    }
                });
            }
        }

        public View getCustomView() {
            return view;
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setText(String text) {
            this.text.setText(text);
        }
    }
}
