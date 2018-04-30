package com.example.cpsadmin.cps;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class treatDetail extends AppCompatActivity {

    private CountDownTimer timer;
    private int millis, seconds;
    private TextView TV_time;
    private Button BT_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treat_detail);
        setTitle("Treatment Details");

        TV_time = findViewById(R.id.treatmentTime);

        millis = 2094000; // 34 minutes and 9 seconds
        seconds = millis/1000;
        seconds = seconds % 60;
    }

    /* cancelTreatment cancels the timer and returns to home screen
    @params: current view
    @return: void
     */
    public void cancelTreatment(View view) {
        timer.cancel();
        Intent cancel = new Intent(this, Home.class);
        startActivity(cancel);
    }

    /* startTimer initializes a timer that tracks treatment time
    @params: current view
    @return: void
     */
    public void startTimer(View view) {
        timer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinish) {
                int sec = (int) millisUntilFinish/1000;
                int min = sec/60;
                sec = sec % 60;
                TV_time.setText("00:" + String.format("%02d", min) + ":" + String.format("%02d", sec));
            }

            @Override
            public void onFinish() {
                Toast.makeText(treatDetail.this, "Treatment Finished", Toast.LENGTH_SHORT).show();
                Intent goHome = new Intent(treatDetail.this, Home.class);
                startActivity(goHome);
            }
        };

        timer.start();
    }
}
