package com.appbuilders.chapterextractor.Libraries.Rester;

import android.net.Uri;
import android.support.v4.util.ArrayMap;

public class ReSTRequest {

    protected String mEndpoint;
    protected String mMethod;
    protected ArrayMap<String, String> mParameters;
    protected ArrayMap<String, String> mFields;

    public static final int REST_REQUEST_METHOD_GET = 0;
    public static final int REST_REQUEST_METHOD_POST = 1;

    public static final int REST_REQUEST_QUERY_PARAMETERS = 0;
    public static final int REST_REQUEST_QUERY_FIELDS = 1;

    public ReSTRequest(int method, String endpoint) {
        switch (method) {
            case REST_REQUEST_METHOD_POST:
                mMethod = "POST";
            break;
            case REST_REQUEST_METHOD_GET:
            default:
                mMethod = "GET";
            break;
        }
        mEndpoint = endpoint;
        mParameters = new ArrayMap<String, String>();
        mFields = new ArrayMap<String, String>();
    }

    public void addParameter(String name, String value) {
        mParameters.put(name, value);
    }

    public void addField(String name, String value) {
        mFields.put(name, value);
    }

    public String buildQuery(int type) {
        String query = "";
        Uri.Builder builder = new Uri.Builder();
        ArrayMap<String, String> map = null;
        switch (type) {
            case REST_REQUEST_QUERY_PARAMETERS:
                map = mParameters;
                break;
            case REST_REQUEST_QUERY_FIELDS:
                map = mFields;
                break;
        }
        if (map != null && map.size() > 0) {
            String name, value;
            for (int i = 0; i < map.size(); i++) {
                name = map.keyAt(i);
                value = map.valueAt(i);
                builder.appendQueryParameter(name, value);
            }
            query = builder.build().getEncodedQuery();
        }
        return query;
    }

}
