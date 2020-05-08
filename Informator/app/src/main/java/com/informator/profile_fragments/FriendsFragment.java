package com.informator.profile_fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.informator.ProfileFragment;
import com.informator.R;
import com.informator.StartActivity;
import com.informator.data.Constants;
import com.informator.data.SearchFriendsListViewItem;
import com.informator.data.StoredData;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    TextView tvWelcome;
    ImageButton btnAddFriend;
    Boolean profile = false;
    ListView listFriends;
    ArrayList<String> friendsUsernames;
    ArrayList<String> friendsOfPersonFullNames;
    ArrayList<Bitmap> friendsOfPersonPictures;
    SearchFriendsListViewItem adapter = null;
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        final Bundle bundle = getArguments();

        listFriends = view.findViewById(R.id.list_friends);
        tvWelcome = (TextView)view.findViewById(R.id.tv_welcome_text_friends_fragment);
        btnAddFriend = view.findViewById(R.id.fragment_friends_add_friend_button);

        friendsOfPersonFullNames = new ArrayList<>();
        friendsOfPersonPictures = new ArrayList<>();
        friendsUsernames = new ArrayList<>();

        try {
            database = FirebaseDatabase.getInstance();
            mDatabase = database.getReference();
            storage = FirebaseStorage.getInstance(Constants.URL_STORAGE);
            storageRef = storage.getReference();
        }
        catch (Exception e){
            Toast.makeText(getActivity(),"Database error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        adapter = new SearchFriendsListViewItem(getActivity(),friendsOfPersonFullNames,friendsOfPersonPictures);
        listFriends.setAdapter(adapter);

        listFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("username",friendsUsernames.get(position));
                ((StartActivity)getActivity()).setFragment(R.id.profile,bundle);
            }
        });

        if(bundle != null) {
            profile = bundle.getBoolean("profile",false);
            friendsUsernames = bundle.getStringArrayList("friends");
        }

        if(!profile) {
            btnAddFriend.setVisibility(View.VISIBLE);
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((StartActivity) getActivity()).setFragment(R.id.search_friends, null);
                }
            });
            if(StoredData.getInstance().getUser().getNumberOfFriends() == 0) {
                tvWelcome.setText(R.string.no_friends);
            }
            else{
                friendsUsernames = StoredData.getInstance().getUser().getFriends();
            }
        }
        else {
            btnAddFriend.setVisibility(View.GONE);

        }

        if(friendsUsernames.size() <= 0) {
            tvWelcome.setText(R.string.no_friends);
        }
        else{
            tvWelcome.setVisibility(View.GONE);
        }
        for(String friend : friendsUsernames){
            mDatabase.child("users").child(friend).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null){
                        friendsOfPersonFullNames.add(String.valueOf(dataSnapshot.child("fullName").getValue()));
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            StorageReference profilePicture = storageRef.child(friend+".jpg");
            profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    friendsOfPersonPictures.add(picture);
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    friendsOfPersonPictures.add(Bitmap.createScaledBitmap(
                            ProfileFragment.drawableToBitmap(getResources().getDrawable(R.drawable.ic_person_outline_black_24dp)),
                            3000,
                            3000,
                            false)
                    );
                    adapter.notifyDataSetChanged();
                }
            });
        }

        return  view;
    }


}
