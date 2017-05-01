package com.fjamtechnology.friendlyreminder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReminderMap extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //// Getting UserID From Main /////////
    String UserID;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    private GoogleMap mMap;
    private LatLng mClickPos;
    String test = "test";
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        UserID = getIntent().getExtras().getString("126516516513246");

        PopulateMenu();

        ///// Map //////////////////////////////////////////////////////////////////////////////////
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        ///
        //// FAB ///////////////////////////////////////////////////////////////////////////////////
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.marker);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#33691E")));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            PopulateMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminder_map, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String id = String.valueOf(item.getItemId());
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navView.getMenu();

        //// DBHELPER Instance ///
        DBHelper helper = new DBHelper(this);

        if(item.getGroupId() == 2){
            if(item.getItemId() == 1){
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(getApplicationContext(), "Please Do A Long Click On Map To Add New Marker", Toast.LENGTH_LONG).show();
            }else if(item.getItemId() == 2){
                m.removeGroup(2);
                m.add(3,3,1,"Done");
                m.setGroupCheckable(1, true, true);
            }
        }else if(item.isChecked()){
            String ID = Integer.toString(item.getItemId());
            m.removeItem(item.getItemId());
            helper.RemoveMarker(ID);
            mMap.clear();
            onMapReady(mMap);
        }else if(item.getGroupId() == 3){
            m.removeGroup(3);
            m.removeGroup(2);
            PopulateMenu();
        }else{
            String Pos = String.valueOf(item.getItemId());

            String Position = helper.GetMarkerPos(Pos);
            String[] LatLongFromMenID = Position.split(",");
            double Lat = Double.valueOf(LatLongFromMenID[0]);
            double Lon = Double.valueOf(LatLongFromMenID[1]);
            LatLng Cordinates = new LatLng(Lat, Lon);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Cordinates, 15));
        }

        return true;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onMapReady(GoogleMap map) {
        //// Creating Nav View To Populate Menu ///
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navView.getMenu();


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                //mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }



        //// Declaring Cursors and DB /////////
        Cursor c ;
        DBHelper dbHelper = new DBHelper(this);
        c = dbHelper.getAllLocations(UserID);

        //// Variables for Markers ///////
        String Name;
        String[] LatLong;
        double lat, lon;
        String MarkerName;
        int NumbMarkers = c.getCount();

        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //// Checking For Markers //////////////////////////////////////////////////////////////////
        if(NumbMarkers > 0){
            ////Zoom On Markers ///////////

            c.moveToFirst();
            for(int i = 0; i < NumbMarkers;){
                //// Declaring Temp Variables ////
                Name = c.getString(1);
                MarkerName = Name;

                //// Getting Variables From DB ////
                LatLong = c.getString(2).split(",");
                lat = Double.parseDouble(LatLong[0]);
                lon = Double.parseDouble(LatLong[1]);
                LatLng LatLngMarker = new LatLng(lat, lon);

                //// Adding Bounds To Builder ////
                builder.include(LatLngMarker);

                mMap.addMarker(new MarkerOptions().position(LatLngMarker).title(Name)).showInfoWindow();

                i++;
                c.moveToNext();

            }

            //// Moving Camera To Maker Bounds ////
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));

            c.close();
        }else{
            c.close();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        //// Click Listener ////
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                String LatLongSTR = arg0.getPosition().toString();
                String LatLongSTRSub = LatLongSTR.substring(10, LatLongSTR.length() - 1);
                String MarkerID = Integer.toString(dbHelper.ReturnMarkerID(LatLongSTRSub));
                Toast.makeText(getApplicationContext(), MarkerID, Toast.LENGTH_SHORT).show();
                return true;
            }

        });

        //// Long Click Add Marker /////////////////////////////////////////////////////////////////
        ////
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ReminderMap.this);
                alert.setTitle("New Marker Creator"); //Set Alert dialog title here
                alert.setMessage("Please Enter A Name For Your New Marker"); //Message here

                // Set an EditText view to get user input
                final EditText input = new EditText(ReminderMap.this);
                alert.setView(input);

                String LatLongSTR = latLng.toString();
                String LatLongSTRSub = LatLongSTR.substring(10, LatLongSTR.length() - 1);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //You will get as string input data in this variable.
                        String MarkerName = input.getEditableText().toString();
                        if(MarkerName.isEmpty()){
                            AlertDialog.Builder alert2 = new AlertDialog.Builder(ReminderMap.this);
                            alert2.setTitle("Did Not Enter Name For Marker"); //Set Alert dialog title here
                            alert2.setMessage("Please Make Sure You Have Entered A Name For Your New Marker");

                            alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onMapLongClick(latLng);
                                }
                            });

                            alert2.show();

                        }else {
                            //// Creating Marker ID And Getting The ID From Database ///
                            int MarkerID;
                            MarkerID = dbHelper.InsertNewMarker(MarkerName, LatLongSTRSub, UserID);
                            builder.include(latLng);
                            mMap.addMarker(new MarkerOptions().position(latLng).title(MarkerName));
                            //// Adding Item To Drawer From New Marker /////
                            int MarkerOrder = MarkerID + 3;
                            m.add(1, MarkerID, MarkerOrder, MarkerName).setIcon(R.drawable.green_marker);
                            LatLngBounds bounds = builder.build();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 450));
                        }
                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                    }
                });

                alert.show();

            }
        });
        ////

    }

    public void PopulateMenu(){
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navView.getMenu();
        m.clear();

        //// Declaring Cursors and DB /////////
        Cursor c ;
        DBHelper dbHelper = new DBHelper(this);
        c = dbHelper.getAllLocations(UserID);

        //// Variables for Markers ///////
        String MarkerName;
        int MarkerID, MarkerOrder;
        int NumbMarkers = c.getCount();

        //// Checking For Markers //////////////////////////////////////////////////////////////////
        if(NumbMarkers > 0){
            ////Zoom On Markers ///////////

            c.moveToFirst();
            for(int i = 0; i < NumbMarkers;){
                //// Declaring Temp Variables ////
                MarkerName = c.getString(1);
                MarkerID = c.getInt(0);
                MarkerOrder = MarkerID + 2;

                m.add(1, MarkerID, MarkerOrder, MarkerName).setIcon(R.drawable.green_marker);

                i++;
                c.moveToNext();

            }
            c.close();
            m.add(2,1,1,"Add Marker");
            m.add(2,2,2,"Delete Marker");
        }else{
            c.close();
            m.clear();
            m.add(2,1,1,"Add Marker");
            m.add(2,2,2,"Delete Marker");
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        //mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


}
