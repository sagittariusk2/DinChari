package com.sagittariusk2.dinchari;

import static com.google.android.material.timepicker.TimeFormat.CLOCK_12H;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sagittariusk2.dinchari.DataModel.SimpleTask;
import com.sagittariusk2.dinchari.DataModel.User;
import com.sagittariusk2.dinchari.databinding.ActivityCreateTaskBinding;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class AddTaskActivity extends AppCompatActivity {

    private ActivityCreateTaskBinding binding;

    String category = "personal";
    int[] notify_arr = {5, 10, 15, 30, 45, 60};
    String[] repeat_arr = {":::no-repeat:::", "daily", "weekly", "monthly", "yearly"};
    DatabaseReference dbReference;

    public static final String channel = "DINCHARI_NOTIFICATION_CHANNEL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateTaskBinding.inflate(getLayoutInflater());
        View parentView = binding.getRoot();
        setContentView(parentView);

        SharedPreferences dinChariCurrentUser = getSharedPreferences("DinChariCurrentUser", MODE_PRIVATE);
        String x = dinChariCurrentUser.getString("userName", "demoName");
        String y = dinChariCurrentUser.getString("userPhone", "demoPhone");
        User globalUser = new User(x, y);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        dbReference = firebaseDatabase.getReference().child("user").child(globalUser.getUserPhone());
        dbReference.keepSynced(true);

        // Create Notification in Android System
        createNotificationChannel();

        // Selecting Date
        binding.dateTextView.setOnClickListener(v -> selectDate());
        binding.dateSelectButton.setOnClickListener(v -> selectDate());

        // Selecting Start Time
        binding.buttonStartTime.setOnClickListener(v -> selectStartTime());
        binding.textViewStartTime.setOnClickListener(v -> selectStartTime());

        // Selecting End Time
        binding.buttonEndTime.setOnClickListener(v -> selectEndTime());
        binding.textViewEndTime.setOnClickListener(v -> selectEndTime());

        // Getting the category
        binding.radioGroupCategory.setOnCheckedChangeListener((radioGroup, id) -> category = ((MaterialRadioButton) findViewById(id)).getText().toString());

        // Schedule Button
        binding.buttonScheduleTask.setOnClickListener(view -> gatherTaskInfo());
    }

    private void gatherTaskInfo() {
        String taskName = Objects.requireNonNull(binding.editTextTaskName.getText()).toString();
        if(taskName.isEmpty()) {
            binding.editTextTaskName.setError("Enter a name!!!");
            return;
        }
        if(binding.dateTextView.getText().toString().isEmpty()) {
            errorSnack("Pick A date!!!");
            return;
        }
        String taskScheduledDate = Objects.requireNonNull(String.valueOf(decodeDate(binding.dateTextView.getText().toString())));
        String taskScheduledTime = Objects.requireNonNull(binding.textViewStartTime.getText()).toString();
        String taskCompleteTime = Objects.requireNonNull(binding.textViewEndTime.getText()).toString();
        int taskNotify = notify_arr[binding.spinnerNotify.getSelectedItemPosition()];
        String taskRepeat = repeat_arr[binding.spinnerRepeat.getSelectedItemPosition()];
        String taskDesc = Objects.requireNonNull(binding.editTextMessage.getText()).toString();
        String taskLink = Objects.requireNonNull(binding.editTextTaskLink.getText()).toString();

        if(taskScheduledTime.isEmpty()) {
            errorSnack("Pick a start Time");
            return;
        }

        if(taskCompleteTime.isEmpty()) {
            errorSnack("Pick a end Time");
            return;
        }

        if(taskDesc.isEmpty()) {
            binding.editTextMessage.setError("Enter a description!!!");
            return;
        }

        String id = UUID.randomUUID().toString();
        SimpleTask simpleTask = new SimpleTask(id,
                taskName, //name of task
                taskDesc,
                taskLink,
                category,
                false,
                taskScheduledDate,
                taskScheduledTime,
                taskCompleteTime,
                taskNotify,
                taskRepeat,
                System.currentTimeMillis());
        updateToDataBase(simpleTask);
    }

    private void updateToDataBase(SimpleTask simpleTask) {
        dbReference.child("simpleTask").child(simpleTask.getTaskID()).setValue(simpleTask);
        Calendar calendar1 = Calendar.getInstance();
        String[] x = simpleTask.getTaskScheduleTime().split(":");
        calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(x[0]));
        calendar1.set(Calendar.MINUTE, Integer.parseInt(x[1]));
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        setAlarm(simpleTask, calendar1);
    }

    private void setAlarm(SimpleTask simpleTask, Calendar calendar1) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("notificationID", simpleTask.getNotificationID());
        intent.putExtra("taskID", simpleTask.getTaskID());
        intent.putExtra("taskCategory", simpleTask.getTaskCategory());
        intent.putExtra("taskDesc", simpleTask.getTaskDesc());
        intent.putExtra("taskName", simpleTask.getTaskName());
        intent.putExtra("date", simpleTask.getTaskScheduleDate());
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                (int)simpleTask.getNotificationID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis()-((long) simpleTask.getTaskNotifyBefore() *60000), pendingIntent);

        successSnack();
        new Handler().postDelayed(() -> {
            Intent intent1 = new Intent(AddTaskActivity.this, TaskViewShowActivity.class);
            intent1.putExtra("date", simpleTask.getTaskScheduleDate());
            startActivity(intent1);
            finish();
        }, 3000);
    }

    private void createNotificationChannel() {
        CharSequence name = "ReminderChannel";
        String description = "Channel description";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(channel, name, importance);
        notificationChannel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void successSnack() {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Task Scheduled Successfully. We will remind you well in advance", Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.GREEN);
        snackbar.show();
    }

    private void errorSnack(String message) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.RED);
        snackbar.show();
    }

    private LocalDate decodeDate(@NonNull String toString) {
        if(toString.equalsIgnoreCase("Today")) {
            return LocalDate.now();
        }
        if(toString.equalsIgnoreCase("Yesterday")) {
            return LocalDate.now().minusDays(1);
        }
        if(toString.equalsIgnoreCase("Tomorrow")) {
            return LocalDate.now().plusDays(1);
        }
        String[] x = toString.split(" ");
        HashMap<String, Integer> mpp = new HashMap<>();
        mpp.put("Jan", 1);
        mpp.put("Feb", 2);
        mpp.put("Mar", 3);
        mpp.put("Apr", 4);
        mpp.put("May", 5);
        mpp.put("Jun", 6);
        mpp.put("Jul", 7);
        mpp.put("Aug", 8);
        mpp.put("Sep", 9);
        mpp.put("Oct", 10);
        mpp.put("Nov", 11);
        mpp.put("Dec", 12);
        return LocalDate.of(Integer.parseInt(x[2]), mpp.get(x[1]), Integer.parseInt(x[0]));
    }

    private String encodeDate(LocalDate localDate) {
        if(localDate.equals(LocalDate.now())) {
            return "Today";
        }
        if(localDate.equals(LocalDate.now().plusDays(1))) {
            return "Tomorrow";
        }
        if(localDate.equals(LocalDate.now().minusDays(1))) {
            return "Yesterday";
        }
        String x = localDate.getMonth().toString().substring(0, 1).toUpperCase();
        String y = localDate.getMonth().toString().substring(1, 3).toLowerCase();
        return ""+ (localDate.getDayOfMonth()) +" "+x+y+" "+localDate.getYear();
    }

    private void selectEndTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(CLOCK_12H)
                .setHour(hour)
                .setMinute(minute)
                .build();

        materialTimePicker.show(getSupportFragmentManager(), "endTimeTag");
        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            int hour1 = materialTimePicker.getHour();
            int minute1 = materialTimePicker.getMinute();
            binding.textViewEndTime.setText(setStringTime(hour1, minute1));
        });
    }

    private void selectStartTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(CLOCK_12H)
                .setHour(hour)
                .setMinute(minute)
                .build();

        materialTimePicker.show(getSupportFragmentManager(), "startTimeTag");
        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            int hour1 = materialTimePicker.getHour();
            int minute1 = materialTimePicker.getMinute();
            binding.textViewStartTime.setText(setStringTime(hour1, minute1));
        });
    }

    private void selectDate() {
        MaterialDatePicker<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select a date")
                .build();
        builder.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        builder.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                binding.dateTextView.setText(encodeDate(decodeDate(builder.getHeaderText())));
            }
        });
    }

    private String setStringTime(int hour, int minute) {
        String h = String.valueOf(hour);
        if (h.length() == 1) h = "0" + h;
        String m = String.valueOf(minute);
        if (m.length() == 1) m = "0" + m;
        return h + ":" + m;
    }
}