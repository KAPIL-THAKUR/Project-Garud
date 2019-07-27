package com.example.projectgarudadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddSafePlace extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    EditText editText5,editText6,editText7,editText8;
    Button button3;
    String PlaceName,Address,Latitude,Longitude;
    Spinner spinner;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_safe_place);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer3);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView nvdrawer3 = (NavigationView) findViewById(R.id.nvdrawer3);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrwaerContext(nvdrawer3);
        editText5 = findViewById(R.id.editText5);
        editText6 = findViewById(R.id.editText6);
        editText7 = findViewById(R.id.editText7);
        editText8 = findViewById(R.id.editText8);
        button3 = findViewById(R.id.button3);
        spinner = findViewById(R.id.spinner);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        View view=getLayoutInflater().inflate(R.layout.header,null);
        final TextView textView2=view.findViewById(R.id.textView2);
        final TextView textView3=view.findViewById(R.id.textView3);

        textView2.setText("Project Garud");
        textView3.setText("**Team Rebel**");

        nvdrawer3.addHeaderView(view);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(AddSafePlace.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.City));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Adding Place...");
                progressDialog.show();
                if (Validate()) {
                    PlaceName = editText5.getText().toString();
                    Address = editText6.getText().toString();
                    Latitude = editText7.getText().toString();
                    Longitude = editText8.getText().toString();
                    final double LatitudeValue = Double.parseDouble(Latitude);
                    final double LongitudeValue = Double.parseDouble(Longitude);
                    String CityName = spinner.getSelectedItem().toString();
                    if (CityName.equals("Select")) {
                        progressDialog.dismiss();
                        Toast.makeText(AddSafePlace.this,"Select The City in DropDown",Toast.LENGTH_SHORT).show();
                    } else {
                        final DatabaseReference reference = firebaseDatabase.getReference("Safe Place").child(CityName);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int i = (int) (dataSnapshot.getChildrenCount() + 1);
                                final SafePlace safePlace = new SafePlace(PlaceName, Address, LatitudeValue, LongitudeValue);
                                reference.child("Place No:" + i).setValue(safePlace);
                                Toast.makeText(AddSafePlace.this, "Place Added SuccessFully in DataBase", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddSafePlace.this, ActiveUsers.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }else{
                    progressDialog.dismiss();
                }
            }
        });
    }

    boolean Validate(){
        boolean result= false;
        PlaceName=editText5.getText().toString();
        Address=editText6.getText().toString();
        Latitude=editText7.getText().toString();
        Longitude=editText8.getText().toString();
        if(PlaceName.isEmpty() ||  Address.isEmpty() || Latitude.isEmpty() ||  Longitude.isEmpty()){
            Toast.makeText(AddSafePlace.this,"Above Fields Can't be Empty",Toast.LENGTH_SHORT).show();
        }else{
            result=true;
        }
        return result;
    }

    public boolean selectItemdrwaer (MenuItem item){

        switch (item.getItemId()) {

            case R.id.Home:
                startActivity(new Intent(AddSafePlace.this, ActiveUsers.class));
                break;

            case R.id.Earthquake:
                startActivity(new Intent(AddSafePlace.this, EarthquakePushNotify.class));
                break;

            case R.id.AddSafePlace:
                startActivity(new Intent(AddSafePlace.this, AddSafePlace.class));
                break;

            case R.id.BuildingRequest:
                startActivity(new Intent(AddSafePlace.this, BuildingSafetyCheckRequest.class));
                break;

            case R.id.LogOut:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(AddSafePlace.this, MainActivity.class));
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
