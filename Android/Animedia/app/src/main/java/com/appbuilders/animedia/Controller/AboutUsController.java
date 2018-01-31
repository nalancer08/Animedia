package com.appbuilders.animedia.Controller;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appbuilders.animedia.Libraries.Social.Facebook;
import com.appbuilders.animedia.Libraries.Social.Mail;
import com.appbuilders.animedia.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionButton;

public class AboutUsController extends AppCompatActivity {

    private FloatingActionButton fb;
    private FloatingActionButton mail;
    private FloatingActionButton appbuilders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_controller);

        //this.whats = (FloatingActionButton) findViewById(R.id.whats_app);
        this.fb = (FloatingActionButton) findViewById(R.id.fb);
        this.mail = (FloatingActionButton) findViewById(R.id.mail);
        this.appbuilders = (FloatingActionButton) findViewById(R.id.appbuilders);

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

        this.appbuilders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOwnApps();
            }
        });

    }

    public void openOwnApps() {

        final Dialog dialog = new Dialog(AboutUsController.this);
        dialog.setContentView(R.layout.appbuilders_apps_dialog);
        dialog.getWindow().setLayout(getX() - 20, getY() - threeRuleY(200));
        dialog.setCanceledOnTouchOutside(true);

        Button download_fotomultas = dialog.findViewById(R.id.download_fotomultas);
        Button download_poke = dialog.findViewById(R.id.download_poke);

        download_fotomultas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String appPackageName = "com.appbuilders.fotomultascdmx";

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        download_poke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String appPackageName = "com.appbuilders.quienesestepoke";

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        dialog.show();
    }

    protected int threeRuleY(int value) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heigth = size.y;

        return (heigth * value) / 1794;
    }

    protected int threeRuleX(int value) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width * value) / 1000;
    }

    protected int getY() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    protected int getX() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}