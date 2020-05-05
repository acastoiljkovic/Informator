package com.informator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.informator.data.Constants;
import com.informator.data.StoredData;
import com.informator.data.User;
import com.informator.data.UserWithPicture;
import com.informator.profile_fragments.*;

import com.google.android.material.tabs.TabLayout;
import com.informator.profile_fragments.EventsFragment;
import com.informator.profile_fragments.TabAdapterProfile;


public class ProfileFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;
    Bitmap picture;
    User user;
    private TabAdapterProfile adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageViewProfilePicture;
    private TextView tvFullName;
    private ImageView imageViewEditProfile;
    private TextView tvEditProfile;
    private TextView tvFriends;
    private TextView tvGroups;
    private TextView tvPoints;
    SharedPreferences sharedPreferences;
    String username = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        Bundle bundle = getArguments();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_profile_toolbar);

        sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        if(bundle != null) {
            username = bundle.get("username").toString();

        }

        imageViewEditProfile = (ImageView) view.findViewById(R.id.imageView_editProfile);
        tvEditProfile = (TextView) view.findViewById(R.id.textView_editProfile);
        imageViewProfilePicture = (ImageView) view.findViewById(R.id.profile_picture);
        tvFullName = (TextView) view.findViewById(R.id.tvFullName);
        if (username != null) {
//            imageViewEditProfile.setImageResource(R.drawable.ic_add_black_24dp);
//            tvEditProfile.setText(R.string.add_friend);
//            tvFullName.setText(R.string.no_text);
//            imageViewProfilePicture.setImageResource(R.drawable.ic_person_outline_black_24dp);
//            try{
//                database = FirebaseDatabase.getInstance();
//                storage = FirebaseStorage.getInstance(Constants.URL_STORAGE);
//                storageRef = storage.getReference();
//                mDatabase = database.getReference();
//            }
//            catch (Exception e){
//            }
//            mDatabase.child("users").child(username).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if(dataSnapshot != null){
//
//                        user = dataSnapshot.getValue(User.class);
//                        tvFullName.setText(String.valueOf(user.getFullName()));
//                    }
//
//                    StorageReference profilePicture = storageRef.child(user.getUsername()+".jpg");
//
//                    picture = null;
//                    profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                        @Override
//                        public void onSuccess(byte[] bytes) {
//                            picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                            imageViewProfilePicture.setImageBitmap(picture);
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Toast.makeText(getContext(), "Error while fetching data...", Toast.LENGTH_SHORT).show();
//
//                }
//            });
        }
        else {


            if (StoredData.getInstance().getUser() != null) {
                imageViewProfilePicture.setImageBitmap(StoredData.getInstance().user.getProfilePhoto());
                tvFullName.setText(StoredData.getInstance().user.getFullName());
            }

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.logout) {
                        StoredData.getInstance().setUser(null);
                        Intent i = new Intent(getContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putBoolean(Constants.SHARED_PREFERENCES_LOGGED, false);
                        edit.putString(Constants.SHARED_PREFERENCES_EMAIL, "");
                        edit.putString(Constants.SHARED_PREFERENCES_USERNAME, "");
                        edit.putString(Constants.SHARED_PREFERENCES_PASSWORD, "");
                        edit.commit();
//                    Toast.makeText(getContext(),"Successful logout",Toast.LENGTH_SHORT).show();
                    } else if (item.getItemId() == R.id.search_friends) {
                        ((StartActivity) getActivity()).setFragment(R.id.search_friends, null);

                    } else if (item.getItemId() == R.id.send_message) {
                        ((StartActivity) getActivity()).setFragment(R.id.send_message, null);

                    } else if (item.getItemId() == R.id.add_friends_bluetooth) {
//                        ((StartActivity) getActivity()).setFragment(R.id.add_friends_bluetooth, null);
                        Bundle bundle = new Bundle();
                        bundle.putString("username","test");
                        ((StartActivity)getActivity()).setFragment(R.id.profile,bundle);
                    }
                    return false;
                }
            });


        }

        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout_profile);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_profile);


        adapter = new TabAdapterProfile(getFragmentManager());
        adapter.addFragment(new RankingFragment(), "Ranking");
        adapter.addFragment(new FriendsFragment(), "Friends");
        adapter.addFragment(new PhotosFragment(), "Photos");
        adapter.addFragment(new EventsFragment(), "Events");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
