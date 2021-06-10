package com.example.maps_rajwinderkaur_c0812274;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.maps_rajwinderkaur_c0812274.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;

    public static final int REQUEST_CODE = 1;

    private Marker homeMarker;
    private Marker desMarker;
    double latitude, longitude;
    double end_latitude, end_longitude;

    Polyline polyline;
    Polygon polygon;
    private  Geocoder geocoder;

    public static final int POLYGON_SIDES = 4;

    List<Marker> markerList = new ArrayList<>();
    LocationManager locationManager;
    LocationListener locationListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        @NonNull ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                setHomeMarker(location);
            }
        };
        if (!isGrantedPermission())
            requestlocationPermission();
        else
            startUpdateLocation();

        mMap.setOnMarkerDragListener(this);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick( LatLng latLng) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(addresses.size() > 0){
                        Address address = addresses.get(0);

                        String Address = address.getAddressLine(0);

                        String StreetName = address.getFeatureName();
                        String postalCode = address.getPostalCode();
                        String city = address.getLocality();
                        String province = address.getAdminArea();

                        MarkerOptions options = new MarkerOptions()
                                .position(latLng)
                                .title("Your point")
                                .snippet(Address)
                                .draggable(true);

                        //(end_latitude,end_longitude));

                       /*  float results[] = new float[10];
                        Location.distanceBetween(latitude,longitude,end_latitude,end_longitude, results);
                        options.snippet("Distance = " + results[0]);
                       */
                        if(markerList.size() == POLYGON_SIDES)
                            clearMap();

                        markerList.add(mMap.addMarker(options));
                        if(markerList.size() == POLYGON_SIDES)
                            drawshape();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }


    private void requestlocationPermission() {
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private boolean isGrantedPermission() {

        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(REQUEST_CODE == requestCode){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,0,locationListener);
    }
    }

    private void setHomeMarker(Location location) {
        LatLng userloc = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions()
                .position(userloc)
                .title("you are here")
                .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_baseline_directions_bike_24))
                .snippet("your location");
       homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userloc, 15));
    }

     private BitmapDescriptor bitmapDescriptorFromVector(Context context,int vectorResId){
         Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResId);
         vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),
                 vectorDrawable.getIntrinsicHeight());
         Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                 vectorDrawable.getMinimumHeight(),Bitmap.Config.ARGB_8888);
         Canvas canvas = new Canvas(bitmap);
         vectorDrawable.draw(canvas);
         return  BitmapDescriptorFactory.fromBitmap(bitmap);
     }
    private void drawshape() {

        PolygonOptions options = new PolygonOptions()
                .fillColor(0x3500FF00)
                .strokeColor(Color.RED)
                .strokeWidth(5);
        for(int a=0 ; a < POLYGON_SIDES; a++)
            options.add(markerList.get(a).getPosition());

        polygon = mMap.addPolygon(options);
    }

    private void clearMap(){

        for (Marker marker : markerList)
            marker.remove();

        markerList.clear();
        polygon.remove();
        polygon = null;
    }
    private void drawline() {
        PolylineOptions options = new PolylineOptions()
                .color(Color.RED)
                .width(10)
                .add(homeMarker.getPosition(), desMarker.getPosition());
        Polyline polyline = mMap.addPolyline(options);
    }


    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
     
