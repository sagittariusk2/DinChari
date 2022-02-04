package com.sagittariusk2.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import com.anggastudio.spinnerpickerdialog.SpinnerPickerDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.sagittariusk2.ui.DataModel.RepeatedTask;
import com.sagittariusk2.ui.DataModel.SimpleTask;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.UUID;

import at.markushi.ui.CircleButton;

public class UIClass extends AppCompatActivity {

    AppCompatEditText editText_taskName, editText_message, editText_taskLink;
    AppCompatTextView textView_date, textView_start_time, textView_end_time;
    CircleButton button_date, button_start_time, button_end_time;
    AppCompatSpinner spinner_notify, spinner_repeat;
    RadioGroup radio_group_category;
    MaterialRadioButton radio_work, radio_personal, radio_health;
    HorizontalScrollView week_container;
    MaterialCheckBox check_sun, check_mon, check_tue, check_wed, check_thu, check_fri, check_sat;
    Button button_schedule_task;

    String taskRepeat = "", taskScheduledTime, taskCompleteTime, weekdays, category="work";
    LocalDate taskScheduledDate;
    int[] notify_arr = {15, 20, 30, 45, 60};
    String[] repeat_arr = {":::no-repeat:::", "daily", "weekly", "monthly", "yearly"};
    String[] cate_arr = {"work", "personal", "health"};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_task);

        {
            editText_taskName = findViewById(R.id.editText_taskName);
            editText_message = findViewById(R.id.editText_message);
            editText_taskLink = findViewById(R.id.editText_taskLink);
            textView_date = findViewById(R.id.textView_date);
            textView_start_time = findViewById(R.id.textView_start_time);
            textView_end_time = findViewById(R.id.textView_end_time);
            button_date = findViewById(R.id.button_date);
            button_start_time = findViewById(R.id.button_start_time);
            button_end_time = findViewById(R.id.button_end_time);
            spinner_notify = findViewById(R.id.spinner_notify);
            spinner_repeat = findViewById(R.id.spinner_repeat);
            radio_group_category = findViewById(R.id.radio_group_category);
            radio_work = findViewById(R.id.radio_work);
            radio_personal = findViewById(R.id.radio_personal);
            radio_health = findViewById(R.id.radio_health);
            week_container = findViewById(R.id.week_container);
            check_sun = findViewById(R.id.check_sun);
            check_mon = findViewById(R.id.check_mon);
            check_tue = findViewById(R.id.check_tue);
            check_wed = findViewById(R.id.check_wed);
            check_thu = findViewById(R.id.check_thu);
            check_fri = findViewById(R.id.check_fri);
            check_sat = findViewById(R.id.check_sat);
            button_schedule_task = findViewById(R.id.button_schedule_task);
        }

        LocalTime localTime = LocalTime.now();
        textView_start_time.setText(String.valueOf(localTime.getHour())+':'+localTime.getMinute());
        textView_end_time.setText(String.valueOf(localTime.plusHours(1).getHour())+':'+localTime.plusHours(1).getMinute());
        taskScheduledDate = LocalDate.now();
        textView_date.setText(String.valueOf(taskScheduledDate));

        button_start_time.setOnClickListener(view -> {
            final TimePickerDialoge timePickerDialoge = new TimePickerDialoge(this);
            timePickerDialoge.setOnClickListener(new TimePickerDialoge.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSet(int hour, int minute) {
                    textView_start_time.setText(String.valueOf(hour)+':'+minute);
                }

                @Override
                public void onCancel() {

                }
            });
            timePickerDialoge.show();
        });

        button_end_time.setOnClickListener(view -> {
            final TimePickerDialoge timePickerDialoge = new TimePickerDialoge(this);
            timePickerDialoge.setOnClickListener(new TimePickerDialoge.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSet(int hour, int minute) {
                    String m = String.valueOf(minute);
                    if(m.length()==1) m = '0'+m;
                    textView_end_time.setText(String.valueOf(hour)+':'+m);
                }

                @Override
                public void onCancel() {

                }
            });
            timePickerDialoge.show();
        });

        button_date.setOnClickListener(view -> {
                final SpinnerPickerDialog spinnerPickerDialog = new SpinnerPickerDialog();
                spinnerPickerDialog.setContext(UIClass.this);
                spinnerPickerDialog.setOnDialogListener(new SpinnerPickerDialog.OnDialogListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSetDate(int month, int day, int year) {
                        check_sun.setChecked(false);
                        check_sun.setEnabled(true);
                        check_mon.setChecked(false);
                        check_mon.setEnabled(true);
                        check_tue.setChecked(false);
                        check_tue.setEnabled(true);
                        check_wed.setChecked(false);
                        check_wed.setEnabled(true);
                        check_thu.setChecked(false);
                        check_thu.setEnabled(true);
                        check_fri.setChecked(false);
                        check_fri.setEnabled(true);
                        check_sat.setChecked(false);
                        check_sat.setEnabled(true);
                        taskScheduledDate = LocalDate.of(year, month+1, day);
                        textView_date.setText(taskScheduledDate.toString());
                        if(taskScheduledDate.getDayOfWeek().toString().equalsIgnoreCase("sunday")) {
                            check_sun.setChecked(true);
                            check_sun.setEnabled(false);
                        } else if(taskScheduledDate.getDayOfWeek().toString().equalsIgnoreCase("monday")) {
                            check_mon.setChecked(true);
                            check_mon.setEnabled(false);
                        } else if(taskScheduledDate.getDayOfWeek().toString().equalsIgnoreCase("tuesday")) {
                            check_tue.setChecked(true);
                            check_tue.setEnabled(false);
                        } else if(taskScheduledDate.getDayOfWeek().toString().equalsIgnoreCase("wednesday")) {
                            check_wed.setChecked(true);
                            check_wed.setEnabled(false);
                        } else if(taskScheduledDate.getDayOfWeek().toString().equalsIgnoreCase("thursday")) {
                            check_thu.setChecked(true);
                            check_thu.setEnabled(false);
                        } else if(taskScheduledDate.getDayOfWeek().toString().equalsIgnoreCase("friday")) {
                            check_fri.setChecked(true);
                            check_fri.setEnabled(false);
                        } else if(taskScheduledDate.getDayOfWeek().toString().equalsIgnoreCase("saturday")) {
                            check_sat.setChecked(true);
                            check_sat.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onDismiss() {

                    }

                });
                spinnerPickerDialog.show(getSupportFragmentManager(), "");
        });

        spinner_repeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                week_container.setVisibility(View.GONE);
                taskRepeat = repeat_arr[i];
                if(i==2) {
                    week_container.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        radio_group_category.setOnCheckedChangeListener((radioGroup, i) -> category = cate_arr[i]);

        button_schedule_task.setOnClickListener(view -> {
            gatherTaskInfo();
        });
    }

    private void gatherTaskInfo() {
        String taskName = Objects.requireNonNull(editText_taskName.getText()).toString();
        String taskDesc = Objects.requireNonNull(editText_message.getText()).toString();
        String taskLink = Objects.requireNonNull(editText_taskLink.getText()).toString();
        taskScheduledTime = Objects.requireNonNull(textView_start_time.getText()).toString();
        taskCompleteTime = Objects.requireNonNull(textView_end_time.getText()).toString();
        int taskNotify = notify_arr[spinner_notify.getSelectedItemPosition()];
        String id = UUID.randomUUID().toString();
        if(spinner_repeat.getSelectedItemPosition()==0) {
            SimpleTask simpleTask = new SimpleTask(id,
                    taskName,
                    taskDesc,
                    taskLink,
                    false,
                    taskScheduledDate,
                    taskScheduledTime,
                    taskNotify,
                    taskCompleteTime);
            simpleTask.setTaskCategory(category);
            updateToDataBase(simpleTask);
        } else {
            if(check_sun.isChecked()) weekdays += "sunday ";
            if(check_mon.isChecked()) weekdays += "monday ";
            if(check_tue.isChecked()) weekdays += "tuesday ";
            if(check_wed.isChecked()) weekdays += "wednesday ";
            if(check_thu.isChecked()) weekdays += "thursday ";
            if(check_fri.isChecked()) weekdays += "friday ";
            if(check_sat.isChecked()) weekdays += "saturday ";
            RepeatedTask repeatedTask = new RepeatedTask(id,
                    taskName, //name of task
                    taskDesc,
                    taskLink,
                    false,
                    taskScheduledDate,
                    taskScheduledTime,
                    taskNotify,
                    taskCompleteTime,
                    taskScheduledDate,
                    taskRepeat,
                    weekdays);
            repeatedTask.setTaskCategory(category);
            updateToDataBase(repeatedTask);
        }
    }

    private void updateToDataBase(SimpleTask simpleTask) {

    }

    private void updateToDataBase(RepeatedTask repeatedTask) {
    }
}
