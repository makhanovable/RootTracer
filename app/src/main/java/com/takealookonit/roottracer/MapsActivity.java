package com.takealookonit.roottracer;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.takealookonit.roottracer.backend.GetPoints;
import com.takealookonit.roottracer.backend.HttpAddPoint;
import com.takealookonit.roottracer.backend.models.Point;
import com.takealookonit.roottracer.backend.models.PointWrapper;
import com.takealookonit.roottracer.database.EmailDB;
import com.takealookonit.roottracer.database.LastDB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GetPoints.GetPointsInterface {

    private GoogleMap mMap;
    private TextView textView;

    private List<LatLng> lines = new ArrayList<>();
    private List<Long> time = new ArrayList<>();
    private List<Double> distance = new ArrayList<>();

    private long startTime = 0;
    private long endTime = 0;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private Button startStopButton;
    boolean start = true;
    SharedPreferences sharedPreferences;

    String email;
    int route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sharedPreferences = getPreferences(1212);
        route = sharedPreferences.getInt("route", 0);
        route++;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loadRoutes();

        EmailDB database = new EmailDB(this);
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM ema ", null);
        int a = cursor.getColumnIndex("email");
        while (cursor.moveToNext()) {
            email = cursor.getString(a);
        }
        cursor.close();

        startStopButton = findViewById(R.id.start_stop_button);
        startTime = System.nanoTime();

        textView = findViewById(R.id.average_velocity_text);
    }

    private void loadRoutes() {
        GetPoints getPoints = new GetPoints(this);
        getPoints.getPointsWrapper();
    }

    @Override
    protected void onDestroy() {
        sharedPreferences = getPreferences(1212);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("route", route);
        editor.apply();
        super.onDestroy();
    }

    //Add new coordinates if location changed
    private void changed(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (lines.size() == 0) {
            addLatlng(latLng);
        } else {
            double distance = calculateDistance(latLng, lines.get(lines.size() - 1));
            System.out.println("Distance is: " + distance);
            if (distance > 60) {
                this.distance.add(distance);
                addLatlng(latLng);
            }

        }

        drawShapes();
    }


    private void addLatlng(LatLng latLng) {
        lines.add(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        endTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        time.add(endTime);

        HttpAddPoint httpAddPoint = new HttpAddPoint(this); // TODO sent to server
        httpAddPoint.execute(email, latLng.latitude + "", latLng.longitude + "", endTime + "",
                route + "");
    }

    private void averageVelocity() {
        double sum = 0;
        for (int i = 0; i < distance.size(); i++) {
            long t = time.get(i + 1) - time.get(i);
            double v = distance.get(i) / (t / 1000);
            sum += v;
        }
        textView.setText("Average velocity is: " + (sum / distance.size()));
    }

    //Drawing  lines circles etc on map
    private void drawShapes() {

        PolylineOptions path = new PolylineOptions();

        for (LatLng latLng : lines) {
            path.add(latLng);

            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng).radius(20)
                    .fillColor(Color.argb(255, 255, 60, 60));
            Circle circle = mMap.addCircle(circleOptions);

        }
        Polyline polyline = mMap.addPolyline(path);
        stylePolyLine(polyline);

        if (lines.size() > 1) {
            Matrix matrix = new Matrix();
            int i = lines.size() - 1;
            double rotDegree = Math.toDegrees(
                    Math.atan2(lines.get(i).latitude - lines.get(i - 1).latitude,
                            lines.get(i).longitude - lines.get(i - 1).longitude));
            matrix.postRotate((float) rotDegree);
            Bitmap arrowBit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_keyboard_arrow_up_black_18dp_1x);
            Bitmap arr = Bitmap.createBitmap(arrowBit, 0, 0, 24, 24, matrix, true);
            Marker marker = mMap.addMarker(new MarkerOptions().position(lines.get(i)).icon(BitmapDescriptorFactory.fromBitmap(arr)));

        }
    }

    private void stylePolyLine(Polyline polyline) {
        String type = "";
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(12);
        polyline.setJointType(JointType.ROUND);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(17);
    }

    public void treckingOnOff(View view) {
        if (start) {
            setListener();
            route++;
            startStopButton.setText("Stop");
            start = false;
        } else {
            start = true;
            startStopButton.setText("Start");
            locationManager.removeUpdates(locationListener);
            Toast.makeText(this, "Size of dist:" + distance.size() + " Size of time:" + time.size(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setListener() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Toast.makeText(this, "Checking", Toast.LENGTH_SHORT).show();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {// add time
                changed(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    //Calculating Distance
    private double calculateDistance(LatLng startPos, LatLng endPos) {
        double radius = 3958.75;
        double lat1 = startPos.latitude;
        double lat2 = endPos.latitude;
        double lon1 = startPos.longitude;
        double lon2 = endPos.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double valueResult = radius * c;

        int meterConversion = 1609;
        return valueResult * meterConversion;
    }

    @Override
    public void getPointsWrapper(PointWrapper pointWrapper) {
        // TODO
        List<Point> points = pointWrapper.getPoints();
        List<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getEmail().equals(email)) {
                latLngs.add(new LatLng(Double.parseDouble(points.get(i).getLat()),
                        Double.parseDouble(points.get(i).getLot())));
            }
        }

    }
}