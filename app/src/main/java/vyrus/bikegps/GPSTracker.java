package vyrus.bikegps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class GPSTracker extends Service implements LocationListener {
    private static final int LOCATION_ACCURACY_INT = 10;
    private static double latitude;
    private static double longitude;
    private final Context trackerContext;
    private long mapMinDistanceForUpdates;
    private long mapMinTimeBwUpdate;
    private boolean isGPSEnabled = false;
    private float accuracy;

    private LocationManager locationManager;

    public GPSTracker(final Context context, final long minDistanceForUpdates, final long minTimeBwUpdate) {
        this.trackerContext = context;
        this.mapMinDistanceForUpdates = minDistanceForUpdates;
        this.mapMinTimeBwUpdate = minTimeBwUpdate;
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null && location.getAccuracy() < LOCATION_ACCURACY_INT) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = location.getAccuracy();
        } else {
            isGPSEnabled = false;
            if (ActivityCompat.checkSelfPermission(trackerContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(trackerContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    mapMinTimeBwUpdate,
                    mapMinDistanceForUpdates, this);
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    accuracy = location.getAccuracy();
                } else {
                    Toast.makeText(trackerContext, R.string.gps_error_location_message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onProviderDisabled(final String provider) {
    }

    @Override
    public void onProviderEnabled(final String provider) {
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    }

    @Override
    public IBinder onBind(final Intent arg0) {
        return null;
    }
}
