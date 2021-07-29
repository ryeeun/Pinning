package com.econo21.pinning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    ImageView splashImage;
    Animation anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate_up);
        splashImage = findViewById(R.id.splashImage);
        splashImage.startAnimation(anim);

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 3000); // 3000ms = 3초
    }

    private class SplashHandler implements Runnable{
        public void run(){
            startActivity(new Intent(SplashActivity.this, StartActivity.class));
            SplashActivity.this.finish();
        }
    }
}