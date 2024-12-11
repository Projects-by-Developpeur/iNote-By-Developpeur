package com.example.inote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutUsActivity extends AppCompatActivity {
    String whichone="", whichoneContact="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_about_us);
        Intent intent = getIntent();

        if (!intent.getStringExtra("about").isEmpty() && intent.getStringExtra("about")!=null)
        {
            if (intent.getStringExtra("about").equals("aboutG")){
                whichone="guest";
                whichoneContact="contactG";
            }

            if (intent.getStringExtra("about").equals("aboutP")){
                whichone="profile";
                whichoneContact="contactP";
            }
        }

       findViewById(R.id.call_to_action_button).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent2 = new Intent(AboutUsActivity.this,ContactActivity.class);
               intent2.putExtra("contact",whichoneContact);
               startActivity(intent2);
               finish();
           }
       });


        //back button
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (whichone.equals("guest"))
                {
                    Intent intent2 = new Intent(AboutUsActivity.this,GuestActivity.class);
                    startActivity(intent2);
                    finish();
                } else  if (whichone.equals("profile"))
                {
                    Intent intent2 = new Intent(AboutUsActivity.this, ProfileActivity.class);
                    startActivity(intent2);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (whichone.equals("guest"))
        {
            Intent intent2 = new Intent(this,GuestActivity.class);
            startActivity(intent2);
            finish();
        } else  if (whichone.equals("profile"))
        {
            Intent intent2 = new Intent(this, ProfileActivity.class);
            startActivity(intent2);
            finish();
        }


    }

}