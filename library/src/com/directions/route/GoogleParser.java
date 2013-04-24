package com.directions.route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GoogleParser extends XMLParser implements Parser {
    /** Distance covered. **/
    private int distance;

    public GoogleParser(String feedUrl) {
            super(feedUrl);
    }

    /**
     * Parses a url pointing to a Google JSON object to a Route object.
     * @return a Route object based on the JSON object by Haseem Saheed
     */

    public Route parse() {
            // turn the stream into a string
            final String result = convertStreamToString(this.getInputStream());
            //Create an empty route
            final Route route = new Route();
            //Create an empty segment
            final Segment segment = new Segment();
            try {
                    //Tranform the string into a json object
                    final JSONObject json = new JSONObject(result);
                    //Get the route object
                    final JSONObject jsonRoute = json.getJSONArray("routes").getJSONObject(0);
                    //Get the leg, only one leg as we don't support waypoints
                    final JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(0);
                    //Get the steps for this leg
                    final JSONArray steps = leg.getJSONArray("steps");
                    //Number of steps for use in for loop
                    final int numSteps = steps.length();
                    //Set the name of this route using the start & end addresses
                    route.setName(leg.getString("start_address") + " to " + leg.getString("end_address"));
                    //Get google's copyright notice (tos requirement)
                    route.setCopyright(jsonRoute.getString("copyrights"));
                    //Get the total length of the route.
                    route.setLength(leg.getJSONObject("distance").getInt("value"));
                    //Get any warnings provided (tos requirement)
                    if (!jsonRoute.getJSONArray("warnings").isNull(0)) {
                            route.setWarning(jsonRoute.getJSONArray("warnings").getString(0));
                    }
                    /* Loop through the steps, creating a segment for each one and
                     * decoding any polylines found as we go to add to the route object's
                     * map array. Using an explicit for loop because it is faster!
                     */
                    for (int i = 0; i < numSteps; i++) {
                            //Get the individual step
                            final JSONObject step = steps.getJSONObject(i);
                            //Get the start position for this step and set it on the segment
                            final JSONObject start = step.getJSONObject("start_location");
                            final LatLng position = new LatLng(start.getDouble("lat"),
                                    start.getDouble("lng"));
                            segment.setPoint(position);
                            //Set the length of this segment in metres
                            final int length = step.getJSONObject("distance").getInt("value");
                            distance += length;
                            segment.setLength(length);
                            segment.setDistance(distance/1000);
                            //Strip html from google directions and set as turn instruction
                            segment.setInstruction(step.getString("html_instructions").replaceAll("<(.*?)*>", ""));
                            //Retrieve & decode this segment's polyline and add it to the route.
                            route.addPoints(decodePolyLine(step.getJSONObject("polyline").getString("points")));
                            //Push a copy of the segment to the route
                            route.addSegment(segment.copy());
                    }
            } catch (JSONException e) {
                    Log.e(e.getMessage(), "Google JSON Parser - " + feedUrl);
            }
            return route;
    }

    /**
     * Convert an inputstream to a string.
     * @param input inputstream to convert.
     * @return a String of the inputstream.
     */

    private static String convertStreamToString(final InputStream input) {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    final StringBuilder sBuf = new StringBuilder();

    String line = null;
    try {
        while ((line = reader.readLine()) != null) {
            sBuf.append(line);
        }
    } catch (IOException e) {
            Log.e(e.getMessage(), "Google parser, stream2string");
    } finally {
        try {
            input.close();
        } catch (IOException e) {
            Log.e(e.getMessage(), "Google parser, stream2string");
        }
    }
    return sBuf.toString();
}

    /**
     * Decode a polyline string into a list of GeoPoints.
     * @param poly polyline encoded string to decode.
     * @return the list of GeoPoints represented by this polystring.
     */

    private List<LatLng> decodePolyLine(final String poly) {
            int len = poly.length();
            int index = 0;
            List<LatLng> decoded = new ArrayList<LatLng>();
            int lat = 0;
            int lng = 0;

            while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
            } while (b >= 0x20);
                    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lng += dlng;

            decoded.add(new LatLng(
                    lat/100000d, lng/100000d
                    ));
            }

            return decoded;
            }
}