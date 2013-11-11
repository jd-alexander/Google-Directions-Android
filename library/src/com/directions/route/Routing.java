package com.directions.route;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay using some classes created by Hesham Saeed.
 * @author Joel Dean
 * Requires an instance of the map activity and the application's current context for the progress dialog.
 * 
 */

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.os.AsyncTask;


public class Routing extends AsyncTask<LatLng, Void, Route>
{
  protected Context _mContext;
  protected ArrayList<RoutingListener> _aListeners;


  /**
   * Initializes the context needed for the progress dialog, the map, and the
   *   color of the route.
   * @param context
   */
	public Routing(Context mContext)
	{
		this._mContext = mContext;
    this._aListeners = new ArrayList<RoutingListener>();
	}

  public void registerListener(RoutingListener mListener) {
    _aListeners.add(mListener);
  }

  protected void dispatchOnStart() {
    for (RoutingListener mListener: _aListeners) {
      mListener.onStart();
    }
  }

  protected void dispatchOnFailure() {
    for (RoutingListener mListener: _aListeners) {
      mListener.onFailure();
    }
  }

  protected void dispatchOnSuccess(PolylineOptions mOptions) {
    for (RoutingListener mListener: _aListeners) {
      mListener.onSuccess(mOptions);
    }
  }

  /**
   * Performs the call to the google maps API to acquire routing data and 
   *   deserializes it to a format the map can display.
   * @param points
   * @return
   */
	@Override
	protected Route doInBackground(LatLng... aPoints) {
    return new GoogleParser(constructURL(aPoints)).parse();
	}

	protected String constructURL(LatLng... points) {
    LatLng start = points[0];
    LatLng dest = points[1];
    String sJsonURL = "http://maps.googleapis.com/maps/api/directions/json?";

    final StringBuffer mBuf = new StringBuffer(sJsonURL);
    mBuf.append("origin=");
    mBuf.append(start.latitude);
    mBuf.append(',');
    mBuf.append(start.longitude);
    mBuf.append("&destination=");
    mBuf.append(dest.latitude);
    mBuf.append(',');
    mBuf.append(dest.longitude);
    mBuf.append("&sensor=true&mode=walking");

    return mBuf.toString();
	}

	@Override
	protected void onPreExecute() {
	  dispatchOnStart();
	}

	@Override
	protected void onPostExecute(Route result) 
	{		
    if(result==null) {
      dispatchOnFailure();
    }
    else
    {
      PolylineOptions mOptions = new PolylineOptions();

      for (LatLng point : result.getPoints()) {
        mOptions.add(point);
      }

      dispatchOnSuccess(mOptions);
    }
  }//end onPostExecute method	
}
