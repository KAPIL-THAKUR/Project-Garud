package com.example.projectgarudadmin;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EarthquakePushNotify extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String GeoLocation,Magnitude;
    EditText editText3,editText4;
    Button button2;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_push_notify);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer2);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView nvdrawer2 = (NavigationView) findViewById(R.id.nvdrawer2);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrwaerContext(nvdrawer2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        button2 = findViewById(R.id.button2);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        View view=getLayoutInflater().inflate(R.layout.header,null);
        final TextView textView2=view.findViewById(R.id.textView2);
        final TextView textView3=view.findViewById(R.id.textView3);

        textView2.setText("Project Garud");
        textView3.setText("**Team Rebel**");

        nvdrawer2.addHeaderView(view);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Pushing Notification...");
                progressDialog.show();
                if(Validate()){
                    GeoLocation = editText3.getText().toString();
                    Magnitude = editText4.getText().toString();
                    double MagnitudeValue = Double.parseDouble(Magnitude);
                    final DatabaseReference reference = firebaseDatabase.getReference("Earthqauke");
                    final EarthquakeActivity earthquakeActivity = new EarthquakeActivity(GeoLocation,MagnitudeValue);
                    reference.setValue(earthquakeActivity);
                    Toast.makeText(EarthquakePushNotify.this,"Notification Uploaded to Database",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EarthquakePushNotify.this,ActiveUsers.class));
                }else{
                    progressDialog.dismiss();
                }
            }
        });
    }

    boolean Validate(){
        boolean result= false;
        GeoLocation=editText3.getText().toString();
        Magnitude=editText4.getText().toString();
        if(GeoLocation.isEmpty() ||  Magnitude.isEmpty()){
            Toast.makeText(EarthquakePushNotify.this,"GeoLocation or Magnitude Can't be Empty",Toast.LENGTH_SHORT).show();
        }else{
            result=true;
        }
        return result;
    }

    public boolean selectItemdrwaer (MenuItem item){

        switch (item.getItemId()) {

            case R.id.Home:
                startActivity(new Intent(EarthquakePushNotify.this, ActiveUsers.class));
                break;

            case R.id.Earthquake:
                startActivity(new Intent(EarthquakePushNotify.this, EarthquakePushNotify.class));
                break;

            case R.id.AddSafePlace:
                startActivity(new Intent(EarthquakePushNotify.this, AddSafePlace.class));
                break;

            case R.id.BuildingRequest:
                startActivity(new Intent(EarthquakePushNotify.this, BuildingSafetyCheckRequest.class));
                break;

            case R.id.LogOut:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(EarthquakePushNotify.this, MainActivity.class));
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
}
