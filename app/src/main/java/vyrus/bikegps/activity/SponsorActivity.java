package vyrus.bikegps.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONException;

import java.io.IOException;

import vyrus.bikegps.DataBase;
import vyrus.bikegps.Descriptions;
import vyrus.bikegps.R;

/**
 * Created by Vyrus on 04/12/2016.
 */
public class SponsorActivity extends Activity implements Descriptions.OnTaskCompleted {
    private static final String TAG = "SponsorActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String SELECT_DESCRIPTION_QUERY_SQL = "Select desc_name from bike_description ";
    private static final int HANDLER_DELAY_MILLIS = 1000;
    private static final int FLIP_INTERVAL_MILLIS = 2500;
    private static final String PACKAGE = "package:";

    private DataBase dataBase;
    private ViewFlipper sponsorViewFlipper;
    private Descriptions desc;
    private Descriptions.OnTaskCompleted listener = new Descriptions.OnTaskCompleted() {
        public void onTaskCompleted() {
            final Intent intent = new Intent(SponsorActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse(PACKAGE + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sponsor_fragm);

        sponsorViewFlipper = findViewById(R.id.sponsorViewFlipper);
        final Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        final Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        sponsorViewFlipper.setInAnimation(in);
        sponsorViewFlipper.setOutAnimation(out);

        sponsorViewFlipper.setFlipInterval(FLIP_INTERVAL_MILLIS);
        sponsorViewFlipper.startFlipping();

        dataBase = new DataBase(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Cursor have = dataBase.getData(SELECT_DESCRIPTION_QUERY_SQL);
                if (have == null || have.getCount() <= 0) {
                    try {
                        final GeoJsonLayer layer = new GeoJsonLayer(null, R.raw.mapjust, getApplicationContext());
                        desc = new Descriptions(listener, layer, dataBase);
                        desc.execute();
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    permission();
                    sponsorViewFlipper.stopFlipping();
                    final Intent intent = new Intent(SponsorActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, HANDLER_DELAY_MILLIS);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        }
    }

    @Override
    public void onTaskCompleted() {
    }

    private void permission() {
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                || !checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && !checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                || !checkPermission(Manifest.permission.GET_ACCOUNTS)) {
            requestPermission();
        }
    }

    private boolean checkPermission(final String permission) {
        final int result = ActivityCompat.checkSelfPermission(getApplicationContext(), permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {

            Toast.makeText(getApplicationContext(), R.string.gps_permission_grated_info_message, Toast.LENGTH_SHORT).show();
            startInstalledAppDetailsActivity(this);
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERMISSION_REQUEST_CODE);
        }
    }
}
