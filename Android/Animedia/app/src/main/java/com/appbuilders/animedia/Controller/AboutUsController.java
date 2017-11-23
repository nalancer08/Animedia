package com.appbuilders.animedia.Controller;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.appbuilders.animedia.Libraries.Social.Facebook;
import com.appbuilders.animedia.Libraries.Social.Mail;
import com.appbuilders.animedia.R;
import com.github.clans.fab.FloatingActionButton;

public class AboutUsController extends AppCompatActivity {

    private FloatingActionButton fb;
    private FloatingActionButton mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_controller);

        //this.whats = (FloatingActionButton) findViewById(R.id.whats_app);
        this.fb = (FloatingActionButton) findViewById(R.id.fb);
        this.mail = (FloatingActionButton) findViewById(R.id.mail);

        this.fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = Facebook.getFacebookPageURL(getApplicationContext(), "https://www.facebook.com/appbuildersoficial/", "appbuildersoficial");
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });

        this.mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = Mail.from(getApplicationContext())
                        .to("erick.appbuilders@gmail.com")
                        .subject("Hola! App Builders")
                        .build();
                startActivity(emailIntent);
            }
        });


    }


}
