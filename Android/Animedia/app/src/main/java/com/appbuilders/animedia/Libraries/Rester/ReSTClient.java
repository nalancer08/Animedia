package com.appbuilders.animedia.Libraries.Rester;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ReSTClient {

    protected String mServiceUrl;

    public ReSTClient() {
        this.mServiceUrl = "";
    }

    public ReSTClient(String serviceUrl) {
        this.mServiceUrl = serviceUrl;
    }

    private class ReSTWorker extends AsyncTask<Void, Void, Integer> {

        protected ReSTRequest mRequest;
        protected ReSTResponse mResponse;
        protected ReSTCallback mCallback;

        protected final String TAG = "DXGO";

        public ReSTWorker(ReSTRequest request, ReSTCallback callback) {
            mRequest = request;
            mCallback = callback;
            mResponse = new ReSTResponse();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            int ret = 0;
            try {
                String parameters = mRequest.buildQuery(ReSTRequest.REST_REQUEST_QUERY_PARAMETERS);
                //Log.d("DXGO", "PARAMETROS = " + parameters);
                String fields = mRequest.buildQuery(ReSTRequest.REST_REQUEST_QUERY_FIELDS);
                //Log.d("DXGO", "CAMPOS = " + fields);
                String endpoint = mServiceUrl + mRequest.mEndpoint + (parameters.length() >= 0 ? "?" + parameters : "");
                //Log.d("DXGO", "ENDPOINT = " + endpoint);
                URL url = new URL(endpoint);
                if ( url.getProtocol().compareTo("https") == 0 ) {
                    // Use HTTPS
                    conn = (HttpsURLConnection) url.openConnection();
                    //Log.d(TAG, "USANDO HTTPS" );
                } else {
                    // Use HTTP
                    conn = (HttpURLConnection) url.openConnection();
                }
                conn.setReadTimeout(100000);
                conn.setConnectTimeout(150000);
                conn.setRequestMethod(mRequest.mMethod);
                conn.setDoInput(true);

                if ( mRequest.mMethod.compareTo("POST") == 0 ) {
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(fields);
                    writer.flush();
                    writer.close();
                    os.close();
                    //Log.d(TAG, "ENTRE A LA MIERDA DE POST");
                }

                conn.connect();

                mResponse.statusCode = conn.getResponseCode();
                //Log.d(TAG, "ALGO IMPORTANTE = " + mResponse.statusCode);

                if (mResponse.statusCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("AB_DEV", String.valueOf(br));
                    while ((line = br.readLine()) != null) {
                        mResponse.body += line;
                    }
                    if (mResponse.body.length() > 0) {
                        mResponse.contentType = conn.getContentType();
                        mResponse.contentLength = conn.getContentLength();
                        //Log.d("AB_DEV", mResponse.contentType);
                        if ( mResponse.contentType.compareTo("application/json") == 0 ) {
                            mResponse.json = new JSONObject(mResponse.body);
                        }
                    }
                    ret = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return ret;
        }

        protected void onPostExecute(Integer result){
            if (result == 1 && mResponse.statusCode == 200) {
                mCallback.onSuccess(mResponse);
            } else {
                mCallback.onError(mResponse);
            }
        }
    }

    public void execute(ReSTRequest request, ReSTCallback callback) {
        ReSTWorker worker = new ReSTWorker(request, callback);
        worker.execute();
    }
}
