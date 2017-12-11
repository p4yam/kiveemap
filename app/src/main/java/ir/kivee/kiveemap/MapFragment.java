package ir.kivee.kiveemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by payam on 12/9/17.
 */

public class MapFragment extends SupportMapFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private final int[] mapTypes = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    private int currentMapType = 1;
    private LocationManager locationManager;
    private CameraPosition position;
    private GoogleMap map;
    private final int MY_PERMISSION_ACCESS_LOCATION = 12;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != googleApiClient && googleApiClient.isConnected())
            googleApiClient.disconnect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationManager = (LocationManager) getActivity()
                .getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled && isNetworkEnabled) {
            new getLocationAsync().execute();

        }

    }


    private void setCamera(Location location) {
        position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(),
                        location.getLongitude()))
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);
        checkPermission();
        map.setMyLocationEnabled(true);
        map.setMapType(mapTypes[currentMapType]);
        map.setTrafficEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position(latLng);
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            options.title(geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0)
                    .getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        options.icon(BitmapDescriptorFactory.defaultMarker());
        map.addMarker(options);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position(latLng);
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            options.title(geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0)
                    .getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        options.icon(BitmapDescriptorFactory.defaultMarker());
        map.addMarker(options);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMapClickListener(this);

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity()
                        , Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_LOCATION);
        }
    }

    class getLocationAsync extends AsyncTask<Void, Void, Location> {

        @Override
        protected Location doInBackground(Void... voids) {
            checkPermission();
            currentLocation = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            return currentLocation;
        }

        @Override
        protected void onPostExecute(Location location) {
            super.onPostExecute(location);
            setCamera(location);
        }
    }
}

