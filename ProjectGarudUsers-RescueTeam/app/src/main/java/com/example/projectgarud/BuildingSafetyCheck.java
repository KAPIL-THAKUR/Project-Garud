package com.example.projectgarud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BuildingSafetyCheck extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;
    LocationListener locationListener;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    EditText editText7,editText8,editText9;
    TextView editText10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_safety_check);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer5);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView nvdrawer5 = (NavigationView) findViewById(R.id.nvdrawer5);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrwaerContext(nvdrawer5);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Details...");
       // progressDialog.setCancelable(false);
        progressDialog.show();
        editText7 = findViewById(R.id.editText7);
        editText8 = findViewById(R.id.editText8);
        editText9 = findViewById(R.id.editText9);
        editText10 = findViewById(R.id.editText10);
        Button button5 = findViewById(R.id.button5);

        View view=getLayoutInflater().inflate(R.layout.header,null);
        final TextView textView2=view.findViewById(R.id.textView2);
        final TextView textView3=view.findViewById(R.id.textView3);

        textView2.setText("Project Garud");
        textView3.setText("**Team Rebel**");

        nvdrawer5.addHeaderView(view);

        final DatabaseReference reference = firebaseDatabase.getReference("Project Garud Users").child(firebaseAuth.getUid()).child("Details");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationData locationData1 = dataSnapshot.getValue(LocationData.class);
                String name = locationData1.getName();
                editText7.setText(name);


                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(BuildingSafetyCheck.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BuildingSafetyCheck.this, new String[]
                                    {Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION_PERMISSION);
                }


                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();
                        editText10.setText(""+latitude +", "+ longitude);
                        progressDialog.dismiss();

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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate()) {
                    String name = editText7.getText().toString();
                    String address = editText8.getText().toString();
                    String mobile = editText9.getText().toString();
                    String coordinates = editText10.getText().toString();

                    DatabaseReference reference1 = firebaseDatabase.getReference("Building Safety Requests").child(firebaseAuth.getUid());
                    BuildingSaftey buildingSaftey = new BuildingSaftey(name, address, mobile, coordinates);
                    reference1.setValue(buildingSaftey);

                    startActivity(new Intent(BuildingSafetyCheck.this, MapsActivity.class));
                    Toast.makeText(BuildingSafetyCheck.this, "SucessFully Requested", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    boolean Validate(){
        boolean result= false;
        String name = editText7.getText().toString();
        String address=editText8.getText().toString();
        String mobile = editText9.getText().toString();
        String coordinate=editText10.getText().toString();
        if(name.isEmpty() || address.isEmpty() || mobile.isEmpty() || coordinate.isEmpty()){
            Toast.makeText(BuildingSafetyCheck.this,"Above Fields Can't be Empty",Toast.LENGTH_SHORT).show();
        }else{
            result=true;
        }
        return result;
    }

    public boolean selectItemdrwaer (MenuItem item){

        switch (item.getItemId()) {

            case R.id.Home:
                startActivity(new Intent(BuildingSafetyCheck.this, MapsActivity.class));
                break;

            case R.id.Help:
                startActivity(new Intent(BuildingSafetyCheck.this, Help.class));
                break;

            case R.id.AboutUs:
                startActivity(new Intent(BuildingSafetyCheck.this, AboutUs.class));
                break;

            case R.id.BuildingSafetyCheck:
                startActivity(new Intent(BuildingSafetyCheck.this, BuildingSafetyCheck.class));
                break;

            case R.id.LogOut:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(BuildingSafetyCheck.this, MainActivity.class));
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
                startActivity(new Intent(BuildingSafetyCheck.this,Help.class));
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.help_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
