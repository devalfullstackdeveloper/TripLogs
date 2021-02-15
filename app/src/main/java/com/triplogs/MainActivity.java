package com.triplogs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.triplogs.helper.ApiConstant;
import com.triplogs.helper.LogClass;
import com.triplogs.helper.OkHttpWrapper;
import com.triplogs.helper.SharedPrefHelper;
import com.triplogs.user.Home;
import com.triplogs.user.LocationActivity;
import com.triplogs.user.LoginScreen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
          //  Intent i = new Intent(MainActivity.this, Home.class);

            if(SharedPrefHelper.getPrefsHelper().isPrefExists(SharedPrefHelper.CONFIG_RESPONSE_BODY)){

                    Intent i = new Intent(MainActivity.this, Home.class);
                    startActivity(i);
                    finish();

            }else {
                Intent i = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(i);
                finish();
            }

        }, 1500);

    }



}