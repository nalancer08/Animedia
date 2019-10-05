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
import android.graphics.drawable.BitmapDrawable;
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
import com.appbuilders.animedia.Controller.HelpController;
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
import com.google.android.gms.ads.MobileAds;

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
    private boolean transitit_version = true;

    public SplashView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Setting status bar color
        this.setStatusBarColor();

        // Adding background
        int id = this.context.getResources().getIdentifier("splash_new", "drawable", this.context.getPackageName());
        Bitmap image = BitmapFactory.decodeStream(this.context.getResources().openRawResource(id));
        //this.screenCanvas.setBackground(new BitmapDrawable(image));
        ImageView bk = new ImageView(this.context);
        bk.setImageBitmap(image);
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

    public JSONObject getStatus() {

        final Credentials credentials = Credentials.getInstance(this.context);
        final JSONObject[] resp = {null};

        progress.setProgress(3);
        ReSTClient rest = new ReSTClient(credentials.getUrl() + "/status");
        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_GET, "");
        request.addParameter("token", credentials.getToken());
        request.addParameter("updater", "ok");
        rest.execute(request, new ReSTCallback() {

            @Override
            public void onSuccess(ReSTResponse response) {

                Log.d("DXGOP", "RESPUESTA STATUS = " + response.body);

                JSONObject res = JsonFileManager.stringToJSON(response.body);

                try {

                    if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                        JSONObject data = res.getJSONObject("data");
                        if (data.has("pig_data_app_uuid")) {

                            //com.appbuilders.credentials.Credentials.getInstance(context).setAppUuid(data.getString("pig_data_app_uuid"));
                            if (data.getString("updater").equals(BuildConfig.VERSION_NAME)) {
                                String url = data.getString("uri");
                                goToHelper(url);
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
                showErrorAlert("Error", "Servidores ocupados, intentelo ms tarde \n Error: 0xs");
            }
        });

        return resp[0];
    }

    private void showErrorAlert(String title, String message) {

        new SimpleDialog.Builder(context)
                .setTitle(title)
                .setContent(message, 3)
                .setBtnConfirmText("Cerrar")
                .onConfirm(new SimpleDialog.BtnCallback() {
                    @Override
                    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                        activity.finish();

                    }
                })
                .show();    // Must be called at the end
    }

    private void goToHelper(final String url) {

        progress.setProgress(7);
        new CountDownTimer(2000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                Intent intent = new Intent(context, HelpController.class);
                intent.putExtra("url", url);
                progress.setProgress(10);
                activity.startActivity(intent);
                activity.finish();
            }
        }.start();
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

    @SuppressLint("NewApi")
    protected void setStatusBarColor() {

        Window window = this.activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.rgb(0, 0, 0));
        }
    }
}