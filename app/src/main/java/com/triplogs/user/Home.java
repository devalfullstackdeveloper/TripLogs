package com.triplogs.user;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.jtv7.rippleswitchlib.RippleSwitch;
import com.triplogs.R;
import com.triplogs.helper.ApiConstant;
import com.triplogs.helper.LogClass;
import com.triplogs.helper.MyLocationService;
import com.triplogs.helper.OkHttpWrapper;
import com.triplogs.helper.SharedPrefHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Response;

public class Home extends AppCompatActivity {

    CardView cardView;
    RippleSwitch rippleSwitch;
    ImageView imgSetting;

    TextView tvSpeed, tvAltitude, tvHeading, tvLatitude, tvLongitude, tvAccuracy, tvTimestamp, tvDeviceId, tvVCode;
    RadioButton radioButton1, radioButton2;

    boolean isLocationOn = false;

    LocationManager lm;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initId();
        setOnclick();
        radioButton2.setChecked(true);
        getDuration();
        IntentFilter filter = new IntentFilter("locationData");
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case "locationData":
                        String Lat = intent.getStringExtra("Lat");
                        String Long = intent.getStringExtra("Long");
                        String accurarcy = intent.getStringExtra("accurarcy");
                        String timestamp = intent.getStringExtra("timestamp");
                        String altitude = intent.getStringExtra("altitude");
                        String heading = intent.getStringExtra("heading");
                        String speed = intent.getStringExtra("speed");
                        if (rippleSwitch.isChecked()) {

                            tvAltitude.setText(" "+altitude + ",");
                            tvHeading.setText(" "+heading + ",");
                            tvLatitude.setText(" "+Lat + ",");
                            tvLongitude.setText(" "+Long + ",");
                            tvAccuracy.setText(" "+accurarcy + ",");
                            tvTimestamp.setText(" "+timestamp + ",");

                            if (radioButton1.isChecked()) {
                                tvSpeed.setText(speed);
                            } else {
                                float sp = (float) (Float.parseFloat(speed) / 1.609);
                                tvSpeed.setText(" "+sp + "");
                            }


                        }

                        break;
                }
            }

        }, filter);

        if (SharedPrefHelper.getPrefsHelper().isPrefExists(SharedPrefHelper.CONFIG_RESPONSE_BODY)) {
            if (SharedPrefHelper.getPrefsHelper().isPrefExists(SharedPrefHelper.PREF_IS_CHECK)) {
                if (SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.PREF_IS_CHECK).equals("true")) {
                    rippleSwitch.setChecked(true);
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.sky_blue));
                    @SuppressLint("HardwareIds")
                    String android_id = Settings.Secure.getString(Home.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    tvDeviceId.setText(" "+android_id + ",");
                    startMyService();
                }
            }
            String configResponse = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.CONFIG_RESPONSE_BODY);
            LogClass.e("configResponse", "configResponse :-> " + configResponse);
        }


        radioButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                radioButton1.setChecked(true);
                radioButton2.setChecked(false);
                return true;
            }
        });
        radioButton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                radioButton2.setChecked(true);
                radioButton1.setChecked(false);
                return true;
            }
        });

        IntentFilter filter2 = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter2.addAction(Intent.ACTION_PROVIDER_CHANGED);
        registerReceiver(gpsSwitchStateReceiver, filter2);


    }


    private String getDate(long time) {
        SimpleDateFormat simpleformat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleformat.format(time);
    }


    private void initId() {

        cardView = findViewById(R.id.cv_card);
        rippleSwitch = findViewById(R.id.rippleSwitch);
        imgSetting = findViewById(R.id.img_setting);
        tvSpeed = findViewById(R.id.tv_speed);
        tvAltitude = findViewById(R.id.tv_altitude);
        tvHeading = findViewById(R.id.tv_heading);
        tvLatitude = findViewById(R.id.tv_latitude);
        tvLongitude = findViewById(R.id.tv_longitude);
        tvAccuracy = findViewById(R.id.tv_accuracy);
        tvTimestamp = findViewById(R.id.tv_timestamp);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        tvDeviceId = findViewById(R.id.tv_device_id);
        tvVCode = findViewById(R.id.tv_v_code);
        tvVCode.setText("V " + ApiConstant.Versions.CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkOnOf();
        if(rippleSwitch.isChecked()){
            checkOnOf();
        }
    }

    private void setOnclick() {

        rippleSwitch.setOnCheckedChangeListener(b -> {
            if (b) {
                checkOnOf();
            } else {
                SharedPrefHelper.getPrefsHelper().setData(SharedPrefHelper.PREF_IS_CHECK, "false");
                cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                stopMyService();
                stopTrip();
                tvSpeed.setText("");
                tvAltitude.setText("");
                tvHeading.setText("");
                tvLatitude.setText("");
                tvLongitude.setText("");
                tvAccuracy.setText("");
                tvTimestamp.setText("");
                tvDeviceId.setText("");
            }
//            if( checkOnOf()) {
//                if (b) {
//                    if(isLocationOn) {
//                        SharedPrefHelper.getPrefsHelper().setData(SharedPrefHelper.PREF_IS_CHECK, "true");
//                        cardView.setCardBackgroundColor(getResources().getColor(R.color.sky_blue));
//                        startTrip();
//                        startMyService();
//
//                        @SuppressLint("HardwareIds")
//                        String android_id = Settings.Secure.getString(Home.this.getContentResolver(), Settings.Secure.ANDROID_ID);
//                        tvDeviceId.setText(android_id);
//                    }
//                } else {
//
//                    SharedPrefHelper.getPrefsHelper().setData(SharedPrefHelper.PREF_IS_CHECK, "false");
//                    cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
//                    stopMyService();
//                    stopTrip();
//                    tvSpeed.setText("");
//                    tvAltitude.setText("");
//                    tvHeading.setText("");
//                    tvLatitude.setText("");
//                    tvLongitude.setText("");
//                    tvAccuracy.setText("");
//                    tvTimestamp.setText("");
//                    tvDeviceId.setText("");
//
//                }
//            }

        });

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);

//                builder.setCancelable(false);
//                builder.setMessage(" Do you want to change your server \n configuration ? ")
//                        .setCancelable(true)
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                //   dialog.dismiss();
////                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
////                                startActivity(browserIntent);
//
//                            }
//                        })
//
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                   dialog.dismiss();
////                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
////                                startActivity(browserIntent);
//
//                            }
//                        });

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_label_editor, null);
                builder.setView(dialogView);

                TextView textYes = (TextView) dialogView.findViewById(R.id.textView_yes);
                TextView textNo = (TextView) dialogView.findViewById(R.id.textView_no);

                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.show();
                textYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        Intent i = new Intent(Home.this, LoginScreen.class);
                        startActivity(i);
                    }
                });
                textNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
