package com.directions.route;
//by Haseem Saheed
import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RouteOverlay extends Overlay {
    /** GeoPoints representing this routePoints. **/
    private final List<GeoPoint> routePoints;
    /** Colour to paint routePoints. **/
    private int colour;
    /** Alpha setting for route overlay. **/
    private static final int ALPHA = 120;
    /** Stroke width. **/
    private static final float STROKE = 8.5f;
    /** Route path. **/
    private final Path path;
    /** Point to draw with. **/
    private final Point p;
    /** Paint for path. **/
    private final Paint paint;


    /**
     * Public constructor.
     * 
     * @param route Route object representing the route.
     * @param defaultColour default colour to draw route in.
     */

    public RouteOverlay(final Route route, final int defaultColour) {
            super();
            routePoints = route.getPoints();
            colour = defaultColour;
            path = new Path();
            p = new Point();
            paint = new Paint();
    }

    @Override
    public final void draw(final Canvas c, final MapView mv,
                    final boolean shadow) {
            super.draw(c, mv, shadow);

            paint.setColor(colour);
            paint.setAlpha(ALPHA);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(STROKE);
            paint.setStyle(Paint.Style.STROKE);

            redrawPath(mv);
            c.drawPath(path, paint);
    }

    /**
     * Set the colour to draw this route's overlay with.
     * 
     * @param c  Int representing colour.
     */
    public final void setColour(final int c) {
            colour = c;
    }

    /**
     * Clear the route overlay.
     */
    public final void clear() {
            routePoints.clear();
    }

    /**
     * Recalculate the path accounting for changes to
     * the projection and routePoints.
     * @param mv MapView the path is drawn to.
     */

    private void redrawPath(final MapView mv) {
            final Projection prj = mv.getProjection();
            path.rewind();
            final Iterator<GeoPoint> it = routePoints.iterator();
            prj.toPixels(it.next(), p);
            path.moveTo(p.x, p.y);
            while (it.hasNext()) {
                    prj.toPixels(it.next(), p);
                    path.lineTo(p.x, p.y);
            }
            path.setLastPoint(p.x, p.y);
    }

}