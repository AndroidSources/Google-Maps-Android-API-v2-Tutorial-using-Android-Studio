package com.androidsources.googlemaps;

import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final int ERROR_DIALOG_REQUEST = 1 ;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void gotoLocation(double lat,double lng,float zoom) {
        Log.d("value","gotoLocation called");
        LatLng latLng=new LatLng(lat,lng);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        mMap.moveCamera(update);
    }

    private boolean checkServices() {
        int isAvailable= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (isAvailable== ConnectionResult.SUCCESS){
            return true;
        }else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog=GooglePlayServicesUtil.getErrorDialog(isAvailable,this,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(MainActivity.this, "Cannot connnect to mapping Service", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap=mapFragment.getMap();
        }
        return (mMap!=null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //getting the latitude value
            double latitudeValue=mLastLocation.getLatitude();
            Log.d("value",""+latitudeValue);
            //getting the longitude value
            double longitudeValue=mLastLocation.getLongitude();
            if(checkServices()){
                if(initMap()){

                    gotoLocation(latitudeValue, longitudeValue, 15);

                    // Other supported types include: MAP_TYPE_NORMAL,
                    // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    //mMap.setMyLocationEnabled(true);

                    //Setting up the marker
                    MarkerOptions marker= new MarkerOptions()
                            .title("Dharmapuri")
                            .position(new LatLng(latitudeValue,longitudeValue))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(marker);
                }
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GettingLocation", "onConnectionFailed");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GettingLocation", "onConnectionFailed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}


