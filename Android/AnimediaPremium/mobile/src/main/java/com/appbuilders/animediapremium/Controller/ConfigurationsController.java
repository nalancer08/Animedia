package com.appbuilders.animediapremium.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appbuilders.animediapremium.BuildConfig;
import com.appbuilders.animediapremium.R;
import com.appbuilders.credentials.Configurations;
import com.polyak.iconswitch.IconSwitch;

public class ConfigurationsController extends AppCompatActivity {

    private IconSwitch native_video_switch;
    private IconSwitch volume_switch;
    private Button show_tutorial_again_button;
    private TextView app_version;
    private Configurations configurations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurations_controller);

        // Getting views
        this.native_video_switch = (IconSwitch) findViewById(R.id.video_native_switch);
        this.volume_switch = (IconSwitch) findViewById(R.id.volume_switch);
        this.show_tutorial_again_button = (Button) findViewById(R.id.show_tutorial_again_button);
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
        this.setShowTutorialAgain();

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

    private void setShowTutorialAgain() {

        this.show_tutorial_again_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Configurations configs = Configurations.getInstance(ConfigurationsController.this);
                configs.remove("showed_tutorial_" + BuildConfig.VERSION_NAME);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

}