package com.appbuilders.animediapremium.Core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 10/11/17
 */

public class UninstallIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // fetching package names from extras
        String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");
        Log.d("DXGO", "PACKS ::: " + packageNames.toString());

        if(packageNames!=null){
            for(String packageName: packageNames){
                if(packageName!=null && packageName.equals("com.appbuilders.animedia")){
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                    new ListenActivities(context).start();

                }
            }
        }
    }

}
