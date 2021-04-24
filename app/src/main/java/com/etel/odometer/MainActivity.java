package com.etel.odometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private OdometerService odometer;
    private boolean bound = false;
    Button b ;
    double distance = 0.0;
    double distanceKM = 0.0;
    double reset = 0;
    String distanceStr ,distanceKMStr
;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OdometerService.OdomterBinder odomterBinder = (OdometerService.OdomterBinder) service;
            odometer = odomterBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        watchMileage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound){
            unbindService(connection);
            bound = false;
        }
    }

    private void watchMileage() {
        final TextView distanceview = (TextView) findViewById(R.id.distance);
        final TextView distanceviewKM = (TextView) findViewById(R.id.distanceKM);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                if(odometer != null){
                    distance = odometer.getMiles();
                    distanceKM = odometer.getKilometre();
                    reset = odometer.reset();

                }
                 distanceStr = String.format("%1$,.2f miles", distance);
                 distanceKMStr = String.format("%1$,.2f kilometre", distanceKM);
                b = findViewById(R.id.button);
                distanceview.setText(distanceStr);
                distanceviewKM.setText(distanceKMStr);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        distanceview.setText("0");
                        distanceviewKM.setText("0");

                    }
                });

                handler.postDelayed(this, 1000);
            }
        });
    }
}