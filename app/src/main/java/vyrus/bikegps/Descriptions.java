package vyrus.bikegps;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonGeometry;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonLineString;
import com.google.maps.android.geojson.GeoJsonMultiLineString;
import com.google.maps.android.geojson.GeoJsonMultiPoint;
import com.google.maps.android.geojson.GeoJsonMultiPolygon;
import com.google.maps.android.geojson.GeoJsonPoint;
import com.google.maps.android.geojson.GeoJsonPolygon;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vyrus on 07/12/2016.
 */

public class Descriptions extends AsyncTask {
    public static final String GEO_JSON_PROPERTY_OWNER = "owner";
    public static final String GEO_JSON_PROPERTY_NAME = "name";
    public static final String GEO_JSON_PROPERTY_MARKER_COLOR = "marker-color";
    private static final String TAG = "description";
    private static final String HTTP_MUNTZOMANI_RO_BIKE_MAP_APP_PICS = "http://muntzomani.ro/BikeMapApp_Pics/";
    private static final String HTTP_MUNTZOMANI_RO_PIC_ADDRESS = "http://muntzomani.ro/";
    private static final String EMPTY_STRING = "";
    private static final String HTML_BR_TAG = "<br><br>";
    private static final String CHARSET_UTF_8 = "UTF-8";
    private static final String GEO_JSON_PROPERTY_DESCRIPTION = "description";
    private static final String CHARACTER_SLASH = "/";
    private static final String STRING_ARTE = "arte";
    private static final String STRING_ARTE_FUNDATION = "ARTE+Fundation";

    private static ArrayList<CardsDescriptions> cardDetails = new ArrayList<CardsDescriptions>();
    private GeoJsonLayer geoJsonLayer;
    private DataBase markerDataBase;
    private OnTaskCompleted onTaskCompletedListener;

    public Descriptions(final OnTaskCompleted listener, final GeoJsonLayer layout, final DataBase dataBase) {
        this.geoJsonLayer = layout;
        this.markerDataBase = dataBase;
        this.onTaskCompletedListener = listener;
    }

    @Override
    protected Object doInBackground(final Object[] params) {
        List<String> pictureLinkList = new ArrayList<String>();
        try {
            final LinkListFromURLThread pictureList = new LinkListFromURLThread(HTTP_MUNTZOMANI_RO_BIKE_MAP_APP_PICS);
            pictureList.start();
            pictureList.join();
            pictureLinkList = pictureList.getPicList();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }

        for (GeoJsonFeature feature : geoJsonLayer.getFeatures()) {
            final String all = feature.getProperty(GEO_JSON_PROPERTY_DESCRIPTION);
            String description;
            String owner = EMPTY_STRING;
            if (all != null && all.contains(HTML_BR_TAG)) {
                description = all.substring(0, all.lastIndexOf(HTML_BR_TAG));
            } else {
                description = all;
            }
            final List<String> allImageLinks = new ArrayList<>();

            if (feature.getProperty(GEO_JSON_PROPERTY_OWNER) != null) {
                owner = feature.getProperty(GEO_JSON_PROPERTY_OWNER);
            }
            String getURL = null;
            for (String picture : pictureLinkList) {
                String name = feature.getProperty(GEO_JSON_PROPERTY_NAME);
                try {
                    getURL = picture;
                    String urlPictureDecoder = java.net.URLDecoder.decode(picture, CHARSET_UTF_8);
                    if (urlPictureDecoder.toLowerCase().contains(CHARACTER_SLASH.toLowerCase())) {
                        picture = java.net.URLDecoder.decode(picture.substring(picture.lastIndexOf(CHARACTER_SLASH) + 1), CHARSET_UTF_8);
                    }
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, e.toString());
                }

                if (picture.toLowerCase().contains(STRING_ARTE) && name.toLowerCase().contains(STRING_ARTE)) {
                    picture = STRING_ARTE_FUNDATION;
                    name = STRING_ARTE_FUNDATION;
                }

                if (picture.toLowerCase().contains(name.toLowerCase())) {
                    allImageLinks.add(HTTP_MUNTZOMANI_RO_PIC_ADDRESS + getURL.substring(2, getURL.length() - 1));
                }
            }
            final GeoJsonGeometry points = feature.getGeometry();
            final List<LatLng> coordination = getCoordinatesFromGeometry(points);
            final CardsDescriptions cardDetail = new CardsDescriptions(feature.getProperty(GEO_JSON_PROPERTY_NAME),
                    feature.getProperty(GEO_JSON_PROPERTY_MARKER_COLOR), description, coordination.get(0),
                    allImageLinks, owner);
            cardDetails.add(cardDetail);
        }

        for (CardsDescriptions cardDetail : cardDetails) {
            String secondImage;
            String firstImage = null;

            try {
                firstImage = cardDetail.getImagePath().get(0);
                secondImage = cardDetail.getImagePath().get(1);
            } catch (Exception e) {
                secondImage = null;
            }

            markerDataBase.insertDesc(cardDetail.getTitle(), cardDetail.getMarkerColor(), cardDetail.getDescription(),
                    cardDetail.getCoordination().toString(), firstImage, secondImage, cardDetail.getOwner());
        }
        return geoJsonLayer;
    }

    protected void onPostExecute(final Object result) {
        super.onPostExecute(result);
        if (onTaskCompletedListener != null) {
            onTaskCompletedListener.onTaskCompleted();
        }
    }

    private List<LatLng> getCoordinatesFromGeometry(final GeoJsonGeometry geometry) {
        final List<LatLng> coordinates = new ArrayList<>();
        Geometry geometryType = Geometry.valueOf(geometry.getType());

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
                break;
        }
        return coordinates;
    }

    public enum Geometry {
        MultiPolygon,
        Polygon,
        MultiLineString,
        LineString,
        MultiPoint,
        Point
    }

    public interface OnTaskCompleted {
        void onTaskCompleted();
    }
}

class LinkListFromURLThread extends Thread {
    private static final String TAG = "LinkListFromURLThread";
    private String mUrl;
    private List<String> rez = new ArrayList<String>();

    LinkListFromURLThread(String url) {
        this.mUrl = url;
    }

    private static List<String> getTagValues(final String str) {
        final Pattern tagRegex = Pattern.compile("<a href=(.+?)>");

        final List<String> tagValues = new ArrayList<String>();
        final Matcher matcher = tagRegex.matcher(str);
        while (matcher.find()) {
            tagValues.add(matcher.group(1));
        }
        return tagValues;
    }

    List<String> getPicList() {
        return rez;
    }

    public void run() {
        final StringBuilder stringBuilder = new StringBuilder();
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(this.mUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();
            while (data != -1) {
                char current = (char) data;
                data = inputStreamReader.read();
                stringBuilder.append(current);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        rez = getTagValues(stringBuilder.toString());
    }
}