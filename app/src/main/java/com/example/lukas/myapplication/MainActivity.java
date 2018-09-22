package com.example.lukas.myapplication;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String org;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.check_now_button).setOnTouchListener(mCheckNowListener);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        org = PreferenceManager.getDefaultSharedPreferences(this).getString("origin", org);
    }

    private final View.OnTouchListener mCheckNowListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            TextView myAwesomeTextView = (TextView)findViewById(R.id.fullscreen_content);
            EditText source = (EditText) findViewById(R.id.source);
            EditText destination = (EditText) findViewById(R.id.destination);
            String urlToRead = "http://transport.opendata.ch/v1/connections?from="+source.getText()+"&to="+destination.getText()+"&limit=1";

            try {

                URL url = new URL(urlToRead);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                boolean t = true;
                while ((line = rd.readLine()) != null) {
                    //line.contains("delay:")
                    if( t) {
                        myAwesomeTextView.setText(line);
                        myAwesomeTextView.setBackgroundColor(Color.RED);
                        //t = !t;
                    }
                    else {
                        myAwesomeTextView.setText("No Delay");
                        myAwesomeTextView.setBackgroundColor(Color.GREEN);
                        t = !t;
                    }
                }

            }

            catch(Exception e){e.printStackTrace();}
            return false;
        }
    };
}
