package com.geotec.coordenadas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    final static int REQUEST_CODE_PERMISSION_LOCATION = 1;
    final static String[] PERMISSIONS = new String[]{ACCESS_FINE_LOCATION};

    private Button btnGetLocation;
    public TextView lblTextLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //requestPermissions();

        this.btnGetLocation = (Button) findViewById(R.id.btn_get_location);
        this.lblTextLocation = (TextView) findViewById(R.id.lbl_text_location);

        this.btnGetLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_location:
                getLocation();
                break;
        }
    }

    private void showLocation(Location location) {
        String textLocation = getString(R.string.lbl_text_location);
        textLocation += "\n\t\tLongitude: "   + location.getLongitude();
        textLocation += "\n\t\tLatitude: "    + location.getLatitude();
        textLocation += "\n\t\tAltitude: "    + location.getAltitude();
        textLocation += "\n\t\tAccuracy: "    + location.getAccuracy();
        textLocation += "\n\t\tSpeed: "       + location.getSpeed();
        textLocation += "\n\t\tTime: "        + location.getTime();
        lblTextLocation.setText(textLocation);
    }

    private void showError(String msg) {
        btnGetLocation.setEnabled(true);
        lblTextLocation.setText(msg);
    }

    private void getLocation() {
        btnGetLocation.setEnabled(false);
        lblTextLocation.setText(getString(R.string.getting_location));
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localization localization = new Localization();

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, (LocationListener) localization);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, (LocationListener) localization);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSION_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PermissionsResult", "requestCode: " + requestCode);
        boolean noPermissions = true;
        for (int g: grantResults) {
            if (g < 0) {
                noPermissions = false;
            }
        }
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (noPermissions) getLocation();
                else showError(getString(R.string.permissions_denied));
                break;
        }
    }

    private class Localization implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            switch (i) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String s) {
            showError(getString(R.string.on_provider_enabled));
        }

        @Override
        public void onProviderDisabled(String s) {
            showError(getString(R.string.on_provider_disabled));
        }
    }
}