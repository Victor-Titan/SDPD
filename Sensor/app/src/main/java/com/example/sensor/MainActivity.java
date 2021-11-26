package com.example.sensor;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    SensorManager sensorManager;
    Sensor magFieldSensor, accelerometer;
    SensorEventListener sensorListener;
    LocationListener locationListener;
    LocationManager locationManager;
    TextView orientationView, locationView;

    private float[] gravityValues = new float[3];
    private float[] geoMagnetValues = new float[3];
    private float[] orientation = new float[3];
    private float[] rotationMatrix = new float[9];

    private final static int ALL_PERMISSIONS_RESULT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        magFieldSensor = sensorManager
                .getDefaultSensor(TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager
                .getDefaultSensor(TYPE_ACCELEROMETER);
        sensorListener = new MySensorEventListener();
        locationListener = new MyLocationListener();
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        orientationView =
                (TextView) findViewById(R.id.orientationView);
        locationView =
                (TextView) findViewById(R.id.locationView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener,
                magFieldSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener,
                accelerometer, SensorManager.SENSOR_DELAY_UI);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(ACCESS_FINE_LOCATION);
            permissions.add(ACCESS_COARSE_LOCATION);

            permissionsToRequest = findUnAskedPermissions(permissions);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                if (permissionsToRequest.size() > 0)
                    requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }

            return;
        }
        locationManager.requestLocationUpdates
                (LocationManager.NETWORK_PROVIDER,
                        0, 0, locationListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
        locationManager.removeUpdates(locationListener);
    }


    class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int sensorEventType = event.sensor.getType();
            if (sensorEventType == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy
                        (event.values, 0, gravityValues, 0, 3);
            } else if (sensorEventType ==
                    Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy
                        (event.values, 0, geoMagnetValues, 0, 3);
            } else {
                return;
            }
            if (SensorManager.getRotationMatrix(rotationMatrix,
                    null, gravityValues, geoMagnetValues)) {
                SensorManager.getOrientation(rotationMatrix,
                        orientation);
                orientationView.setText
                        ("Yaw: " + orientation[0] + "\n"
                                + "Pitch: " + orientation[1] + "\n"
                                + "Roll: " + orientation[2]);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor,
                                      int accuracy) {
            if (accuracy <= 1) {
                Toast.makeText(MainActivity.this, "Please shake the " +
                        "device in a figure eight pattern to " +
                        "improve sensor accuracy!", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            locationView.setText
                    ("Latitude: " + location.getLatitude() + "\n"
                            + "Longitude: " + location.getLongitude());
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
        }
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}