//                PowerMenu dialogMenu = getDialogPowerMenu(Home.this, Home.this);
//
//
//                View footerView = dialogMenu.getFooterView();
//                TextView textView_yes = footerView.findViewById(R.id.textView_yes);
//                textView_yes.setOnClickListener(
//                        view -> {
//                            //   Toast.makeText(getBaseContext(), "Yes", Toast.LENGTH_SHORT).show();
//                            dialogMenu.dismiss();
//                            Intent i = new Intent(Home.this, LoginScreen.class);
//                            startActivity(i);
//                            // finish();
//
//                        });
//                TextView textView_no = footerView.findViewById(R.id.textView_no);
//                textView_no.setOnClickListener(
//                        view -> {
//                            // Toast.makeText(getBaseContext(), "No", Toast.LENGTH_SHORT).show();
//                            dialogMenu.dismiss();
//                        });
//                dialogMenu.showAtCenter(v);
            }
        });
    }

    private void startMyService() {
        // Intent startIntent = new Intent(Home.this, LocationForegroundService.class);
        Intent startIntent = new Intent(Home.this, MyLocationService.class);
        startIntent.setAction("start");
        startService(startIntent);

    }


    private void stopMyService() {

        //  Intent stopIntent = new Intent(Home.this, LocationForegroundService.class);
        Intent stopIntent = new Intent(Home.this, MyLocationService.class);
        stopIntent.setAction("stop");
        startService(stopIntent);
    }

    String android_id = "";

    @SuppressLint("HardwareIds")
    private void startTrip() {
        android_id = Settings.Secure.getString(Home.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        new StartTrip().execute();

    }

    private void stopTrip() {
        android_id = Settings.Secure.getString(Home.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        new StopTrip().execute();

    }

    public String convertToIso() {
      //  String date = getDate(new Date().getTime()) + "";

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
       // LogClass.e("dateShiv", "status :" +dateFormatGmt.format(new Date()) );

        return ""+dateFormatGmt.format(new Date());
    }

    private void getDuration() {

        String loginResponse = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.CONFIG_RESPONSE_BODY, "");
        if (!loginResponse.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(loginResponse);
                JSONObject response = jsonObject.getJSONObject("response");
                JSONObject apiurl = response.getJSONObject("apiurl");
                String baseUrl = apiurl.getString("url");
                String url = baseUrl + ApiConstant.Urls.CONFIG;
                OkHttpWrapper okHttpWrapper = new OkHttpWrapper();
                LogClass.e("OkHttpWrapper", "url: " + url);
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
                            try {
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONObject jsonObject2 = jsonObject.getJSONObject("responceData");
                                String durationTime = jsonObject2.getString("apiCallDuration");
                                LogClass.e("onResponse", "durationTime :" + durationTime);
                                SharedPrefHelper.getPrefsHelper().setData(SharedPrefHelper.API_CALL_DURATION, durationTime);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                LogClass.e("onResponse", "durationTime error :" + e);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }



                    @Override
                    public void checkInterNet(boolean status) {

                        LogClass.e("checkInterNet", "status :" + status);
                    }
                });

                okHttpWrapper.getApiCall(url, Home.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    public class StartTrip extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // android_id
            //  android_id="jigar_patel_111";
            String myResponse = "";
                String date =  convertToIso() + "";
            String mainObj = "{\"startTime\": \"" + date + "\",\"stopTime\": \"\",\"deviceId\": \"" + "" + android_id + "" + "\"}";
            LogClass.e("stsrtripLog", "mainObj :" + mainObj);

            try {
                String loginResponse = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.CONFIG_RESPONSE_BODY, "");
                JSONObject jsonObject = new JSONObject(loginResponse);
                JSONObject response = jsonObject.getJSONObject("response");
                JSONObject apiurl = response.getJSONObject("apiurl");
                String baseUrl = apiurl.getString("url");
                String url = baseUrl + ApiConstant.Urls.START_TRIP;
                myResponse = new OkHttpWrapper().post(url, mainObj);
                LogClass.e("stsrtripLog", "body :" + myResponse);

                JSONObject jsonObjecttt = new JSONObject(myResponse);
                JSONObject responceData = jsonObjecttt.getJSONObject("responceData");
                String _id = responceData.getString("_id");
                SharedPrefHelper.getPrefsHelper().setData(SharedPrefHelper.TRIP_ID, _id);
                LogClass.e("stsrtripLog", "_id :" + _id);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                LogClass.e("stsrtripLog", "body :" + e);
            }

            return myResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            LogClass.e("stsrtripLog", "result :" + result);
            startMyService();
        }
    }

    public class StopTrip extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String tripId = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.TRIP_ID);
            String myResponse = "";
            String date =  convertToIso()  + "";
            // String mainObj = "{\"startTime\": \"\",\"stopTime\": \""+date+"\",\"deviceId\": \""+android_id+"\"}";
            //   String mainObj = " {_id: \""+tripId+"\", startTime: \"\", stopTime: \""+date+"\", deviceId: \""+android_id+"\" } ";
            String mainObj = "{ \"_id\": \"" + tripId + "\", \"startTime\": \"\",\"stopTime\": \"" + date + "\",\"deviceId\": \"" + android_id + "\"}";


            try {
                String loginResponse = SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.CONFIG_RESPONSE_BODY, "");
                JSONObject jsonObject = new JSONObject(loginResponse);
                JSONObject response = jsonObject.getJSONObject("response");
                JSONObject apiurl = response.getJSONObject("apiurl");
                String baseUrl = apiurl.getString("url");
                String url = baseUrl + ApiConstant.Urls.START_TRIP;
                LogClass.e("stsrtripLog", "mainObj :" + mainObj + " url: " + url);
                myResponse = new OkHttpWrapper().put(url, mainObj);
                LogClass.e("stsrtripLog", "body :" + myResponse);


            } catch (IOException | JSONException e) {
                e.printStackTrace();
                LogClass.e("stsrtripLog", "body :" + e);
            }

            return myResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            LogClass.e("stsrtripLog", "result :" + result);
        }
    }


    private void checkOnOf() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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


            new AlertDialog.Builder(Home.this)
                    .setMessage("Please enable location from your phone setting")
                    .setPositiveButton("allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setCancelable(false)
                    .show();


        } else {

            isLocationOn = true;
            SharedPrefHelper.getPrefsHelper().setData(SharedPrefHelper.PREF_IS_CHECK, "true");
            cardView.setCardBackgroundColor(getResources().getColor(R.color.sky_blue));
            startTrip();


            @SuppressLint("HardwareIds")
            String android_id = Settings.Secure.getString(Home.this.getContentResolver(), Settings.Secure.ANDROID_ID);
            tvDeviceId.setText(" "+android_id+",");
        }
    }

    private BroadcastReceiver gpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGpsEnabled || isNetworkEnabled) {
                    //   Toast.makeText(getApplicationContext(),"on",Toast.LENGTH_LONG).show();
                    isLocationOn = true;
                    if (rippleSwitch.isChecked()) {
                        SharedPrefHelper.getPrefsHelper().setData(SharedPrefHelper.PREF_IS_CHECK, "true");
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.sky_blue));
                        startTrip();
                        startMyService();

                        @SuppressLint("HardwareIds")
                        String android_id = Settings.Secure.getString(Home.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                        tvDeviceId.setText(" "+android_id+",");
                    }
                } else {

                    isLocationOn = false;
//                    SharedPrefHelper.getPrefsHelper().setData(SharedPrefHelper.PREF_IS_CHECK, "false");
//                    cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
//                    stopMyService();
//                    stopTrip();
//                    tvSpeed.setText("");
//                    tvAltitude.setText("");
//                    tvHeading.setText("");
//                    tvLatitude.setText("");
//                    tvLongitude.setText("");
//                    tvAccuracy.setText("");
//                    tvTimestamp.setText("");
//                    tvDeviceId.setText("");
//
//                    rippleSwitch.setChecked(false);
                }
            }
        }
    };
}