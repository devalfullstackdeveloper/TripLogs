package com.triplogs.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.triplogs.R;
import com.triplogs.interfaces.LocationInterface;
import com.triplogs.user.Home;
import com.triplogs.user.LoginScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static com.triplogs.MyApplication.CHANNEL_1_ID;

public class LocationForegroundService extends Service {


    String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
    String channelName = "My Background Service";

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    boolean isStart = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

      //  Timer timer = new Timer();
        if (intent.getAction().equals("start")) {

            String durations = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.API_CALL_DURATION, "4000");
            if (!durations.equals("")) {
                isStart = true;
                context = this;
                locationTrack = new LocationTrack(getApplicationContext());
                Timer timer = new Timer();
                timer.schedule(new TimerClass(), 0, Integer.parseInt(durations));

                String CHANNEL_ONE_ID = "com.triplogs";
                String CHANNEL_ONE_NAME = "Channel One";
                NotificationChannel notificationChannel = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                            CHANNEL_ONE_NAME, IMPORTANCE_HIGH);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.setShowBadge(true);
                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.createNotificationChannel(notificationChannel);
                }


                NotificationChannel chan = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
                    chan.setLightColor(Color.BLUE);
                    chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    assert manager != null;
                    manager.createNotificationChannel(chan);
                }


                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
                Notification notification = notificationBuilder.setOngoing(true)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("App is running in background")
                        .setPriority(NotificationManager.IMPORTANCE_MIN)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .build();
                startForeground(2, notification);
            }


        } else if (intent.getAction().equals("stop")) {
            LogClass.e("unregisterListener","unregisterListener stop ");
           // timer.cancel();

            locationTrack.stopListener();
            isStart = false;
            LogClass.i("onStartCommand", "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelfResult(startId);
        }

        return START_STICKY;

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void NotificationUpdate(String message) {
        try {
            Intent notificationIntent = new Intent(this, Home.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            final Notification[] notification = {new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Trip Logs")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build()};
            startForeground(1, notification[0]);
            NotificationChannel notificationChannel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_1_ID, "My Counter Service", NotificationManager.IMPORTANCE_DEFAULT);
            }
            NotificationManager notificationManager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager = getSystemService(NotificationManager.class);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LocationTrack locationTrack;


    class TimerClass extends TimerTask {
        public void run() {
            LogClass.e("myfunction", "----------");
            if (isStart) {
                if (locationTrack.canGetLocation()) {


                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();
                    double accurarcy = locationTrack.getAccurarcy();
                    double timestamp = locationTrack.getTimeStamp();
                    double altitude = locationTrack.getAltitude();
                    double speed = locationTrack.getSpeed();
                    double heading = locationTrack.getHeading();
                    NotificationUpdate("Lat: " + latitude + ",Long: " + longitude + ",Accurarcy: " + accurarcy);


                    setTripLogs(speed + "",
                            altitude + "",
                            heading + "",
                            latitude + "",
                            longitude + "",
                            accurarcy + "",
                            timestamp + ""
                    );
                } else {

                    locationTrack.showSettingsAlert();
                }
            }else {
                cancel();
            }

        }
    }


    private void setTripLogs(String speed, String altitude, String heading, String latitude, String longitude, String accuracy, String timestamp) {

        String tripId = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.TRIP_ID, "");
        OkHttpWrapper okHttpWrapper = new OkHttpWrapper();

        String jsonobj = "{\n" +
                "    \"logs\" : \n" +
                "    [\n" +
                "        {\n" +
                "    \"tripId\" :\""+tripId+"\",\n" +
                "    \"speed\" :\""+speed+"\",\n" +
                "    \"altitude\" :\""+altitude+"\",\n" +
                "    \"heading\" :\""+heading+"\",\n" +
                "    \"latitude\" :\""+latitude+"\",\n" +
                "    \"longitude\" :\""+longitude+"\",\n" +
                "    \"accuracy\" :\""+accuracy+"\",\n" +
                "    \"timestamp\" :\""+timestamp+"\"\n" +
                "    }\n" +
                "    ]\n" +
                "}";

        try {

            final String myResponse = okHttpWrapper.post( ApiConstant.Urls.TRIP_LOGS,jsonobj);
            LogClass.e("onResponse", "body :" + myResponse);
        } catch (IOException e) {
            e.printStackTrace();
            LogClass.e("onResponse", "body :" + e);
        }
    }


}
