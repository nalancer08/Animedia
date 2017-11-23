package com.appbuilders.animedia.Controller;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.appbuilders.animedia.BuildConfig;
import com.appbuilders.animedia.R;
import com.appbuilders.credentials.Configurations;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.polyak.iconswitch.IconSwitch;

import java.io.IOException;

public class ConfigurationsController extends AppCompatActivity {

    private IconSwitch native_video_switch;
    private IconSwitch volume_switch;
    private TextView app_version;
    private Configurations configurations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurations_controller);

        // Getting views
        this.native_video_switch = (IconSwitch) findViewById(R.id.video_native_switch);
        this.volume_switch = (IconSwitch) findViewById(R.id.volume_switch);
        this.app_version = (TextView) findViewById(R.id.app_version);

        //TextView titlePage = (TextView) findViewById(R.id.textView2);
        //titlePage.setTypeface(Typeface.createFromAsset( this.getAssets(), "Specify.ttf"));

        // Getting configurations
        this.configurations = Configurations.getInstance(this);

        // Cheking native volume
        if (configurations.exists("native_video_switch")) {
            this.native_video_switch.setChecked(IconSwitch.Checked.RIGHT);
        }

        // Checking
        if (configurations.exists("volume_switch")) {
            this.volume_switch.setChecked(IconSwitch.Checked.RIGHT);
        }

        // Setting callbacks
        this.setNativeVideoSwitchCallback();
        this.setVolumeSwitchCallback();

        // Setting app verison text
        this.app_version.setText("Version " + BuildConfig.VERSION_NAME);
    }

    private void setNativeVideoSwitchCallback() {

        this.native_video_switch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked checked) {

                if (checked.toString().equals("RIGHT")) { // Taken option
                    configurations.add("native_video_switch", true);
                } else if (checked.toString().equals("LEFT")) { // Delete option
                    configurations.remove("native_video_switch");
                }
            }
        });
    }

    private void setVolumeSwitchCallback() {

        this.volume_switch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked checked) {

                if (checked.toString().equals("RIGHT")) { // Taken option
                    configurations.add("volume_switch", true);
                } else if (checked.toString().equals("LEFT")) { // Delete option
                    configurations.remove("volume_switch");
                }
            }
        });
    }
}