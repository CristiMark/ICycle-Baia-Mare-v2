package vyrus.bikegps.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import vyrus.bikegps.CardsDescriptions;
import vyrus.bikegps.CustomTimerTask;
import vyrus.bikegps.DataBase;
import vyrus.bikegps.Descriptions;
import vyrus.bikegps.DrawRoute;
import vyrus.bikegps.R;
import vyrus.bikegps.SliderImageAdapter;

import static vyrus.bikegps.DataBase.TABLE_COLUMN_DESC_COLOR;
import static vyrus.bikegps.DataBase.TABLE_COLUMN_DESC_COORDINATE;
import static vyrus.bikegps.DataBase.TABLE_COLUMN_DESC_DESCRIPTION;
import static vyrus.bikegps.DataBase.TABLE_COLUMN_DESC_IMG;
import static vyrus.bikegps.DataBase.TABLE_COLUMN_DESC_IMG_SEC;
import static vyrus.bikegps.DataBase.TABLE_COLUMN_DESC_NAME;
import static vyrus.bikegps.DataBase.TABLE_COLUMN_OWNER;
import static vyrus.bikegps.Descriptions.GEO_JSON_PROPERTY_MARKER_COLOR;
import static vyrus.bikegps.Descriptions.GEO_JSON_PROPERTY_NAME;
import static vyrus.bikegps.Descriptions.GEO_JSON_PROPERTY_OWNER;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected static final String EMPTY_STRING = "";
    protected static final String SQL_QUERY_COLOR_STRING = "Select * from bike_description where desc_color is not null  group by desc_name order by  case "
            + "WHEN desc_name LIKE 'Step%' THEN 1 "
            + "WHEN desc_name LIKE '%Victor Gorduza%' THEN 3 "
            + "WHEN desc_name LIKE '%Planetarium%' THEN 5 "
            + "WHEN desc_name LIKE '%Colony%' THEN 7  "
            + "WHEN desc_name LIKE '%Fossil%' THEN 8  "
            + "WHEN desc_name LIKE '%„Firiza” dam%' THEN 9 ELSE 10 END ";
    protected static final String STRING_ZERO = "0";
    protected static final int DOUBLE_BACK_EXIT_HANDLER_DELAY_MILLIS = 2000;
    protected static final String HTTP_MAPS_GOOGLE_COM_MAPS_DADDR = "http://maps.google.com/maps?daddr=";
    protected static final String HTTP_MAPS_GOOGLE_MODE_WALKING = "&mode=walking";
    protected static final float GOOGLE_MPAS_SET_ALPHA = 0.50f;
    protected static final char CHARACTER_HEX_COLOR = '#';
    private static final String HTTP_WWW_MUNTZOMANI_RO = "http://www.muntzomani.ro";
    private static final String HTTPS_MUNTZOMANI_RO_CULTURAL_EVENTS = "https://muntzomani.ro/cultural-events/";
    private static final String TAG = "MainActivity";
    private static final String HTML_BR_TAG = "<br><br>";
    private static final String PACKAGE = "package:";
    private static final String STRING_LINE_SYMBOL = "¬";
    private static final String STRING_NOT = "not";
    private static final String CAN_T_CONNECT_TO_GOOGLE_PLAY_SERVICES = "Can't connect to Google Play Services!";
    private static final String STRING_PLUS_TO = "+to:";
    private static final String CHARACTER_COMMA = ",";
    private static final char CHAR_OPENING_BRACKET = '(';
    private static final char CHAR_CLOSING_BRACKET = ')';
    private static final String HTML_BR_SIMPLE_TAG = "<br>";
    private static final String HTML_CLOSING_TAG = "</>";
    private static final String STRING_NEW_LINE_SYMBOL = "\n";
    private static String allCoord = "";
    private static DataBase dataBase;
    private GoogleMap googleMap;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetView;
    private LinearLayout linearLayoutContain;
    private CountDownTimer shortestLocationJumpPeriod;
    private GoogleApiClient googleApiClient;
    private FloatingActionMenu fab;
    private FloatingActionButton routeWithGoogleMap;
    private FloatingActionButton goToTop;
    private List<Double> distanceBtwPoints = new ArrayList<>();
    private int minIndex;
    private Timer timer;
    private TimerTask updateProfile;
    private List<LatLng> lineCoordinates = new ArrayList<>();
    private List<Polyline> polylineList = new ArrayList<>();
    private List<View> cardViews = new ArrayList<>();
    private CopyOnWriteArrayList<Marker> markerCopyOnWriteArrayList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Marker> markerIsVisibleCopyOnWriteArrayList = new CopyOnWriteArrayList<>();
    private int screenHeight;
    private int screenWight;
    private boolean doubleBackToExitPressedOnce = false;
    private Location lastLocation;
    private DrawRoute drawRoute;

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

    public static <T extends Comparable<T>> int findMinIndex(final List<T> xs) {
        int minIndex;
        if (xs.isEmpty()) {
            minIndex = -1;
        } else {
            final ListIterator<T> itr = xs.listIterator();
            T min = itr.next();
            minIndex = itr.previousIndex();
            while (itr.hasNext()) {
                final T curr = itr.next();
                if (curr.compareTo(min) < 0) {
                    min = curr;
                    minIndex = itr.previousIndex();
                }
            }
        }
        return minIndex;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheets);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linearLayoutContain = findViewById(R.id.linear_layout_content_list);

        final Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        screenWight = size.x;
        screenHeight = size.y;


        //  TestAdaptor mDbHelper = new TestAdaptor(getApplicationContext());
        //   mDbHelper.createDatabase();
        //  mDbHelper.open();

        //  Cursor testdata = mDbHelper.getTestData();

        //mDbHelper.close();
        dataBase = new DataBase(getApplicationContext());


        routeWithGoogleMap = findViewById(R.id.floating_action_button_route_with_google_map);
        routeWithGoogleMap.setAlpha(GOOGLE_MPAS_SET_ALPHA);
        bottomSheetView = findViewById(R.id.nested_scroll_view_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);

        int bottomSheetHeight;
        if (screenHeight <= 820) {
            bottomSheetHeight = 150;
        } else {
            bottomSheetHeight = 270;
        }

        bottomSheetBehavior.setPeekHeight(bottomSheetHeight);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        goToTop = findViewById(R.id.floating_action_bar_go_up);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(final View bottomSheet, final int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    fab.showMenu(true);
                    bottomSheet.scrollTo(0, 0);
                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    fab.hideMenu(true);
                    goToTop.show();
                    bottomSheet.requestLayout();
                    bottomSheet.invalidate();
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    goToTop.hide();
                    fab.showMenu(true);
                    bottomSheet.requestLayout();
                    bottomSheet.invalidate();
                }
            }

            @Override
            public void onSlide(final View bottomSheet, final float slideOffset) {
            }
        });

        initPointOfInterestFabMenu();

        final GoogleMapOptions options = new GoogleMapOptions();
        options.liteMode(true)
                .compassEnabled(true)
                .scrollGesturesEnabled(true)
                .zoomGesturesEnabled(true)
                .rotateGesturesEnabled(true);

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDrawerSlide(final View drawerView, final float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                if (slideOffset == 1) {
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    goToTop.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                }
                if (slideOffset == 0) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setHideable(false);
                    goToTop.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                }
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
    }

    private void initPointOfInterestFabMenu() {
        fab = findViewById(R.id.floating_action_bar_point_of_interests);

        int rightHeight;
        if (screenHeight <= 820) {
            rightHeight = 100;
        } else {
            rightHeight = 180;
        }
        fab.setPadding(0, 0, 20, rightHeight);
        fab.setClosedOnTouchOutside(true);

        for (POI pointOfInterestFabDetail : POI.values()) {
            final String poiLabel = getString(pointOfInterestFabDetail.titleRes);
            final String poiTag = pointOfInterestFabDetail.tag;
            final int resImgId = this.getResources().
                    getIdentifier(poiTag, "mipmap", this.getPackageName());

            final com.github.clans.fab.FloatingActionButton poiButton =
                    new com.github.clans.fab.FloatingActionButton(getApplicationContext());

            poiButton.setLabelText(poiLabel);
            poiButton.setColorNormal(Color.WHITE);
            poiButton.setColorPressed(Color.WHITE);
            poiButton.setButtonSize(FloatingActionButton.SIZE_MINI);
            poiButton.setImageResource(resImgId);
            poiButton.setTag(poiTag);

            fab.addMenuButton(poiButton);

            poiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    colorRoundOnClick(v);
                }
            });
        }
    }

    public void goToTopOnClick(final View v) {
        bottomSheetView.scrollTo(0, 0);
    }

    @Override
    public void onBackPressed() {
        googleApiClient.disconnect();

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.double_back_click_to_exit_message, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, DOUBLE_BACK_EXIT_HANDLER_DELAY_MILLIS);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_slider, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_easy) {
            setRoute(getColorResourceHexCode(R.color.settings_draw_route_easy), DifficultyLevel.Light.name());
            zoomCamera(15, new LatLng(47.662349, 23.576890));
        }
        if (id == R.id.nav_mediu) {
            setRoute(getColorResourceHexCode(R.color.settings_draw_route_medium), DifficultyLevel.Medium.name());
            zoomCamera(14, new LatLng(47.660080, 23.573015));
        }
        if (id == R.id.nav_hard) {
            setRoute(getColorResourceHexCode(R.color.settings_draw_route_hard), DifficultyLevel.Hard.name());
            zoomCamera(11, new LatLng(47.718170, 23.618475));
        }
        if (id == R.id.nav_extra_hard) {
            setRoute(getColorResourceHexCode(R.color.settings_draw_route_extra_hard), DifficultyLevel.Extrem.name());
            zoomCamera(11, new LatLng(47.718170, 23.618475));
        } else if (id == R.id.nav_address) {
            final Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(HTTP_WWW_MUNTZOMANI_RO));
            startActivity(i);
        } else if (id == R.id.nav_events) {
            final Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(HTTPS_MUNTZOMANI_RO_CULTURAL_EVENTS));
            startActivity(i);
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void zoomCamera(final int zoom, final LatLng coordination) {
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordination)
                .zoom(zoom)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;
        map.getUiSettings().setTiltGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        permissionStaffForMap();

        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);

        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(47.6666667, 23.5833333))
                .zoom(12)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        retrieveFileFromResource();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
            }
        });

        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick() {

        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        if (googleApiClient.isConnected()) {
            checkIfHaveLocation();
        }
        return true;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        for (View onlyOne : cardViews) {
            onlyOne.setVisibility(View.GONE);
            final String getTitle = marker.getTitle();
            final CardsDescriptions r = (CardsDescriptions) onlyOne.getTag();
            final String tempTitle = r.getTitle();
            final String colorTitle = r.getMarkerColor();

            if (getTitle.equals(tempTitle)) {
                final RelativeLayout colorBarCard = onlyOne.findViewById(R.id.relative_layout_color_bar);
                colorBarCard.setBackgroundColor(forCardBanner(colorTitle));
                colorRoundFAB(colorTitle);
                final ImageView route = onlyOne.findViewById(R.id.image_button_navigate_to);
                route.setVisibility(View.VISIBLE);
                onlyOne.setVisibility(View.VISIBLE);
            }
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addDetailsOnList() {
        final Cursor cursor = dataBase.getData(SQL_QUERY_COLOR_STRING);

        while (cursor.moveToNext()) {
            final String color = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_DESC_COLOR));
            final String descTitle = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_DESC_NAME));
            final String desc = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_DESC_DESCRIPTION));
            final String descCoordinate = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_DESC_COORDINATE));
            final String owner = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_OWNER));
            // final String img = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_DESC_IMG));
            final Bitmap img = getImage(cursor.getBlob(cursor.getColumnIndex(TABLE_COLUMN_DESC_IMG)));
            //   final String imgSec = cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_DESC_IMG_SEC));
            byte[] orgImg = cursor.getBlob(cursor.getColumnIndex(TABLE_COLUMN_DESC_IMG_SEC));
            Bitmap imgSec = getImage(orgImg);

            final View card = getLayoutInflater().inflate(R.layout.junk_cardview_google, null);
            card.setTag(new CardsDescriptions(descTitle, color, null, null, null, owner));
            cardViews.add(card);
            final ImageView nav = card.findViewById(R.id.image_button_navigate_to);
            nav.setTag(descCoordinate);
            final TextView title = card.findViewById(R.id.text_view_marker_title);
            final TextView content = card.findViewById(R.id.button_marker_content_text);
            final RelativeLayout colorBarCard = card.findViewById(R.id.relative_layout_color_bar);
            colorBarCard.setBackgroundColor(forCardBanner(EMPTY_STRING));
            title.setText(descTitle);
            title.setTag(descTitle);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    for (Marker marker : markerCopyOnWriteArrayList) {

                        if (marker.getTitle().equals(v.getTag().toString())) {
                            marker.showInfoWindow();
                            zoomCamera(13, marker.getPosition());
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                }
            });

            final ViewPager mViewPager = card.findViewById(R.id.view_pager_marker_image);
            final TabLayout tabLayout = card.findViewById(R.id.tab_layout_dots);
            final SliderImageAdapter adapterView = new SliderImageAdapter(this);
            final List<Bitmap> sliderImagesLinks = new ArrayList<>();

            if (img != null) {
                sliderImagesLinks.add(img);
            }

            if (imgSec != null) {
                sliderImagesLinks.add(imgSec);
            }

            adapterView.setImages(sliderImagesLinks);
            mViewPager.setAdapter(adapterView);
            mViewPager.setTag(sliderImagesLinks);
            if (imgSec != null) {
                tabLayout.setupWithViewPager(mViewPager);
            }

            if (desc != null && desc.contains(HTML_BR_TAG)) {
                content.setText(desc.substring(desc.lastIndexOf(HTML_CLOSING_TAG) + 1) + STRING_NEW_LINE_SYMBOL);
            } else if (desc != null && desc.contains(HTML_BR_SIMPLE_TAG)) {
                content.setText(desc.replace(HTML_BR_SIMPLE_TAG, STRING_NEW_LINE_SYMBOL) + STRING_NEW_LINE_SYMBOL);
            } else {
                content.setText(desc + STRING_NEW_LINE_SYMBOL);
            }
            content.setAutoLinkMask(Linkify.WEB_URLS);
            content.setLinksClickable(true);
            content.setLinkTextColor(Color.BLUE);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            linearLayoutContain.addView(card);
        }
    }

    public void routeOnClick(final View v) {
        final String latLog = v.getTag().toString().substring(v.getTag().toString().lastIndexOf(CHAR_OPENING_BRACKET) + 1, v.getTag().toString().indexOf(CHAR_CLOSING_BRACKET));
        final Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(HTTP_MAPS_GOOGLE_COM_MAPS_DADDR + latLog));
        startActivity(intent);
    }

    public Bitmap getImage(byte[] image) {
        if (image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        } else {
            return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_icon);
        }
    }

    public double calculationByDistance(final LatLng startPoint, final LatLng endPoint) {
        final int radius = 6371; // radius of earth in Km
        final double lat1 = startPoint.latitude;
        final double lat2 = endPoint.latitude;
        final double lon1 = startPoint.longitude;
        final double lon2 = endPoint.longitude;
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLon = Math.toRadians(lon2 - lon1);
        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        final double c = 2 * Math.asin(Math.sqrt(a));
        return radius * c;
    }

    public void addPoly(final ArrayList<PolylineOptions> polyLinesOpt, final String color) {
        polylineList.clear();

        for (PolylineOptions polylineOption : polyLinesOpt) {
            if (polylineOption.getColor() == drawRoute.toColorPoly(color)) {
                Polyline polyline = googleMap.addPolyline(polylineOption);
                polylineList.add(polyline);
            }
        }
    }

    public void colorRoundOnClick(final View v) {
        if (DrawRoute.getPolylineOptions() != null) {
            routeWithGoogleMap.hide();
            POI poi = POI.fromTag((String) v.getTag());

            assert poi != null;
            switch (poi) {

                case SCIENCE:
                    justColor(EMPTY_STRING);
                    removePolyline();
                    listParseByColor(STRING_ZERO);
                    justColor(getColorResourceHexCode(R.color.point_of_interest_mining_science_icon_blue));
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetView.scrollTo(0, 0);
                    listParseByColor(getColorResourceHexCode(R.color.point_of_interest_mining_science_icon_blue));
                    zoomCamera(13, new LatLng(47.660928, 23.537552));
                    fab.getMenuIconView().setImageResource(R.mipmap.blue_btn);
                    fab.close(true);
                    break;

                case CULTURE:
                    justColor(EMPTY_STRING);
                    removePolyline();
                    listParseByColor(STRING_ZERO);
                    justColor(getColorResourceHexCode(R.color.point_of_interest_museum_icon_purple));
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetView.scrollTo(0, 0);
                    listParseByColor(getColorResourceHexCode(R.color.point_of_interest_museum_icon_purple));
                    zoomCamera(15, new LatLng(47.662349, 23.576890));
                    fab.getMenuIconView().setImageResource(R.mipmap.purple_btn);
                    fab.close(true);
                    break;

                case NATURE:
                    justColor(EMPTY_STRING);
                    removePolyline();
                    listParseByColor(STRING_ZERO);
                    justColor(getColorResourceHexCode(R.color.point_of_interest_forest_icon_green));
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetView.scrollTo(0, 0);
                    zoomCamera(11, new LatLng(47.723870, 23.592410));
                    listParseByColor(getColorResourceHexCode(R.color.point_of_interest_forest_icon_green));
                    fab.getMenuIconView().setImageResource(R.mipmap.green_btn);
                    fab.close(true);
                    break;

                case HISTORY:
                    justColor(EMPTY_STRING);
                    removePolyline();
                    listParseByColor(STRING_ZERO);
                    justColor(getColorResourceHexCode(R.color.point_of_interest_monument_building_icon_yellow));
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetView.scrollTo(0, 0);
                    listParseByColor(getColorResourceHexCode(R.color.point_of_interest_monument_building_icon_yellow));
                    zoomCamera(14, new LatLng(47.660200, 23.582038));
                    fab.getMenuIconView().setImageResource(R.mipmap.yellow_btn);
                    fab.close(true);
                    break;

                case ALL:
                    justColor(EMPTY_STRING);
                    removePolyline();
                    listParseByColor(STRING_ZERO);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetView.scrollTo(0, 0);
                    zoomCamera(12, new LatLng(47.6666667, 23.5833333));
                    fab.getMenuIconView().setImageResource(R.mipmap.white_btn);
                    fab.close(true);
                    break;

                case ARTS:
                    justColor(EMPTY_STRING);
                    removePolyline();
                    listParseByColor(STRING_ZERO);
                    justColor(getColorResourceHexCode(R.color.point_of_interest_painting_art_icon_red));
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetView.scrollTo(0, 0);
                    listParseByColor(getColorResourceHexCode(R.color.point_of_interest_painting_art_icon_red));
                    zoomCamera(14, new LatLng(47.660551, 23.574187));
                    fab.getMenuIconView().setImageResource(R.mipmap.red_btn);
                    fab.close(true);
                    break;
                default:
                    Toast.makeText(this, R.string.sorry, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void colorRoundFAB(final String color) {

        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_forest_icon_green))) {
            fab.getMenuIconView().setImageResource(R.mipmap.green_btn);
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_monument_building_icon_yellow))) {
            fab.getMenuIconView().setImageResource(R.mipmap.yellow_btn);
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_museum_icon_purple))) {
            fab.getMenuIconView().setImageResource(R.mipmap.purple_btn);
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_mining_science_icon_blue))) {
            fab.getMenuIconView().setImageResource(R.mipmap.blue_btn);
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_painting_art_icon_red))) {
            fab.getMenuIconView().setImageResource(R.mipmap.red_btn);
        }
        if (color.contains(DifficultyLevel.Light.name())) {
            fab.getMenuIconView().setImageResource(R.mipmap.light_btn);
        }
        if (color.contains(DifficultyLevel.Medium.name())) {
            fab.getMenuIconView().setImageResource(R.mipmap.mediu_btn);
        }
        if (color.contains(DifficultyLevel.Hard.name())) {
            fab.getMenuIconView().setImageResource(R.mipmap.hard_btn);
        }
        if (color.contains(DifficultyLevel.Extrem.name())) {
            fab.getMenuIconView().setImageResource(R.mipmap.extrem_btn);
        }
    }

    public void justColor(final String color) {
        markerIsVisibleCopyOnWriteArrayList = new CopyOnWriteArrayList<>();
        minIndex = 0;

        stopShortestDistJump();

        for (Marker marker : markerCopyOnWriteArrayList) {
            final String col = marker.getTag().toString().substring(0, marker.getTag().toString().indexOf(STRING_LINE_SYMBOL));

            if (!col.equals(color)) {
                marker.setVisible(false);
            }
            if (col.equals(color)) {
                markerIsVisibleCopyOnWriteArrayList.add(marker);

            }
            if (color.equals(EMPTY_STRING)) {
                marker.setVisible(true);
            }
        }
    }

    void stopShortestDistJump() {

        try {
            if (shortestLocationJumpPeriod != null) {
                shortestLocationJumpPeriod.cancel();
                if (updateProfile != null) {
                    updateProfile.cancel();
                }
                if (timer != null) {
                    timer.cancel();
                }
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        } catch (Exception e) {
        }
    }

    public void justRoute(final String owner) {
        markerIsVisibleCopyOnWriteArrayList = new CopyOnWriteArrayList<>();
        minIndex = 0;

        stopShortestDistJump();

        for (Marker marker : markerCopyOnWriteArrayList) {
            final String markerOwner = marker.getTag().toString().substring(marker.getTag().toString().indexOf(STRING_LINE_SYMBOL) + 1);

            if (!markerOwner.contains(owner)) {
                marker.setVisible(false);
            } else {
                allCoord += marker.getPosition().latitude + CHARACTER_COMMA + marker.getPosition().longitude + STRING_PLUS_TO;
                markerIsVisibleCopyOnWriteArrayList.add(marker);
            }
            if (owner.equals(STRING_NOT)) {
                marker.setVisible(true);
            }
        }
    }

    public void routeWithNetOnClick(final View v) {
        if (!allCoord.isEmpty()) {
            allCoord = allCoord.substring(0, allCoord.length() - 4);
        }
        final Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(HTTP_MAPS_GOOGLE_COM_MAPS_DADDR + allCoord.trim() + HTTP_MAPS_GOOGLE_MODE_WALKING));
        allCoord = EMPTY_STRING;
        startActivity(intent);
    }

    public void floatingActionButtonOnClick(final View v) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void getShortestPoint(final Location lastLocation) {
        final double lat = lastLocation.getLatitude();
        final double lon = lastLocation.getLongitude();
        LatLng myPosition = new LatLng(lat, lon);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        if (myPosition != null) {
            distanceBtwPoints.clear();
            for (int i = 0; i < markerIsVisibleCopyOnWriteArrayList.size(); i++) {
                distanceBtwPoints.add(calculationByDistance(myPosition, markerIsVisibleCopyOnWriteArrayList.get(i).getPosition()));
            }

            minIndex = findMinIndex(distanceBtwPoints);
        }
    }

    public void checkIfHaveLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation == null) {
                final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            } else {
                getShortestPoint(lastLocation);
                shortestPoint();
            }
        }
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        checkIfHaveLocation();
    }

    @Override
    public void onConnectionSuspended(final int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.i(TAG, CAN_T_CONNECT_TO_GOOGLE_PLAY_SERVICES);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onMapClick(final LatLng latLng) {
    }

    private List<LatLng> getCoordinatesFromGeometry(final GeoJsonGeometry geometry) {
        final List<LatLng> coordinates = new ArrayList<>();
        final Descriptions.Geometry geometryType = Descriptions.Geometry.valueOf(geometry.getType());

        switch (geometryType) {
            case Point:
                coordinates.add(((GeoJsonPoint) geometry).getCoordinates());
                lineCoordinates.add(((GeoJsonPoint) geometry).getCoordinates());
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

    private void retrieveFileFromResource() {
        try {
            final GeoJsonLayer layer = new GeoJsonLayer(googleMap, R.raw.mapjust, this);
            addGeoJsonLayerToMap(layer);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void addGeoJsonLayerToMap(final GeoJsonLayer layer) {
        drawRoute = new DrawRoute(this, layer);
        addMarkers(layer);
        drawRoute.execute();
        addDetailsOnList();

        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @Override
            public void onFeatureClick(final GeoJsonFeature feature) {
                updateBottomSheetContent(feature);
            }
        });
    }

    private void addMarkers(final GeoJsonLayer layer) {

        for (GeoJsonFeature feature : layer.getFeatures()) {
            final String color = feature.getProperty(GEO_JSON_PROPERTY_MARKER_COLOR);
            final String title = feature.getProperty(GEO_JSON_PROPERTY_NAME);
            final String ownerRoute = feature.getProperty(GEO_JSON_PROPERTY_OWNER);
            final String col = (color != null) ? color : EMPTY_STRING;
            final String owner = (ownerRoute != null) ? ownerRoute : EMPTY_STRING;
            if (color != null) {
                final GeoJsonGeometry points = feature.getGeometry();
                final List<LatLng> coordinates = getCoordinatesFromGeometry(points);

                if (points.getType().equals(Descriptions.Geometry.Point.toString())) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(coordinates.get(0))
                            .title(title)
                            .icon(toIcon(col));
                    Marker mMarker = googleMap.addMarker(markerOptions);
                    mMarker.setTag(col + STRING_LINE_SYMBOL + owner);
                    markerCopyOnWriteArrayList.add(mMarker);
                    markerIsVisibleCopyOnWriteArrayList = markerCopyOnWriteArrayList;
                }
            }
        }
    }

    private void listParseByColor(final String colorHex) {

        for (View card : cardViews) {
            final CardsDescriptions markerTag = (CardsDescriptions) card.getTag();
            final String color = markerTag.getMarkerColor();

            if (!color.equals(colorHex)) {
                card.setVisibility(View.GONE);
            }

            if (colorHex.equals(STRING_ZERO)) {
                final RelativeLayout colorBarCard = card.findViewById(R.id.relative_layout_color_bar);
                colorBarCard.setBackgroundColor(forCardBanner(EMPTY_STRING));
                card.setVisibility(View.VISIBLE);
            }
            if (color.equals(colorHex)) {
                final RelativeLayout colorBarCard = card.findViewById(R.id.relative_layout_color_bar);
                colorBarCard.setBackgroundColor(forCardBanner(color));
            }
        }
    }

    private BitmapDescriptor toIcon(final String color) {
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_forest_icon_green))) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.green_btn);
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_monument_building_icon_yellow))) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.yellow_btn);
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_museum_icon_purple))) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.purple_btn);
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_mining_science_icon_blue))) {

            return BitmapDescriptorFactory.fromResource(R.mipmap.blue_btn);
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_painting_art_icon_red))) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.red_btn);
        } else {
            return BitmapDescriptorFactory.fromResource(R.mipmap.white_btn);
        }
    }

    private void updateBottomSheetContent(final GeoJsonFeature marker) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private int forCardBanner(final String color) {

        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_forest_icon_green))) {
            return Color.parseColor(getColorResourceHexCode(R.color.point_of_interest_forest_icon_green));
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_monument_building_icon_yellow))) {
            return Color.parseColor(getColorResourceHexCode(R.color.point_of_interest_monument_building_icon_yellow));
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_museum_icon_purple))) {
            return Color.parseColor(getColorResourceHexCode(R.color.point_of_interest_museum_icon_purple));
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_mining_science_icon_blue))) {
            return Color.parseColor(getColorResourceHexCode(R.color.point_of_interest_mining_science_icon_blue));
        }
        if (color.equals(getColorResourceHexCode(R.color.point_of_interest_painting_art_icon_red))) {
            return Color.parseColor(getColorResourceHexCode(R.color.point_of_interest_painting_art_icon_red));
        }
        if (color.contains(DifficultyLevel.Light.name())) {
            return Color.parseColor(getColorResourceHexCode(R.color.difficulty_color_mode_easy));
        }
        if (color.contains(DifficultyLevel.Medium.name())) {
            return Color.parseColor(getColorResourceHexCode(R.color.difficulty_color_mode_medium));
        }
        if (color.contains(DifficultyLevel.Hard.name())) {
            return Color.parseColor(getColorResourceHexCode(R.color.difficulty_color_mode_hard));
        }
        if (color.contains(DifficultyLevel.Extrem.name())) {
            return Color.parseColor(getColorResourceHexCode(R.color.difficulty_color_mode_extra_hard));
        } else {
            return Color.LTGRAY;
        }
    }

    private void shortestPoint() {

        if (minIndex > -1) {
            try {
                timer = new Timer();
                shortestLocationJumpPeriod = new CountDownTimer(10000, 2000) {

                    public void onTick(final long millisUntilFinished) {
                        updateProfile = new CustomTimerTask(markerIsVisibleCopyOnWriteArrayList.get(minIndex));
                        timer.scheduleAtFixedRate(updateProfile, 10, 2000);

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerIsVisibleCopyOnWriteArrayList.get(minIndex).getPosition(), 14));
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }

                    public void onFinish() {
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        timer.purge();
                        timer.cancel();
                    }

                }.start();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        } else {
            if (lastLocation != null) {
                checkIfHaveLocation();
            }
        }
    }

    private void setRoute(final String color, final String level) {
        justRoute(STRING_NOT);
        justRoute(level);
        removePolyline();
        colorRoundFAB(STRING_ZERO);
        colorRoundFAB(level);
        listRouteParseByColor(STRING_ZERO);
        listRouteParseByColor(level);
        routeWithGoogleMap.show();
        addPoly(DrawRoute.getPolylineOptions(), color);
    }

    private void listRouteParseByColor(final String level) {

        for (View card : cardViews) {
            final CardsDescriptions markerTag = (CardsDescriptions) card.getTag();
            final String route = markerTag.getOwner();
            if (!route.contains(level)) {
                card.setVisibility(View.GONE);
            }

            if (level.equals(STRING_ZERO)) {
                final RelativeLayout colorBarCard = card.findViewById(R.id.relative_layout_color_bar);
                colorBarCard.setBackgroundColor(forCardBanner(EMPTY_STRING));
                card.setVisibility(View.VISIBLE);
            }
            if (route.contains(level)) {
                final RelativeLayout colorBarCard = card.findViewById(R.id.relative_layout_color_bar);
                colorBarCard.setBackgroundColor(forCardBanner(route));
            }
        }
    }

    private void removePolyline() {

        for (Polyline line : polylineList) {
            line.setVisible(false);
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
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(getApplicationContext(), R.string.gps_permission_grated_info_message, Toast.LENGTH_SHORT).show();
            startInstalledAppDetailsActivity(this);
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void permissionStaffForMap() {

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                || checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            googleMap.setMyLocationEnabled(true);
        } else {
            if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    && !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    || !checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && !checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission();
            } else {
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    private String getColorResourceHexCode(int colorResourceId) {
        return CHARACTER_HEX_COLOR + getResources().getString(colorResourceId).substring(3);
    }

    private enum POI {
        ALL(R.string.poi_all, "white_btn"),
        SCIENCE(R.string.poi_science, "blue_btn"),
        NATURE(R.string.poi_nature, "green_btn"),
        ARTS(R.string.poi_arts, "red_btn"),
        HISTORY(R.string.poi_history, "yellow_btn"),
        CULTURE(R.string.poi_culture, "purple_btn");

        private final int titleRes;
        private final String tag;

        POI(int titleRes, String tag) {
            this.titleRes = titleRes;
            this.tag = tag;
        }

        public static POI fromTag(String tag) {
            for (POI poi : POI.values()) {
                if (poi.tag.equals(tag)) {
                    return poi;
                }
            }
            Log.e(TAG, "POI is null, this should not happen - tag = " + tag);
            return null;
        }
    }

    enum DifficultyLevel {
        Light, Medium, Hard, Extrem
    }
}