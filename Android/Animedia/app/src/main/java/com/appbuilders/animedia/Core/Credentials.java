package com.appbuilders.animedia.Core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.appbuilders.animedia.Libraries.JsonFileManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by eec000i on 31/07/17.
 */

public class Credentials {

    private static Credentials mInstance;
    private Context context;
    private static String packagee = "animedia";

    private boolean debug = false;
    private String token = "e4803d711ba56d1bc9bf97a45e49d9f67b2e3bd8004893728a866f7e6cec1eeb.8f6952dfc83073f80afbc048857d52d533a57970";
    private String key = "8f6952dfc83073f80afbc048857d52d533a57970";
    private String url = "";

    protected int user_id = 0;
    protected String user_bearer = "";

    private String currentVersion = "";

    public static Credentials getInstance(Context context) {

        if ( mInstance == null ) {
            Class clazz = Credentials.class;
            synchronized (clazz) {
                mInstance = new Credentials(context);
            }
        }
        return mInstance;
    }

    public static Credentials getInstance(Context context, boolean debug) {

        if ( mInstance == null ) {
            Class clazz = Credentials.class;
            synchronized (clazz) {
                mInstance = new Credentials(context, debug);
            }
        }
        return mInstance;
    }

    /**
     * Contructor to generate in memory only once all the pokemons
     * */
    public Credentials(Context context) {

        this.context = context;
        this.init();
    }

    /**
     * Contructor to generate in memory only once all the pokemons
     * */
    public Credentials(Context context, boolean debug) {

        this.context = context;
        this.debug = debug;
        this.init();
    }

    private void init() {

        this.url = this.debug ? "http://192.168.1.69/appbuilders/apis/animedia" : "https://appbuilders.com.mx/apis/animedia";
        //this.url = this.debug ? "http://192.168.1.80/appbuilders/apis/animedia" : "http://appbuilders.com.mx/apis/animedia";
    }

    public String getToken() {

        return this.token;
    }

    public String getUrl() {

        return this.url;
    }

    public String getUserId() {

        String dataString = this.getPreference("userLogin");

        if (!dataString.equals("")) {

            try {

                JSONObject data = JsonFileManager.stringToJSON(dataString);
                    JSONObject user = data.getJSONObject("user");
                        String id = user.getString("id");
                return id;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getUserUuid() {

        String dataString = this.getPreference("userLogin");

        if (!dataString.equals("")) {

            try {

                JSONObject data = JsonFileManager.stringToJSON(dataString);
                JSONObject user = data.getJSONObject("user");
                String uuid = user.getString("uuid");
                return uuid;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getUserFbid() {

        String dataString = this.getPreference("userLogin");

        if (!dataString.equals("")) {

            try {

                JSONObject data = JsonFileManager.stringToJSON(dataString);
                JSONObject user = data.getJSONObject("user");
                String id = user.getString("fbid");
                return id;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getNicename() {

        String dataString = this.getPreference("userLogin");

        if (!dataString.equals("")) {

            try {

                JSONObject data = JsonFileManager.stringToJSON(dataString);
                    JSONObject user = data.getJSONObject("user");
                        String nicename = user.getString("nicename");
                return nicename;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getBearer() {

        String dataString = this.getPreference("userLogin");

        if (!dataString.equals("")) {

            try {

                JSONObject object = JsonFileManager.stringToJSON(dataString);
                String bearer = object.getString("bearer");
                return bearer;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getBit() {

        String dataString = this.getPreference("userLogin");

        if (!dataString.equals("")) {

            try {

                JSONObject object = JsonFileManager.stringToJSON(dataString);
                String bearer = object.getString("bit");
                return bearer;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void setUserLogin(JSONObject object ) {

        this.savePreference("userLogin", object.toString());
    }

    public boolean existsPreviousLogin() {

        return this.existsPreference("userLogin");
    }

    private JSONObject getDataof(String preference) {

        String prefString = this.getPreference(preference);

        if (!prefString.equals("")) {

            try {

                JSONObject object = JsonFileManager.stringToJSON(prefString);
                JSONObject data = object.getJSONObject("data");
                Log.d("AB_DEV", data.toString());
                return data;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /***********************************************************************************************
     *                              Methods to get and save preferences                            *
     ***********************************************************************************************/

    public String getPreference(String preference) {

        SharedPreferences prefs = this.context.getSharedPreferences(Credentials.packagee, Context.MODE_PRIVATE);
        return prefs.getString(preference, "");
    }

    public void savePreference(String key, String value) {

        SharedPreferences prefs = this.context.getSharedPreferences(Credentials.packagee, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void removePreference(String key) {

        if (existsPreference(key)) {
            SharedPreferences prefs = this.context.getSharedPreferences(Credentials.packagee, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.commit();
        }
    }

    public boolean existsPreference(String preference) {

        SharedPreferences prefs = this.context.getSharedPreferences(Credentials.packagee, Context.MODE_PRIVATE);
        String temp =  prefs.getString(preference, "");
        return (temp.equals("")) ? false : true;
    }

    public void printExistedPreferences() {

        SharedPreferences prefs = this.context.getSharedPreferences(Credentials.packagee, Context.MODE_PRIVATE);
        Map<String,?> keys = prefs.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("AB_DEV", entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}