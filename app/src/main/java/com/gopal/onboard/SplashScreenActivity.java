package com.gopal.onboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.gopal.ebookapp.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash_screen );

        //Splash Screen duration
        int secondsDelayed = 1;
        new Handler().postDelayed( new Runnable() {
            public void run() {
                startActivity( new Intent( getApplicationContext(), IntroActivity.class ) );
                finish();
            }
        }, secondsDelayed * 1500 );

    }

}