package com.sagittariusk2.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;

public class TimePickerDialoge {
    private Context context;

    public TimePickerDialoge(Context context) {
        this.context = context;
    }

    protected interface OnClickListener {
        void onSet(int hour, int minute);
        void onCancel();
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void show() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.timepicker_dialoge, null);
        TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton("Set", (dialogInterface, i) -> {
            onClickListener.onSet(timePicker.getHour(), timePicker.getMinute());
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> onClickListener.onCancel());
        builder.show();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
