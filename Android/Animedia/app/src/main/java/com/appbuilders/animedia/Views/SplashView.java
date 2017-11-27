package com.appbuilders.animedia.Views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.appbuilders.animedia.BuildConfig;
import com.appbuilders.animedia.Controller.HomeController;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Libraries.JsonFileManager;
import com.appbuilders.animedia.Libraries.Rester.ReSTCallback;
import com.appbuilders.animedia.Libraries.Rester.ReSTClient;
import com.appbuilders.animedia.Libraries.Rester.ReSTRequest;
import com.appbuilders.animedia.Libraries.Rester.ReSTResponse;
import com.appbuilders.animedia.R;
import com.appbuilders.credentials.Configurations;
import com.appbuilders.surface.SfPanel;
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

    private SfPanel progressPanel;
    private RoundCornerProgressBar progress;

    public SplashView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Adding background
        int id = this.context.getResources().getIdentifier("splash_without_logo", "drawable", this.context.getPackageName());
        Bitmap image = BitmapFactory.decodeStream(this.context.getResources().openRawResource(id));
        this.screenCanvas.setBackground(new BitmapDrawable(image));

        this.progress = new RoundCornerProgressBar(this.context, null);
        this.progress.setMax(10);
        this.progress.setProgress(0);
        this.progress.setProgressColor(R.color.yellowItemSelected);
        this.progress.setSecondaryProgressColor(R.color.gray);
        this.progress.setProgressBackgroundColor(R.color.yellowItemSelected);

        this.progressPanel = new SfPanel();
        this.progressPanel.setSize(-70, -3).setView(this.progress);
        this.progressPanel.setPosition(SfPanel.SF_POSITION_ABSOLUTE).setOrigin(SfPanel.SF_UNSET, SfPanel.SF_UNSET, threeRuleY(0), threeRuleX(15));
        this.screen.append(this.progressPanel);
        this.addView(this.progress);

        // Update
        this.screen.update(this.context);

        // Getting status
        this.getStatus();
    }

    public JSONObject getStatus() {

        final Credentials credentials = Credentials.getInstance(this.context);
        final JSONObject[] resp = {null};

        progress.setProgress(3);
        ReSTClient rest = new ReSTClient(credentials.getUrl() + "/status");
        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_GET, "");
        request.addParameter("token", credentials.getToken());
        rest.execute(request, new ReSTCallback() {

            @Override
            public void onSuccess(ReSTResponse response) {

                Log.d("DXGOP", "RESPUESTA STATUS = " + response.body);

                JSONObject res = JsonFileManager.stringToJSON(response.body);

                try {

                    if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                        JSONObject data = res.getJSONObject("data");
                        if (data.has("pig_data_app_uuid")) {

                            com.appbuilders.credentials.Credentials.getInstance(context).setAppUuid(data.getString("pig_data_app_uuid"));
                            if (data.getString("version").equals(BuildConfig.VERSION_NAME)) {
                                resp[0] = data;
                                progress.setProgress(5);
                                showConditions();
                            } else {
                                showErrorAlert("Error", "Necesitas actualizar tu aplicación para poder seguir viendo anime \n Error: 1xs");
                            }

                        } else {
                            showErrorAlert("Error", "Necesitas actualizar tu aplicación para poder seguir viendo anime, si el error persiste contactanos \n Error: 3xs");
                        }

                    }  else {
                        showErrorAlert("Error", "Servidores ocupados \n Error: 2xs");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorAlert("Error", "Servidores ocupados, intentelo ms tarde \n Error: 1xs");
                }
            }

            @Override
            public void onError(ReSTResponse response) {
                showErrorAlert("Error", "Servidores ocupados, intentelo ms tarde \n Error: 1xs");
            }
        });

        return resp[0];
    }

    private void askForLatestAnimes() {

        Credentials credentials = Credentials.getInstance(this.context);
        progress.setProgress(7);
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
                        progress.setProgress(10);
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

    private void showConditions() {

        final Configurations configs = Configurations.getInstance(this.context);

        if (!configs.exists("terms_and_conditions")) {

            /*new SimpleDialog.Builder(context)
                    .setTitle("Términos y condiciones")
                    .setCustomView(R.layout.dialog_privacity_comnditions)
                    .setBtnConfirmText("Acepto")
                    .setBtnConfirmTextColor("#de413e")
                    .setBtnCancelText("No acepto", false)
                    .setBtnCancelTextColor("#de413e")
                    .setCancelable(true)          // Default value is false
                    .onConfirm(new SimpleDialog.BtnCallback() {
                        @Override
                        public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {

                            configs.add("terms_and_conditions", true);
                            askForLatestAnimes();
                        }
                    })
                    .setBtnCancelText("Cancel", false)
                    .onCancel(new SimpleDialog.BtnCallback() {
                        @Override
                        public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                            activity.finish();
                        }
                    })
                    .show();*/

            final Dialog dialog = new Dialog(this.context);
            dialog.setContentView(R.layout.conditions_dialog);
            dialog.setTitle("Términos y condiciones");
            dialog.setCanceledOnTouchOutside(false);

            dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    com.appbuilders.credentials.Credentials credentials = com.appbuilders.credentials.Credentials.getInstance(context);
                    credentials.buildPigData();
                    configs.add("terms_and_conditions", true);
                    askForLatestAnimes();
                }
            });

            dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });

            dialog.show();

        } else {
            askForLatestAnimes();
        }
    }

    protected int threeRuleY(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heigth = size.y;

        return (heigth * value) / 1794;
    }

    protected int threeRuleX(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width * value) / 1000;
    }
}