package me.fishy.testapp.common.engines;

import android.text.Editable;
import android.text.TextWatcher;

import me.fishy.testapp.common.holders.UserDataHolder;

public class TargetWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        try{
            UserDataHolder.getInstance().setTargetMonthlyPayments(Double.parseDouble(editable.toString()));
        } catch (NumberFormatException e){
            UserDataHolder.getInstance().setTargetMonthlyPayments(0);
        }
    }
}
