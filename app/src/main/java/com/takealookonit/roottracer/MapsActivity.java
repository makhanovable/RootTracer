package com.takealookonit.roottracer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.takealookonit.roottracer.backend.HttpAddPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ArrayList<LatLng> lines = new ArrayList<>();
    private ArrayList<Long> time = new ArrayList<>();

    private long startTime = 0;
    private long endTime = 0;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        setListener();

        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences = getPreferences(LoginAuthActivity.EMAIL);
        email = sharedPreferences.getString("email", "makhanovable@gmail.com");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(MapsActivity.this, TrackerService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(MapsActivity.this, TrackerService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(MapsActivity.this, TrackerService.class);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(MapsActivity.this, TrackerService.class);
        startService(intent);
    }

    public void startTrack(View view) {
        Intent intent = new Intent(MapsActivity.this, TrackerService.class);
        startService(intent);
    }



    //Add new coordinates if location changed
    private void changed(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        HttpAddPoint httpAddPoint = new HttpAddPoint(null);
        Toast.makeText(this,location.getLatitude() + ""+
                location.getLongitude() + "",Toast.LENGTH_SHORT).show();
        httpAddPoint.execute(email, location.getLatitude() + "",
                location.getLongitude() + "", endTime + "", "1000000");//TODO route
//        if (lines.size() == 0) {
//            lines.add(latLng);
//        } else {
//            double distance = calculateDistance(latLng, lines.get(lines.size() - 1));
//            System.out.println("Distance is: " + distance);
//            if (distance > 0.00001) {
//                lines.add(latLng);
//            }
//
//        }
//
//        drawShapes();
    }

    //Drawing  lines circles etc on map
    private void drawShapes() {
        PolylineOptions path = new PolylineOptions();
        CircleOptions circleOptions = new CircleOptions();
        for (LatLng latLng : lines) {
            path.add(latLng);
            circleOptions.center(latLng).radius(20).fillColor(Color.argb(255, 255, 0, 0));
        }
        Polyline polyline = mMap.addPolyline(path);
        Circle circle = mMap.addCircle(circleOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        setListener();
    }

    private void setListener() {
        Toast.makeText(this, "Checking", Toast.LENGTH_SHORT).show();
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {// add time
                Toast.makeText(MapsActivity.this, "CLLCLCLC",Toast.LENGTH_SHORT).show();
                endTime = System.currentTimeMillis() - startTime;
                changed(location);
                startTime = System.currentTimeMillis();
                time.add(endTime);
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    //Calculating Distance
    private double calculateDistance(LatLng startPos, LatLng endPos) {
        int radius = 6371;
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
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int lmIndec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        return meter;
    }
}