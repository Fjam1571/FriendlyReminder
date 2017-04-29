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
import android.widget.EditText;
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
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PopulateMenu();

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

        //// Menu Item Long Click Listener /////////////////////////////////////////////////////////


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        NavigationView navView3 = (NavigationView) findViewById(R.id.nav_view);
        Menu m3 = navView3.getMenu();

        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();

      if(item.getGroupId() == 1) {

          //// Declaring Cursors and DB /////////
          Cursor c1;
          DBHelper dbHelper1 = new DBHelper(this);
          c1 = dbHelper1.getAllReminders(id);

          //// Variables for Reminders///////
          int IDColumn;
          String ReminderText;
          int NumbReminders = c1.getCount();

          //// Checking For Reminders //////////////////////////////////////////////////////////////////
          if (NumbReminders > 0) {

              m3.removeGroup(1);

              c1.moveToFirst();
              for (int i = 0; i < NumbReminders; ) {
                  //// Declaring Temp Variables ////
                  IDColumn = c1.getInt(0);
                  ReminderText = c1.getString(1);

                  m3.add(2, IDColumn, IDColumn, ReminderText);

                  i++;
                  c1.moveToNext();

              }
              c1.close();
          } else {
              c1.close();
          }
      }
      /////////////////////////////////////////////////////

        return true;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onMapReady(GoogleMap map) {
        //// Creating Nav View To Populate Menu ///
        NavigationView navView1 = (NavigationView) findViewById(R.id.nav_view);
        Menu m1 = navView1.getMenu();

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
                            alert2.setMessage("Please Make Sure You Have Entered A Name For Your New Marker"); //Message here

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
                            MarkerID = dbHelper.InsertNewMarker(MarkerName, LatLongSTRSub);
                            builder.include(latLng);
                            mMap.addMarker(new MarkerOptions().position(latLng).title(MarkerName));
                            //// Adding Item To Drawer From New Marker /////
                            m1.add(1, MarkerID, 1, MarkerName);
                            LatLngBounds bounds = builder.build();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
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

        //// Declaring Cursors and DB /////////
        Cursor c ;
        DBHelper dbHelper = new DBHelper(this);
        c = dbHelper.getAllLocations();

        //// Variables for Markers ///////
        String MarkerName;
        int MarkerID;
        int NumbMarkers = c.getCount();

        //// Checking For Markers //////////////////////////////////////////////////////////////////
        if(NumbMarkers > 0){
            ////Zoom On Markers ///////////

            c.moveToFirst();
            for(int i = 0; i < NumbMarkers;){
                //// Declaring Temp Variables ////
                MarkerName = c.getString(1);
                MarkerID = c.getInt(0);

                m.add(1, MarkerID, MarkerID, MarkerName);

                i++;
                c.moveToNext();

            }

            c.close();
        }else{
            c.close();
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

}
