package com.triplogs.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.triplogs.R;
import com.triplogs.model.LocationData;
import com.triplogs.user.Home;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static com.triplogs.MyApplication.CHANNEL_1_ID;

/**
 * Created by roberto on 9/29/16.
 */

public class MyLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
    String channelName = "My Background Service";
    Context context;


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    boolean isStart = false;

    int contTimer = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals("start")) {

            String durations = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.API_CALL_DURATION, "7000");
            if (!durations.equals("")) {
                isStart = true;
                context = this;
                contTimer = Integer.parseInt("" + durations.trim().charAt(0));
                Timer timer = new Timer();
                timer.schedule(new TimerClassTwo(), 0, 1000);

//                final Handler handler = new Handler();
//                final int delay = 1000; // 1000 milliseconds == 1 second
//
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        timestamp =convertToIso() + "";
//                        LogClass.e("timestamp","timestamp : "+timestamp);
//                        handler.postDelayed(this, delay);
//                    }
//                }, delay);


//                Handler h = new Handler();
//                final Runnable r = new Runnable() {
//                    int count = 0;
//                    @Override
//                    public void run() {
//                        count++;
//
//
//                       // LogClass.e("timestamp","timestamp : "+""+count*1.8);
//                        timestamp =convertToIso() + "";
//                        LogClass.e("timestamp","timestamp : "+timestamp);
//                        h.postDelayed(this, 1000); //ms
//                    }
//                };
//                h.postDelayed(r, 1000);

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
                Notification notification = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    notification = notificationBuilder.setOngoing(true)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("App is running in background")
                            .setPriority(NotificationManager.IMPORTANCE_MIN)
                            .setCategory(Notification.CATEGORY_SERVICE)
                            .build();
                } else {
                    notification = notificationBuilder.setOngoing(true)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("App is running in background")
                            .setCategory(Notification.CATEGORY_SERVICE)
                            .build();
                }
                startForeground(2, notification);

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();

                mGoogleApiClient.connect();
            }


        } else if (intent.getAction().equals("stop")) {
            LogClass.e("unregisterListener", "unregisterListener stop ");
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.unregisterConnectionCallbacks(this);
            }
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (Exception ignored) {

            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //  LogClass.e("onLocationChanged", "error " + connectionResult);
    }


    String lat = "";
    String longi = "";
    String accurarcy = "";
    String timestamp = "";
    String altitude = "";
    String speed = "";
    String heading = "";
    Queue<LocationData> queue = new LinkedList<>();
    Queue<LocationData> queue2;

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        Intent intent = new Intent("locationData");
        intent.putExtra("Lat", "" + location.getLatitude());
        intent.putExtra("Long", "" + location.getLongitude());
        intent.putExtra("accurarcy", "" + location.getAccuracy());
        intent.putExtra("timestamp", "" + location.getTime());
        intent.putExtra("altitude", "" + location.getAltitude());
        intent.putExtra("speed", "" + location.getSpeed());


        if (!lat.equals("") && !longi.equals("")) {
            float d = (float) distance(Double.parseDouble(lat), Double.parseDouble(longi), location.getLatitude(), location.getLongitude());
            heading = (d + 0.0) + "";
            intent.putExtra("heading", heading);
        }

        sendBroadcast(intent);
        NotificationUpdate("Lat: " + location.getLatitude() + ",Long: " + location.getLongitude() + ",speed: " + location.getSpeed());
    }


    public String convertToIso() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return "" + dateFormatGmt.format(new Date());
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    int obj = 0;
    //int temp = 0;

    class TimerClassTwo extends TimerTask {
        public void run() {

            if (isStart) {
                if (mLastLocation != null) {
                    String temp = convertToIso() + "";
                    if (timestamp.equals(temp)) {
                        timestamp = temp;
                    } else {
                        timestamp = temp;
                        LogClass.e("timestamp", "timestamp : " + timestamp);
                        lat = mLastLocation.getLatitude() + "";
                        longi = mLastLocation.getLongitude() + "";
                        accurarcy = mLastLocation.getAccuracy() + "";
                        timestamp = convertToIso() + "";
                        altitude = mLastLocation.getAltitude() + "";
                        if (mLastLocation.getSpeed() < 0) {
                            speed = "0";
                        } else {
                            speed = (mLastLocation.getSpeed() * 3.6) + "";
                        }


                        LogClass.e("timestamp", "timestamp : " + timestamp);
                        LogClass.e("timestamp", "speed : " + speed);

                        String tripId = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.TRIP_ID);
                        if (heading.trim().equals("")) {
                            heading = "0.0";
                        }

                        if (accurarcy == null || accurarcy.trim().equals("") || accurarcy.equals("null")) {
                            accurarcy = "-1";
                        }

                        LocationData locationData = new LocationData(tripId, speed, altitude, heading, lat, longi, accurarcy, timestamp);
                        queue.add(locationData);

                        if (obj == contTimer - 1) {
                            obj = 0;
                            LogClass.e("LocationData", "call function----");
                            setTripLogs();

                        } else {
                            LogClass.e("LocationData", "call function--" + contTimer + "==" + obj);
                            obj++;
                        }

                    }


                }


            } else {
                cancel();
            }

        }
    }

    private void setTripLogs() {

        String prefix = "{\n" +
                "    \"logs\" : \n" +
                "    [\n";

        String sufix = "\n" +
                "    ]\n" +
                "}";

        String concatStr = "";
        int i = 0;
        int size = queue.size() - 1;
        queue2 = queue;
        for (LocationData locationData : queue2) {
            if (i == 0) {
                String obj = "{\n" +
                        "    \"tripId\" :\"" + locationData.getTripId() + "\",\n" +
                        "    \"speed\" :\"" + locationData.getSpeed() + "\",\n" +
                        "    \"altitude\" :\"" + locationData.getAltitude() + "\",\n" +
                        "    \"heading\" :\"" + locationData.getHeading() + "\",\n" +
                        "    \"latitude\" :\"" + locationData.getLatitude() + "\",\n" +
                        "    \"longitude\" :\"" + locationData.getLongitude() + "\",\n" +
                        "    \"accuracy\" :\"" + locationData.getAccuracy() + "\",\n" +
                        "    \"timestamp\" :\"" + locationData.getTimestamp() + "\"\n" +
                        "    },\n";
                concatStr = concatStr + obj;
            } else if (size != i) {
                String obj = "{\n" +
                        "    \"tripId\" :\"" + locationData.getTripId() + "\",\n" +
                        "    \"speed\" :\"" + locationData.getSpeed() + "\",\n" +
                        "    \"altitude\" :\"" + locationData.getAltitude() + "\",\n" +
                        "    \"heading\" :\"" + locationData.getHeading() + "\",\n" +
                        "    \"latitude\" :\"" + locationData.getLatitude() + "\",\n" +
                        "    \"longitude\" :\"" + locationData.getLongitude() + "\",\n" +
                        "    \"accuracy\" :\"" + locationData.getAccuracy() + "\",\n" +
                        "    \"timestamp\" :\"" + locationData.getTimestamp() + "\"\n" +
                        "    },\n";
                concatStr = concatStr + obj;
            } else {
                String obj = "{\n" +
                        "    \"tripId\" :\"" + locationData.getTripId() + "\",\n" +
                        "    \"speed\" :\"" + locationData.getSpeed() + "\",\n" +
                        "    \"altitude\" :\"" + locationData.getAltitude() + "\",\n" +
                        "    \"heading\" :\"" + locationData.getHeading() + "\",\n" +
                        "    \"latitude\" :\"" + locationData.getLatitude() + "\",\n" +
                        "    \"longitude\" :\"" + locationData.getLongitude() + "\",\n" +
                        "    \"accuracy\" :\"" + locationData.getAccuracy() + "\",\n" +
                        "    \"timestamp\" :\"" + locationData.getTimestamp() + "\"\n" +
                        "    }\n";
                concatStr = concatStr + obj;
            }
            i++;
        }

        String mainObj = prefix + concatStr + sufix;
        LogClass.e("tripLog", "queue :" + mainObj.trim());

        String loginResponse = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.CONFIG_RESPONSE_BODY, "");
        if (!loginResponse.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(loginResponse);
                JSONObject response = jsonObject.getJSONObject("response");
                JSONObject apiurl = response.getJSONObject("apiurl");
                String baseUrl = apiurl.getString("url");
                String url = baseUrl + ApiConstant.Urls.TRIP_LOGS;
                OkHttpWrapper okHttpWrapper = new OkHttpWrapper();

                LogClass.e("tripLog", "url :" + url);
                LogClass.e("tripLog", "permas :" + mainObj.replace(" ", ""));
                try {
                    final String myResponse = okHttpWrapper.post(url, mainObj.replace(" ", ""));
                    LogClass.e("tripLog", "body :" + myResponse);
                    mainObj = "";
                    queue.removeAll(queue);
                } catch (IOException e) {
                    e.printStackTrace();
                    LogClass.e("tripLog", "body :" + e);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    boolean dilogVisebel = true;

    private boolean checkOnOf() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {

            if (dilogVisebel) {
                dilogVisebel = false;

//               getApplicationContext().runOnUiThread(new Runnable() {
//                    public void run() {
//
//                    }
//                });
//                 new Handler(Looper.getMainLooper()) {
//                    @Override
//                    public void handleMessage(Message message) {
//                        new AlertDialog.Builder(context)
//                                .setMessage("Enable Network")
//                                .setNegativeButton("Cancel", null)
//                                .setPositiveButton("allow", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                                    }
//                                })
//                                .show();
//                    }
//
//                };

            }

            return false;
        } else {
            dilogVisebel = true;
            return true;
        }
    }

}
