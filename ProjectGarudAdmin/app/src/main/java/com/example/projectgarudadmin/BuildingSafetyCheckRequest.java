package com.example.projectgarudadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuildingSafetyCheckRequest extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    ListView listView;
    List arrayList = new ArrayList<>();
    String keylist;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_safety_check_request);

        listView = findViewById(R.id.ListView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer10);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView nvdrawer10 = (NavigationView) findViewById(R.id.nvdrawer10);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrwaerContext(nvdrawer10);
        //header email name
        View view = getLayoutInflater().inflate(R.layout.header, null);
        final TextView textView2=view.findViewById(R.id.textView2);
        final TextView textView3=view.findViewById(R.id.textView3);

        textView2.setText("Project Garud");
        textView3.setText("**Team Rebel**");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        nvdrawer10.addHeaderView(view);

        final CustomAdapter customAdapter = new CustomAdapter(this, R.layout.building_safety_layout, arrayList);
        listView.setAdapter(customAdapter);

        final DatabaseReference databaseReference = firebaseDatabase.getReference("Building Safety Requests");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    BuildingSafety buildingSafety = ds.getValue(BuildingSafety.class);

                    int count=0;
                    if(count==0){
                        keylist = ds.getKey();
                        count++;
                    }

                    arrayList.add(new BuildingSafety("Name: " +buildingSafety.getName(), "Mobile: " + buildingSafety.getMobile(), "Address: " + buildingSafety.getAddress(), "Coordinates: " + buildingSafety.getCoordinates()));

                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final AlertDialog.Builder builder=new AlertDialog.Builder(BuildingSafetyCheckRequest.this);
                builder.setMessage("Are you done with this and want to Delete this Refer Request??").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        databaseReference.child(keylist).removeValue();
                        startActivity(new Intent(BuildingSafetyCheckRequest.this, BuildingSafetyCheckRequest.class));

                    }
                }).setNegativeButton("No",null);

                AlertDialog alertDialog=builder.create();
                alertDialog.show();

            }
        });

    }

    public boolean selectItemdrwaer (MenuItem item){

        switch (item.getItemId()) {

            case R.id.Home:
                startActivity(new Intent(BuildingSafetyCheckRequest.this, ActiveUsers.class));
                break;

            case R.id.Earthquake:
                startActivity(new Intent(BuildingSafetyCheckRequest.this, EarthquakePushNotify.class));
                break;

            case R.id.AddSafePlace:
                startActivity(new Intent(BuildingSafetyCheckRequest.this, AddSafePlace.class));
                break;

            case R.id.BuildingRequest:
                startActivity(new Intent(BuildingSafetyCheckRequest.this, BuildingSafetyCheckRequest.class));
                break;

            case R.id.LogOut:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(BuildingSafetyCheckRequest.this, MainActivity.class));
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

    class CustomAdapter extends ArrayAdapter<String> {
        List arrayList;
        Context context;
        int resource;
        TextView textView6,textView8,textView12,textView13;


        public CustomAdapter(@NonNull Context context, int resource, List arrayList) {
            super(context, resource, arrayList);
            this.context = context;
            this.resource = resource;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.building_safety_layout, null);
            textView6 = view.findViewById(R.id.textView6);
            textView8 = view.findViewById(R.id.textView8);
            textView12 = view.findViewById(R.id.textView12);
            textView13 = view.findViewById(R.id.textView13);

            final BuildingSafety buildingSafety = (BuildingSafety) arrayList.get(position);
            textView6.setText(buildingSafety.getName());
            textView8.setText(buildingSafety.getMobile());
            textView12.setText(buildingSafety.getAddress());
            textView13.setText(buildingSafety.getCoordinates());

            return view;
        }

    }
}
