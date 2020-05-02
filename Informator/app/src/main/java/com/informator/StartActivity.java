package com.informator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.informator.profile_fragments.SearchFriendsFragment;
import com.informator.profile_fragments.SendMessageFragment;

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
                setFragment(menuItem.getItemId());
                return true;
            }
        });
    }

    public static Fragment getSelectedFragment(int itemId){

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
        else if(itemId==R.id.add_group){
//            retFragment=new AddGroupFragment();
        }
        else if(itemId==R.id.find_group){
//            retFragment=new FindGroupFragment();
        }
        else if(itemId==R.id.search_on_map){
//            retFragment=new SearchMapFragment();
        }
        else if(itemId==R.id.add_event){
//            retFragment=new AddEventFragment();
        }
        else if(itemId==R.id.find_event){
//            retFragment=new FindEventFragment();
        }
        else if(itemId==R.id.search_friends){
            retFragment=new SearchFriendsFragment();
        }
        else if(itemId==R.id.send_message){
            retFragment=new SendMessageFragment();
        }

        return retFragment;
    }

    public void setFragment(int fragmentId){
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, getSelectedFragment(fragmentId)).commit();
        }
        catch (Exception e){
            Log.e("Error :",e.getMessage().toString());
        }
    }

}
