package com.appbuilders.animedia.Libraries.Social;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by saer6003 on 23/02/2017.
 */

public class Facebook {


    /* This method return the URL for Intent */
    public static String getFacebookPageURL(Context context, String url, String page_id) {

        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + url;
            } else { //older versions of fb app
                return "fb://page/" + page_id;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return url; //normal web url
        }
    }

    public static void openFbPage(Context context, String url, String page_id) {

        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = Facebook.getFacebookPageURL(context.getApplicationContext(), "https://www.facebook.com/YourPageName", "YourPageName");
        facebookIntent.setData(Uri.parse(facebookUrl));
        context.getApplicationContext().startActivity(facebookIntent);

    }

}
