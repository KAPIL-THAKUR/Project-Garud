package com.example.projectgarud;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.projectgarud.App.CHANNEL_1_ID;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Marker marker2,marker1;
    LocationListener locationListener;
    ProgressDialog progressDialog;
    Spinner spinner;
    HashMap<String, String> markerMap = new HashMap<String, String>();
    NotificationManagerCompat notificationManager;
    private static int SPLASH_TIME_OUT=60000;
    int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Detecting Location...");
        //progressDialog.setCancelable(false);
        progressDialog.show();

        notificationManager = NotificationManagerCompat.from(this);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MapsActivity.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.City));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final DatabaseReference reference6 = firebaseDatabase.getReference("Safe Place");

                if(position == 1){
                    reference6.child("Delhi").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mMap.clear();
                            for(int i=1;i<dataSnapshot.getChildrenCount()+1;i++) {
                                final int finalI = i;
                                reference6.child("Delhi").child("Place No:" + i).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                        String PlaceName = safePlace.getSafeplace();
                                        String Address = safePlace.getAddress();
                                        double Latitude = safePlace.getLatitude();
                                        double Longitude = safePlace.getLongitude();
                                        LatLng latLng = new LatLng(Latitude, Longitude);
                                        marker1 = mMap.addMarker(new MarkerOptions().position(latLng).title(PlaceName).snippet(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.building)));
                                        String id = marker1.getId();
                                        markerMap.put(id,"action"+ finalI);
                                        mMap.setMaxZoomPreference(20);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

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
                else if(position == 2){
                    reference6.child("Ghaziabad").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mMap.clear();
                            for(int i=1;i<dataSnapshot.getChildrenCount()+1;i++) {
                                final int finalI = i;
                                reference6.child("Ghaziabad").child("Place No:" + i).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                        String PlaceName = safePlace.getSafeplace();
                                        String Address = safePlace.getAddress();
                                        double Latitude = safePlace.getLatitude();
                                        double Longitude = safePlace.getLongitude();
                                        LatLng latLng = new LatLng(Latitude, Longitude);
                                        marker1 = mMap.addMarker(new MarkerOptions().position(latLng).title(PlaceName).snippet(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.building)));
                                        String id = marker1.getId();
                                        markerMap.put(id,"action"+ finalI);
                                        mMap.setMaxZoomPreference(20);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

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
                else if(position == 3){
                    reference6.child("Greater Noida").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mMap.clear();
                            for(int i=1;i<dataSnapshot.getChildrenCount()+1;i++) {
                                final int finalI = i;
                                reference6.child("Greater Noida").child("Place No:" + i).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                        String PlaceName = safePlace.getSafeplace();
                                        String Address = safePlace.getAddress();
                                        double Latitude = safePlace.getLatitude();
                                        double Longitude = safePlace.getLongitude();
                                        LatLng latLng = new LatLng(Latitude, Longitude);
                                        marker1 = mMap.addMarker(new MarkerOptions().position(latLng).title(PlaceName).snippet(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.building)));
                                        String id = marker1.getId();
                                        markerMap.put(id,"action"+ finalI);
                                        mMap.setMaxZoomPreference(20);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

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
                else if(position == 4){
                    reference6.child("Noida").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mMap.clear();
                            for(int i=1;i<dataSnapshot.getChildrenCount()+1;i++) {
                                final int finalI = i;
                                reference6.child("Noida").child("Place No:" + i).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                        String PlaceName = safePlace.getSafeplace();
                                        String Address = safePlace.getAddress();
                                        double Latitude = safePlace.getLatitude();
                                        double Longitude = safePlace.getLongitude();
                                        LatLng latLng = new LatLng(Latitude, Longitude);
                                        marker1 = mMap.addMarker(new MarkerOptions().position(latLng).title(PlaceName).snippet(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.building)));
                                        String id = marker1.getId();
                                        markerMap.put(id,"action"+ finalI);
                                        mMap.setMaxZoomPreference(20);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

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

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        if (marker.equals(marker2)) {

                        } else {

                            String actionId = markerMap.get(marker.getId());

                            if (actionId.equals("action1")) {

                                reference6.child(spinner.getSelectedItem().toString()).child("Place No:1").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                        String PlaceName = safePlace.getSafeplace();
                                        String Address = safePlace.getAddress();
                                        double Latitude = safePlace.getLatitude();
                                        double Longitude = safePlace.getLongitude();

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
                                        LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                                        final View view1 = factory.inflate(R.layout.alert_earthquake, null);
                                        builder1.setView(view1);
                                        builder1.setTitle(PlaceName)
                                                .setMessage("Address : " + Address + "\n\n" + "Latitude : " + Latitude + "\n\n" + "Longitude : " + Longitude)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        Toast.makeText(MapsActivity.this, "Use this Data for Your Safety", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        AlertDialog alertDialog = builder1.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                        alertDialog.getWindow().setLayout(700, 1200);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            } else if (actionId.equals("action2")) {
                                reference6.child(spinner.getSelectedItem().toString()).child("Place No:2").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                        String PlaceName = safePlace.getSafeplace();
                                        String Address = safePlace.getAddress();
                                        double Latitude = safePlace.getLatitude();
                                        double Longitude = safePlace.getLongitude();

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
                                        LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                                        final View view1 = factory.inflate(R.layout.alert_earthquake, null);
                                        builder1.setView(view1);
                                        builder1.setTitle(PlaceName)
                                                .setMessage("Address : " + Address + "\n\n" + "Latitude : " + Latitude + "\n\n" + "Longitude : " + Longitude)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        Toast.makeText(MapsActivity.this, "Use this Data for Your Safety", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        AlertDialog alertDialog = builder1.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                        alertDialog.getWindow().setLayout(700, 1200);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else if (actionId.equals("action3")) {
                                reference6.child(spinner.getSelectedItem().toString()).child("Place No:3").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                        String PlaceName = safePlace.getSafeplace();
                                        String Address = safePlace.getAddress();
                                        double Latitude = safePlace.getLatitude();
                                        double Longitude = safePlace.getLongitude();

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
                                        LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                                        final View view1 = factory.inflate(R.layout.alert_earthquake, null);
                                        builder1.setView(view1);
                                        builder1.setTitle(PlaceName)
                                                .setMessage("Address : " + Address + "\n\n" + "Latitude : " + Latitude + "\n\n" + "Longitude : " + Longitude)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        Toast.makeText(MapsActivity.this, "Use this Data for Your Safety", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        AlertDialog alertDialog = builder1.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                        alertDialog.getWindow().setLayout(700, 1200);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else if (actionId.equals("action4")) {
                                reference6.child(spinner.getSelectedItem().toString()).child("Place No:4").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                        String PlaceName = safePlace.getSafeplace();
                                        String Address = safePlace.getAddress();
                                        double Latitude = safePlace.getLatitude();
                                        double Longitude = safePlace.getLongitude();

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
                                        LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                                        final View view1 = factory.inflate(R.layout.alert_earthquake, null);
                                        builder1.setView(view1);
                                        builder1.setTitle(PlaceName)
                                                .setMessage("Address : " + Address + "\n\n" + "Latitude : " + Latitude + "\n\n" + "Longitude : " + Longitude)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        Toast.makeText(MapsActivity.this, "Use this Data for Your Safety", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        AlertDialog alertDialog = builder1.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                        alertDialog.getWindow().setLayout(700, 1200);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else if (actionId.equals("action5")) {
                                reference6.child(spinner.getSelectedItem().toString()).child("Place No:5").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                        String PlaceName = safePlace.getSafeplace();
                                        String Address = safePlace.getAddress();
                                        double Latitude = safePlace.getLatitude();
                                        double Longitude = safePlace.getLongitude();

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
                                        LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                                        final View view1 = factory.inflate(R.layout.alert_earthquake, null);
                                        builder1.setView(view1);
                                        builder1.setTitle(PlaceName)
                                                .setMessage("Address : " + Address + "\n\n" + "Latitude : " + Latitude + "\n\n" + "Longitude : " + Longitude)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        Toast.makeText(MapsActivity.this, "Use this Data for Your Safety", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        AlertDialog alertDialog = builder1.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                        alertDialog.getWindow().setLayout(700, 1200);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView nvdrawer = (NavigationView) findViewById(R.id.nvdrawer);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrwaerContext(nvdrawer);

        View view=getLayoutInflater().inflate(R.layout.header,null);
        final TextView textView2=view.findViewById(R.id.textView2);
        final TextView textView3=view.findViewById(R.id.textView3);

        textView2.setText("Project Garud");
        textView3.setText("**Team Rebel**");

        nvdrawer.addHeaderView(view);



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

                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            String address = addresses.get(0).getAddressLine(0);
                           String City = addresses.get(0).getLocality();
                            Log.d("mylog", "Complete Address: " + addresses.toString());
                            Log.d("mylog", "Address: " + address);
                            if(GeoLocation.equals(City) && Magnitude > 2) {
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

                final DatabaseReference reference1 = firebaseDatabase.getReference("Project Garud Users").child(firebaseAuth.getUid()).child("Coordinates");
                final LocationData locationData=new LocationData(latitude,longitude);
                reference1.setValue(locationData);

                final DatabaseReference reference2 = firebaseDatabase.getReference("Project Garud Users").child(firebaseAuth.getUid()).child("Details");
                reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LocationData locationData1 = dataSnapshot.getValue(LocationData.class);
                        String name = locationData1.getName();
                        LatLng latLng = new LatLng(latitude, longitude);
                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            String address = addresses.get(0).getAddressLine(0);
                            String City = addresses.get(0).getLocality();
                            Log.d("mylog", "Complete Address: " + addresses.toString());
                            Log.d("mylog", "Address: " + address);
                            progressDialog.dismiss();
                            mMap.clear();
                            marker2 = mMap.addMarker(new MarkerOptions().position(latLng).title(name).snippet(City).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                            mMap.setMaxZoomPreference(20);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

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


    }

    public boolean selectItemdrwaer (MenuItem item){

        switch (item.getItemId()) {

            case R.id.Home:
                startActivity(new Intent(MapsActivity.this, MapsActivity.class));
                break;

            case R.id.Help:
                startActivity(new Intent(MapsActivity.this, Help.class));
                break;

            case R.id.AboutUs:
                startActivity(new Intent(MapsActivity.this, AboutUs.class));
                break;

            case R.id.BuildingSafetyCheck:
                startActivity(new Intent(MapsActivity.this, BuildingSafetyCheck.class));
                break;

            case R.id.LogOut:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
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
        switch (item.getItemId()){
            case R.id.Help:
                startActivity(new Intent(MapsActivity.this,Help.class));
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
        // Add a marker in Sydney and move the camera
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    // Creates and displays a notification
    private void Notification() {

        if(count==0) {
            Notification2();
            count=count+1;
        }

            final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

            Intent notificationIntent = new Intent(getApplicationContext(), EarthquakeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notification = builder.setContentIntent(pendingIntent);
            notificationManager.notify(0, builder.build());


        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
        LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
        final View view1 = factory.inflate(R.layout.alert_earthquake, null);
        builder1.setView(view1);
        builder1 .setTitle("Danger Earthquake")
                .setMessage("Safe Yourself From Shocks and Live Your Location?\n\nMove to Safe Place or Open Area if Already at Safe Place take a Long Breath. We are Sending Rescue Team.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(MapsActivity.this,EarthquakeActivity.class));
                        Toast.makeText(MapsActivity.this,"Checking Safe Place For You",Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog =builder1.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.getWindow().setLayout(700,1200);
    }

    public void Notification2(){

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent activityIntent = new Intent(MapsActivity.this, EarthquakeActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(MapsActivity.this,
                        0, activityIntent, 0);

                Intent broadcastIntent = new Intent(MapsActivity.this, NotificationReceiver.class);
                broadcastIntent.putExtra("toastMessage", "We are Sending Rescue Teams");
                PendingIntent actionIntent = PendingIntent.getBroadcast(MapsActivity.this,
                        0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification1 = new NotificationCompat.Builder(MapsActivity.this, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.garud)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.earthquake_icon))
                        .setContentTitle("Earthquake")
                        .setContentText("Are You Safe?")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .addAction(R.mipmap.ic_launcher, "Yes", actionIntent)
                        .build();

                notificationManager.notify(1, notification1);
            }
        },SPLASH_TIME_OUT);
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


