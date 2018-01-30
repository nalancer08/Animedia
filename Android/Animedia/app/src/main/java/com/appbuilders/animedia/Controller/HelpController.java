package com.appbuilders.animedia.Controller;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.appbuilders.animedia.Adapter.HelpAdapter;
import com.appbuilders.animedia.Controls.CirclePageIndicator;
import com.appbuilders.animedia.Fragment.HelpOne;
import com.appbuilders.animedia.Fragment.HelpThree;
import com.appbuilders.animedia.Fragment.HelpTwo;
import com.appbuilders.animedia.R;

import java.util.ArrayList;

public class HelpController extends AppCompatActivity {

    private HelpAdapter pagerAdapter;
    private ViewPager pagerView;
    private CirclePageIndicator circlePagerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_controller);

        // Setting full-screen
        this.fullScreen();

        // Getting fragment manager
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        // Making list of Fragments
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new HelpOne());
        fragments.add(new HelpTwo());
        fragments.add(new HelpThree());

        // Setting pager
        this.pagerAdapter = new HelpAdapter(fm, fragments);
        this.pagerView = (ViewPager) findViewById(R.id.pager);
        this.pagerView.setAdapter(this.pagerAdapter);
        this.pagerView.setOffscreenPageLimit(3);
        //this.pagerView.setPagingEnabled(false);
        //this.pagerView.setCurrentItem(1, true);

        this.circlePagerView = (CirclePageIndicator) findViewById(R.id.indicator);
        this.circlePagerView.setViewPager(this.pagerView);
        final float density = getResources().getDisplayMetrics().density;
        this.circlePagerView.setRadius(7 * density);
        this.setPagerListener();
    }

    protected void setPagerListener() {

        this.circlePagerView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 2) {
                    circlePagerView.setVisibility(View.INVISIBLE);
                } else if (circlePagerView.getVisibility() == View.INVISIBLE){
                    circlePagerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

                if (position == 2) {
                    circlePagerView.setVisibility(View.INVISIBLE);
                } else if (circlePagerView.getVisibility() == View.INVISIBLE){
                    circlePagerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            //Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            //Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
}
