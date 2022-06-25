package me.fishy.testapp.app.recycler;

import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.fishy.testapp.R;
import me.fishy.testapp.app.ui.fragment.schedule.NewScheduleFragment;
import me.fishy.testapp.common.engines.OnSwipeTouchEngine;
import me.fishy.testapp.common.holders.UserDataHolder;

public class ScheduledRecyclerAdapter extends RecyclerView.Adapter<ScheduledRecyclerAdapter.ViewHolder> {
    public JSONArray jsonList = new JSONArray();
    protected final boolean shouldSwipe;
    private boolean isDirty = false;

    public ScheduledRecyclerAdapter(ArrayList<JSONObject> json){
        try{
            for (JSONObject i : json){
                jsonList.put(i);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        } finally {
            shouldSwipe = false;
        }
    }

    public ScheduledRecyclerAdapter(ArrayList<JSONObject> json, boolean shouldSwipe){
        try{
            for (JSONObject i : json){
                jsonList.put(i);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            jsonList = new JSONArray();
        } finally{
            this.shouldSwipe = shouldSwipe;
        }
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

    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public boolean isDirty() {
        return isDirty;
    }

    @NonNull
    @Override
    public ScheduledRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bbvscheduled_layout, parent, false);
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
            holder.setDate(pos.getLong("date"));
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
        private Calendar time;

        public ViewHolder(View v) {
            super(v);
            this.view = v;
            this.title = view.findViewById(R.id.titleBox);
            this.text = view.findViewById(R.id.textBox);

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String format = "MM/dd/yy hh:mm";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
                    String pmoram;

                    if (time.get(Calendar.HOUR_OF_DAY) >= 12){
                        pmoram = "PM";
                    } else {
                        pmoram = "AM";
                    }

                    Toast.makeText(view.getContext(), dateFormat.format(time.getTime()) + " " + pmoram, Toast.LENGTH_SHORT).show();
                }
            });

            if (shouldSwipe){
                this.view.setOnTouchListener(new OnSwipeTouchEngine(view.getContext()){
                    @Override
                    public void onSwipeRight(){
                        jsonList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    }
                });
            }

            view.findViewById(R.id.bbvscheduled_x).setOnClickListener((yes) -> {
                try{
                    JSONObject j = jsonList.getJSONObject(getAdapterPosition());
                    jsonList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    ArrayList<JSONObject> userData = UserDataHolder.getInstance().getScheduled();
                    userData.remove(j);
                    UserDataHolder.getInstance().setScheduled(userData);

                    NewScheduleFragment.cancelNotification(view.getContext(), j.getInt("code"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }

        public View getCustomView() {
            return view;
        }

        public void setDate(long time){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            this.time = calendar;
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setText(String text) {
            this.text.setText(text);
        }
    }
}
