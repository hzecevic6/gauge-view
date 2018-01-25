package com.example.hzecevic.gaugetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GaugeView gaugeView = findViewById(R.id.gaugeView);
        gaugeView.showGauge(new String[] {"20", "45", "80", "90"}, new String[]{"#009900", "#FFFF00", "#FFA500", "#FF0000", "#F0F0F0"});
    }
}
