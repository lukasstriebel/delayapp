package com.example.lukas.myapplication;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import 	android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

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
            TextView myAwesomeTextView = (TextView) findViewById(R.id.fullscreen_content);

            myAwesomeTextView.setBackgroundColor(Color.WHITE);
            /*myAwesomeTextView.setText("Getting data.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myAwesomeTextView.setText("Getting data..");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            myAwesomeTextView.setText("Getting data...");

            EditText source = (EditText) findViewById(R.id.source);
            EditText destination = (EditText) findViewById(R.id.destination);
            String src = "Buelach";//source.getText();
            String dest = "Zuerich";//destination.getText();
            String time = "07:35";
            String getUrl = "http://transport.opendata.ch/v1/connections?from=" +
                    src + "&to=" + dest + "&time=" + time +"&limit=1";

            try {
                URL url = new URL(getUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                StringBuilder stringBuilder = new StringBuilder();
                
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);                        
                }
                System.out.println(stringBuilder.toString());
                final JSONObject response = new JSONObject(stringBuilder.toString());
                final JSONArray connections = response.getJSONArray("connections");
                JSONObject from = connections.getJSONObject(0).getJSONObject("from");
                String delayString = from.getString("delay");
                int delay = delayString == "null" ? 0 : Integer.parseInt(delayString);
                if (delay == 0) {
                    myAwesomeTextView.setText("No Delay known");
                    myAwesomeTextView.setBackgroundColor(Color.GREEN);
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(300);
                } else {
                    myAwesomeTextView.setText("Expected delay: " + delay + " minutes");
                    myAwesomeTextView.setBackgroundColor(Color.RED);
                }

            } catch (Exception e) {
                e.printStackTrace();
                myAwesomeTextView.setText("Connection Error. Try again later");
            }
            return false;
        }
    };
}
