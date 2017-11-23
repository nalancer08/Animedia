package com.appbuilders.animedia.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.appbuilders.animedia.BuildConfig;
import com.appbuilders.animedia.Controller.HomeController;
import com.appbuilders.animedia.Controller.MainActivity;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Libraries.JsonFileManager;
import com.appbuilders.animedia.Libraries.Rester.ReSTCallback;
import com.appbuilders.animedia.Libraries.Rester.ReSTClient;
import com.appbuilders.animedia.Libraries.Rester.ReSTRequest;
import com.appbuilders.animedia.Libraries.Rester.ReSTResponse;
import com.appbuilders.surface.SurfaceActivityView;
import com.brouding.simpledialog.SimpleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 26/10/17
 */

public class SplashView extends SurfaceActivityView {


    public SplashView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Adding background
        int id = this.context.getResources().getIdentifier("splash", "drawable", this.context.getPackageName());
        Bitmap image = BitmapFactory.decodeStream(this.context.getResources().openRawResource(id));
        this.screenCanvas.setBackground(new BitmapDrawable(image));

        // Getting status
        this.getStatus();
    }

    public JSONObject getStatus() {

        final Credentials credentials = Credentials.getInstance(this.context, true);
        final JSONObject[] resp = {null};

        ReSTClient rest = new ReSTClient(credentials.getUrl() + "/status");
        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_GET, "");
        request.addParameter("token", credentials.getToken());
        rest.execute(request, new ReSTCallback() {

            @Override
            public void onSuccess(ReSTResponse response) {

                Log.d("AB_DEV", "RESPUESTA = " + response.body);

                JSONObject res = JsonFileManager.stringToJSON(response.body);

                try {

                    if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                        JSONObject data = res.getJSONObject("data");
                        if (data.getString("version").equals(BuildConfig.VERSION_NAME)) {
                            resp[0] = data;
                            askForLatestAnimes(credentials);
                        } else {
                            showErrorAlert("Error", "Necesitas actualizar tu aplicación para poder seguir viendo anime");
                        }
                    }  else {
                        showErrorAlert("Error", "Servidores ocupados");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ReSTResponse response) {

                String errorMessage;
                if (response.statusCode == 404) {
                    errorMessage = "HUMAN used SEARCH\nBut, it failed!";
                } else {
                    errorMessage = "Error " + Integer.toString(response.statusCode);
                }
                Toast.makeText(context, "Try again!!", Toast.LENGTH_SHORT).show();
            }
        });

        return resp[0];
    }

    private void askForLatestAnimes(Credentials credentials) {

        ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes/latest/medias");
        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
        request.addParameter("token", credentials.getToken());
        rest.execute(request, new ReSTCallback() {

            @Override
            public void onSuccess(ReSTResponse response) {

                Log.d("DXGO", "RESPUESTA = " + response.body);

                JSONObject res = JsonFileManager.stringToJSON(response.body);

                try {

                    if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                        JSONArray data = res.getJSONArray("data");
                        Intent intent = new Intent(context, HomeController.class);
                        intent.putExtra("latestAnimes", data.toString());
                        activity.startActivity(intent);
                        activity.finish();

                    }  else {
                        showErrorAlert("Error", "Problemas de conexión");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ReSTResponse response) {

                String errorMessage;
                if (response.statusCode == 404) {
                    errorMessage = "HUMAN used SEARCH\nBut, it failed!";
                } else {
                    errorMessage = "Error " + Integer.toString(response.statusCode);
                }
                Toast.makeText(context, "Try again!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showErrorAlert(String title, String message) {

        new SimpleDialog.Builder(context)
                .setTitle(title)
                .setContent(message, 3)
                .setBtnConfirmText("Cerrar")

                //.setBtnConfirmTextColor("#de413e")
                //.setBtnCancelText("Cancel")
                //.setBtnCancelTextColor("#de413e")
                //.setCancelable(true)          // Default value is false
                .onConfirm(new SimpleDialog.BtnCallback() {
                    @Override
                    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                        activity.finish();

                    }
                })
                //.setBtnCancelText("Cancel", false)
                //.onCancel(new SimpleDialog.BtnCallback() {
                //    @Override
                //    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                //        // Do something
                //    }
                //})
                .show();    // Must be called at the end
    }
}