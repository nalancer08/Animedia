package com.appbuilders.animedia.Controller;

import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.SplashView;
import com.appbuilders.credentials.Configurations;
import com.appbuilders.credentials.CredentialsOptions;
import com.appbuilders.credentials.Preferences;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SplashController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.removeCache();

        Preferences prefs = new Preferences(this);
        //prefs.removePreference("advances");
        prefs.printExistedPreferences();


        CredentialsOptions options = new CredentialsOptions(
                "https://appbuilders.com.mx/apis/animedia",
                "http://192.168.1.68/appbuilders/apis/animedia",
                "8f6952dfc83073f80afbc048857d52d533a57970",
                true);
        com.appbuilders.credentials.Credentials credentials = com.appbuilders.credentials.Credentials.getInstance(this).Builder(options).build();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("DXGOP", "Firebase ID ::: " + token);
        credentials.needSynchronize((token != null) ? token : "");

        new SplashView(this, false);
    }

    protected void removeCache() {

        Credentials credentials = Credentials.getInstance(this);
        credentials.removePreference("latestAnimes");
        credentials.removePreference("allAnimes");
        //credentials.removePreference("genresAnimes");
    }
}