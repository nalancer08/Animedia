package com.appbuilders.animedia.Controller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.appbuilders.animedia.Controls.AutoResizeTextView;
import com.appbuilders.animedia.R;

public class PremiumVersionController extends AppCompatActivity {

    AutoResizeTextView lastPrice;
    Button buttonPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_version);

        // Setting status bar
        setStatusBarColor();

        // Getting the last price, to set middle line
        this.lastPrice = (AutoResizeTextView) findViewById(R.id.autoResizeTextView6);
        this.lastPrice.setPaintFlags(this.lastPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Setting premium button
        this.buttonPremium = (Button) findViewById(R.id.make_me_premium);
        this.buttonPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String appPackageName = "com.appbuilders.animediapremium";

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }

    protected void setStatusBarColor() {

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.rgb(0, 197, 155));
        }
    }

}
