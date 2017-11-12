package com.appbuilders.animedia.Controller;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.SplashView;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SplashController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.removeCache();
        new SplashView(this, true);
        //Log.d("DXGO", "Me la pela :: " + FirebaseInstanceId.getInstance().getToken());
        Log.d("DXGOP", "Me la pela :: " + Build.class.getFields().toString());


        JSONObject build = new JSONObject();

        Field[] fields = Build.class.getFields();
        Map<String, String> map = new HashMap<String, String>();
        for(Field f : fields)
            try {
            build.put(f.getName(), f.get(f.getName()));
            Log.d("DXGOP", "NAME ::: " + f.getName() + " ____ :::: " + f.get(f.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        //map.put(f.getName(),(String) f.get(entity));
        Log.d("DXGOP", "BUILD :::: " + build.toString());
    }

    protected void removeCache() {

        Credentials credentials = Credentials.getInstance(this);
        credentials.removePreference("latestAnimes");
        credentials.removePreference("ascAnimes");
        credentials.removePreference("genresAnimes");
    }
}
