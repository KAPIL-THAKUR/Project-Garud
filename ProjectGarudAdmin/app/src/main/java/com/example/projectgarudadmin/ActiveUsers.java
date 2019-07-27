package com.example.projectgarudadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.ArrayList;
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
    Marker marker1,marker;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_users);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        final NavigationView nvdrawer = (NavigationView) findViewById(R.id.nvdrawer);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrwaerContext(nvdrawer);
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

        nvdrawer.addHeaderView(view);

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
                                        if(SafeZone.equals("No")) {
                                            progressDialog.dismiss();
                                            marker = mMap.addMarker(new MarkerOptions().position(latLng[0]).title(Name[0]).snippet(locationData2[0].getLatitude() + ", " + locationData2[0].getLongitude()).icon(BitmapDescriptorFactory.fromResource(R.drawable.danger_marker)));
                                            mMap.setMaxZoomPreference(20);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 10.0f));
                                        }if(SafeZone.equals("Yes")){
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

            case R.id.Home:
                startActivity(new Intent(ActiveUsers.this, ActiveUsers.class));
                break;

            case R.id.Earthquake:
                startActivity(new Intent(ActiveUsers.this, EarthquakePushNotify.class));
                break;

            case R.id.AddSafePlace:
                startActivity(new Intent(ActiveUsers.this, AddSafePlace.class));
                break;

            case R.id.BuildingRequest:
                startActivity(new Intent(ActiveUsers.this, BuildingSafetyCheckRequest.class));
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
    public boolean onOptionsItemSelected (MenuItem item){
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
}
