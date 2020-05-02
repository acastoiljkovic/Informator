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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,getSelectedFragment(menuItem.getItemId())).commit();
                return true;
            }
        });
    }

    public Fragment getSelectedFragment(int itemId){

        Fragment retFragment = null;

        if(itemId==R.id.groups){
            retFragment=new GroupsFragment();
        }
        else if(itemId==R.id.map){
            retFragment=new MapFragment();
        }
        else if(itemId==R.id.events)
        {
            retFragment=new EventsFragment();
        }
        else if(itemId==R.id.profile){
            retFragment=new ProfileFragment();
        }

        return retFragment;
    }

}
