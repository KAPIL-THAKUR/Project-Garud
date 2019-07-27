package com.example.projectgarud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText editText, editText2;
    Button button;
    String email, pass;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    TextView textView, textView8, textView9;
    ImageView imageView4;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permission();

        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        textView = findViewById(R.id.textView);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        imageView4 = findViewById(R.id.imageView4);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        textView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView8.getText().toString().equals("Create Free Account? Register")) {
                    textView8.setText("Already have an account? Login");
                    button.setText("Register");
                } else {
                    textView8.setText("Create Free Account? Register");
                    button.setText("Login");
                }
            }
        });

        email = editText.getText().toString().toLowerCase();
        pass = editText2.getText().toString();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            if(firebaseAuth.getUid().equals("anjKmUfk0vbZgvbttx1TJVf9uYV2")){
                progressDialog.dismiss();
                startActivity(new Intent(MainActivity.this, ActiveUsers.class));
            }
            else {
                progressDialog.dismiss();
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        }

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

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
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



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.getText().toString().equals("Login")){
                    progressDialog.setMessage("Verifying....");
                    progressDialog.show();
                    if(Validate()) {
                        //Admin for Rescue Team
                        if (email.equals("kapil882466@gmail.com")) {
                            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(MainActivity.this, ActiveUsers.class));
                                        Toast.makeText(MainActivity.this, "Rescue Team Login Sucessfull", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Rescue Team Login Failed", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        textView9.setText("Check Internet Connectivity !!");
                                        textView9.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        } else {
                            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.setMessage("Loading Details...");
                                        final DatabaseReference reference = firebaseDatabase.getReference("Project Garud Users");
                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(firebaseAuth.getUid())) {
                                                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                                                    Toast.makeText(MainActivity.this, "Login Sucessfull", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    progressDialog.dismiss();
                                                    textView9.setVisibility(View.INVISIBLE);
                                                    textView8.setVisibility(View.INVISIBLE);
                                                    editText.setHint("Enter your Name");
                                                    editText.setText(null);
                                                    imageView4.setVisibility(View.INVISIBLE);
                                                    editText2.setVisibility(View.INVISIBLE);
                                                    button.setText("Continue");
                                                    textView.setText("Enter Details");
                                                    button.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (editText.getText().toString().isEmpty()) {
                                                                Toast.makeText(MainActivity.this, "Name Can't be Empty", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                progressDialog.setMessage("Checking Gps Permission...");
                                                                progressDialog.show();
                                                                firebaseAuth = FirebaseAuth.getInstance();
                                                                firebaseDatabase = FirebaseDatabase.getInstance();
                                                                final DatabaseReference reference1 = firebaseDatabase.getReference("Project Garud Users").child(firebaseAuth.getUid()).child("Details");
                                                                String name = editText.getText().toString();
                                                                LocationData locationData = new LocationData(name);
                                                                reference1.setValue(locationData);
                                                                if (Permission()) {
                                                                    progressDialog.dismiss();
                                                                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                                                                    Toast.makeText(MainActivity.this, "Login Sucessfull", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    Permission();
                                                                    Toast.makeText(MainActivity.this, "Allow Location Permission & Click Continue", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        textView9.setText("Email Not Registered If Already Registered Check Internet !!");
                                        textView9.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }else{
                        progressDialog.dismiss();
                    }

                }else if(button.getText().toString().equals("Register")){
                    progressDialog.setMessage("Getting Register....");
                    progressDialog.show();
                    if(Validate()){
                        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    editText.setHint("Enter your Name");
                                    editText.setText(null);
                                    imageView4.setVisibility(View.INVISIBLE);
                                    editText2.setVisibility(View.INVISIBLE);
                                    button.setText("Continue");
                                    textView.setText("Enter Details");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(editText.getText().toString().isEmpty()){
                                                Toast.makeText(MainActivity.this,"Name Can't be Empty",Toast.LENGTH_SHORT).show();
                                            }else {
                                                progressDialog.setMessage("Checking Gps Permission...");
                                                progressDialog.show();
                                                firebaseAuth = FirebaseAuth.getInstance();
                                                firebaseDatabase = FirebaseDatabase.getInstance();
                                                final DatabaseReference reference1 = firebaseDatabase.getReference("Project Garud Users").child(firebaseAuth.getUid()).child("Details");
                                                String name = editText.getText().toString();
                                                LocationData locationData = new LocationData(name);
                                                reference1.setValue(locationData);
                                                final DatabaseReference databaseReference = firebaseDatabase.getReference("Project Garud Users").child(firebaseAuth.getUid()).child("Safe Person");
                                                SafePerson safePerson1 = new SafePerson("No");
                                                databaseReference.setValue(safePerson1);
                                                if (Permission()) {
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                                                    Toast.makeText(MainActivity.this, "Registerd Sucessfull", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    progressDialog.dismiss();
                                                    Permission();
                                                    Toast.makeText(MainActivity.this,"Allow Location Permission & Click Continue",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(MainActivity.this,"Registered Failed",Toast.LENGTH_SHORT).show();
                                    textView9.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                }
                            }
                        });

                    }else{
                        progressDialog.dismiss();
                   }
                }

            }
        });

    }
    boolean Validate(){
        boolean result= false;
        email=editText.getText().toString();
        pass=editText2.getText().toString();
        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(MainActivity.this,"Email or Password Can't be Empty",Toast.LENGTH_SHORT).show();
        }else{
            result=true;
        }
        return result;
    }

    boolean Permission(){
        boolean result=false;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }else{
            result=true;
        }
        return result;
    }

    @Override
    public void onBackPressed() {

        MainActivity.super.onBackPressed();
        firebaseAuth.signOut();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseAuth.signOut();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.help_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.Help:
                startActivity(new Intent(MainActivity.this,Help.class));
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
