package com.takealookonit.roottracer;

/**
 * Created by Makhanov Madiyar
 * Date: 1/27/18.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.takealookonit.roottracer.backend.HttpAddPoint;
import com.takealookonit.roottracer.database.EmailDB;
import com.takealookonit.roottracer.database.LastDB;

import java.util.ArrayList;

/**
 * Created by Makhanov Madiyar
 * Date: 8/3/17.
 */

public class TrackerService extends Service {

    private SQLiteDatabase database;
    private ContentValues contentValues;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    public static int NOTIFICATION_ID = 26529;
    String email;int route;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EmailDB database = new EmailDB(this);
        LastDB lastDB = new LastDB(this);
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        SQLiteDatabase liteDatabase = lastDB.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM ema ", null);
        int a = cursor.getColumnIndex("email");
        while (cursor.moveToNext()) {
            email = cursor.getString(a);
        }
        cursor.close();
        Cursor c = liteDatabase.rawQuery("SELECT * FROM lastroute", null);
        int aa = c.getColumnIndex("route");
        while (c.moveToNext()) {
            route = c.getInt(aa);
        }
        c.close();
        startTrack();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        builder = null;
        notificationManager.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

    public void startTrack() {
        Intent resultIntent = new Intent(this, MapsActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentTitle("Tracking");

        builder.setContentIntent(resultPendingIntent);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        builder.setContentText("developed by hanz");
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        track();
    }

    private long startTime = 0;
    private long endTime = 0;
    public void track() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {// add time
                endTime = System.currentTimeMillis() - startTime;
                changed(location);
                startTime = System.currentTimeMillis();
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

    private void changed(Location location) {
        HttpAddPoint httpAddPoint = new HttpAddPoint(null);
        httpAddPoint.execute(email, location.getLatitude() + "",
                location.getLongitude() + "", endTime + "", route + "");//TODO route
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}