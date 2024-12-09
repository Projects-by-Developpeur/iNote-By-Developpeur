package com.example.inote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactActivity extends AppCompatActivity {
    String whichone="";
    private LinearLayout gmailLayout, instagramLayout, tiktokLayout, facebookLayout, youtubeLayout, phoneLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        // Initialize the LinearLayouts for each card
        gmailLayout = findViewById(R.id.gp);
        instagramLayout = findViewById(R.id.igp);
        tiktokLayout = findViewById(R.id.tp);
        facebookLayout = findViewById(R.id.fbp);
        youtubeLayout = findViewById(R.id.yp);
        phoneLayout = findViewById(R.id.cp);

        // Set click listeners for each card layout
        setupClickListeners();

        Intent intent = getIntent();

            if (!intent.getStringExtra("contact").isEmpty() && intent.getStringExtra("contact")!=null)
            {
                if (intent.getStringExtra("contact").equals("contactG")){
                    whichone="guest";
                }

                if (intent.getStringExtra("contact").equals("contactP")){
                    whichone="profile";
                }
            }



    }

    private void setupClickListeners() {
        // Gmail Click
        gmailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail("contact.developpeur27@gmail.com");
            }
        });

        // Instagram Click
        instagramLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstagram("@developpeur.27");
            }
        });

        // TikTok Click
        tiktokLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTikTok("@dveloppeur.27");
            }
        });

        // Facebook Click
        facebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebook();
            }
        });

        // YouTube Click
        youtubeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYouTube("@developpeur.27");
            }
        });

        // Phone Click
        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall("0542577666");
            }
        });
    }

    private void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + email));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    // Method to open Instagram
    private void openInstagram(String username) {
        String url = "http://instagram.com/" + username.replace("@", "");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    // Method to open TikTok
    private void openTikTok(String username) {
        String url = "https://www.tiktok.com/@"+ username.replace("@", "");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    // Method to open Facebook
    private void openFacebook() {
        String url = "https://www.facebook.com/profile.php?id=61554111433446";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    // Method to open YouTube
    private void openYouTube(String username) {
        String url = "https://www.youtube.com/@"+ username.replace("@", "");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    // Method to make a phone call
    private void makePhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
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