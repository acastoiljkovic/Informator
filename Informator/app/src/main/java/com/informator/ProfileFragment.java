package com.informator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.informator.data.SearchFriendsListViewItem;
import com.informator.data.StoredData;
import com.informator.data.User;
import com.informator.data.UserWithPicture;
import com.informator.profile_fragments.*;

import com.google.android.material.tabs.TabLayout;
import com.informator.profile_fragments.EventsFragment;
import com.informator.profile_fragments.TabAdapterProfile;

import java.util.ArrayList;


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
    private LinearLayout editOrAdd;
    SharedPreferences sharedPreferences;
    String username = null;
    boolean isFriends = false;
    ArrayList<String> friendsOfPerson;
    RankingFragment fragmentRanking;
    FriendsFragment fragmentFriends;
    com.informator.profile_fragments.EventsFragment fragmentEvents;
    PhotosFragment fragmentPhotos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        Bundle bundle = getArguments();

        editOrAdd = (LinearLayout)view.findViewById(R.id.edit_profile_or_add_friend);

        fragmentRanking = new RankingFragment();
        fragmentFriends = new FriendsFragment();
        fragmentEvents = new com.informator.profile_fragments.EventsFragment();
        fragmentPhotos = new PhotosFragment();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_profile_toolbar);

        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout_profile);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_profile);


        adapter = new TabAdapterProfile(getFragmentManager());

        sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        if(bundle != null) {
            username = bundle.get("username").toString();

        }

        imageViewEditProfile = (ImageView) view.findViewById(R.id.imageView_editProfile);
        tvEditProfile = (TextView) view.findViewById(R.id.textView_editProfile);
        imageViewProfilePicture = (ImageView) view.findViewById(R.id.profile_picture);
        tvFullName = (TextView) view.findViewById(R.id.tvFullName);
        tvFriends = (TextView) view.findViewById(R.id.tvFriends);
        tvGroups = (TextView) view.findViewById(R.id.tvGroups);
        tvPoints = (TextView) view.findViewById(R.id.tvPoints);
        //gledamo tudji profil
        if (username != null && username.compareTo(StoredData.getInstance().getUser().getUsername()) != 0) {
            friendsOfPerson = new ArrayList<>();
            Bitmap image = drawableToBitmap(getContext().getResources().getDrawable(R.drawable.ic_person_outline_black_24dp));
            imageViewEditProfile.setImageResource(R.drawable.ic_add_black_24dp);
/*            tvEditProfile.setText(R.string.add_friend);
            tvFullName.setText(R.string.no_text);*/
            imageViewProfilePicture.setImageBitmap(Bitmap.createScaledBitmap(image, 3000, 3000, false));
            try{
                database = FirebaseDatabase.getInstance();
                storage = FirebaseStorage.getInstance(Constants.URL_STORAGE);
                storageRef = storage.getReference();
                mDatabase = database.getReference();
            }
            catch (Exception e){
            }
            mDatabase.child("users").child(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = new User();
                    if(dataSnapshot != null){
                        user.setUsername(String.valueOf(dataSnapshot.child("username").getValue()));
                        user.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                        user.setFullName(String.valueOf(dataSnapshot.child("fullName").getValue()));
                        tvFullName.setText(String.valueOf(user.getFullName()).toUpperCase());
                        for(DataSnapshot data1 : dataSnapshot.child("friends").getChildren()){
                            friendsOfPerson.add(String.valueOf(data1.getValue()));
                            if(String.valueOf(data1.getValue()).compareTo(StoredData.getInstance().getUser().getUsername()) == 0){
                                isFriends = true;
                                tvEditProfile.setText(R.string.remove_friend);
                                imageViewEditProfile.setImageResource(R.drawable.ic_delete_black_24dp);
                            }
                        }
                        tvFriends.setText(getContext().getResources().getString(R.string.friends) +" " + friendsOfPerson.size());

                        Bundle bundleProfile = new Bundle();
                        bundleProfile.putBoolean("profile",true);
                        fragmentRanking.setArguments(bundleProfile);
                        Bundle bundleFrends = new Bundle();
                        bundleFrends.putBoolean("profile",true);
                        bundleFrends.putStringArrayList("friends",friendsOfPerson);
                        fragmentFriends.setArguments(bundleFrends);
                        fragmentPhotos.setArguments(bundleProfile);
                        fragmentEvents.setArguments(bundleProfile);
                        adapter.addFragment(fragmentRanking, "Ranking");
                        adapter.addFragment(fragmentFriends, "Friends");
                        adapter.addFragment(fragmentPhotos, "Photos");
                        adapter.addFragment(fragmentEvents, "Events");

                        viewPager.setAdapter(adapter);
                        tabLayout.setupWithViewPager(viewPager);
                    }

                    StorageReference profilePicture = storageRef.child(user.getUsername()+".jpg");

                    picture = null;
                    profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            imageViewProfilePicture.setImageBitmap(Bitmap.createScaledBitmap(picture, 3000, 3000, false));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error while fetching data...", Toast.LENGTH_SHORT).show();

                }
            });

            editOrAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isFriends){
                        mDatabase.child("users").child(username)
                                .child("friends").child(StoredData.getInstance().user.getUsername())
                                .setValue(StoredData.getInstance().user.getUsername());
                        mDatabase.child("users").child(StoredData.getInstance().user.getUsername())
                                .child("friends").child(username).setValue(username);
                        StoredData.getInstance().getUser().addFriend(username);
                        Toast.makeText(getContext(),"Dodao",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mDatabase.child("users").child(username)
                                .child("friends").child(StoredData.getInstance().user.getUsername()).removeValue();
                        mDatabase.child("users").child(StoredData.getInstance().user.getUsername())
                                .child("friends").child(username).removeValue();
                        StoredData.getInstance().getUser().removeFriend(username);
                        Toast.makeText(getContext(),"Obrisao",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        // gledamo svoj profil
        else {


            if (StoredData.getInstance().getUser() != null) {
                imageViewProfilePicture.setImageBitmap(StoredData.getInstance().user.getProfilePhoto());
                tvFullName.setText(StoredData.getInstance().user.getFullName().toUpperCase());
                tvFriends.setText(getContext().getResources().getString(R.string.friends) +" " + StoredData.getInstance().getUser().getNumberOfFriends());
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
                    } else if (item.getItemId() == R.id.search_friends) {
                        ((StartActivity) getActivity()).setFragment(R.id.search_friends, null);

                    } else if (item.getItemId() == R.id.send_message) {
                        ((StartActivity) getActivity()).setFragment(R.id.send_message, null);

                    } else if (item.getItemId() == R.id.add_friends_bluetooth) {
                        ((StartActivity) getActivity()).setFragment(R.id.add_friends_bluetooth, null);
                    }
                    return false;
                }
            });
            editOrAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO otovri edit profile fragment
                }
            });

            fragmentRanking.setArguments(null);
            Bundle bundleFrends = new Bundle();
            bundleFrends.putStringArrayList("friends",StoredData.getInstance().getUser().getFriends());
            fragmentFriends.setArguments(bundleFrends);
            fragmentPhotos.setArguments(null);
            fragmentEvents.setArguments(null);
            adapter.addFragment(fragmentRanking, "Ranking");
            adapter.addFragment(fragmentFriends, "Friends");
            adapter.addFragment(fragmentPhotos, "Photos");
            adapter.addFragment(fragmentEvents, "Events");

            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }



        return view;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
