package com.appbuilders.animedia.Libraries;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by Erick on 28/10/2016.
 */

public class JsonFileManager {

    protected Context context;

    public JsonFileManager(Context context) {

        this.context = context;
    }

    @Nullable
    public File makeRootPath() {

        File path = Environment.getExternalStorageDirectory();
        path = new File( path.getPath() + "/Android/data/" + context.getPackageName() );

        if(!path.exists()){
            path.mkdirs();
        }

        return path;
    }

    @NonNull
    public static Boolean checkRootPath(Context context) {

        File path = Environment.getExternalStorageDirectory();
        path = new File( path.getPath() + "/Android/data/" + context.getPackageName() );

        return path.exists();
    }

    @Nullable
    public static File getRootPath(Context context) {

        File path = Environment.getExternalStorageDirectory();
        path = new File( path.getPath() + "/Android/data/" + context.getPackageName() );

        if ( path.exists() ) {
            return path;
        }

        return null;
    }

    @NonNull
    public Boolean checkRootPath() {

        File path = Environment.getExternalStorageDirectory();
        path = new File( path.getPath() + "/Android/data/" + context.getPackageName() );

        return path.exists();
    }

    @Nullable
    public File getRootPath() {

        File path = Environment.getExternalStorageDirectory();
        path = new File( path.getPath() + "/Android/data/" + context.getPackageName() );

        if ( path.exists() ) {
            return path;
        }

        return null;
    }

    // Funciones para crear directorios generales
    @Nullable
    public File makeFolder(String folderName) {

        File path = Environment.getExternalStorageDirectory();
        path = new File( path.getPath() + "/Android/data/" + context.getPackageName() + "/" + folderName );

        if ( !path.exists() ) {
            path.mkdirs();
        }

        return path;
    }

    @Nullable
    public Boolean checkFolder(String folderName) {

        File path = Environment.getExternalStorageDirectory();
        path = new File( path.getPath() + "/Android/data/" + context.getPackageName() + "/" + folderName );

        return path.exists();
    }

    private File getFolder(String folderName) {

        File path = Environment.getExternalStorageDirectory();
        path = new File( path.getPath() + "/Android/data/" + context.getPackageName() + "/" + folderName );

        if ( path.exists() ) {
            return path;
        }
        return null;
    }

    // Funciones para crear archivos
    @Nullable
    public File makeFile( String subFolder, String nameFile ) {

        File path;

        if ( subFolder.compareTo("") == 0 ) {
            path = getRootPath(context);
        } else {
            path = this.getFolder(subFolder);
        }

        return new File( path + "/" + nameFile );
    }

    @Nullable
    public Boolean checkFile( String subFolder, String nameFile ) {

        File path;

        if ( subFolder.compareTo("") == 0 ) {

            path = getRootPath(context);
        } else {

            path = this.getFolder(subFolder);
        }

        File f = new File( path + "/" + nameFile );

        return f.exists();
    }

    // Funciones para salvar informacion, y obtener informacion
    public void saveData( String subFolder, String body, String fileName ) {
        try {

            File path;

            if ( subFolder.compareTo("") == 0 ) {
                path = getRootPath(context);
            } else {
                path = this.getFolder(subFolder);
            }

            FileWriter file = new FileWriter( path + "/" + fileName );
            file.write(body);
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.d("DXGO", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    @Nullable
    public String getData( String subFolder, String fileName ) {
        try {

            File path;

            if ( subFolder.compareTo("") == 0 ) {
                path = getRootPath(context);
            } else {
                path = this.getFolder(subFolder);
            }

            File f = new File( path + "/" + fileName );

            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("DXGO", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    @Nullable
    public String getDataFromAssets( String file_name ) {
        try {
            InputStream is = context.getAssets().open(file_name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.e("DXGO", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    @Nullable
    public String getDataFromAssets2( String file_name ) {
        try {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try (InputStream is = context.getAssets().open(file_name)) {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                return writer.toString();
            }
        } catch (IOException e) {
            Log.e("DXGO", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    // Funciones de manipulacion de JSON y String
    @Nullable
    public static JSONObject stringToJSON(String body ) {

        JSONObject obj;

        try {
            obj = new JSONObject(body);
            return obj;
        } catch( Throwable t ) {
            Log.e("DXGO", "Couldnt parse information to JSON Object");
            return null;
        }
    }

    @Nullable
    public static JSONArray stringToJsonArray(String body) {

        JSONArray array = null;
        try {
            array = new JSONArray(body);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("AB_DEV", "Couldnt parse information to JSON Array");
        }
        return array;
    }

    @NonNull
    public static Boolean checkValue(JSONArray json, String key, String value ) {

        return json.toString().contains("\"" + key + "\":\"" + value + "\"");
    }

    @NonNull
    public static Boolean checkId(JSONArray json, String id ) {

        return json.toString().contains("\"id\":\"" + id + "\"");
    }
}
