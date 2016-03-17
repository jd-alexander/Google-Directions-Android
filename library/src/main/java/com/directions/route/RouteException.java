package com.directions.route;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nelson on 1/13/16.
 */
public class RouteException extends Exception {
    private static final String TAG = "RouteException";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "error_message";

    private String statusCode;
    private String message;

    public RouteException(JSONObject json){
        if(json == null){
            statusCode = "";
            message = "Parsing error";
            return;
        }
        try {
            statusCode = json.getString(KEY_STATUS);
            message = json.getString(KEY_MESSAGE);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException while parsing RouteException argument. Msg: " + e.getMessage());
        }
    }

    public RouteException(String msg){
        message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
