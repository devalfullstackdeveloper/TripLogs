package com.triplogs;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.DisplayMetrics;

import com.triplogs.helper.SharedPrefHelper;

public class MyApplication extends Application {

    protected static MyApplication mInstance;
    private DisplayMetrics displayMetrics = null;
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static MyApplication getApp() {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new MyApplication();
            mInstance.onCreate();
            return mInstance;
        }
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        new SharedPrefHelper(this);


//        registerActivityLifecycleCallbacks(new ActivityLifecycleHandler());
    }

    /**
     * get device screen density
     */
    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    /**
     * get Screen Height of device
     *
     * @return
     */
    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(this.getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    /**
     * get Screen width of device
     *
     * @return
     */
    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(this.getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    /**
     * Convert dp into pixel
     */
    public int dp2px(float f) {
        return (int) (0.5F + f * getScreenDensity());
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel 2");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }

}