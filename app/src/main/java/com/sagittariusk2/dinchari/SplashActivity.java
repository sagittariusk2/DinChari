package com.sagittariusk2.dinchari;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sagittariusk2.dinchari.DataModel.User;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    public FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    private SharedPreferences dinChariCurrentUser;
    public User globalUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);

        // TODO : Give a best ICON & Name

        dinChariCurrentUser = getSharedPreferences("DinChariCurrentUser", MODE_PRIVATE);

        new Handler().postDelayed(() -> {
            if(dinChariCurrentUser.getBoolean("signedUser", false)) {
                String x = dinChariCurrentUser.getString("userName", "demoName");
                String y = dinChariCurrentUser.getString("userPhone", "demoPhone");
                globalUser = new User(x, y);
                Intent i=new Intent(SplashActivity.this,
                        MainActivity.class);
                startActivity(i);
            } else {
                Intent intent2 = new Intent(SplashActivity.this, LogInSignUp.class);
                startActivity(intent2);
            }
            finish();
        }, 1000);
    }
}
