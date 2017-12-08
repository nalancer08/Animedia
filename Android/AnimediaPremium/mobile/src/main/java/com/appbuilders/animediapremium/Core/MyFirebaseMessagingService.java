package com.appbuilders.animediapremium.Core;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 10/11/17
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "DXGOP";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        //Log.d(TAG, "MEssage ::: " + remoteMessage.toString());
        //Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Log.d(TAG, "Collapse Key : " + remoteMessage.getCollapseKey());
        Log.d(TAG, "From : " + remoteMessage.getFrom());
        Log.d(TAG, "Message ID : " + remoteMessage.getMessageId());
        Log.d(TAG, "Message Type : " + remoteMessage.getMessageType());
        Log.d(TAG, "To : " + remoteMessage.getTo());
        Log.d(TAG, "Data : " + remoteMessage.getData());
        Log.d(TAG, "Notification : " + remoteMessage.getNotification().toString());
        Log.d(TAG, "Notification Body : " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "Notification Body Localization Key : " + remoteMessage.getNotification().getBodyLocalizationKey());
        Log.d(TAG, "Notification Action : " + remoteMessage.getNotification().getClickAction());
        Log.d(TAG, "Notification Color : " + remoteMessage.getNotification().getColor());
        Log.d(TAG, "Notification icon : " + remoteMessage.getNotification().getIcon());
        Log.d(TAG, "Notification sound : " + remoteMessage.getNotification().getSound());
        Log.d(TAG, "Notification tag : " + remoteMessage.getNotification().getTag());
        Log.d(TAG, "Notification title : " + remoteMessage.getNotification().getTitle());
        Log.d(TAG, "Notification title localization : " + remoteMessage.getNotification().getTitleLocalizationKey());
        Log.d(TAG, "Notification body args : " + Arrays.toString(remoteMessage.getNotification().getBodyLocalizationArgs()));
        Log.d(TAG, "Notification title args : " + Arrays.toString(remoteMessage.getNotification().getTitleLocalizationArgs()));
        Log.d(TAG, "Notification link : " + remoteMessage.getNotification().getLink());
        Log.d(TAG, "Send time : " + remoteMessage.getSentTime());
        Log.d(TAG, "Ttl : " + remoteMessage.getTtl());
    }
}
