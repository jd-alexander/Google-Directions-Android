package com.directions.route;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nelson on 1/13/16.
 */
public class RouteException extends Exception {
    private static final String TAG = "RouteException";
    private final String KEY_STATUS = "status";
    private final String KEY_MESSAGE = "error_message";

    private String statusCode;
    private String message;

    public RouteException(JSONObject json){
        if(json == null){
            statusCode = "";
            message = "Parsing error";
            return;
        }else{
            try {
                Log.d(TAG,"json: "+json.toString());
                Log.d(TAG,"Before accessing key: "+KEY_STATUS);
                statusCode = json.getString(KEY_STATUS);
                Log.d(TAG,"Before accessing key: "+KEY_MESSAGE);
                message = json.getString(KEY_MESSAGE);
            } catch (JSONException e) {
                Log.d(TAG, "JSONException. Msg: "+e.getMessage());
            }
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
