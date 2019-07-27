package com.example.projectgarud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ActiveUsers extends AppCompatActivity implements OnMapReadyCallback{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String keylist;
    GoogleMap mMap;
    Spinner spinner2;
    String city ="";
    Marker marker,marker1;
    ProgressDialog progressDialog;
    LocationManager locationManager;
    LocationListener locationListener;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_users);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync( this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer6);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        final NavigationView nvdrawer6 = (NavigationView) findViewById(R.id.nvdrawer6);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrwaerContext(nvdrawer6);
        firebaseAuth = FirebaseAuth.getInstance();
        spinner2 = findViewById(R.id.spinner2);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Detecting Users...");
        progressDialog.show();

        View view=getLayoutInflater().inflate(R.layout.header,null);
        final TextView textView2=view.findViewById(R.id.textView2);
        final TextView textView3=view.findViewById(R.id.textView3);

        textView2.setText("Project Garud");
        textView3.setText("**Team Rebel**");

        nvdrawer6.addHeaderView(view);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                final double latitude = location.getLatitude();
                final double longitude = location.getLongitude();

                DatabaseReference reference = firebaseDatabase.getReference("Earthqauke");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LocationData locationData = dataSnapshot.getValue(LocationData.class);
                        String GeoLocation = locationData.getGeoLocation();
                        double Magnitude = locationData.getMagnitude();

                        Geocoder geocoder = new Geocoder(ActiveUsers.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            String address = addresses.get(0).getAddressLine(0);
                            String City = addresses.get(0).getLocality();
                            Log.d("mylog", "Complete Address: " + addresses.toString());
                            Log.d("mylog", "Address: " + address);
                            if (GeoLocation.equals(City) && Magnitude > 2) {
                                Notification();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ActiveUsers.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.UserInCity));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(myAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Project Garud Users");

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressDialog.show();
                if(position==0){
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                keylist = ds.getKey();
                                final String[] Name = {""};
                                databaseReference.child(keylist).child("Details").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final  LocationData locationData = dataSnapshot.getValue(LocationData.class);
                                        Name[0] = locationData.getName();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                final LocationData[] locationData2 = new LocationData[1];
                                final LatLng[] latLng = new LatLng[1];
                                databaseReference.child(keylist).child("Coordinates").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        locationData2[0] = dataSnapshot.getValue(LocationData.class);
                                        latLng[0] = new LatLng(locationData2[0].getLatitude(), locationData2[0].getLatitude());

                                        //progressDialog.dismiss();
                                       // marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                       // mMap.setMaxZoomPreference(20);
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                databaseReference.child(keylist).child("Safe Person").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SafePerson safePerson = dataSnapshot.getValue(SafePerson.class);
                                        String SafeZone = safePerson.getSafezone();
                                        System.out.println(SafeZone);
                                        if (SafeZone.equals("No")) {
                                            progressDialog.dismiss();
                                            marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.danger_marker)));
                                            mMap.setMaxZoomPreference(20);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 10.0f));
                                        } else if (SafeZone.equals("Yes")) {
                                            progressDialog.dismiss();
                                            marker1 = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                            mMap.setMaxZoomPreference(20);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 10.0f));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if(position==1){
                    mMap.clear();
                    city = spinner2.getSelectedItem().toString();
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                keylist = ds.getKey();
                                final String[] Name = {""};
                                databaseReference.child(keylist).child("Details").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final  LocationData locationData = dataSnapshot.getValue(LocationData.class);
                                        Name[0] = locationData.getName();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                final LocationData[] locationData2 = new LocationData[1];
                                final LatLng[] latLng = new LatLng[1];
                                databaseReference.child(keylist).child("Coordinates").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        locationData2[0] = dataSnapshot.getValue(LocationData.class);
                                        latLng[0] = new LatLng(locationData2[0].getLatitude(), locationData2[0].getLatitude());

                                        //progressDialog.dismiss();
                                        // marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                        // mMap.setMaxZoomPreference(20);
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                databaseReference.child(keylist).child("Safe Person").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SafePerson safePerson = dataSnapshot.getValue(SafePerson.class);
                                        String SafeZone = safePerson.getSafezone();
                                        Geocoder geocoder = new Geocoder(ActiveUsers.this, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(locationData2[0].getLatitude(), locationData2[0].getLongitude(), 1);
                                            String address = addresses.get(0).getAddressLine(0);
                                            String City = addresses.get(0).getLocality();
                                            Log.d("mylog", "Complete Address: " + addresses.toString());
                                            Log.d("mylog", "Address: " + address);
                                            if (city.equals(City)) {
                                                if(SafeZone.equals("No")) {
                                                    progressDialog.dismiss();
                                                    marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.danger_marker)));
                                                    mMap.setMaxZoomPreference(20);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                                }if(SafeZone.equals("Yes")){
                                                    progressDialog.dismiss();
                                                    marker1 = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                                    mMap.setMaxZoomPreference(20);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                                }
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if(position==2){
                    mMap.clear();
                    city = spinner2.getSelectedItem().toString();
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                keylist = ds.getKey();
                                final String[] Name = {""};
                                databaseReference.child(keylist).child("Details").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final  LocationData locationData = dataSnapshot.getValue(LocationData.class);
                                        Name[0] = locationData.getName();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                final LocationData[] locationData2 = new LocationData[1];
                                final LatLng[] latLng = new LatLng[1];
                                databaseReference.child(keylist).child("Coordinates").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        locationData2[0] = dataSnapshot.getValue(LocationData.class);
                                        latLng[0] = new LatLng(locationData2[0].getLatitude(), locationData2[0].getLatitude());

                                        //progressDialog.dismiss();
                                        // marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                        // mMap.setMaxZoomPreference(20);
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                databaseReference.child(keylist).child("Safe Person").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SafePerson safePerson = dataSnapshot.getValue(SafePerson.class);
                                        String SafeZone = safePerson.getSafezone();
                                        Geocoder geocoder = new Geocoder(ActiveUsers.this, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(locationData2[0].getLatitude(), locationData2[0].getLongitude(), 1);
                                            String address = addresses.get(0).getAddressLine(0);
                                            String City = addresses.get(0).getLocality();
                                            Log.d("mylog", "Complete Address: " + addresses.toString());
                                            Log.d("mylog", "Address: " + address);
                                            if (city.equals(City)) {
                                                if(SafeZone.equals("No")) {
                                                    progressDialog.dismiss();
                                                    marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.danger_marker)));
                                                    mMap.setMaxZoomPreference(20);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                                }if(SafeZone.equals("Yes")){
                                                    progressDialog.dismiss();
                                                    marker1 = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                                    mMap.setMaxZoomPreference(20);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                                }
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if(position==3){
                    mMap.clear();
                    city = spinner2.getSelectedItem().toString();
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                keylist = ds.getKey();
                                final String[] Name = {""};
                                databaseReference.child(keylist).child("Details").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final  LocationData locationData = dataSnapshot.getValue(LocationData.class);
                                        Name[0] = locationData.getName();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                final LocationData[] locationData2 = new LocationData[1];
                                final LatLng[] latLng = new LatLng[1];
                                databaseReference.child(keylist).child("Coordinates").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        locationData2[0] = dataSnapshot.getValue(LocationData.class);
                                        latLng[0] = new LatLng(locationData2[0].getLatitude(), locationData2[0].getLatitude());

                                        //progressDialog.dismiss();
                                        // marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                        // mMap.setMaxZoomPreference(20);
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                databaseReference.child(keylist).child("Safe Person").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SafePerson safePerson = dataSnapshot.getValue(SafePerson.class);
                                        String SafeZone = safePerson.getSafezone();
                                        Geocoder geocoder = new Geocoder(ActiveUsers.this, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(locationData2[0].getLatitude(), locationData2[0].getLongitude(), 1);
                                            String address = addresses.get(0).getAddressLine(0);
                                            String City = addresses.get(0).getLocality();
                                            Log.d("mylog", "Complete Address: " + addresses.toString());
                                            Log.d("mylog", "Address: " + address);
                                            if (city.equals(City)) {
                                                if(SafeZone.equals("No")) {
                                                    progressDialog.dismiss();
                                                    marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.danger_marker)));
                                                    mMap.setMaxZoomPreference(20);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                                }if(SafeZone.equals("Yes")){
                                                    progressDialog.dismiss();
                                                    marker1 = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                                    mMap.setMaxZoomPreference(20);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                                }
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if(position==4){
                    mMap.clear();
                    city = spinner2.getSelectedItem().toString();
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                keylist = ds.getKey();
                                final String[] Name = {""};
                                databaseReference.child(keylist).child("Details").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final  LocationData locationData = dataSnapshot.getValue(LocationData.class);
                                        Name[0] = locationData.getName();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                final LocationData[] locationData2 = new LocationData[1];
                                final LatLng[] latLng = new LatLng[1];
                                databaseReference.child(keylist).child("Coordinates").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        locationData2[0] = dataSnapshot.getValue(LocationData.class);
                                        latLng[0] = new LatLng(locationData2[0].getLatitude(), locationData2[0].getLatitude());

                                        //progressDialog.dismiss();
                                        // marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                        // mMap.setMaxZoomPreference(20);
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                databaseReference.child(keylist).child("Safe Person").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SafePerson safePerson = dataSnapshot.getValue(SafePerson.class);
                                        String SafeZone = safePerson.getSafezone();
                                        Geocoder geocoder = new Geocoder(ActiveUsers.this, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(locationData2[0].getLatitude(), locationData2[0].getLongitude(), 1);
                                            String address = addresses.get(0).getAddressLine(0);
                                            String City = addresses.get(0).getLocality();
                                            Log.d("mylog", "Complete Address: " + addresses.toString());
                                            Log.d("mylog", "Address: " + address);
                                            if (city.equals(City)) {
                                                if(SafeZone.equals("No")) {
                                                    progressDialog.dismiss();
                                                    marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.danger_marker)));
                                                    mMap.setMaxZoomPreference(20);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                                }if(SafeZone.equals("Yes")){
                                                    progressDialog.dismiss();
                                                    marker1 = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.safe_marker)));
                                                    mMap.setMaxZoomPreference(20);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 15.0f));
                                                }
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean selectItemdrwaer (MenuItem item){

        switch (item.getItemId()) {

            case R.id.HomeAdmin:
                startActivity(new Intent(ActiveUsers.this, ActiveUsers.class));
                break;

            case R.id.AboutUsAdmin:
                startActivity(new Intent(ActiveUsers.this, AboutUsAdmin.class));
                break;

            case R.id.LogOut:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(ActiveUsers.this, MainActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpDrwaerContext (NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectItemdrwaer(item);
                return true;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case R.id.Help:
                startActivity(new Intent(ActiveUsers.this,Help.class));
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    // Creates and displays a notification
    private void Notification() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1.0", "Project Garud", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Natural Disaster Management (Earthqauke)");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1.0")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.garud)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.earthquake_icon))
                .setContentTitle("Danger Earthquake")
                .setContentText("Safe Yourself From Earthquake!")
                .setAutoCancel(true)
                // .setSound(Uri.parse("android.resource://com.example.projectgarud/" + R.raw.kyabata))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



        Intent notificationIntent = new Intent(getApplicationContext(), EarthquakeActivityAdmin.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = builder.setContentIntent(pendingIntent);
        notificationManager.notify(0, builder.build());

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ActiveUsers.this);
        LayoutInflater factory = LayoutInflater.from(ActiveUsers.this);
        final View view1 = factory.inflate(R.layout.alert_earthquake, null);
        builder1.setView(view1);
        builder1 .setTitle("Danger Earthquake")
                .setMessage("Safe Yourself From Shocks and Live Your Location?\n\nMove to Safe Place or Open Area if Already at Safe Place take a Long Breath. We are Sending Rescue Team.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(ActiveUsers.this,EarthquakeActivityAdmin.class));
                        Toast.makeText(ActiveUsers.this,"Checking Safe Place For You",Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog =builder1.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.getWindow().setLayout(700,1200);
    }



    private static long back_pressed;

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.help_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
