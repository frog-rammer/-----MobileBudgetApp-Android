package com.example.mobileproject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class AddExpense extends Dialog {
    private Context context;
    public AddExpense(@NonNull Context context) {
        super(context);
        this.context = context;
    }
    protected void OnCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_expense_add);
    }
}