package com.fjamtechnology.friendlyreminder;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReminderMap extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{

    private GoogleMap mMap;
    private LatLng mClickPos;
    String test = "test";

    /**
     * method run when activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ///// Map //////////////////////////////////////////////////////////////////////////////////
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ////////////////////////////////////////////////////////////////////////////////////////////

        //// FAB ///////////////////////////////////////////////////////////////////////////////////
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.marker);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#33691E")));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    /**
     * method that determines if the back button was pressed and if the drawer is open to close it
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * // Inflate the menu; this adds items to the action bar if it is present.
     * @param menu the menu
     * @return if created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminder_map, menu);
        return true;
    }

    /**
     * If an item in the menu has been selected
     * @param item  the item
     * @return returns the item selected
     */
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

    /**
     * it a navigation item is selected
     * @param item the item
     * @return the item selected
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * code that will not run until the map has been created and is fully ready
     * @param map the google map
     */
    @Override
    public void onMapReady(GoogleMap map) {
        //// Declaring Cursors and DB /////////
        Cursor c ;
        DBHelper dbHelper = new DBHelper(this);
        c = dbHelper.getAllLocations();

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
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            c.moveToFirst();
            for(int i = 0; i < NumbMarkers;){
                //// Declaring Temp Variables ////
                Name = c.getString(0);
                MarkerName = Name;

                //// Getting Variables From DB ////
                LatLong = c.getString(1).split(",");
                lat = Double.parseDouble(LatLong[0]);
                lon = Double.parseDouble(LatLong[1]);
                LatLng LatLngMarker = new LatLng(lat, lon);

                //// Adding Bounds To Builder ////
                builder.include(LatLngMarker);

                mMap.addMarker(new MarkerOptions().position(LatLngMarker).title(Name)).showInfoWindow();

                i++;
                c.move(i);

            }

            //// Moving Camera To Maker Bounds ////
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 500));

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
                String title;
                title = arg0.getTitle();
                Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show();
                return true;
            }

        });

        //// Long Click Add Marker /////////////////////////////////////////////////////////////////
        ////
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mClickPos = latLng;
                new AlertDialog.Builder(ReminderMap.this)
                        .setPositiveButton("Create", ReminderMap.this)
                        .setNegativeButton("Cancel", null)
                        .setMessage(latLng.toString())
                        .show();
            }
        });
        ////
    }


    public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(getApplicationContext(), test, Toast.LENGTH_LONG).show();
    }

}
