package com.directions.sample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, com.google.android.gms.location.LocationListener {
    private static final int PERMISSIONS_REQUEST_ENABLE_LOCATION = 121;
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 3000;
    private static final String START_PLACE_KEY = "START_PLACE_KEY";
    private static final String END_PLACE_KEY = "END_PLACE_KEY";
    private static final String TAG = "MainActivity";
    @Nullable
    protected GoogleMap map;
    protected static Place startPlace, endPlace;
    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @InjectView(R.id.start)
    AutoCompleteTextView starting;
    @InjectView(R.id.destination)
    AutoCompleteTextView destination;
    @InjectView(R.id.send)
    ImageView send;
    @InjectView(R.id.cardview)
    CardView cardView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.spinner)
    Spinner spinner;
    private static final String LOG_TAG = "MyActivity";
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;
    private ProgressDialog progressDialog;
    private List<Pair<Polyline, Route>> routesLines;
    Pair<Polyline, Route> selectedRoute;
    //private static final int[] COLORS = new int[]{R.color.primary_dark, R.color.primary, R.color.primary_light, R.color.accent, R.color.primary_dark_material_light};


    //    private static final LatLngBounds BOUNDS_JAMAICA = new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
//            new LatLng(72.77492067739843, -9.998857788741589));
    private static final LatLngBounds BOUNDS_WORLD = new LatLngBounds(new LatLng(-85, -180),
            new LatLng(85, 180));

    private LocationRequest mLocationRequest;
    private boolean firstLocationUpdate;
    private AbstractRouting.TravelMode travelMode = AbstractRouting.TravelMode.DRIVING;

    /**
     * This activity loads a map and then displays the route and pushpins on it.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        catch (NullPointerException npe) {}
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        mAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_WORLD, null);

        /*
        * Adds auto complete adapter to both auto complete
        * text views.
        * */
        starting.setAdapter(mAdapter);
        destination.setAdapter(mAdapter);

        /*
        * Sets the startPlace and destination points based on the values selected
        * from the autocomplete text views.
        * */

        starting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);
                        startPlace = place;
                        starting.clearFocus();
                        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(starting.getWindowToken(), 0);
                    }
                });
            }
        });
        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

              /*
              Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);
                        endPlace = place;
                        destination.clearFocus();
                        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(destination.getWindowToken(), 0);
                    }
                });

            }
        });

        spinner.setAdapter(new ListAdapter());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                travelMode = AbstractRouting.TravelMode.values()[position];
                if (map!=null) {
                    map.clear();
                }
                if (routesLines!=null) {
                    routesLines.clear();
                }
                selectedRoute = null;
                if (startPlace==null || endPlace==null) {
                    cardView.setVisibility(View.VISIBLE);
                }
                else {
                    cardView.setVisibility(View.GONE);
                    route();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                map.clear();
//                cardView.setVisibility(View.VISIBLE);
            }
        });

        /*
        These text watchers set the startPlace and end points to null because once there's
        * a change after a value has been selected from the dropdown
        * then the value has to reselected from dropdown to get
        * the correct location.
        * */
        starting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                if (startPlace != null) {
                    startPlace = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (endPlace != null) {
                    endPlace = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.action_car_directions) {
            cardView.setVisibility(View.VISIBLE);
            travelMode = AbstractRouting.TravelMode.DRIVING;
            return true;
        }
        else if (item.getItemId() == R.id.action_bus_directions) {
            cardView.setVisibility(View.VISIBLE);
            travelMode = AbstractRouting.TravelMode.TRANSIT;
            return true;
        }
        else if (item.getItemId() == R.id.action_bike_directions) {
            cardView.setVisibility(View.VISIBLE);
            travelMode = AbstractRouting.TravelMode.BIKING;
            return true;
        }
        else if (item.getItemId() == R.id.action_walk_directions) {
            cardView.setVisibility(View.VISIBLE);
            travelMode = AbstractRouting.TravelMode.WALKING;
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.send)
    public void sendRequest() {
        if (Util.Operations.isOnline(this)) {
            if (map != null) {
                map.clear();
            }
            route();
        } else {
            Snackbar.make(coordinatorLayout, getString(R.string.noInternet), Snackbar.LENGTH_SHORT).show();
        }
    }

    public void route() {
        if (startPlace == null || endPlace == null) {
            if (startPlace == null) {
                if (starting.getText().length() > 0) {
                    starting.setError(getString(R.string.choose_from_dropdown));
                } else {
                    Snackbar.make(coordinatorLayout, getString(R.string.choose_start), Snackbar.LENGTH_SHORT).show();
                }
            }
            if (endPlace == null) {
                if (destination.getText().length() > 0) {
                    starting.setError(getString(R.string.choose_from_dropdown));
                } else {
                    Snackbar.make(coordinatorLayout, getString(R.string.choose_stop), Snackbar.LENGTH_SHORT).show();
                }
            }
        } else {
            progressDialog = ProgressDialog.show(this, "Please wait.",
                    "Fetching route information.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(travelMode)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(startPlace.getLatLng(), endPlace.getLatLng())
                    .build();
            routing.execute();
        }
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        progressDialog.dismiss();
        String errorMsg = getString(R.string.error);
        if (e != null) {
            errorMsg += ": " + e.getStatusCode() + " " + e.getMessage();
        }
        Snackbar.make(coordinatorLayout, errorMsg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(List<Route> routes, int shortestRouteIndex) {
        progressDialog.dismiss();

        addRoutesToMap(routes, true);

        cardView.setVisibility(View.GONE);
    }

    private void addRoutesToMap(List<Route> routes, boolean shouldMoveCamera) {
        map.clear();

        if (shouldMoveCamera) {
            final double southLatitude, northernLatitude;
            final double eastLongitude, westLongitude;
            if (startPlace.getLatLng().latitude < endPlace.getLatLng().latitude) {
                southLatitude = startPlace.getLatLng().latitude;
                northernLatitude = endPlace.getLatLng().latitude;
            } else {
                southLatitude = endPlace.getLatLng().latitude;
                northernLatitude = startPlace.getLatLng().latitude;
            }
            if (startPlace.getLatLng().longitude < endPlace.getLatLng().longitude) {
                eastLongitude = startPlace.getLatLng().longitude;
                westLongitude = endPlace.getLatLng().longitude;
            } else {
                eastLongitude = endPlace.getLatLng().longitude;
                westLongitude = startPlace.getLatLng().longitude;
            }

            map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                    new LatLng(southLatitude, eastLongitude),
                    new LatLng(northernLatitude, westLongitude)), 0));
        }
        routesLines = null;
        routesLines = new ArrayList<>();
        int[] grayColors = getResources().getIntArray(R.array.GrayShades);

        //add route(s) to the map.
        for (int i = 0; i < routes.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions();
            if (i == 0) {
                polyOptions.color(getResources().getColor(R.color.primary_dark));
            } else {
                final int index = i - 1;
                if (index < grayColors.length) {
                    polyOptions.color(grayColors[index]);
                } else {
                    polyOptions.color(grayColors[grayColors.length - 1]);
                }
            }
            polyOptions.width(20 - i * 2);
            polyOptions.zIndex(routes.size() - i);
            polyOptions.addAll(routes.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polyline.setClickable(true);
            final Route route = routes.get(i);
            final Pair<Polyline, Route> routeLine = new Pair(polyline, route);
            routesLines.add(routeLine);
            if (i == 0) {
                selectedRoute = routeLine;
            }
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(startPlace.getLatLng());
        options.title(startPlace.getName().toString());
        options.snippet(startPlace.getAddress().toString());
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        map.addMarker(options);


        // End marker
        options = new MarkerOptions();
        options.position(endPlace.getLatLng());
        options.title(endPlace.getName().toString());
        options.snippet(endPlace.getAddress().toString());
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        map.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "Routing was cancelled.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.v(LOG_TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ENABLE_LOCATION);
            return;
        }
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setPadding(50, 50, 50, 50);
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ENABLE_LOCATION);
        } else {
            map.setMyLocationEnabled(true);
        }
         /*
        * Updates the bounds being used by the auto complete adapter based on the position of the
        * map.
        * */
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                mAdapter.setBounds(bounds);
            }
        });

        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                if (routesLines != null) {

                    final List<Route> newRouteLines = new ArrayList<>();
                    for (Pair<Polyline, Route> p : routesLines) {
                        newRouteLines.add(p.second);
                    }

                    for (Pair<Polyline, Route> p : routesLines) {
                        if (p.first.equals(polyline)) {
                            Route r = p.second;
                            String txt = "Distance: " + r.getDistanceText() +
                                    "\nTime: " + r.getDurationText();
                            if (!TextUtils.isEmpty(r.getWarning())) {
                                txt += "\nWarning: " + r.getWarning();
                            }

                            //add selected route at the top of routes.
                            newRouteLines.remove(r);
                            newRouteLines.add(0, r);
                            addRoutesToMap(newRouteLines, false);
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, txt, Snackbar.LENGTH_LONG);

                            if ((newRouteLines.size() > 1) && (!polyline.equals(routesLines.get(0).first))) {
                                snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Route oldRoute = newRouteLines.get(1);
                                        newRouteLines.remove(oldRoute);
                                        newRouteLines.add(0, oldRoute);
                                        addRoutesToMap(newRouteLines, false);
                                    }
                                });
                            }
                            snackbar.show();
                            break;

                        }
                    }
                }
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if (firstLocationUpdate) {
            firstLocationUpdate = false;
            map.moveCamera(center);
            map.animateCamera(zoom);
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ENABLE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (map != null && !map.isMyLocationEnabled()) {
                    map.setMyLocationEnabled(true);
                }
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    protected class ListAdapter extends BaseAdapter implements SpinnerAdapter {

        @Override
        public int getCount() {
            return AbstractRouting.TravelMode.values().length;
        }

        @Override
        public Object getItem(int position) {
            return AbstractRouting.TravelMode.values()[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view==null) {
                view = getLayoutInflater().inflate(R.layout.spinner_layout, parent, false);
            }
            bindView(view, position);
            return view;
        }

        private void bindView(View view, int position) {
            ViewHolder vh = new ViewHolder(view);
            AbstractRouting.TravelMode trvlMode = AbstractRouting.TravelMode.values()[position];
            vh.name.setText(trvlMode.name());
            Resources res = getResources();
            TypedArray icons = res.obtainTypedArray(R.array.travel_mode_imgs);
            Drawable drawable = icons.getDrawable(position);
            vh.icon.setImageDrawable(drawable);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return super.getDropDownView(position, convertView, parent);
        }

        class ViewHolder {
            @InjectView(R.id.name)
            TextView name;
            @InjectView(R.id.icon) ImageView icon;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
