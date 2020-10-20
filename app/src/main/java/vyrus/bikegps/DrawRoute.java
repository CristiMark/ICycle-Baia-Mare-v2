package vyrus.bikegps;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonGeometry;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonLineString;
import com.google.maps.android.geojson.GeoJsonMultiLineString;
import com.google.maps.android.geojson.GeoJsonMultiPoint;
import com.google.maps.android.geojson.GeoJsonMultiPolygon;
import com.google.maps.android.geojson.GeoJsonPoint;
import com.google.maps.android.geojson.GeoJsonPolygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vyrus on 07/12/2016.
 */

public class DrawRoute extends AsyncTask {
    protected static final char CHAR_HEX_COLOR = '#';
    private static final String EMPTY_STRING = "";
    private static final String PROPERTY_STROKE = "stroke";
    private static final String LINE_STRING = "LineString";
    private static final int LINE_DRAW_WIDTH = 18;
    private static ArrayList<PolylineOptions> polylineOptions = new ArrayList<PolylineOptions>();
    private static GeoJsonLayer geoJsonLayoutLayer;
    private Context drawRouteContext;

    public DrawRoute(final Context context, final GeoJsonLayer geoJsonLayer) {
        this.drawRouteContext = context;
        this.geoJsonLayoutLayer = geoJsonLayer;
    }

    public static ArrayList<PolylineOptions> getPolylineOptions() {
        return polylineOptions;
    }

    public int toColorPoly(final String color) {

        if (color.equals(getColorResourceHexCode(R.color.settings_draw_route_easy))) {
            return Color.parseColor(getColorResourceHexCode(R.color.difficulty_color_mode_easy));
        }
        if (color.equals(getColorResourceHexCode(R.color.settings_draw_route_medium))) {
            return Color.parseColor(getColorResourceHexCode(R.color.difficulty_color_mode_medium));
        }
        if (color.equals(getColorResourceHexCode(R.color.settings_draw_route_hard))) {
            return Color.parseColor(getColorResourceHexCode(R.color.difficulty_color_mode_hard));
        }
        if (color.equals(getColorResourceHexCode(R.color.settings_draw_route_extra_hard))) {
            return Color.parseColor(getColorResourceHexCode(R.color.difficulty_color_mode_extra_hard));
        }
        if (color.equals(getColorResourceHexCode(R.color.settings_draw_route_red))) {
            return Color.RED;
        } else {
            return Color.CYAN;
        }
    }

    @Override
    protected Object doInBackground(final Object[] params) {
        PolylineOptions options = new PolylineOptions();

        for (GeoJsonFeature feature : geoJsonLayoutLayer.getFeatures()) {
            final GeoJsonGeometry points = feature.getGeometry();

            if (points.getType().equals(LINE_STRING)) {
                final String color = feature.getProperty(PROPERTY_STROKE);
                final String col = (color != null) ? color : EMPTY_STRING;

                final List<LatLng> coordinatesFromGeometry = getCoordinatesFromGeometry(points);
                for (int i = 0; i < coordinatesFromGeometry.size(); i++) {
                    options = new PolylineOptions().width(LINE_DRAW_WIDTH).color(toColorPoly(col));
                    options.addAll(coordinatesFromGeometry);

                }
                polylineOptions.add(options);
            }
        }
        return options;
    }

    private List<LatLng> getCoordinatesFromGeometry(final GeoJsonGeometry geometry) {
        final List<LatLng> coordinates = new ArrayList<>();
        final Descriptions.Geometry geometryType = Descriptions.Geometry.valueOf(geometry.getType());

        switch (geometryType) {
            case Point:
                coordinates.add(((GeoJsonPoint) geometry).getCoordinates());
                break;
            case MultiPoint:
                final List<GeoJsonPoint> points = ((GeoJsonMultiPoint) geometry).getPoints();
                for (GeoJsonPoint point : points) {
                    coordinates.add(point.getCoordinates());
                }
                break;
            case LineString:
                coordinates.addAll(((GeoJsonLineString) geometry).getCoordinates());
                break;
            case MultiLineString:
                final List<GeoJsonLineString> lines =
                        ((GeoJsonMultiLineString) geometry).getLineStrings();
                for (GeoJsonLineString line : lines) {
                    coordinates.addAll(line.getCoordinates());
                }
                break;
            case Polygon:
                final List<? extends List<LatLng>> lists =
                        ((GeoJsonPolygon) geometry).getCoordinates();
                for (List<LatLng> list : lists) {
                    coordinates.addAll(list);
                }
                break;
            case MultiPolygon:
                final List<GeoJsonPolygon> polygons =
                        ((GeoJsonMultiPolygon) geometry).getPolygons();
                for (GeoJsonPolygon polygon : polygons) {
                    for (List<LatLng> list : polygon.getCoordinates()) {
                        coordinates.addAll(list);
                    }
                }
                break;
            default:
        }
        return coordinates;
    }

    private String getColorResourceHexCode(int colorResourceId) {
        return CHAR_HEX_COLOR + drawRouteContext.getResources().getString(colorResourceId).substring(3);
    }
}
