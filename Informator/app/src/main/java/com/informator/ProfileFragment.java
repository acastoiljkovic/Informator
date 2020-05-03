package com.informator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.informator.data.Constants;
import com.informator.data.StoredData;
import com.informator.profile_fragments.*;

import com.google.android.material.tabs.TabLayout;
import com.informator.profile_fragments.EventsFragment;
import com.informator.profile_fragments.TabAdapterProfile;


public class ProfileFragment extends Fragment {

    private TabAdapterProfile adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageViewProfilePicture;
    private TextView tvFullName;
    private TextView tvFriends;
    private TextView tvGroups;
    private TextView tvPoints;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout_profile);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_profile);

        sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        adapter = new TabAdapterProfile(getFragmentManager());
        adapter.addFragment(new RankingFragment(),"Ranking");
        adapter.addFragment(new FriendsFragment(),"Friends");
        adapter.addFragment(new PhotosFragment(),"Photos");
        adapter.addFragment(new EventsFragment(),"Events");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);



        if(StoredData.getInstance().getUser() != null) {
            imageViewProfilePicture = (ImageView) view.findViewById(R.id.profile_picture);
            imageViewProfilePicture.setImageBitmap(StoredData.getInstance().user.getProfilePhoto());
            tvFullName = (TextView) view.findViewById(R.id.tvFullName);
            tvFullName.setText(StoredData.getInstance().user.getFullName());
        }

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_profile_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.logout){
                    StoredData.getInstance().setUser(null);
                    Intent i = new Intent(getContext(),MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putBoolean(Constants.SHARED_PREFERENCES_LOGGED,false);
                    edit.putString(Constants.SHARED_PREFERENCES_EMAIL,"");
                    edit.putString(Constants.SHARED_PREFERENCES_USERNAME,"");
                    edit.putString(Constants.SHARED_PREFERENCES_PASSWORD,"");
                    edit.commit();
//                    Toast.makeText(getContext(),"Successful logout",Toast.LENGTH_SHORT).show();
                }
                else if( item.getItemId() == R.id.search_friends){
                    ((StartActivity) getActivity()).setFragment(R.id.search_friends);

                }
                else if( item.getItemId() == R.id.send_message){
                    ((StartActivity) getActivity()).setFragment(R.id.send_message);

                }
                else if( item.getItemId() == R.id.add_friends_bluetooth){
                    ((StartActivity) getActivity()).setFragment(R.id.add_friends_bluetooth);

                }
                return false;
            }
        });




        return view;
    }

}
