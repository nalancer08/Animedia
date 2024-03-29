package com.appbuilders.animedia.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.appbuilders.animedia.BuildConfig;
import com.appbuilders.animedia.Controller.HomeController;
import com.appbuilders.animedia.Controller.UpdateController;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Core.Util;
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
 * Revision 2 - 30/01/18
 */

public class SplashView extends SurfaceActivityView {

    private SfPanel progressPanel;
    private RoundCornerProgressBar progress;
    private String url = "";

    public SplashView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Setting status bar color
        this.setStatusBarColor();

        // Adding background
        ImageView bk = new ImageView(this.context);
        bk.setImageBitmap(Util.getImage(this.context, "splash_new"));
        bk.setAdjustViewBounds(true);
        bk.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.screen.setView(bk);
        this.addView(bk);

        this.progress = new RoundCornerProgressBar(this.context, null);
        this.progress.setMax(10);
        this.progress.setProgress(0);
        this.progress.setProgressColor(Color.rgb(255, 255, 255));
        this.progress.setSecondaryProgressColor(R.color.gray);
        this.progress.setProgressBackgroundColor(R.color.yellowItemSelected);

        this.progressPanel = new SfPanel();
        this.progressPanel.setSize(-70, -2).setView(this.progress);
        this.progressPanel.setPosition(SfPanel.SF_POSITION_ABSOLUTE).setOrigin(SfPanel.SF_UNSET, SfPanel.SF_UNSET, threeRuleY(50), threeRuleX(150));
        this.screen.append(this.progressPanel);
        this.addView(this.progress);

        // Update
        this.screen.update(this.context);

        // Getting status
        this.getStatus();
    }

    public void getStatus() {

        final Credentials credentials = Credentials.getInstance(this.context);

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

                            String version = data.getString("version");
                            String supported = data.getString("supported_version");
                            //url = data.getString("uri");

                            if (supported.equals(BuildConfig.VERSION_NAME)) {

                                // YOU ARE USING THE LAST STABLE VERSION
                                progress.setProgress(5);
                                showSupportedAlert("Alerta", "Estas usando la ultima versión estable de la aplicación, te reocmendamos que actualices");

                            } else if (version.equals(BuildConfig.VERSION_NAME)) {

                                // YOU CAN UPDATE
                                progress.setProgress(5);
                                showConditions();

                            } else {
                                showUpdatedAlert("Error", "Necesitas actualizar tu aplicación para poder seguir viendo anime \n Error: 1xs");
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
                showErrorAlert("Error", "Servidores ocupados, intentelo ms tarde \n Error: 0xs");
            }
        });
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
                        final Intent intent = new Intent(context, HomeController.class);
                        intent.putExtra("latestAnimes", data.toString());

                        new CountDownTimer(1500, 1000) {

                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {

                                progress.setProgress(10);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        }.start();


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

    private void showSupportedAlert(String title, String message) {

        new SimpleDialog.Builder(context)
                .setTitle(title)
                .setContent(message, 3)
                .setBtnConfirmText("Descargar")
                .onConfirm(new SimpleDialog.BtnCallback() {
                    @Override
                    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                        Intent intent = new Intent(context, UpdateController.class);
                        intent.putExtra("url", url);
                        progress.setProgress(10);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                })
                .setBtnCancelText("Continuar")
                .onCancel(new SimpleDialog.BtnCallback() {
                    @Override
                    public void onClick(@NonNull SimpleDialog simpleDialog, @NonNull SimpleDialog.BtnAction btnAction) {
                        showConditions();
                    }
                })
                .show();
    }

    private void showUpdatedAlert(String title, String message) {

        new SimpleDialog.Builder(context)
                .setTitle(title)
                .setContent(message, 3)
                .setBtnConfirmText("Descargar")
                .onConfirm(new SimpleDialog.BtnCallback() {
                    @Override
                    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                        Intent intent = new Intent(context, UpdateController.class);
                        intent.putExtra("url", url);
                        progress.setProgress(10);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                })
                .show();
    }

    private void showConditions() {

        final Configurations configs = Configurations.getInstance(this.context);

        if (!configs.exists("terms_and_conditions")) {

            final Dialog dialog = new Dialog(this.context);
            dialog.setContentView(R.layout.conditions_dialog);
            dialog.setTitle("Términos y condiciones");
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(getX() - 20, getY() - threeRuleY(250));
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

    protected int getY() {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    protected int getX() {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @SuppressLint("NewApi")
    protected void setStatusBarColor() {

        Window window = this.activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.rgb(0, 0, 0));
        }
    }
}