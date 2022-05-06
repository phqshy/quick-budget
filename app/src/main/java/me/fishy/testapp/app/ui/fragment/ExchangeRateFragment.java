package me.fishy.testapp.app.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.fishy.testapp.R;
import me.fishy.testapp.common.engines.TextWatcherEngine;
import me.fishy.testapp.common.request.ExchangeGetRequest;

public class ExchangeRateFragment extends Fragment {
    private JSONArray exchangeJson = new JSONArray();
    private EditText inputText;
    private EditText outputText;
    private Spinner inputSpinner;
    private Spinner outputSpinner;

    public ExchangeRateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exchange, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextWatcherEngine.clearInstances();

        if (exchangeJson.length() == 0){
            try{
                new ExchangeGetRequest("https://phqsh.me/exchange/usd", getContext()).get().thenAcceptAsync((e) -> {
                    try {
                        JSONObject usdJSON = new JSONObject(e);
                        usdJSON = usdJSON.getJSONObject("conversion_rates");
                        exchangeJson.put(usdJSON);
                        System.out.println("usd gotten");
                    } catch (JSONException jsonException) {
                        Toast.makeText(getContext(), "Failed to load exchange rates", Toast.LENGTH_LONG).show();
                        jsonException.printStackTrace();
                    }
                });

                new ExchangeGetRequest("https://phqsh.me/exchange/jpy", getContext()).get().thenAcceptAsync((e) -> {
                    try {
                        JSONObject usdJSON = new JSONObject(e);
                        usdJSON = usdJSON.getJSONObject("conversion_rates");
                        exchangeJson.put(usdJSON);
                        System.out.println("jpy gotten");
                    } catch (JSONException jsonException) {
                        Toast.makeText(getContext(), "Failed to load exchange rates", Toast.LENGTH_LONG).show();
                        jsonException.printStackTrace();
                    }
                });

                new ExchangeGetRequest("https://phqsh.me/exchange/gbp", getContext()).get().thenAcceptAsync((e) -> {
                    try {
                        JSONObject usdJSON = new JSONObject(e);
                        usdJSON = usdJSON.getJSONObject("conversion_rates");
                        exchangeJson.put(usdJSON);
                        System.out.println("gbp gotten");
                    } catch (JSONException jsonException) {
                        Toast.makeText(getContext(), "Failed to load exchange rates", Toast.LENGTH_LONG).show();
                        jsonException.printStackTrace();
                    }
                });

                new ExchangeGetRequest("https://phqsh.me/exchange/eur", getContext()).get().thenAcceptAsync((e) -> {
                    try {
                        JSONObject usdJSON = new JSONObject(e);
                        usdJSON = usdJSON.getJSONObject("conversion_rates");
                        exchangeJson.put(usdJSON);
                        System.out.println(exchangeJson + "\n exchange json");
                    } catch (JSONException jsonException) {
                        Toast.makeText(getContext(), "Failed to load exchange rates", Toast.LENGTH_LONG).show();
                        jsonException.printStackTrace();
                    }
                });

                initViews(view, exchangeJson);
            } catch (IOException e){
                Toast.makeText(getContext(), "Failed to load exchange rates", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void initViews(View view, JSONArray array){
        this.inputText = view.findViewById(R.id.exchange_input);
        this.outputText = view.findViewById(R.id.exchange_input_out);
        this.inputSpinner = view.findViewById(R.id.exchange_options);
        this.outputSpinner = view.findViewById(R.id.exchange_options_out);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.exchange_spinner_options, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        inputSpinner.setAdapter(adapter);
        outputSpinner.setAdapter(adapter);

        inputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextWatcherEngine instance = TextWatcherEngine.findInstance("output_exchange");
                instance.update(instance.getTextView().getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        outputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextWatcherEngine instance = TextWatcherEngine.findInstance("input_exchange");
                instance.update(instance.getTextView().getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        inputText.addTextChangedListener(new TextWatcherEngine("input_exchange", inputText, array, inputSpinner));
        outputText.addTextChangedListener(new TextWatcherEngine("output_exchange", outputText, array, outputSpinner));
    }
}