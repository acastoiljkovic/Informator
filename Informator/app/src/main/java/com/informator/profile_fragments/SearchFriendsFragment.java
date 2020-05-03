package com.informator.profile_fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.informator.R;
import com.informator.data.SearchFriendsListViewItem;
import com.informator.data.User;

import java.util.ArrayList;
import java.util.BitSet;

public class SearchFriendsFragment extends Fragment {

    EditText etSearchFriends;
    ImageButton btnSearchFrineds;
    ListView listViewSearchFriends;
    ArrayList<String> fullname = null;
    ArrayList<Bitmap> profileImages = null;
    SearchFriendsListViewItem adapter = null;

    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_friends,container,false);

        etSearchFriends = (EditText) view.findViewById(R.id.etSearchFriends);
        btnSearchFrineds = (ImageButton) view.findViewById(R.id.btnSearchFriends);
        listViewSearchFriends = (ListView) view.findViewById(R.id.listViewSearchFriends);

        fullname = new ArrayList<>();
        profileImages = new ArrayList<>();
        adapter = new SearchFriendsListViewItem(getActivity(), fullname, profileImages);

        listViewSearchFriends.setAdapter(adapter);

        try {
            database = FirebaseDatabase.getInstance();
            mDatabase = database.getReference();
            storage = FirebaseStorage.getInstance("gs://informator-b509e.appspot.com");
            storageRef = storage.getReference();
        }
        catch (Exception e){
            Toast.makeText(getActivity(),"Database error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        etSearchFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 2){
                    fetchDataFromDatabase(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnSearchFrineds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataFromDatabase(etSearchFriends.getText().toString());
            }
        });

        return view;

    }

    private void fetchDataFromDatabase(final String text){
        fullname.clear();
        profileImages.clear();
        mDatabase.child("users").child("").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    user = snapshot.getValue(User.class);
                    if(user.getEmail().contains(text)){
                        fullname.add(user.getFullName());
                        StorageReference profilePicture = storageRef.child(user.getUsername()+".jpg");
                        profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                profileImages.add(picture);
                                adapter = new SearchFriendsListViewItem(getActivity(), fullname, profileImages);
                                listViewSearchFriends.setAdapter(adapter);
                            }
                        });
                    }
                    else if(user.getFullName().contains(text)){
                        fullname.add(user.getFullName());
                        StorageReference profilePicture = storageRef.child(user.getUsername()+".jpg");
                        profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                profileImages.add(picture);
                                adapter = new SearchFriendsListViewItem(getActivity(), fullname, profileImages);
                                listViewSearchFriends.setAdapter(adapter);

                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
