package com.example.projectgarud;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AboutUsAdmin extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    FirebaseAuth firebaseAuth;
    TextView textView17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_admin);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer7);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView nvdrawer7 = (NavigationView) findViewById(R.id.nvdrawer7);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrwaerContext(nvdrawer7);
        firebaseAuth = FirebaseAuth.getInstance();

        textView17 = findViewById(R.id.textView17);

        textView17.setText("Our team is made up of the best innovators, software developers and tech enthusiasts. Through our research-based concepts, frameworks, and resources we are making an app i.e. “Project Garud” which is still under implementation and this app is dedicated to help and save the life of human beings. Our belief is to develop a model using latest technology to reduce the risk of earthquake."+
                "\n\nThis project is being  implemented by Abhay Dhauni, Dhruv Jain, Himanshu Upadhyay, Jayesh Bansal and Kapil Thakur.");


        View view=getLayoutInflater().inflate(R.layout.header,null);
        final TextView textView2=view.findViewById(R.id.textView2);
        final TextView textView3=view.findViewById(R.id.textView3);

        textView2.setText("Project Garud");
        textView3.setText("**Team Rebel**");

        nvdrawer7.addHeaderView(view);

    }

    public boolean selectItemdrwaer (MenuItem item){

        switch (item.getItemId()) {

            case R.id.HomeAdmin:
                startActivity(new Intent(AboutUsAdmin.this, ActiveUsers.class));
                break;

            case R.id.AboutUsAdmin:
                startActivity(new Intent(AboutUsAdmin.this, AboutUsAdmin.class));
                break;

            case R.id.LogOut:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(AboutUsAdmin.this, MainActivity.class));
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
                startActivity(new Intent(AboutUsAdmin.this,Help.class));
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
