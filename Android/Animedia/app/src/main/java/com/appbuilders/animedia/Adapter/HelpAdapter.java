package com.appbuilders.animedia.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 18/01/18
 */

public class HelpAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> mFragments;
    FragmentManager mFragmentManager;

    public HelpAdapter(FragmentManager fm) {

        super(fm);
    }

    public HelpAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {

        super(fm);
        this.mFragmentManager = fm;
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        return this.mFragments.get(position);
    }

    @Override
    public int getCount() {

        return mFragments.size();
    }
}
