package com.rym.benjmaa.alzheimermate.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rym.benjmaa.alzheimermate.Models.CircleTransform;
import com.rym.benjmaa.alzheimermate.Models.DirectionObject;
import com.rym.benjmaa.alzheimermate.Models.GsonRequest;
import com.rym.benjmaa.alzheimermate.Models.Helper;
import com.rym.benjmaa.alzheimermate.Models.LegsObject;
import com.rym.benjmaa.alzheimermate.Models.PolylineObject;
import com.rym.benjmaa.alzheimermate.Models.RouteObject;
import com.rym.benjmaa.alzheimermate.Models.StepsObject;
import com.rym.benjmaa.alzheimermate.Models.VolleySingleton;
import com.rym.benjmaa.alzheimermate.R;
import com.rym.benjmaa.alzheimermate.Utils.AppSingleton;
import com.rym.benjmaa.alzheimermate.Utils.SharedData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {
        private GoogleMap mMap;
        private GoogleApiClient client;
        private LocationRequest locationRequest;
        private Location lastLocation;
        private Marker currentLocationMarker;
        private static final String TAG ="hh";
       // private GoogleApiClient mGoogleApiClient;
        private Location mLastLocation;
        private LocationRequest mLocationRequest;
        private double latitudeValue = 0.0;
        private double longitudeValue = 0.0;
        private static final int PERMISSION_LOCATION_REQUEST_CODE = 100;
        private List<LatLng> latLngList;
        private MarkerOptions yourLocationMarker;
        private String url;
        private Location  loc;
        public static String lat,lon;
        private int test=0;

        public static final String CONNECTIONS="prefs_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // UiSettings uis= new uis.getUiSettings();
        //UiSettings.setMapToolbarEnabled(true);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //  toolbar.setTitle("Where am I");
        //this.setStatusBarColor(R.color.primaryDarkColor);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryDarkColor)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                if (latLngList.size() > 0) {
                    refreshMap(mMap);
                    latLngList.clear();
                }
                //36.836532, 10.136032
                LatLng home = new LatLng(36.836532, 10.136032);
                latLngList.add(home);
                Log.d(TAG, "Marker number " + latLngList.size());
                mMap.addMarker(yourLocationMarker);
                mMap.addMarker(new MarkerOptions().position(home));
                LatLng defaultLocation = yourLocationMarker.getPosition();
                LatLng destinationLocation = home;
                //use Google Direction API to get the route between these Locations
                String directionApiPath = Helper.getUrl(String.valueOf(defaultLocation.latitude), String.valueOf(defaultLocation.longitude),
                        String.valueOf(destinationLocation.latitude), String.valueOf(destinationLocation.longitude));
                Log.d(TAG, "Path " + directionApiPath);
                getDirectionFromDirectionApiServer(directionApiPath);

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        latLngList = new ArrayList<>();
        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mLocationRequest = createLocationRequest();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_drawer, menu);
        ImageView imV=findViewById(R.id.imageView);
        TextView user=findViewById(R.id.Username);
        TextView mail=findViewById(R.id.user_mail);
        if(SharedData.username!=null && SharedData.alzMail!=null && SharedData.imUri!=null) {
            Picasso.with(getBaseContext()).load(SharedData.imUri).transform(new CircleTransform()).into(imV);
            // Picasso.with(getBaseContext()).load(SharedData.imUri).into(imV);
            user.setText(SharedData.username);
            mail.setText(SharedData.alzMail);
        }else {


            Picasso.with(getBaseContext()).load(R.drawable.profile).into(imV);
        }
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
           /* final SharedPreferences preferences=getSharedPreferences(CONNECTIONS,MODE_PRIVATE);
            String userConnections=preferences.getString("Username",null);

            if(userConnections!=null){
                startActivity(new Intent(this,ResponsableActivity.class));
            }else {*/
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
           // }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng esprit = new LatLng(36.862499, 10.195556);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            // int[] grantResults)
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener((GoogleMap.OnMyLocationButtonClickListener) this);
            mMap.setOnMyLocationClickListener((GoogleMap.OnMyLocationClickListener) this);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(esprit));
            mMap.animateCamera(CameraUpdateFactory.zoomBy(6));
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }

    }

    protected synchronized void buildGoogleApiClient(){
    client = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    client.connect();

    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
       /* if(yourLocationMarker.isVisible())
        {
          //  yourLocationMarker.remove();
        }*/
        mMap.clear();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Connection method has been called");
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
                            assignLocationValues(mLastLocation);
                            setDefaultMarkerOption(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                             url = SharedData.url+"position";
                             lat=Double.toString(mLastLocation.getLongitude());
                             lon=Double.toString(mLastLocation.getLatitude());

                        }else{
                            ActivityCompat.requestPermissions(NavigationDrawerActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
                makeJsonObjectRequest(url,lat,lon);

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // permission was denied, show alert to explain permission
                    showPermissionAlert();
                }else{
                    //permission is granted now start a background service
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
                        assignLocationValues(mLastLocation);
                        setDefaultMarkerOption(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    }
                }
            }
        }
    }
    private void assignLocationValues(Location currentLocation){
        if (currentLocation != null) {
            latitudeValue = currentLocation.getLatitude();
            longitudeValue = currentLocation.getLongitude();
            Log.d(TAG, "Latitude: " + latitudeValue + " Longitude: " + longitudeValue);
            markStartingLocationOnMap(mMap, new LatLng(latitudeValue, longitudeValue));
            addCameraToMap(new LatLng(latitudeValue, longitudeValue));
        }
    }
    private void addCameraToMap(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    private void showPermissionAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_request_title);
        builder.setMessage(R.string.app_permission_notice);
        builder.create();
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityCompat.checkSelfPermission(NavigationDrawerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(NavigationDrawerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NavigationDrawerActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NavigationDrawerActivity.this, R.string.permission_refused, Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
    private void markStartingLocationOnMap(GoogleMap mapObject, LatLng location){



        mapObject.addMarker(new MarkerOptions().position(location).title("Current location"));
        mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));

    }
    private void refreshMap(GoogleMap mapInstance){
        mapInstance.clear();
    }
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
    private void setDefaultMarkerOption(LatLng location){
        if(yourLocationMarker == null){
            yourLocationMarker = new MarkerOptions();
        }
        yourLocationMarker.position(location);
    }
    @Override
    protected void onStart() {
        client.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        client.disconnect();
        super.onStop();
    }
    private void getDirectionFromDirectionApiServer(String url){
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(serverRequest);
    }
    private Response.Listener<DirectionObject> createRequestSuccessListener() {
        return new Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
                try {
                    Log.d("JSON Response", response.toString());
                    if(response.getStatus().equals("OK")){
                        List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                        drawRouteOnMap(mMap, mDirections);
                    }else{
                        Toast.makeText(NavigationDrawerActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }
    private List<LatLng> getDirectionPolylines(List<RouteObject> routes){
        List<LatLng> directionList = new ArrayList<LatLng>();
        for(RouteObject route : routes){
            List<LegsObject> legs = route.getLegs();
            for(LegsObject leg : legs){
                List<StepsObject> steps = leg.getSteps();
                for(StepsObject step : steps){
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline){
                        directionList.add(direction);
                    }
                }
            }
        }
        return directionList;
    }
    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }
    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
        PolylineOptions options = new PolylineOptions().width(10).color(R.color.colorPrimary).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(1).latitude, positions.get(1).longitude))
                .zoom(17)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            startActivity(new Intent(this,NavigationDrawerActivity.class));
            // Handle the camera action
        } else if (id == R.id.nav_pills) {

            try{
                startActivity(new Intent(this,MedsActivity.class));
            }catch (Exception e){
                System.out.println(e);
            }

        } /*else if (id == R.id.nav_Pictures) {

            try{
                startActivity(new Intent(this,PersonnesConfiancesActivity.class));
            }catch (Exception e){
                System.out.println(e);
            }
        } */ else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
      //  mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("It's Me!"));
        lastLocation=location;
        if(currentLocationMarker !=null){
            currentLocationMarker.remove();
            yourLocationMarker.visible(false);
        }
        LatLng latLng= new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(340.0F));
        currentLocationMarker= mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(8));

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
 /*   public void makeJsonObjectRequest(String urlJsonObj) {
        String REQUEST_TAG = "com.androidAlzheimerMate.positionRequest";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, (JSONObject) null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("success").equals("true")) {
                        System.out.println("position ajoutée dans la base de donnée");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq, REQUEST_TAG);
    }*/
 public void makeJsonObjectRequest(String urlJsonObj,final String la,final String lo) {
    // System.out.println("Current location:\n" + la+" "+lo );
     String REQUEST_TAG = "com.androidAlzheimerMate.positionnnRequest";
     JSONObject jsonBody = new JSONObject();
     try {
         jsonBody.put("latitude", la);
         jsonBody.put("longitude", lo);
         jsonBody.put("email", SharedData.alzMail);
     } catch (JSONException e) {
         e.printStackTrace();
     }
     final String requestBody = jsonBody.toString();
     JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
             urlJsonObj, (JSONObject) null, new Response.Listener<JSONObject>() {

         @Override
         public void onResponse(JSONObject response) {
             try {
                 System.out.println(response);
                 if (response.getString("success").equals("true")){
                     System.out.println("position ajoutée dans la base de donnée");

                 }
             } catch (JSONException e) {
                 e.printStackTrace();
             }
         }
     }, new Response.ErrorListener() {

         @Override
         public void onErrorResponse(VolleyError error) {
         }
     }){


         @Override
         public byte[] getBody() {

             return requestBody.getBytes();
         }


     };

        if (test==0){
            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq, REQUEST_TAG);
            test=1;
        }


 }


}

