package com.fjamtechnology.friendlyreminder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    /**
     * creation method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //// Getting User ID From Login And Populating Menu Drawer Based On User
        UserID = getIntent().getExtras().getString("126516516513246");

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

                FabAnimateCamer();

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
     * controls what happens when theback button is pressed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            PopulateMenu();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Would You Like To Logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(ReminderMap.this, MainActivity.class));
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    /**
     * cretaion of options menu
     * @param menu - the menu
     * @return returns true completed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminder_map, menu);
        return true;
    }

    /**
     * an options item is selected
     * @param item the item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            String UpdatedUserID = UserID;
            Intent intent = new Intent(ReminderMap.this, EditUserInfo.class);
            intent.putExtra("126516516513246", UpdatedUserID);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Navagation item is selected
     * @param item the item selected
     * @return
     */
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
                m.removeGroup(4);
                m.add(3,3,1,"Done");
                m.setGroupCheckable(1, true, true);
                Toast.makeText(getApplicationContext(), "Double Click Marker To Delete", Toast.LENGTH_LONG).show();
            }
        }else if(item.isChecked()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are You Sure You Want To Delete Marker? It Will Be Permanently Deleted");
            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String ID = Integer.toString(item.getItemId());
                            m.removeItem(item.getItemId());
                            helper.RemoverMarkerAndReminders(ID);
                            mMap.clear();
                            onMapReady(mMap);
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog AlertDeleteMarker = builder.create();
            AlertDeleteMarker.show();


        }else if(item.getGroupId() == 3){
            m.removeGroup(3);
            m.removeGroup(2);
            PopulateMenu();
        }else if(m.getItem(0).getGroupId() == 2 && item.getGroupId() == 1){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Go To:");
            builder.setItems(new CharSequence[]
                            {"Marker", "Reminders"},
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which) {
                                case 0:
                                    String Pos = String.valueOf(item.getItemId());

                                    String Position = helper.GetMarkerPos(Pos);
                                    String[] LatLongFromMenID = Position.split(",");
                                    double Lat = Double.valueOf(LatLongFromMenID[0]);
                                    double Lon = Double.valueOf(LatLongFromMenID[1]);
                                    LatLng Cordinates = new LatLng(Lat, Lon);

                                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                                    drawer.closeDrawer(GravityCompat.START);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Cordinates, 19));
                                    break;
                                case 1:
                                    Intent intent = new Intent(ReminderMap.this, Reminders.class);
                                    intent.putExtra("126516516513246", UserID);
                                    intent.putExtra("165165165165166", String.valueOf(item.getItemId()));
                                    startActivity(intent);
                                    finish();
                                    break;
                            }
                        }
                    });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();

        }else if(item.getGroupId() == 4){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Would You Like To Logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(ReminderMap.this, MainActivity.class));
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

        return true;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     *  once the google map is reday to use
      */

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        LatLngBounds.Builder builderMapReset = new LatLngBounds.Builder();

        //// Creating Nav View To Populate Menu ///
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navView.getMenu();
        m.clear();


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();

                mMap.setMyLocationEnabled(true);
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
        int NumbMarkers = c.getCount();


        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //// Checking For Markers //////////////////////////////////////////////////////////////////
        if(NumbMarkers > 0){
            ////Zoom On Markers ///////////
            c.moveToFirst();
            if(NumbMarkers == 1){
                for(int i = 0; i < NumbMarkers;){
                    Name = c.getString(1);
                    //// Getting Variables From DB ////
                    LatLong = c.getString(2).split(",");
                    lat = Double.parseDouble(LatLong[0]);
                    lon = Double.parseDouble(LatLong[1]);
                    LatLng LatLngMarker = new LatLng(lat, lon);

                    mMap.addMarker(new MarkerOptions().position(LatLngMarker).title(Name));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLngMarker, 18));

                    //// Adding Bounds To Builder ////
                    i++;
                    c.moveToNext();

                }
                c.close();
            }else{
                for(int i = 0; i < NumbMarkers;){
                    //// Declaring Temp Variables ////
                    Name = c.getString(1);

                    //// Getting Variables From DB ////
                    LatLong = c.getString(2).split(",");
                    lat = Double.parseDouble(LatLong[0]);
                    lon = Double.parseDouble(LatLong[1]);
                    LatLng LatLngMarker = new LatLng(lat, lon);

                    //// Adding Bounds To Builder ////
                    builderMapReset.include(LatLngMarker);

                    mMap.addMarker(new MarkerOptions().position(LatLngMarker).title(Name));

                    i++;
                    c.moveToNext();

                }

                //// Moving Camera To Maker Bounds ////
                LatLngBounds bounds = builderMapReset.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 350));

                c.close();
            }

        }else{
            c.close();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        PopulateMenu();

        //// Marker Click Listener ////
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTitle().equals("Current Position"))
                {
                    return false;
                }else{
                    DBHelper helper = new DBHelper(getApplicationContext());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));

                    Toast.makeText(getApplicationContext(),"Click On Info Window For Marker Options", Toast.LENGTH_LONG).show();

                    marker.showInfoWindow();
                    return true;
                }

            }

        });

        //// Info Window Click Listener ////
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if(marker.getTitle().equals("Current Position"))
                {

                }else{
                    DBHelper DbHelper = new DBHelper(getApplicationContext());

                    //// Get Reminders From Marker ////
                    String Pos = String.valueOf(marker.getPosition());
                    Pos = Pos.substring(10, Pos.length()-1);
                    int ID = DbHelper.ReturnMarkerID(Pos);
                    String IDSTR = String.valueOf(ID);

                    Cursor cursor = DbHelper.getAllReminders(IDSTR);
                    int NumbReminders = cursor.getCount();
                    String Reminders = "";

                    if(NumbReminders > 0){
                        cursor.moveToFirst();
                        for(int i = 0; i < NumbReminders;){
                            if(cursor.getInt(2) == 0){
                                //// Declaring Temp Variables ////
                                Reminders += "\n\t\t\t\t\t\t" + cursor.getString(1);
                                i++;
                                cursor.moveToNext();
                            }else{
                                //// Declaring Temp Variables ////
                                Reminders += "\n\t\t\t\t\t\t" + cursor.getString(1) + " (Completed)";
                                i++;
                                cursor.moveToNext();
                            }
                        }

                    }else{
                        Reminders = "\n\t\t\t\t\t\t" + "No Reminders Yet";
                    }

                    //// Showing Reminders ////
                    DialogReminders(Reminders, IDSTR);

                }

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                LatLngBounds.Builder builderNewMarker = new LatLngBounds.Builder();

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
                        String NotEmpty = MarkerName.replace(" ", "");
                        if(MarkerName.isEmpty() || NotEmpty == ""){
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
                            //// Declaring Cursors and DB /////////
                            Cursor c ;
                            DBHelper db = new DBHelper(getApplicationContext());
                            c = db.getAllLocations(UserID);

                            //// Variables for Markers ///////
                            double lat, lon;
                            int NumbMarkers = c.getCount();
                            String[] LatLong = new String[NumbMarkers];
                            c.moveToFirst();

                            if(NumbMarkers == 0){
                                int MarkerID;
                                MarkerID = dbHelper.InsertNewMarker(MarkerName, LatLongSTRSub, UserID);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(MarkerName));
                                //// Adding Item To Drawer From New Marker /////
                                int MarkerOrder = MarkerID + 3;
                                builderNewMarker.include(latLng);
                                //m.add(1, MarkerID, MarkerOrder, MarkerName).setIcon(R.drawable.green_marker);
                                PopulateMenu();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                            }else{
                                int MarkerID;
                                MarkerID = dbHelper.InsertNewMarker(MarkerName, LatLongSTRSub, UserID);
                                builderNewMarker.include(latLng);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(MarkerName));
                                //// Adding Item To Drawer From New Marker /////
                                int MarkerOrder = MarkerID + 3;
                                PopulateMenu();

                                for(int i = 0; i < NumbMarkers;){
                                    //// Getting Variables From DB ////
                                    LatLong = c.getString(2).split(",");
                                    lat = Double.parseDouble(LatLong[0]);
                                    lon = Double.parseDouble(LatLong[1]);
                                    LatLng LatLngMarker = new LatLng(lat, lon);

                                    //// Adding Bounds To Builder ////
                                    builderNewMarker.include(LatLngMarker);
                                    i++;
                                    c.moveToNext();

                                }
                                LatLngBounds bounds = builderNewMarker.build();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
                            }

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

    /**
     * method that populates the navigation menu
     */
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
        int MarkerID = 0, MarkerOrder = 0;
        int NumbMarkers = c.getCount();

        //// Checking For Markers //////////////////////////////////////////////////////////////////
        if(NumbMarkers > 0){
            ////Zoom On Markers ///////////

            c.moveToFirst();
            for(int i = 0; i < NumbMarkers;){
                //// Declaring Temp Variables ////
                MarkerName = c.getString(1);
                MarkerID = c.getInt(0);
                MarkerOrder = i + 3;

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

        m.add(4,4,NumbMarkers + 3,"Logout").setIcon(R.drawable.logout);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * method that builds the google api for the map
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * users location has changed
     * @param location - the location
     */
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

    /**
     * checks if the app has location permissions enabled
     */
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

    /**
     * request permissions results
     * @param requestCode the code for permission
     * @param permissions the permissions
     * @param grantResults the results
     */
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
                        mMap.setMyLocationEnabled(true);
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

    /**
     * connecting to google api
     * @param bundle
     */
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

    /**
     * connection to api failed
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * connection to api suspended
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * dialog box for reminders
     * @param Reminders - the reminders
     * @param MarkerID - marker id for reminders
     */
    public void DialogReminders(String Reminders, String MarkerID){

        //// Creating Dialog And View To Show All Reminders If Any In Window ////
        LayoutInflater inflater= LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.scrollable_reminders_infowindow, null);

        TextView textview=(TextView)view.findViewById(R.id.Reminders);
        textview.setText(Reminders);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Reminders");
        alertDialog.setView(view);
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
        //////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////
        /**/DBHelper Helper = new DBHelper(getApplicationContext());/**/
        ////////////////////////////////////////////////////////////////

        //// Going To Reminder ListView Activity ////
        Button GoToReminders = (Button) alert.findViewById(R.id.Edit);
        GoToReminders.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderMap.this, Reminders.class);
                intent.putExtra("126516516513246", UserID);
                intent.putExtra("165165165165166", MarkerID);
                startActivity(intent);
                finish();
                alert.cancel();
            }
        });
        ////////////////////////////////////////////////

        //// Adding New Reminder From Dialog Box ////
        Button AddNewReminder = (Button) alert.findViewById(R.id.AddNewReminder);
        AddNewReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertEnterReminder = new AlertDialog.Builder(ReminderMap.this);
                alertEnterReminder.setTitle("New Reminder"); //Set Alert dialog title here
                alertEnterReminder.setMessage("Please Enter A New Reminder"); //Message here

                // Set an EditText view to get user input
                final EditText input = new EditText(ReminderMap.this);
                alertEnterReminder.setView(input);
                alertEnterReminder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertEnterReminder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String ReminderText = input.getEditableText().toString();

                        if(ReminderText.isEmpty()){
                            AlertDialog.Builder EmptyInput = new AlertDialog.Builder(getApplicationContext());
                            EmptyInput.setTitle("Input Empty"); //Set Alert dialog title here
                            EmptyInput.setMessage("Please Make Sure You Have Entered A Reminder For Your Marker"
                                    + " Or Press Cancel If You Did Not Want To Add A New Reminder");

                            EmptyInput.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            EmptyInput.show();
                        }else{
                            Helper.AddNewReminder(MarkerID,ReminderText);
                            dialog.cancel();
                            alert.cancel();
                            if(Reminders.toString() == "\n\t\t\t\t\t\t" + "No Reminders Yet" ){
                                String RemindersUpdated = "\n\t\t\t\t\t\t" + ReminderText;
                                DialogReminders(RemindersUpdated, MarkerID);
                            }else {
                                String RemindersUpdated = Reminders + "\n\t\t\t\t\t\t" + ReminderText;
                                DialogReminders(RemindersUpdated, MarkerID);
                            }
                        }
                    }
                });

                alertEnterReminder.show();

            }
        });
        ////////////////////////////////////////////////////////////////

        //// Going To Reminder ListView Activity ////
        Button DeleteMarker = (Button) alert.findViewById(R.id.DeleteMarker);
        DeleteMarker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDeleteMarker = new AlertDialog.Builder(ReminderMap.this);
                alertDeleteMarker.setTitle("!!Warning!!"); //Set Alert dialog title here
                alertDeleteMarker.setMessage("Are You Sure You Want To Delete Marker And Its Reminders? This Is Permanent."); //Message here
                alertDeleteMarker.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDeleteMarker.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.RemoverMarkerAndReminders(MarkerID);
                        dialog.cancel();
                        alert.cancel();
                        mMap.clear();
                        onMapReady(mMap);
                    }
                });
                alertDeleteMarker.show();
            }
        });
        ////////////////////////////////////////////////

    }

    /**
     * controls the map animation of displaying all markers on screen when pressing the floating action button
     */
    public void FabAnimateCamer(){

        LatLngBounds.Builder FabBuilder = new LatLngBounds.Builder();

        //// Declaring Cursors and DB /////////
        Cursor c ;
        DBHelper db = new DBHelper(this);
        c = db.getAllLocations(UserID);

        //// Variables for Markers ///////
        String[] LatLong;
        double lat, lon;
        int NumbMarkers = c.getCount();

        //// Checking For Markers //////////////////////////////////////////////////////////////////
        if(NumbMarkers > 0){
            ////Zoom On Markers ///////////
            c.moveToFirst();
            if(NumbMarkers == 1){
                for(int i = 0; i < NumbMarkers;){
                    //// Getting Variables From DB ////
                    LatLong = c.getString(2).split(",");
                    lat = Double.parseDouble(LatLong[0]);
                    lon = Double.parseDouble(LatLong[1]);
                    LatLng LatLngMarker = new LatLng(lat, lon);

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLngMarker, 18));

                    //// Adding Bounds To Builder ////
                    i++;
                    c.moveToNext();

                }
                c.close();
            }else{
                for(int i = 0; i < NumbMarkers;){
                    //// Getting Variables From DB ////
                    LatLong = c.getString(2).split(",");
                    lat = Double.parseDouble(LatLong[0]);
                    lon = Double.parseDouble(LatLong[1]);
                    LatLng LatLngMarker = new LatLng(lat, lon);

                    //// Adding Bounds To Builder ////
                    FabBuilder.include(LatLngMarker);
                    i++;
                    c.moveToNext();

                }

                //// Moving Camera To Maker Bounds ////
                LatLngBounds bounds = FabBuilder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));

                c.close();
            }

        }else{
            Toast.makeText(getApplicationContext(),"No Markers To Focus On", Toast.LENGTH_LONG).show();
        }
    }


}