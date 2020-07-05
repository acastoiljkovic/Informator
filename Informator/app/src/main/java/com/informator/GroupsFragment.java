package com.informator;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.storage.UploadTask;
import com.informator.data.Constants;
import com.informator.data.ListGroupsAdapter;
import com.informator.data.ListUserAdapter;
import com.informator.data.ListVirtualObjectsAdapter;
import com.informator.data.MapPicturesWithName;
import com.informator.data.StoredData;
import com.informator.data.VirtualObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GroupsFragment extends Fragment {
    Toolbar toolbar;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    static final int  CAMERA_PERMISSION = 0;
    static final int  REQUEST_IMAGE_CAPTURE = 1;
    Dialog popup_add_group;
    Bitmap groupPicture;
    ListView listViewGroups;

    MapPicturesWithName friendsPicturesMap;
    private HashMap<String,Integer> friendsUsernameMapPosition;
    ArrayList<String> friendsUsernames;
    ArrayList<Bitmap> friendsPictures;
    ArrayList<String> listSelectedFriends;

    ArrayList<String> groupNames;
    ArrayList<String> groupIds;
    ArrayList<Integer> groupNumberOfMembers;
    MapPicturesWithName groupPictureMap;

    ListUserAdapter listFriendsAdapter;
    ListGroupsAdapter listGroupsAdapter;

    int positionCount;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups,container,false);

        firebaseStorage=FirebaseStorage.getInstance(Constants.URL_STORAGE);
        storageReference=firebaseStorage.getReference();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        listViewGroups=view.findViewById(R.id.list_view_groups);

        friendsPicturesMap=new MapPicturesWithName();
        friendsUsernameMapPosition=new HashMap<>();

        friendsUsernames=new ArrayList<>();
        friendsPictures=new ArrayList<>();

        groupNames=new ArrayList<>();
        groupIds=new ArrayList<>();
        groupNumberOfMembers=new ArrayList<>();
        groupPictureMap=new MapPicturesWithName();


        listGroupsAdapter=new ListGroupsAdapter(getActivity(),groupIds,groupNames,groupNumberOfMembers,groupPictureMap);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_groups_toolbar);


        Initialize(view);

        return view;
    }

    private void Initialize(View view){
        ((StartActivity)getActivity()).setBottomNavItemSelected(R.id.groups);

        toolbar = (Toolbar) view.findViewById(R.id.fragment_groups_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.add_group){
                    addGroup();
                }
                else if(item.getItemId()==R.id.find_group){

                }

                return false;
            }
        });


        databaseReference.child("users").child(StoredData.getInstance().getUser().getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("groupsMember").exists()){
                    for(DataSnapshot ds:dataSnapshot.child("groupsMember").getChildren()){
                        groupIds.add(ds.getValue().toString());
                    }

                    for(final String groupId:groupIds){
                        databaseReference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                groupNames.add(dataSnapshot.child("groupName").getValue().toString());
                                groupNumberOfMembers.add(Integer.valueOf(dataSnapshot.child("numberOfMembers").getValue().toString()));

                                StorageReference group_picture_reference=storageReference.child(groupId+".jpg");

                                if(group_picture_reference!=null){
                                    group_picture_reference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                            listGroupsAdapter.notifyDataSetChanged();
                                            groupPictureMap.add(bitmap,groupId);

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle=new Bundle();
                bundle.putString("idGroup",groupIds.get(position));
                ((StartActivity)getActivity()).setFragment(R.string.single_group,bundle);
            }
        });

        listViewGroups.setAdapter(listGroupsAdapter);


    }

    private void  addGroup(){

        positionCount=0;
        groupPicture=null;
        listSelectedFriends=new ArrayList<>();
        listFriendsAdapter=new ListUserAdapter(getActivity(),friendsUsernames,friendsPicturesMap);
        popup_add_group=new Dialog(this.getActivity());
        popup_add_group.setContentView(R.layout.popup_add_group);
        final EditText editTextTitle=popup_add_group.findViewById(R.id.group_name);
        final ListView listViewFriends=popup_add_group.findViewById(R.id.list_view_friends);
        Button buttonAddGroup=popup_add_group.findViewById(R.id.btn_add_group);
        final ImageButton imageButtonAddGroupPhoto=popup_add_group.findViewById(R.id.add_group_photo);

        imageButtonAddGroupPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION
                            );
                }
                else{
                    takePicture();
                }
            }
        });

        buttonAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupPicture==null){
                    imageButtonAddGroupPhoto.setBackgroundResource(R.drawable.button_red_border);
                    return;
                }

                final String key=databaseReference.child("groups").push().getKey();
                databaseReference.child("groups").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        databaseReference.child("groups").child(key).child("groupId").setValue(key);
                        databaseReference.child("groups").child(key).child("groupName").setValue(editTextTitle.getText().toString());

                        for(String frined:listSelectedFriends){
                            databaseReference.child("groups").child(key).child("groupMembers").child(frined).setValue(frined);
                            databaseReference.child("users").child(frined).child("groupsMember").child(key).setValue(key);
                        }
                        databaseReference.child("groups").child(key).child("numberOfMembers").setValue(listSelectedFriends.size()+1);

                        databaseReference.child("users").child(StoredData.getInstance().getUser().getUsername()).child("groupsMember").child(key).setValue(key);

                        StorageReference group_image_reference=storageReference.child(key+".jpg");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        if(group_image_reference!=null){
                            groupPicture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = group_image_reference.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error while uploading picture to server : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getActivity(), "Virtual object image successfully upload", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                popup_add_group.cancel();
            }
        });

        for(final String friendUsername: StoredData.getInstance().getUser().getFriends()){
            friendsUsernames.add(friendUsername);
            friendsUsernameMapPosition.put(friendUsername,positionCount);
            positionCount++;

            StorageReference friendPicture=storageReference.child(friendUsername+".jpg");

            if(friendPicture!=null){
                friendPicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        friendsPictures.add(bitmap);
                        listFriendsAdapter.notifyDataSetChanged();
                        int pos=friendsUsernameMapPosition.get(friendUsername);
                        //friendsPictures.set(pos,bitmap);
                        friendsPicturesMap.add(bitmap,friendUsername);

                    }
                });
            }
        }

        listViewFriends.setAdapter(listFriendsAdapter);

        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(checkIfSelectedFriend(StoredData.getInstance().getUser().getFriend(position))){
                    listViewFriends.getChildAt(position).setBackgroundColor(Color.WHITE);
                    listSelectedFriends.remove(StoredData.getInstance().getUser().getFriend(position));
                }
                else
                {
                    listViewFriends.getChildAt(position).setBackgroundColor(Color.GRAY);
                    listSelectedFriends.add(StoredData.getInstance().getUser().getFriend(position));
                }

            }
        });
        popup_add_group.show();




    }

    private boolean checkIfSelectedFriend(String friendUsername){
        for(String username:listSelectedFriends){
            if(username.compareTo(friendUsername)==0){
                return true;
            }
        }

        return false;
    }

    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_IMAGE_CAPTURE){
            Bundle extras = data.getExtras();
            groupPicture = (Bitmap) extras.get("data");

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
