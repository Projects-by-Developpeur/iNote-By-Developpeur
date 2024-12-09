package com.example.inote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.gesture.GestureUtils;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView iNote, guest;
    private ImageView logo;
    private Button buttonLogin, buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        iNote = findViewById(R.id.iNote);
        guest = findViewById(R.id.guest);
        logo = findViewById(R.id.logo);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignUp= findViewById(R.id.buttonSignUp);

        // Load animations
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        Animation animationButton = AnimationUtils.loadAnimation(this, R.anim.animation2);

        logo.setAnimation(animation );
        logo.setVisibility(View.VISIBLE);
        iNote.setAnimation(animation);
        iNote.setVisibility(View.VISIBLE);

         buttonLogin.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonLogin.setAnimation(animation );
                buttonLogin.setVisibility(View.VISIBLE);
            }
        }, 150);

        buttonSignUp.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonSignUp.setAnimation(animation );
                buttonSignUp.setVisibility(View.VISIBLE);
            }
        }, 250);

        guest.postDelayed(new Runnable() {
            @Override
            public void run() {
                guest.setAnimation(animationButton );
                guest.setVisibility(View.VISIBLE);
            }
        }, 350);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                finish();

            }
        });
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GuestActivity.class));
                finish();

            }
        });
    }

}