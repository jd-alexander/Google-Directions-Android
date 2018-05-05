package com.directions.route;
//by Haseem Saheed

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Route implements Parcelable {
    private String name;
    private final List<LatLng> points;
    private List<Segment> segments;
    private String copyright;
    private String warning;
    private String country;
    private LatLngBounds latLgnBounds;
    private int length;
    private String polyline;
    private String durationText;
    private int durationValue;
    private String distanceText;
    private int distanceValue;
    private String endAddressText;
    private PolylineOptions polyOptions;
    private long durationInTrafficValue;
    private String durationInTrafficText;





    public Route() {
        points = new ArrayList<>();
        segments = new ArrayList<>();
    }

    private Route(Parcel in) {
        name = in.readString();

        // points
        if (in.readInt() == 1) {
            points = new ArrayList<>();
            in.readList(points, LatLng.class.getClassLoader());
        } else {
            points = null;
        }

        // segments
        if (in.readInt() == 1) {
            segments = new ArrayList<>();
            in.readList(segments, Segment.class.getClassLoader());
        } else {
            segments = null;
        }

        copyright = in.readString();
        warning = in.readString();
        country = in.readString();
        latLgnBounds = in.readParcelable(LatLngBounds.class.getClassLoader());
        length = in.readInt();
        polyline = in.readString();
        durationText = in.readString();
        durationValue = in.readInt();
        durationInTrafficText = in.readString();
        durationInTrafficValue = in.readLong();
        distanceText = in.readString();
        distanceValue = in.readInt();
        endAddressText = in.readString();
        polyOptions = in.readParcelable(PolylineOptions.class.getClassLoader());
    }

    public PolylineOptions getPolyOptions() {
        return polyOptions;
    }

    public void setPolyOptions(PolylineOptions polyOptions) {
        this.polyOptions = polyOptions;
    }

    public String getEndAddressText() {
        return endAddressText;
    }

    public void setEndAddressText(String endAddressText) {
        this.endAddressText = endAddressText;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public int getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(int durationValue) {
        this.durationValue = durationValue;
    }

    public int getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(int distanceValue) {
        this.distanceValue = distanceValue;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public void addPoint(final LatLng p) {
        points.add(p);
    }

    public void addPoints(final List<LatLng> points) {
        this.points.addAll(points);
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void addSegment(final Segment s) {
        segments.add(s);
    }

    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param copyright the copyright to set
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * @return the copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * @param warning the warning to set
     */
    public void setWarning(String warning) {
        this.warning = warning;
    }

    /**
     * @return the warning
     */
    public String getWarning() {
        return warning;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param polyline the polyline to set
     */
    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    /**
     * @return the polyline
     */
    public String getPolyline() {
        return polyline;
    }

    /**
     * @return the LatLngBounds object to map camera
     */
    public LatLngBounds getLatLgnBounds() {
        return latLgnBounds;
    }


    public void setLatLgnBounds(LatLng northeast, LatLng southwest) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(northeast);
        builder.include(southwest);
        this.latLgnBounds = builder.build();
    }

    public Long getDurationInTrafficValue() {
        return durationInTrafficValue;
    }

    public void setDurationInTrafficValue(Long durationInTrafficValue) {
        this.durationInTrafficValue = durationInTrafficValue;
    }

    public String getDurationInTrafficText() {
        return durationInTrafficText;
    }

    public void setDurationInTrafficText(String durationInTrafficText) {
        this.durationInTrafficText = durationInTrafficText;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);

        // points
        if (points == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            dest.writeList(points);
        }

        // segments
        if (segments == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            dest.writeList(segments);
        }

        dest.writeString(copyright);
        dest.writeString(warning);
        dest.writeString(country);
        dest.writeParcelable(latLgnBounds, flags);
        dest.writeInt(length);
        dest.writeString(polyline);
        dest.writeString(durationText);
        dest.writeInt(durationValue);
        dest.writeString(durationInTrafficText);
        dest.writeLong(durationInTrafficValue);
        dest.writeString(distanceText);
        dest.writeInt(distanceValue);
        dest.writeString(endAddressText);
        dest.writeParcelable(polyOptions, flags);
    }

    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}

