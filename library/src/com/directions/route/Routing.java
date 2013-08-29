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

import com.directions.api.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;


public class Routing extends AsyncTask<LatLng,Void,Route>
{
    public enum Start
    {
        RED(R.drawable.start_red),
        BLUE(R.drawable.start_blue),
        ORANGE(R.drawable.start_orange),
        PURPLE(R.drawable.start_purple),
        GREEN(R.drawable.start_green);


        private final int color;
        Start(int color)
        {
            this.color = color;
        }
    }

    public enum Destination
    {
        RED(R.drawable.end_red),
        BLUE(R.drawable.end_blue),
        ORANGE(R.drawable.end_orange),
        PURPLE(R.drawable.end_purple),
        GREEN(R.drawable.end_green);


        private final int color;
        Destination(int color)
        {
            this.color = color;
        }

    }

	private ProgressDialog spinner;
	private Context context;
	private GoogleMap map;
	private int color;
	private Boolean check=false;
    private Boolean pushPins=false;
    private Start startColor;
    private Destination destinationColor;
    private LatLng startPoint;
    private LatLng destinationPoint;
    private Marker start;
    private Marker destination;



	public Routing(Context context,GoogleMap map,int color)
	{
		
		this.context = context;
		this.map = map;
		this.color = color;
	}

    public Routing(Context context,GoogleMap map,int color,Start startColor,Destination destinationColor)
    {

        this.context = context;
        this.map = map;
        this.color = color;
        this.startColor = startColor;
        this.destinationColor = destinationColor;
        pushPins = true;
    }

    public Routing(GoogleMap map,int color,Start startColor,Destination destinationColor)
    {
        this.map = map;
        this.color = color;
        this.startColor = startColor;
        this.destinationColor = destinationColor;
        pushPins = true;
    }
	public Routing(GoogleMap map,int color)
	{
		this.map = map;
		this.color = color;
		check=true;
	}


	@Override
	protected Route doInBackground(LatLng... points) {
        LatLng start = points[0];
        LatLng dest = points[1];
		Parser parser;
	    String jsonURL = "http://maps.googleapis.com/maps/api/directions/json?";
	    final StringBuffer sBuf = new StringBuffer(jsonURL);
	    sBuf.append("origin=");
	    sBuf.append(start.latitude);
	    sBuf.append(',');
	    sBuf.append(start.longitude);
	    sBuf.append("&destination=");
	    sBuf.append(dest.latitude);
	    sBuf.append(',');
	    sBuf.append(dest.longitude);
	    sBuf.append("&sensor=true&mode=walking");
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

            PolylineOptions options = new PolylineOptions().color(color).width(5);

            for (LatLng point : result.getPoints()) {
                options.add(point);
            }
            startPoint = result.getPoints().get(0);
            destinationPoint = result.getPoints().get(result.getPoints().size()-1);



            if(pushPins)
            {
                start = map.addMarker(new MarkerOptions()
                        .position(startPoint).icon(BitmapDescriptorFactory.fromResource(startColor.color))
                );

                destination = map.addMarker(new MarkerOptions()
                        .position(destinationPoint).icon(BitmapDescriptorFactory.fromResource(destinationColor.color))
                );
            }
            map.addPolyline(options);
            // invalidate?
        }
     }//end onPostExecute method


	
	
}