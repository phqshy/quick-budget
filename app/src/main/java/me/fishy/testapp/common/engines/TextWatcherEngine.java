package me.fishy.testapp.common.engines;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class TextWatcherEngine implements TextWatcher {
    private boolean ackChange = false;
    private static ArrayList<TextWatcherEngine> INSTANCES = new ArrayList<>();
    private final String id;
    private final EditText textview;
    private final JSONArray rates;
    private final Spinner spinner;
    private String total = "0";
    private CharSequence before;
    private boolean shouldChange = false;
    private boolean shouldUpdate = false;

    public TextWatcherEngine(String id, EditText textview, JSONArray rates, Spinner spinner) {
        this.textview = textview;
        this.rates = rates;
        this.id = id;
        this.spinner = spinner;
        INSTANCES.add(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        this.before = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (ackChange){
            ackChange = false;
            return;
        }
        update(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public String getId(){
        return this.id;
    }

    public static TextWatcherEngine findInstance(String id){
        for (TextWatcherEngine i : INSTANCES){
            if (i.getId().equals(id)){
                return i;
            }
        }
        return null;
    }

    public void update(CharSequence s){
        //if nothing in the EditText
        if (s.length() == 0) return;

        //check for any non digit characters
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isDigit(c) && c != '.'){
                shouldChange = true;
                return;
            }
        }

        //get spinner and other TextWatcherEngine
        String chosen = spinner.getSelectedItem().toString();
        TextWatcherEngine instance = null;
        switch(this.id){
            case "input_exchange":
                instance = findInstance("output_exchange");
                break;
            case "output_exchange":
                instance = findInstance("input_exchange");
                break;
        }

        //make sure other one isnt null
        if (instance == null) {
            System.err.println("Other instance null!");
            return;
        }

        //find exchange rate JSONObject
        JSONObject currency = null;

        for (int i = 0; i < rates.length(); i++){
            try{
                JSONObject obj = rates.getJSONObject(i);

                if (obj.getDouble(chosen) == 1){
                    currency = obj;
                    break;
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }

        //make sure we actually have an object and get the exchange rate
        double chosenDouble;
        try{
            chosenDouble = currency.getDouble(chosen);
        } catch (NullPointerException | JSONException e) {
            System.err.println("Exchange rate: Invalid rate selected");
            return;
        }

        //do math
        //set amount to rate * amount of money
        double amount = chosenDouble * Double.parseDouble(s.toString());

        //get other TextWatcherEngine's spinner and get the double rate
        Spinner otherSpinner = instance.spinner;
        double rate;
        try{
            rate = currency.getDouble(otherSpinner.getSelectedItem().toString());
        } catch (JSONException e){
            e.printStackTrace();
            return;
        }
        //do more math
        amount *= rate;

        //round to two decimals
        double fillerdub = amount * 100;
        int fillerint = (int) fillerdub;
        amount = (double) fillerint/100;


        //update other text
        instance.setAckChange(true);
        instance.getTextView().setText(String.valueOf(amount));
    }

    public EditText getTextView(){
        return textview;
    }

    public void setAckChange(boolean value){
        this.ackChange = value;
    }

    public static void clearInstances(){
        INSTANCES = new ArrayList<>();
    }
}
