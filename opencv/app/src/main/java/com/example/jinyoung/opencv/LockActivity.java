package com.example.jinyoung.opencv;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;

public class LockActivity extends Activity{
    TextView countdown2;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final String id = intent.getExtras().getString("아이디");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        setContentView(R.layout.lock);


        countdown2 = findViewById(R.id.countdown2);
        CountDownTimer count = new CountDownTimer(10000,1) {
            @Override
            public void onTick(long l) {
                countdown2.setText((String.format(Locale.getDefault(), "%d", l)));
            }

            @Override
            public void onFinish() {
                finish();
                Intent intent = new Intent(LockActivity.this,SecondActivity.class);
                intent.putExtra("아이디",id);
                startActivity(intent);
            }
        }.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }



}


