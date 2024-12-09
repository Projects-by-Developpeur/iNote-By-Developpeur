package com.example.inote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private Button button;
    FirebaseAuth auth;
    FirebaseUser curretuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        auth = FirebaseAuth.getInstance();
        curretuser = auth.getCurrentUser();

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.buttonWrite);

        // Load animations
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        Animation animationButton = AnimationUtils.loadAnimation(this, R.anim.animation2);


        textView.setAnimation(animation);
        textView.setVisibility(View.VISIBLE);
        imageView.setAnimation(animation );
        imageView.setVisibility(View.VISIBLE);

        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setAnimation(animationButton );
                button.setVisibility(View.VISIBLE);
            }
        }, 1000); // Start button after 400ms


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to start new activity
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(curretuser!=null){
            Intent intent=new Intent(WelcomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }
    }

    }
