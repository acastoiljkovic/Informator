package com.informator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new GroupsFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment selectedFragment=null;
                if(menuItem.getItemId()==R.id.groups){
                    selectedFragment=new GroupsFragment();
                    //Toast.makeText(getApplicationContext(),"Groups fragment",Toast.LENGTH_SHORT).show();
                }
                else if(menuItem.getItemId()==R.id.map){
                    selectedFragment=new MapFragment();
                }
                else if(menuItem.getItemId()==R.id.events)
                {
                    selectedFragment=new EventsFragment();
                }
                else if(menuItem.getItemId()==R.id.profile){
                    selectedFragment=new ProfileFragment();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

                return true;
            }
        });
    }

}
