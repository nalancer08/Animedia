package com.appbuilders.animedia.Libraries.Rester;

import org.json.JSONObject;

public class ReSTResponse {

    public int statusCode;
    public int contentLength;
    public String contentType;
    public String body;
    public JSONObject json;

    public ReSTResponse() {
        this.body = "";
        this.json = null;
        this.statusCode = 0;
        this.contentLength = 0;
        this.contentType = "";
    }
}
