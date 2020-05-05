package com.informator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.informator.profile_fragments.AddFriendsBluetoothFragment;
import com.informator.profile_fragments.SearchFriendsFragment;
import com.informator.profile_fragments.SendMessageFragment;

public class StartActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new GroupsFragment()).addToBackStack(null).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public static Fragment getSelectedFragment(int itemId, Bundle bundle){

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
            retFragment.setArguments(bundle);
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
        else if(itemId==R.id.add_friends_bluetooth){
            retFragment=new AddFriendsBluetoothFragment();
        }

        return retFragment;
    }

    public void setFragment(int fragmentId,Bundle bundle){
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, getSelectedFragment(fragmentId,bundle)).addToBackStack(null).commit();
        }
        catch (Exception e){
            Log.e("Error :",e.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        setFragment(menuItem.getItemId(),null);
        return true;
    }
}
