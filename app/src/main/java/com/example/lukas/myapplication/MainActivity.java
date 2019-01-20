package com.example.lukas.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import 	android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String preferenceOrigin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.check_now_button).setOnTouchListener(mCheckNowListener);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Context context = this.getBaseContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Date futureDate = new Date(new Date().getTime() + 86400000);
        futureDate.setHours(11);
        futureDate.setMinutes(15);
        futureDate.setSeconds(0);
        Intent intent = new Intent(context, MyAppReciever.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureDate.getTime(), sender);


        preferenceOrigin = PreferenceManager.getDefaultSharedPreferences(this).getString("origin", preferenceOrigin);
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
            int delay = getDelay();
            if (delay == 0) {
                myAwesomeTextView.setText("No Delay known");
                myAwesomeTextView.setBackgroundColor(Color.GREEN);
                //Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //vibrator.vibrate(300);
                notifyUser("No Delay", "Train on Time");
            } else {
                myAwesomeTextView.setText("Expected delay: " + delay + " minutes");
                myAwesomeTextView.setBackgroundColor(Color.RED);
                notifyUser("Expected Delay", delay + " minutes");
            }

            return false;
        }
    };

    private void notifyUser(String title, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
    }

    private class MyAppReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("received");
            startActivity(new Intent(context,  MainActivity.class));
            int delay = getDelay();
            notifyUser("Delay", delay + " minutes");
        }
    }

    private int getDelay() {
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
            return delayString == "null" ? 0 : Integer.parseInt(delayString);
        } catch (Exception e) {
            e.printStackTrace();
            //myAwesomeTextView.setText("Connection Error. Try again later");
        }
        return 0;
    }
}
