package com.informator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.informator.data.Constants;
import com.informator.data.StoredData;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupFragment extends Fragment {

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    CircleImageView imageViewUserPhoto;
    TextView textViewPost;
    ImageButton imageButtonAddImage;
    Button buttonAddPostToGroup;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_group,container,false);
        firebaseStorage=FirebaseStorage.getInstance(Constants.URL_STORAGE);
        storageReference=firebaseStorage.getReference();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        imageViewUserPhoto=view.findViewById(R.id.user_image_group);

        imageViewUserPhoto.setImageBitmap(StoredData.getInstance().getUser().getProfilePhoto());


        Bundle bundle=this.getArguments();
        final String groupId=bundle.getString("idGroup");

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_group_toolbar);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return false;
            }
        });

        databaseReference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String groupName=dataSnapshot.child("groupName").getValue().toString();
                toolbar.setTitle(groupName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }
}
