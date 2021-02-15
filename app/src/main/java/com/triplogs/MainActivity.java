package com.triplogs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.triplogs.helper.SharedPrefHelper;
import com.triplogs.user.Home;
import com.triplogs.user.LoginScreen;

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