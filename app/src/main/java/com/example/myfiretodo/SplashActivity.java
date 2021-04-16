package com.example.myfiretodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private Animation titleAnim, subTitleAnim;
    private TextView mTextViewTitle, mTextViewSubTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Objects.requireNonNull(getSupportActionBar()).hide();


        //Finding by Ids
        mTextViewTitle = findViewById(R.id.splashTitle);
        mTextViewSubTitle = findViewById(R.id.splashSubtitle);

        titleAnim = AnimationUtils.loadAnimation(this,R.anim.title_anim);
        subTitleAnim = AnimationUtils.loadAnimation(this,R.anim.subtitle_anim);


        mTextViewTitle.setAnimation(titleAnim);
        mTextViewSubTitle.setAnimation(subTitleAnim);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
            }
        },3300);



    }
}