package com.directions.route;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay using some classes created by Hesham Saeed.
 * @author Joel Dean
 * Requires an instance of the map activity and the application's current context for the progress dialog.
 * 
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;


public class Routing extends AsyncTask<GeoPoint,Void,Route>
{
	private ProgressDialog spinner;
	private Context context;
	private MapView map;
	private int color;
	private Boolean check=false;

	public Routing(Context context,MapView map,int color)
	{
		
		this.context = context;
		this.map = map;
		this.color = color;
	}
	public Routing(MapView map,int color)
	{
		this.map = map;
		this.color = color;
		check=true;
	}
	@Override
	protected Route doInBackground(GeoPoint... points) {
		GeoPoint start = points[0];
		GeoPoint dest = points[1];
		Parser parser;
	    String jsonURL = "http://maps.googleapis.com/maps/api/directions/json?";
	    final StringBuffer sBuf = new StringBuffer(jsonURL);
	    sBuf.append("origin=");
	    sBuf.append(start.getLatitudeE6()/1E6);
	    sBuf.append(',');
	    sBuf.append(start.getLongitudeE6()/1E6);
	    sBuf.append("&destination=");
	    sBuf.append(dest.getLatitudeE6()/1E6);
	    sBuf.append(',');
	    sBuf.append(dest.getLongitudeE6()/1E6);
	    sBuf.append("&sensor=true&mode=driving");
	    parser = new GoogleParser(sBuf.toString());
	    Route r =  parser.parse();
	    return r;

	    

	}
	@Override
	protected synchronized void onPreExecute() 
	{ 
		if(!check)
		spinner = ProgressDialog.show(context,"","Loading...", true,false);
	}//end onPreExecute method
	
	protected void onPostExecute(Route result) 
	{
		if(!check)
		spinner.dismiss();
		
        if(result==null)
        {
        	Log.e(null,"No result");
        }
        else
        {
        	RouteOverlay routeOverlay = new RouteOverlay(result,color);
    		map.getOverlays().add(routeOverlay);
    		map.invalidate();
        }
     }//end onPostExecute method
	
	
	
}