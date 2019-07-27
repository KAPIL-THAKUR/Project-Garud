package com.example.projectgarud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EarthquakeActivityAdmin extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseDatabase firebaseDatabase;
    TextView textView14,textView15;
    LocationManager locationManager;
    LocationListener locationListener;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    FirebaseAuth firebaseAuth;
    private GoogleMap mMap;
    Marker marker;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_admin);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Detecting Safe Place...");
        //progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        textView14 = findViewById(R.id.textView14);
        textView15 = findViewById(R.id.textView15);

        DatabaseReference reference = firebaseDatabase.getReference("Earthqauke");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationData locationData = dataSnapshot.getValue(LocationData.class);
                String GeoLocation = locationData.getGeoLocation();
                double Magnitude = locationData.getMagnitude();
                textView14.setText("Earthqauke is expected in "+GeoLocation+" GeoLocation");
                textView15.setText("Magnitude of Earthquake is expected "+Magnitude);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                        Geocoder geocoder = new Geocoder(EarthquakeActivityAdmin.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            String address = addresses.get(0).getAddressLine(0);
                            String City = addresses.get(0).getLocality();
                            Log.d("mylog", "Complete Address: " + addresses.toString());
                            Log.d("mylog", "Address: " + address);
                            if (GeoLocation.equals(City) && Magnitude > 2) {
                                Notification();
                            }else{
                                Toast.makeText(EarthquakeActivityAdmin.this,"You are Safe Now Earthquake is Finished",Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                        LatLng latLng = new LatLng(latitude, longitude);
                        Geocoder geocoder = new Geocoder(EarthquakeActivityAdmin.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            String address = addresses.get(0).getAddressLine(0);
                            final String City = addresses.get(0).getLocality();
                            Log.d("mylog", "Complete Address: " + addresses.toString());
                            Log.d("mylog", "Address: " + address);
                            progressDialog.dismiss();
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location").snippet(City));
                            mMap.setMaxZoomPreference(20);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                            final DatabaseReference reference6 = firebaseDatabase.getReference("Safe Place");
                            reference6.child(City).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        mMap.clear();
                                        for (int i = 1; i < dataSnapshot.getChildrenCount() + 1; i++) {
                                            reference6.child(City).child("Place No:" + i).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    SafePlace safePlace = dataSnapshot.getValue(SafePlace.class);
                                                    String PlaceName = safePlace.getSafeplace();
                                                    String Address = safePlace.getAddress();
                                                    double Latitude = safePlace.getLatitude();
                                                    double Longitude = safePlace.getLongitude();
                                                    LatLng latLng = new LatLng(Latitude, Longitude);
                                                    progressDialog.dismiss();
                                                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title(PlaceName).snippet(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.building)));
                                                    mMap.setMaxZoomPreference(20);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    } else{
                                        progressDialog.dismiss();
                                        Toast.makeText(EarthquakeActivityAdmin.this,"We are Sending Our Team to Save You",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.Help:
                startActivity(new Intent(EarthquakeActivityAdmin.this,Help.class));
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



        Intent notificationIntent = new Intent(getApplicationContext(), EarthquakeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = builder.setContentIntent(pendingIntent);
        notificationManager.notify(0, builder.build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.help_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
