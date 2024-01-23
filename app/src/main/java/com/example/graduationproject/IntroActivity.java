package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class IntroActivity extends AppCompatActivity {
    private Animation top,right;

    private ImageView img;
    private TextView text;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        top= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        right=AnimationUtils.loadAnimation(this,R.anim.right_animation);

        img=findViewById(R.id.logoImg);
        text=findViewById(R.id.logoText);

        img.setAnimation(top);
        text.setAnimation(right);

        Handler handler=new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(IntroActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}