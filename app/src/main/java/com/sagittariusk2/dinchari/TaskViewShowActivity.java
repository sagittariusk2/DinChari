package com.sagittariusk2.dinchari;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sagittariusk2.dinchari.DataModel.SimpleTask;
import com.sagittariusk2.dinchari.DataModel.User;
import com.sagittariusk2.dinchari.databinding.ActivityTaskviewShowBinding;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class TaskViewShowActivity extends AppCompatActivity {

    private ActivityTaskviewShowBinding binding;
    private MaterialTextView day, date;
    private DatabaseReference dbReference;
    private AlarmManager alarmManager;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskviewShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences dinChariCurrentUser = getSharedPreferences("DinChariCurrentUser", MODE_PRIVATE);
        String x1 = dinChariCurrentUser.getString("userName", "demoName");
        String y = dinChariCurrentUser.getString("userPhone", "demoPhone");
        User globalUser = new User(x1, y);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseDatabase.setPersistenceEnabled(true);
        dbReference = firebaseDatabase.getReference().child("user").child(globalUser.getUserPhone());
        dbReference.keepSynced(true);

        // Getting date from last intent
        String x = getIntent().getStringExtra("date");
        if(x.isEmpty()) x="Today";
        LocalDate localDate = decodeDate(x);

        createViews(localDate);

        // Setting name
        binding.nameIDView.setText("Productive Day, \n"+ globalUser.getUserName());

        binding.addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskViewShowActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

    }

    @SuppressLint("SetTextI18n")
    private void createViews(LocalDate localDate) {
        for(int i=0; i<6; i++) {
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.single_day, null, false);
            MaterialTextView dayID = view.findViewById(R.id.dayID);
            MaterialTextView dateID = view.findViewById(R.id.dateID);
            LinearLayoutCompat eachDay = view.findViewById(R.id.eachDay);
            TextView index = view.findViewById(R.id.index);

            String x = localDate.plusDays(i).getDayOfWeek().toString().toUpperCase().substring(0, 1);
            String y = localDate.plusDays(i).getDayOfWeek().toString().toLowerCase().substring(1, 3);
            dayID.setText(x+y);
            dateID.setText(String.valueOf(localDate.plusDays(i).getDayOfMonth()));
            index.setText(String.valueOf(i));

            if(day==null) {
                day=dayID;
            }
            if(date==null) {
                date = dateID;
                binding.dateView2.setText(encodeDate(localDate));
                binding.monthView.setText(localDate.getMonth().toString()+", "+localDate.getYear());
                date.setTextColor(Color.RED);
                day.setTextColor(Color.RED);
                binding.dateView2.setText(encodeDate(localDate));
                loadData(localDate);
            }

            eachDay.setOnClickListener(v -> {
                int finalI = Integer.parseInt(index.getText().toString());
                day.setTextColor(Color.GRAY);
                date.setTextColor(Color.BLACK);
                day = dayID;
                date = dateID;
                binding.dateView2.setText(encodeDate(localDate.plusDays(finalI)));
                binding.monthView.setText(localDate.plusDays(finalI).getMonth().toString()+", "+localDate.plusDays(finalI).getYear());
                date.setTextColor(Color.RED);
                day.setTextColor(Color.RED);
                loadData(localDate.plusDays(finalI));
            });

            binding.dateContainer.addView(view);
        }
    }

    private void loadData(LocalDate localDate) {
        binding.taskContainerLayout.removeAllViews();
        Query query = dbReference.child("simpleTask")
                .orderByChild("taskScheduleDate")
                .equalTo(localDate.toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<SimpleTask> simpleTasks = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    SimpleTask simpleTask = dataSnapshot.getValue(SimpleTask.class);
                    simpleTasks.add(simpleTask);
                }
                simpleTasks.sort(new CustomComparator());

                // Display
                for(SimpleTask simpleTask:simpleTasks) {
                    @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.single_list, null);

                    AppCompatTextView taskName = view.findViewById(R.id.taskName);
                    AppCompatTextView timeDuration = view.findViewById(R.id.timeDuration);
                    AppCompatTextView taskDescTextView = view.findViewById(R.id.taskDescTextView);
                    AppCompatTextView category = view.findViewById(R.id.category);
                    MaterialCheckBox startTime = view.findViewById(R.id.startTime);
                    View sideBar = view.findViewById(R.id.sideBar);
                    View circleView = view.findViewById(R.id.circleView);
                    FloatingActionButton deleteTaskButton = view.findViewById(R.id.deleteTaskButton);
                    LinearLayoutCompat linkProceed = view.findViewById(R.id.linkProceed);

                    taskName.setText(simpleTask.getTaskName());
                    startTime.setText(getTime(simpleTask.getTaskScheduleTime()));
                    timeDuration.setText(getTime(simpleTask.getTaskScheduleTime())+" - "+getTime(simpleTask.getTaskFinishTime()));
                    taskDescTextView.setText(simpleTask.getTaskDesc());
                    category.setText(simpleTask.getTaskCategory());
                    setColor(taskName, circleView, sideBar, simpleTask.getTaskCategory());

                    if(simpleTask.isTaskCompleted()) {
                        startTime.setChecked(true);
                        linkProceed.setBackgroundColor(Color.parseColor("#7464DD17"));
                    }

                    if((LocalDate.now().toString().equalsIgnoreCase(simpleTask.getTaskScheduleDate()) && simpleTask.getTaskFinishTime().compareTo(String.valueOf(LocalTime.now()))<0) || localDate.isBefore(LocalDate.now())) {
                        deleteTaskButton.setVisibility(View.GONE);
                        startTime.setEnabled(false);
                        sideBar.setBackgroundColor(Color.GRAY);
                        if(simpleTask.isTaskCompleted()) {
                            linkProceed.setBackgroundColor(Color.parseColor("#7464DD17"));
                        } else {
                            linkProceed.setBackgroundColor(Color.parseColor("#5EDD2C00"));
                        }

                    }

                    startTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if(isChecked) {
                            dbReference.child("simpleTask").child(simpleTask.getTaskID()).child("taskCompleted").setValue(true).addOnCompleteListener(task -> linkProceed.setBackgroundColor(Color.parseColor("#7464DD17")));
                        } else {
                            dbReference.child("simpleTask").child(simpleTask.getTaskID()).child("taskCompleted").setValue(false).addOnCompleteListener(task -> linkProceed.setBackgroundColor(Color.parseColor("#FFFFFF")));
                        }
                    });

                    deleteTaskButton.setOnClickListener(v -> {
                        dbReference.child("simpleTask").child(simpleTask.getTaskID()).removeValue();
                        deleteFromAlarmManger(simpleTask);
                        loadData(localDate);
                    });

                    linkProceed.setOnClickListener(v -> {
                        if(!simpleTask.getTaskLink().isEmpty()) {
                            Uri uriUrl = Uri.parse(simpleTask.getTaskLink());
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                            startActivity(launchBrowser);
                        }
                    });

                    binding.taskContainerLayout.addView(view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteFromAlarmManger(SimpleTask simpleTask) {
        if(alarmManager==null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("notificationID", simpleTask.getNotificationID());
        intent.putExtra("taskID", simpleTask.getTaskID());
        intent.putExtra("taskCategory", simpleTask.getTaskCategory());
        intent.putExtra("taskDesc", simpleTask.getTaskDesc());
        intent.putExtra("taskName", simpleTask.getTaskName());
        intent.putExtra("date", simpleTask.getTaskScheduleDate());
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                (int)simpleTask.getNotificationID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        successSnack();
    }

    private void successSnack() {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Deleted Successfully", Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.GREEN);
        snackbar.show();
    }

    private void setColor(AppCompatTextView taskName, View circleView, View sideBar, String taskCategory) {
        if(taskCategory.equalsIgnoreCase("Work")) {
            taskName.setTextColor(Color.BLUE);
            sideBar.setBackgroundColor(Color.BLUE);
            circleView.setBackgroundResource(R.drawable.circle);
        } else if(taskCategory.equalsIgnoreCase("Health")) {
            taskName.setTextColor(Color.RED);
            sideBar.setBackgroundColor(Color.RED);
            circleView.setBackgroundResource(R.drawable.red_circle);
        } else {
            taskName.setTextColor(Color.GREEN);
            sideBar.setBackgroundColor(Color.GREEN);
            circleView.setBackgroundResource(R.drawable.green_circle);
        }

    }

    private String getTime(String taskName) {
        String[] x = taskName.split(":");
        String ans;
        if(Integer.parseInt(x[0])<=24 && Integer.parseInt(x[0])>=13) {
            ans = setStringTime(Integer.parseInt(x[0])-12, Integer.parseInt(x[1]))+" PM";
        } else
            ans = taskName+" AM";
        return ans;
    }

    private String setStringTime(int hour, int minute) {
        String h = String.valueOf(hour);
        if (h.length() == 1) h = "0" + h;
        String m = String.valueOf(minute);
        if (m.length() == 1) m = "0" + m;
        return h + ":" + m;
    }

    private static class CustomComparator implements Comparator<SimpleTask> {
        @Override
        public int compare(SimpleTask o1, SimpleTask o2) {
            return o1.getTaskScheduleTime().compareTo(o2.getTaskScheduleTime());
        }
    }

    private LocalDate decodeDate(@NonNull String toString) {
        if(toString.contains("-")) {
            return LocalDate.parse(toString);
        }
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
}
