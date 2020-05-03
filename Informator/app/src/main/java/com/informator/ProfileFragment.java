package com.informator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.informator.data.StoredData;
import com.informator.profile_fragments.*;

import com.google.android.material.tabs.TabLayout;
import com.informator.profile_fragments.EventsFragment;
import com.informator.profile_fragments.TabAdapterProfile;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private TabAdapterProfile adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageViewProfilePicture;
    private TextView tvFullName;
    private TextView tvFriends;
    private TextView tvGroups;
    private TextView tvPoints;




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
                    Toast.makeText(getContext(),"Successful logout",Toast.LENGTH_SHORT).show();
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
