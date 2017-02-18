package com.client.gp.sharksclientapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PickupMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView addrtxt;
    Button submitbtn;
    public static double lat, lng;
    public static String approxAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_map);

        addrtxt = (TextView) findViewById(R.id.addrtxt);
        submitbtn = (Button) findViewById(R.id.submitbtn);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PickupMapActivity.this, SubmitRequestActivity.class));
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        mMap.setMyLocationEnabled(true);

        //show my location
        Location loc = MyApplication.getLastKnownLocation();
        LatLng ll = new LatLng(loc.getLatitude(),loc.getLongitude());//for test onlyyy //ay location we hyt8yr lma ysm3
        //move to this location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));



        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                addrtxt.setText("Loading");
            }
        });
        mMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d("Locationnnn", "lat: "+mMap.getCameraPosition().target.latitude+" Lng: "+mMap.getCameraPosition().target.longitude);
                lat=mMap.getCameraPosition().target.latitude;
                lng=mMap.getCameraPosition().target.longitude;

                //get approx loc
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(PickupMapActivity.this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    approxAddress = addresses.get(0).getAddressLine(0);
                    addrtxt.setText(approxAddress);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });




    }
}
