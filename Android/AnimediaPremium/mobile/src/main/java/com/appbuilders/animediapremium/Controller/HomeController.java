package com.appbuilders.animediapremium.Controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appbuilders.animediapremium.Core.Credentials;
import com.appbuilders.animediapremium.Libraries.JsonBuilder;
import com.appbuilders.animediapremium.Libraries.JsonFileManager;
import com.appbuilders.animediapremium.Libraries.Rester.ReSTCallback;
import com.appbuilders.animediapremium.Libraries.Rester.ReSTClient;
import com.appbuilders.animediapremium.Libraries.Rester.ReSTRequest;
import com.appbuilders.animediapremium.Libraries.Rester.ReSTResponse;
import com.appbuilders.animediapremium.R;
import com.appbuilders.animediapremium.Views.HomeViewFixed;
import com.appbuilders.credentials.Configurations;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeController extends AppCompatActivity {

    CallbackManager callbackManager;
    public FlowingDrawer mDrawer;

    HomeViewFixed view;

    ImageView userPicture;
    LoginButton loginButton;
    TextView userName;
    TextView menu_animes;
    TextView menu_config;
    TextView menu_privacity;
    TextView menu_about_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Calling super
        super.onCreate(savedInstanceState);

        // Remove action bar
        ((AppCompatActivity)this).getSupportActionBar().hide();

        // Initialize Facebook SDK and callbacks
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        // Setting native and Surface layouts
        setContentView(R.layout.activity_home_controller);
        AbsoluteLayout abs = (AbsoluteLayout) findViewById(R.id.content);
        this.view = new HomeViewFixed(this, abs);

        // Initialize elastic menu
        this.initializeElasticMenu();

        // Initialize menu's view
        this.initializeMenuViews();

        // Set super callback for Logout
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {

            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    //write your code here what to do when user logout
                    removePreviousSession();
                }
            }
        };

        // Checking previous session
        this.getPreviousSession();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9132) {
            if(resultCode == Activity.RESULT_OK){

                boolean result = data.getBooleanExtra("result", false);
                if (result) {
                    this.mDrawer.closeMenu(true);
                    this.view.showTutorial();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Log.d("DXGOP", "Me la pelas android");
            }
        }
    }

    protected void getUserDetails(LoginResult loginResult) {

        GraphRequest data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject json, GraphResponse response) {

                    Log.d("DXGO", json.toString());
                    Credentials.getInstance(getApplicationContext()).savePreference("userLoginFB", json.toString());
                    login(json);
                }
            });

        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email, picture.width(512).height(512)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    /**
     * This method allow to restore previous login
     * If it's true it'll put user image
     **/
    protected void getPreviousSession() {

        Credentials credentials = Credentials.getInstance(this);

        if (credentials.existsPreference("userLoginFB")) { // We have previous session

            String jsonString = credentials.getPreference("userLoginFB");
            JSONObject json = JsonBuilder.stringToJson(jsonString);
            this.login(json);
        }
    }

    /**
     * This method allow to remove previous session
     **/
    protected void removePreviousSession() {

        Credentials.getInstance(getApplicationContext()).removePreference("userLoginFB");
        Credentials.getInstance(getApplicationContext()).removePreference("userLogin");
        this.userPicture.setImageResource(R.drawable.user_defualt);
        this.userName.setText("");
    }

    /**
     * Login to App Builders platform
     **/
    protected void login(final JSONObject json) {

        final Credentials credentials = Credentials.getInstance(this);

        String id = "";
        String email = "";
        String name = "";
        String imageUrl = "";

        try {

            id = json.getString("id");
            name = json.getString("name");

            if (json.has("email")) {
                email = json.getString("email");
            }

            JSONObject imageObject = json.getJSONObject("picture");
            JSONObject imageObjectData = imageObject.getJSONObject("data");
            int imageW = imageObjectData.getInt("width");
            int imageH = imageObjectData.getInt("height");
            imageUrl = imageObjectData.getString("url");
            //boolean imageSilhouette = imageObjectData.getBoolean("is_silhouette");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalImageUrl = imageUrl;
        final String finalName = name;

        ReSTClient rest = new ReSTClient(credentials.getUrl() + "/login");
        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
        request.addParameter("token", credentials.getToken());
        request.addField("fbid", id);
        request.addField("nicename", name);
        request.addField("email", (email.equals("") ? id + "@facebook.com" : email ) );
        rest.execute(request, new ReSTCallback() {

            @Override
            public void onSuccess(ReSTResponse response) {

                Log.d("DXGOP", "RESPUESTA = " + response.body);
                JSONObject res = JsonFileManager.stringToJSON(response.body);

                try {

                    if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                        Log.d("DXGO", "USER RESULT ::: " + res.getJSONObject("data").toString());

                        // Saving userLogin
                        JSONObject data = res.getJSONObject("data");
                        credentials.setUserLogin(data);

                        // Filling views fields
                        parseLogin(finalImageUrl, finalName);

                        com.appbuilders.credentials.Credentials cre = com.appbuilders.credentials.Credentials.getInstance(getApplicationContext());
                        Configurations configs = Configurations.getInstance(getApplicationContext());

                        if (!configs.exists("pig_data_user_uuid")) {

                            // Filling views fields
                            parseLogin(finalImageUrl, finalName);

                            cre.setUserUuid(credentials.getUserUuid());
                            cre.synchronize();
                        }

                    }  else {
                        //showErrorAlert("Error", "Problemas de conexión");
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
                Toast.makeText(getApplicationContext(), "Try again!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void parseLogin(String imageUrl, String name) {

        Picasso.with(getApplicationContext()).load(imageUrl).into(this.userPicture);
        this.userName.setText(name);
    }

    protected void initializeElasticMenu() {

        this.mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        this.mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_FULLSCREEN);
        this.mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                    //Log.i("DXGO", "Drawer STATE_CLOSED");
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                //Log.i("DXGO", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });
    }

    protected void initializeMenuViews() {

        final Credentials credentials = Credentials.getInstance(this);
        Typeface specify =  Typeface.createFromAsset( this.getAssets(), "Specify.ttf");

        // User picture
        this.userPicture = (ImageView) findViewById(R.id.userPicture);

        // User name
        this.userName = (TextView) findViewById(R.id.userName);
        this.userName.setTypeface(specify);

        // Login/Logout button
        this.loginButton = (LoginButton) findViewById(R.id.loginFb);
        this.loginButton.setReadPermissions("email");
        this.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        // Anime's button
        this.menu_animes = (TextView) findViewById(R.id.menu_animes);
        this.menu_animes.setTypeface(specify);
        this.menu_animes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (credentials.existsPreviousLogin()) {

                    String animes = getIntent().getStringExtra("latestAnimes");
                    Intent intent = new Intent(HomeController.this, AnimesController.class);
                    intent.putExtra("animes", animes);
                    startActivity(intent);

                } else {
                    Snackbar.make(view, "Debes iniciar sesión para poder ver el contenido", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // Config's button
        this.menu_config = (TextView) findViewById(R.id.menu_config);
        this.menu_config.setTypeface(specify);
        this.menu_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (credentials.existsPreviousLogin()) {

                    Intent intent = new Intent(HomeController.this, ConfigurationsController.class);
                    startActivityForResult(intent, 9132);

                } else {
                    Snackbar.make(view, "Debes iniciar sesión para poder ver el contenido", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        this.menu_privacity = (TextView) findViewById(R.id.menu_privacity);
        this.menu_privacity.setTypeface(specify);
        this.menu_privacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeController.this, PrivacityController.class);
                startActivity(intent);
            }
        });

        this.menu_about_us = (TextView) findViewById(R.id.menu_about_us);
        this.menu_about_us.setTypeface(specify);
        this.menu_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeController.this, AboutUsController.class);
                startActivity(intent);
            }
        });
    }

    public FlowingDrawer getMenu() {

        return this.mDrawer;
    }

    public LoginButton getLoginButton() {

        return this.loginButton;
    }

    @Override
    public void onBackPressed() {

        if (this.mDrawer != null && this.mDrawer.isMenuVisible()) {
            this.mDrawer.closeMenu(true);
        } else {
            super.onBackPressed();
        }
    }
}