package com.appbuilders.animediapremium.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.appbuilders.animediapremium.Core.Credentials;
import com.appbuilders.animediapremium.Views.SplashView;
import com.appbuilders.credentials.CredentialsOptions;
import com.appbuilders.credentials.Preferences;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.removeCache();

        Preferences prefs = new Preferences(this);
        //prefs.removePreference("advances");
        prefs.printExistedPreferences();


        CredentialsOptions options = new CredentialsOptions("https://appbuilders/apis/animedia",
                "http://192.168.1.69/appbuilders/apis/animedia", "8f6952dfc83073f80afbc048857d52d533a57970", false);
        com.appbuilders.credentials.Credentials credentials = com.appbuilders.credentials.Credentials.getInstance(this).Builder(options).build();

        String token = FirebaseInstanceId.getInstance().getToken();
        credentials.needSynchronize((token != null) ? token : "");

        new SplashView(this, true);
    }

    protected void removeCache() {

        Credentials credentials = Credentials.getInstance(this);
        credentials.removePreference("latestAnimes");
        //credentials.removePreference("genresAnimes");
    }
}