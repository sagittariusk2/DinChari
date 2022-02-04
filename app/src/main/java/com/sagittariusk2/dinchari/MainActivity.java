package com.sagittariusk2.dinchari;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagittariusk2.dinchari.DataModel.SimpleTask;
import com.sagittariusk2.dinchari.DataModel.User;
import com.sagittariusk2.dinchari.databinding.ActivityMainBinding;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseReference dbReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View parentView = binding.getRoot();
        setContentView(parentView);

        SharedPreferences dinChariCurrentUser = getSharedPreferences("DinChariCurrentUser", MODE_PRIVATE);
        String x = dinChariCurrentUser.getString("userName", "demoName");
        String y = dinChariCurrentUser.getString("userPhone", "demoPhone");
        User globalUser = new User(x, y);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseDatabase.setPersistenceEnabled(true);
        dbReference = firebaseDatabase.getReference().child("user").child(globalUser.getUserPhone());
        dbReference.keepSynced(true);

        // Set user name
        binding.nameID.setText(globalUser.getUserName());

        // Set greetings
        if(LocalTime.now().getHour()>=5 && LocalTime.now().getHour()<11) {
            binding.greetingsID.setText("Good Morning, ");
        }
        if(LocalTime.now().getHour()>=11 && LocalTime.now().getHour()<16) {
            binding.greetingsID.setText("Good Afternoon, ");
        }
        if(LocalTime.now().getHour()>=16 && LocalTime.now().getHour()<19) {
            binding.greetingsID.setText("Good Evening, ");
        }
        if(LocalTime.now().getHour()>=19 && LocalTime.now().getHour()<24) {
            binding.greetingsID.setText("Good Night, ");
        }
        if(LocalTime.now().getHour()>=0 && LocalTime.now().getHour()<5) {
            binding.greetingsID.setText("Good Night, ");
        }

        // TODO : Set Quotes of the day to motivate


        binding.dateView.setText(encodeDate(LocalDate.now()));
        loadData();

        binding.calenderID.setOnClickListener(v -> {
            MaterialDatePicker<Long> builder = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select a date").build();
            builder.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            builder.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    binding.dateView.setText(encodeDate(decodeDate(builder.getHeaderText())));
                    loadData();
                }
            });
        });

        binding.addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        // Proceed to view Activity as per day mentioned on the top
        binding.todoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskViewShowActivity.class);
            intent.putExtra("date", binding.dateView.getText().toString());
            startActivity(intent);
        });
        binding.progressButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskViewShowActivity.class);
            intent.putExtra("date", binding.dateView.getText().toString());
            startActivity(intent);
        });
        binding.doneButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskViewShowActivity.class);
            intent.putExtra("date", binding.dateView.getText().toString());
            startActivity(intent);
        });
        binding.viewDate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskViewShowActivity.class);
            intent.putExtra("date", binding.dateView.getText().toString());
            startActivity(intent);
        });
        binding.button1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskViewShowActivity.class);
            intent.putExtra("date", binding.dateView.getText().toString());
            startActivity(intent);
        });
        binding.button2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskViewShowActivity.class);
            intent.putExtra("date", binding.dateView.getText().toString());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        // Decode date from the textView
        LocalDate localDate = decodeDate(binding.dateView.getText().toString());
        binding.nameGridDate.setText(binding.dateView.getText().toString());
        binding.nameGridMonth.setText(localDate.getMonth().toString());
        binding.nameGridyear.setText(String.valueOf(localDate.getYear()));

        dbReference.child("simpleTask").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int total=0, comp=0, todo=0, progress=0, done=0;
                    int monthTotal=0, monthComp=0;
                    int weekTotal=0, weekComp=0;
                    int yearTotal=0, yearComp=0;
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        SimpleTask simpleTask = dataSnapshot.getValue(SimpleTask.class);
                        assert simpleTask != null;
                        if(simpleTask.getTaskScheduleDate().equalsIgnoreCase(LocalDate.now().toString())) {
                            if (simpleTask.isTaskCompleted()) comp++;
                            total++;
                            if (LocalTime.parse(simpleTask.getTaskFinishTime()).isBefore(LocalTime.now())) {
                                done++;
                            } else if (LocalTime.parse(simpleTask.getTaskScheduleTime()).isAfter(LocalTime.now())) {
                                todo++;
                            } else {
                                progress++;
                            }
                        }
                        if(LocalDate.parse(simpleTask.getTaskScheduleDate()).getMonth().equals(LocalDate.now().getMonth())) {
                            if (simpleTask.isTaskCompleted()) monthComp++;
                            monthTotal++;
                        }
                        if(LocalDate.parse(simpleTask.getTaskScheduleDate()).compareTo(LocalDate.now())<=0 && LocalDate.parse(simpleTask.getTaskScheduleDate()).compareTo(LocalDate.now().minusDays(7))>0) {
                            if (simpleTask.isTaskCompleted()) weekComp++;
                            weekTotal++;
                        }
                        if(LocalDate.parse(simpleTask.getTaskScheduleDate()).getYear()==(LocalDate.now().getYear())) {
                            if (simpleTask.isTaskCompleted()) yearComp++;
                            yearTotal++;
                        }
                    }
                    binding.todoContent.setText(todo+" tasks remaining");
                    binding.progressDesc.setText(progress+" tasks in progress");
                    binding.doneDesc.setText(done+" tasks done");
                    int x=0;
                    if(total!=0)
                        x = (comp*100)/total;
                    binding.circularProgressBarDate.setProgress(((float) x));
                    binding.perDate.setText(x +"%");

                    int x1=0;
                    if(weekTotal!=0)
                        x1 = (weekComp*100)/weekTotal;
                    binding.circularProgressBarWeek.setProgress(((float) x1));
                    binding.perWeek.setText(x1 +"%");

                    int x2=0;
                    if(monthTotal!=0)
                        x2 = (monthComp*100)/monthTotal;
                    binding.circularProgressBarMonth.setProgress(((float) x2));
                    binding.perMonth.setText(x2 +"%");

                    int x3=0;
                    if(yearTotal!=0)
                        x3 = (yearComp*100)/yearTotal;
                    binding.circularProgressBarYear.setProgress(((float) x3));
                    binding.perYear.setText(x3 +"%");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
}
