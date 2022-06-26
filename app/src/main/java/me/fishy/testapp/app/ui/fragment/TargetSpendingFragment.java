package me.fishy.testapp.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.fishy.testapp.R;
import me.fishy.testapp.common.holders.UserDataHolder;

public class TargetSpendingFragment extends Fragment {
    private EditText targetText;
    private TextView spentLabel;

    public TargetSpendingFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_target_spending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            targetText = view.findViewById(R.id.target_number);
            targetText.setText(String.valueOf(UserDataHolder.getInstance().getTargetMonthlyPayments()));

            spentLabel = view.findViewById(R.id.target_spent);
            spentLabel.setText("$" + UserDataHolder.getInstance().getMonthlyPayments());
        } catch (NullPointerException e){
            UserDataHolder.getInstance().setMonthlyPayments(0);
            UserDataHolder.getInstance().setTargetMonthlyPayments(0);

            targetText.setText(String.valueOf(UserDataHolder.getInstance().getTargetMonthlyPayments()));
        }
    }
}
