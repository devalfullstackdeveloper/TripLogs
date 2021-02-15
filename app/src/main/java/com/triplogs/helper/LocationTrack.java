package com.triplogs.helper;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.triplogs.user.Home;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Response;

public class LocationTrack extends Service implements LocationListener, AverageAngle.ChangeAvgListener {

    private final Context mContext;


    boolean checkGPS = false;


    boolean checkNetwork = false;

    boolean canGetLocation = false;

    Location loc;
    double latitude;
    double longitude;
    double acuracy;
    double speed;
    double altitude;
    double heading;
    double timeStamp;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;
    BearingToNorthProvider bearingToNorthProvider;
    public LocationTrack(Context mContext) {
        this.mContext = mContext;

        new AverageAngle(this);
         bearingToNorthProvider = new BearingToNorthProvider(mContext);
       // bearingToNorthProvider.onLocationChanged(location);
        bearingToNorthProvider.start();
        getLocation();
    }

    private Location getLocation() {

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // get GPS status
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get network provider status
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }
                    }


                }


                if (checkNetwork) {


                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    }

                    if (loc != null) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return loc;
    }

    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
            loc.getAccuracy();
        }
        return longitude;
    }

    public double getAccurarcy() {
        if (loc != null) {
            acuracy = loc.getAccuracy();


        }
        return acuracy;
    }

    public double getSpeed() {
        if (loc != null) {
            speed = loc.getSpeed();
        }
        return speed;
    }

    public double getAltitude() {
        if (loc != null) {


            altitude = loc.getAltitude();
        }
        return altitude;
    }

    public double getHeading() {
        if (loc != null) {


            heading = 0.0;
        }
        return heading;
    }

    public double getTimeStamp() {
        if (loc != null) {


            timeStamp = loc.getTime();
        }
        return timeStamp;
    }

//    double altitude;
//    double heading;
//    double timeStamp;

    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);


        alertDialog.setTitle("GPS is not Enabled!");

        alertDialog.setMessage("Do you want to turn on GPS?");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }


    public void stopListener() {
        bearingToNorthProvider.stop();
        LogClass.e("unregisterListener","unregisterListener stop()");
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(LocationTrack.this);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onLocationChanged(Location location) {
//        GeomagneticField     geoField = new GeomagneticField(
//                Double.valueOf(location.getLatitude()).floatValue(),
//                Double.valueOf(location.getLongitude()).floatValue(),
//                Double.valueOf(location.getAltitude()).floatValue(),
//                System.currentTimeMillis()
//        );
//

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LogClass.e("onLocationChanged", "" + location.getSpeedAccuracyMetersPerSecond());
        }

        try {
            boolean foregroud = new ForegroundCheckTask().execute(mContext).get();
            LogClass.e("foregroud", "foregroud :" + foregroud);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        heading= bearingToNorthProvider.getBearing();
        LogClass.e("bearingToNorthProvider", "bearingToNorthProvider :" +   heading);
        Intent intent = new Intent("locationData");
        intent.putExtra("Lat", "" + location.getLatitude());
        intent.putExtra("Long", "" + location.getLongitude());
        intent.putExtra("accurarcy", "" + location.getAccuracy());
        intent.putExtra("timestamp", "" + location.getTime());
        intent.putExtra("altitude", "" + location.getAltitude());
        intent.putExtra("speed", "" + location.getSpeed());
        intent.putExtra("heading", "" + heading);
        mContext.sendBroadcast(intent);
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void setTripLogs(String speed, String altitude, String heading, String latitude, String longitude, String accuracy, String timestamp) {
        String configResponse = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.CONFIG_RESPONSE_BODY);

        try {
            JSONObject jsonObject = new JSONObject(configResponse);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ApiConstant.Urls.TRIP_LOGS;
        Map<String, Object> params = new HashMap<>();


//        params.put(ApiConstant.ApiKeys.TRIP_ID, );
        params.put(ApiConstant.ApiKeys.SPEED, speed);
        params.put(ApiConstant.ApiKeys.ALTITUDE, altitude);
        params.put(ApiConstant.ApiKeys.HEADING, heading);
        params.put(ApiConstant.ApiKeys.LATITUDE, latitude);
        params.put(ApiConstant.ApiKeys.LONGITUDE, longitude);
        params.put(ApiConstant.ApiKeys.ACCURACY, accuracy);
        params.put(ApiConstant.ApiKeys.TIMESTAMP, timestamp);
        OkHttpWrapper okHttpWrapper = new OkHttpWrapper();


        okHttpWrapper.setResponseListener(new OkHttpWrapper.ResponseLickListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogClass.e("onFailure", "error :" + e);

            }

            @Override
            public void onResponse(Call call, Response response) {

                LogClass.e("onResponse", "response :" + response);
                try {
                    final String myResponse = response.body().string();
                    LogClass.e("onResponse", "body :" + myResponse);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void checkInterNet(boolean status) {

                LogClass.e("checkInterNet", "status :" + status);
            }
        });

        okHttpWrapper.postApiCall(url, params, mContext);
    }

    @Override
    public void onBearingChanged(double bearing) {

        LogClass.e("bearing","bearing: "+bearing);
    }


    class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }


}

