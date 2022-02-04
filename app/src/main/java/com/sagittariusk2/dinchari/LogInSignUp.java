package com.sagittariusk2.dinchari;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.sagittariusk2.dinchari.DataModel.SimpleTask;
import com.sagittariusk2.dinchari.DataModel.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class LogInSignUp extends AppCompatActivity {

    private TextInputEditText phone_number, name;
    private Button send_code;
    private String mVerificationID, userName, userPhone;
    private TextView timerID, result_message;
    private int counter;
    private LinearLayoutCompat otpContainer, nameContainer;
    private AlertDialog dialog;
    private CountryCodePicker ccp;
    private FirebaseAuth mAuth;
    private OtpTextView otp_view;
    private PhoneAuthCredential credential;
    private boolean verified=false;
    private SharedPreferences.Editor myEdit;
    public static final String channel = "DINCHARI_NOTIFICATION_CHANNEL";
    private AlarmManager alarmManager;
    private FirebaseDatabase firebaseDatabase;
    private User globalUser;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        // Initializing All the views
        {
            phone_number = findViewById(R.id.phone_number);
            send_code = findViewById(R.id.send_code);
            timerID = findViewById(R.id.timerID);
            ccp = findViewById(R.id.ccp);
            otp_view = findViewById(R.id.otp_view);
            otpContainer = findViewById(R.id.otpContainer);
            result_message = findViewById(R.id.result_message);
            nameContainer = findViewById(R.id.nameContainer);
            name = findViewById(R.id.name);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);

        // Creating Channel Notification
        createNotificationChannel();

        // Shared Preferences
        SharedPreferences dinChariCurrentUser = getSharedPreferences("DinChariCurrentUser", MODE_PRIVATE);
        myEdit = dinChariCurrentUser.edit();

        mAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View viewBar = inflater.inflate(R.layout.loading, null);
        Sprite doubleBounce = new Wave();
        doubleBounce.setColor(Color.GREEN);
        ProgressBar progressBar = viewBar.findViewById(R.id.progressBar);
        progressBar.setIndeterminateDrawable(doubleBounce);
        builder.setView(viewBar);
        dialog = builder.create();
        dialog.setCancelable(false);

        send_code.setOnClickListener(view -> {
            if(send_code.getText().toString().equalsIgnoreCase("send code")) {
                userPhone = '+' + ccp.getSelectedCountryCode() + Objects.requireNonNull(phone_number.getText()).toString();
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(userPhone)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(LogInSignUp.this)
                                .setCallbacks(mCallbacks)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
                dialog.show();
            } else if(send_code.getText().toString().equalsIgnoreCase("continue")) {
                userName = Objects.requireNonNull(name.getText()).toString();
                if(userName.isEmpty()) {
                    name.setError("Please Enter a name for yourself");
                } else {
                    registerToDatabase();
                }
            }
        });

        otp_view.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                credential = PhoneAuthProvider.getCredential(mVerificationID, otp);
                signInWithPhoneAuthCredential(credential);
                dialog.show();
            }
        });
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            dialog.dismiss();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            dialog.dismiss();
            result_message.setText(e.getLocalizedMessage());
            result_message.setTextColor(Color.RED);
        }

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            dialog.dismiss();
            otpContainer.setVisibility(View.VISIBLE);
            phone_number.setEnabled(false);

            result_message.setText("Code has been sent to your phone number");
            result_message.setTextColor(Color.BLUE);
            mVerificationID = verificationId;

            send_code.setVisibility(View.GONE);
            //Start Timer
            startTimer();
        }

        private void startTimer() {
            timerID.setVisibility(View.VISIBLE);
            counter = 59;
            new CountDownTimer(59000, 1000){
                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished){
                    String x = String.valueOf(counter);
                    if(x.length()==1) x = '0'+x;
                    if(counter<=15) timerID.setTextColor(Color.RED);
                    timerID.setText("Code will be active for another "+x+" seconds");
                    counter--;
                }
                @SuppressLint("SetTextI18n")
                public  void onFinish(){
                }
            }.start();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            if(!verified) {
                result_message.setText("Time Out : Click send button again");
                result_message.setTextColor(Color.RED);
                send_code.setVisibility(View.VISIBLE);
                otpContainer.setVisibility(View.GONE);
                phone_number.setEnabled(true);
                ccp.setEnabled(true);
            }
        }
    };

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            dialog.dismiss();
            if(task.isSuccessful()) {
                verified = true;
                result_message.setTextColor(Color.BLUE);
                result_message.setText("Verified");
                otpContainer.setVisibility(View.GONE);
                dialog.setTitle("Syncing");
                dialog.show();

                Query query = firebaseDatabase.getReference().child("user").orderByChild("userPhone").equalTo(userPhone);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dialog.dismiss();
                        if(snapshot.exists()) {
                            firebaseDatabase.getReference().child("user").child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                            myEdit.putBoolean("signedUser", true);
                                            myEdit.putString("userName", String.valueOf(snapshot1.child("userName").getValue()));
                                            myEdit.putString("userPhone", userPhone);
                                            myEdit.commit();

                                            globalUser = new User(String.valueOf(snapshot1.child("userName").getValue()), userPhone);

                                            // Entering First Time In The App

                                            firebaseDatabase.getReference().child("user").child(userPhone).child("simpleTask").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot shot:snapshot.getChildren()) {
                                                        SimpleTask simpleTask = shot.getValue(SimpleTask.class);
                                                        assert simpleTask != null;
                                                        if(!LocalDate.parse(simpleTask.getTaskScheduleDate()).isBefore(LocalDate.now())
                                                                || (LocalDate.parse(simpleTask.getTaskScheduleDate()).equals(LocalDate.now())
                                                                && LocalTime.parse(simpleTask.getTaskScheduleTime()).isAfter(LocalTime.now()))) {
                                                            setAlarm(simpleTask);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            startActivity(new Intent(LogInSignUp.this, MainActivity.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                            });
                        } else {
                            nameContainer.setVisibility(View.VISIBLE);
                            send_code.setText("Continue");
                            send_code.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    otp_view.showError();
                    result_message.setTextColor(Color.RED);
                    result_message.setText("You have entered an invalid Code");
                }
            }
        });
    }

    private void setAlarm(SimpleTask simpleTask) {
        Calendar calendar1 = Calendar.getInstance();
        String[] x = simpleTask.getTaskScheduleTime().split(":");
        calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(x[0]));
        calendar1.set(Calendar.MINUTE, Integer.parseInt(x[1]));
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        if(System.currentTimeMillis() <= calendar1.getTimeInMillis()-((long) simpleTask.getTaskNotifyBefore() *60000)) {
            if (alarmManager == null)
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("notificationID", simpleTask.getNotificationID());
            intent.putExtra("taskID", simpleTask.getTaskID());
            intent.putExtra("taskCategory", simpleTask.getTaskCategory());
            intent.putExtra("taskDesc", simpleTask.getTaskDesc());
            intent.putExtra("taskName", simpleTask.getTaskName());
            intent.putExtra("date", simpleTask.getTaskScheduleDate());
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    (int) simpleTask.getNotificationID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis() - ((long) simpleTask.getTaskNotifyBefore() * 60000), pendingIntent);
        }
    }

    private void registerToDatabase() {
        User user = new User(userName, userPhone);
        firebaseDatabase.getReference().child("user").child(userPhone).setValue(user).addOnCompleteListener(task -> {
            myEdit.putBoolean("signedUser", true);
            myEdit.putString("userName", userName);
            myEdit.putString("userPhone", userPhone);
            myEdit.commit();
            globalUser = new User(userName, userPhone);
            startActivity(new Intent(LogInSignUp.this, MainActivity.class));
            finish();
        });
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
}
