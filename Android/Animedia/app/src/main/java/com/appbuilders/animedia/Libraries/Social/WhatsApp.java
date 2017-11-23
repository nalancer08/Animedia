package com.appbuilders.animedia.Libraries.Social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by saer6003 on 23/02/2017.
 */

public class WhatsApp {

    public static void sendMessage(String number, String message, Context context) {

        Uri mUri = Uri.parse("smsto:" + number);
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
        mIntent.setPackage("com.whatsapp");
        mIntent.putExtra("sms_body", "message");
        mIntent.putExtra("chat", true);
        context.getApplicationContext().startActivity(mIntent);
    }
}
