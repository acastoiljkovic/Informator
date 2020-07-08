package com.informator;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.informator.data.ListPostsInGroupAdapter;
import com.informator.data.ListUserAdapter;
import com.informator.data.MapPicturesWithName;
import com.informator.data.StoredData;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupFragment extends Fragment {

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    CircleImageView imageViewUserPhoto;
    EditText editTextPost;
    ImageButton imageButtonAddImage;
    Button buttonAddPostToGroup;
    ListView listViewPostsInGroup;
    Dialog popup_add_person_in_group;

    private ArrayList<String> postIds;
    private ArrayList<String> postByUsernames;
    private ArrayList<String> timeOfPosts ;
    private ArrayList<String> contentOfPosts;
    private ArrayList<Integer> numberOfComments;
    private ArrayList<String> flags;
    private MapPicturesWithName picturesOfPosts;
    private MapPicturesWithName picturesOfUser;
    private MapPicturesWithName pictureOfUserNotInGroup;
    private ArrayList<String> listSelectedUsers;
    ArrayList<String> groupMembers;
    private ArrayList<String> userNotInGroup;
    private boolean flag;
    ListUserAdapter listUserAdapter;

    static final int  CAMERA_PERMISSION = 0;
    static final int  REQUEST_IMAGE_CAPTURE = 1;

    Bitmap postPicture=null;
    String groupId;

    ListPostsInGroupAdapter listPostsInGroupAdapter;


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

        postIds=new ArrayList<>();
        postByUsernames=new ArrayList<>();
        timeOfPosts=new ArrayList<>();
        contentOfPosts=new ArrayList<>();
        numberOfComments=new ArrayList<>();
        flags=new ArrayList<>();
        groupMembers=new ArrayList<>();


        picturesOfPosts=new MapPicturesWithName();
        picturesOfUser=new MapPicturesWithName();

        imageViewUserPhoto=view.findViewById(R.id.user_image_group);
        editTextPost=view.findViewById(R.id.text_view_add_postInGroup);
        imageButtonAddImage=view.findViewById(R.id.add_photo_post);
        buttonAddPostToGroup=view.findViewById(R.id.add_post_in_group);
        listViewPostsInGroup=view.findViewById(R.id.listViewPostsInGroup);

        imageViewUserPhoto.setImageBitmap(StoredData.getInstance().getUser().getProfilePhoto());


        Bundle bundle=this.getArguments();
        groupId=bundle.getString("idGroup");

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_group_toolbar);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.add_person_in_group){
                    addPersonInGroup();

                }else if(item.getItemId()==R.id.leave_group){
                    leaveGroup();
                }
                return false;
            }
        });



        databaseReference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String groupName=dataSnapshot.child("groupName").getValue().toString();
                toolbar.setTitle(groupName);
                for(DataSnapshot ds1:dataSnapshot.child("groupMembers").getChildren()){
                    groupMembers.add(ds1.getValue().toString());
                }

                for(DataSnapshot ds:dataSnapshot.child("posts").getChildren()){
                    contentOfPosts.add(ds.child("content").getValue().toString());
                    timeOfPosts.add(ds.child("dateTime").getValue().toString());
                    String username=ds.child("postBy").getValue().toString();
                    postByUsernames.add(ds.child("postBy").getValue().toString());
                    final String idPost=ds.child("postId").getValue().toString();
                    postIds.add(ds.child("postId").getValue().toString());
                    numberOfComments.add(Integer.valueOf(ds.child("numberOfComments").getValue().toString()));

                    String hasPicture=ds.child("hasPicture").getValue().toString();
                    flags.add(hasPicture);

                    if(hasPicture.compareTo("true")==0){
                        StorageReference postImage=storageReference.child(ds.child("postId").getValue().toString()+".jpg");
                        if(postImage!=null){
                            postImage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                                    listPostsInGroupAdapter.notifyDataSetChanged();
                                    picturesOfPosts.add(bitmap,idPost);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    listPostsInGroupAdapter.notifyDataSetChanged();
                                    picturesOfPosts.add(null,idPost);
                                }
                            });
                        }

                    }
                    else
                    {
                        picturesOfPosts.add(null,idPost);
                    }

                    StorageReference userImage=storageReference.child(username+".jpg");
                    if(userImage!=null){
                        userImage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                listPostsInGroupAdapter.notifyDataSetChanged();
                                picturesOfUser.add(bitmap,idPost);



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                listPostsInGroupAdapter.notifyDataSetChanged();
                                picturesOfUser.add(null,idPost);
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listPostsInGroupAdapter=new ListPostsInGroupAdapter(getActivity(),postIds,postByUsernames,timeOfPosts,contentOfPosts,numberOfComments,
                picturesOfPosts,picturesOfUser,flags);

        imageButtonAddImage.setOnClickListener(new View.OnClickListener() {
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

        buttonAddPostToGroup.setOnClickListener(new View.OnClickListener() {
            String postHasPicture;
            boolean hasPic;
            @Override
            public void onClick(View v) {
                if(postPicture==null){
                    postHasPicture="false";
                    hasPic=false;
                }
                else
                {
                    postHasPicture="true";
                    hasPic=true;
                }

                final String postKey=databaseReference.child("groups").push().getKey();
                databaseReference.child("groups").child(groupId).child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        LocalDateTime dateTime=LocalDateTime.now();
                        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String formattedDate=dateTime.format(dateTimeFormatter);


                        databaseReference.child("groups").child(groupId).child("posts").child(postKey).child("postId")
                                .setValue(postKey);
                        databaseReference.child("groups").child(groupId).child("posts").child(postKey).child("postBy")
                                .setValue(StoredData.getInstance().getUser().getUsername());
                        databaseReference.child("groups").child(groupId).child("posts").child(postKey).child("numberOfComments")
                                .setValue(0);
                        databaseReference.child("groups").child(groupId).child("posts").child(postKey).child("content").setValue(editTextPost.getText().toString());
                        databaseReference.child("groups").child(groupId).child("posts").child(postKey).child("hasPicture").setValue(postHasPicture);
                        databaseReference.child("groups").child(groupId).child("posts").child(postKey).child("dateTime").setValue(formattedDate);

                        editTextPost.setText("");

                        if(hasPic){
                            StorageReference groupPostPicture=storageReference.child(postKey+".jpg");
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();

                            if(groupPostPicture!=null){
                                postPicture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();
                                UploadTask uploadTask = groupPostPicture.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Error while uploading picture to server : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        postPicture=null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(getActivity(), "Group post image successfully upload", Toast.LENGTH_SHORT).show();
                                        postPicture=null;
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
        });


        listViewPostsInGroup.setAdapter(listPostsInGroupAdapter);
        return view;
    }

    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    private boolean checkIfInGroup(String user){
        for(String groupMember:groupMembers){
            if(user.compareTo(groupMember)==0){
                return false;
            }
        }

        return true;
    }

    private void addPersonInGroup(){
        popup_add_person_in_group=new Dialog(this.getActivity());
        pictureOfUserNotInGroup=new MapPicturesWithName();
        userNotInGroup=new ArrayList<>();
        listSelectedUsers=new ArrayList<>();
        popup_add_person_in_group.setContentView(R.layout.popup_add_person_in_group);
        final ListView listView=popup_add_person_in_group.findViewById(R.id.list_view_friends_no_in_group);
        Button btnAddPersonInGroup=popup_add_person_in_group.findViewById(R.id.btn_add_person_in_group);

        for(String allUsers:StoredData.getInstance().getUser().getFriends()){
            if(checkIfInGroup(allUsers)){
                userNotInGroup.add(allUsers);
            }
        }

        listUserAdapter=new ListUserAdapter(getActivity(),userNotInGroup,pictureOfUserNotInGroup);
        for(final String user:userNotInGroup){
            StorageReference storageReference1=storageReference.child(user+".jpg");
            if(storageReference1!=null){
                storageReference1.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        listUserAdapter.notifyDataSetChanged();
                        pictureOfUserNotInGroup.add(bitmap,user);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listUserAdapter.notifyDataSetChanged();
                        pictureOfUserNotInGroup.add(null,user);
                    }
                });
            }
        }

        btnAddPersonInGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(final String user:listSelectedUsers){

                    groupMembers.add(user);

                    databaseReference.child("groups").child(groupId).child("groupMembers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            databaseReference.child("groups").child(groupId).child("groupMembers").child(user).setValue(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    databaseReference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            databaseReference.child("groups").child(groupId).child("groupMembers").child(user).setValue(user);
                            int numberOfMembers=Integer.valueOf(dataSnapshot.child("numberOfMembers").getValue().toString());
                            numberOfMembers++;
                            databaseReference.child("groups").child(groupId).child("numberOfMembers").setValue(numberOfMembers);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    databaseReference.child("users").child(user).child("groupsMember").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            databaseReference.child("users").child(user).child("groupsMember").child(groupId).setValue(groupId);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                popup_add_person_in_group.cancel();


            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(checkIfSelectedUser(StoredData.getInstance().getUser().getFriend(position))){
                    listView.getChildAt(position).setBackgroundColor(Color.WHITE);
                    listSelectedUsers.remove(StoredData.getInstance().getUser().getFriend(position));
                }
                else
                {
                    listView.getChildAt(position).setBackgroundColor(Color.GRAY);
                    listSelectedUsers.add(StoredData.getInstance().getUser().getFriend(position));
                }
            }
        });

        listView.setAdapter(listUserAdapter);
        popup_add_person_in_group.show();
    }

    private void leaveGroup(){
        databaseReference.child("groups").child(groupId).child("groupMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child("groups").child(groupId).child("groupMembers").child(StoredData.getInstance().getUser().getUsername()).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numberOfMembers=Integer.valueOf(dataSnapshot.child("numberOfMembers").getValue().toString());
                numberOfMembers--;
                databaseReference.child("groups").child(groupId).child("numberOfMembers").setValue(numberOfMembers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(StoredData.getInstance().getUser().getUsername()).child("groupsMember")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        databaseReference.child("users").child(StoredData.getInstance().getUser().getUsername()).child("groupsMember")
                                .child(groupId).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        ((StartActivity)getActivity()).setFragment(R.id.groups,null);
    }

    private boolean checkIfSelectedUser(String friendUsername){
        for(String username:listSelectedUsers){
            if(username.compareTo(friendUsername)==0){
                return true;
            }
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_IMAGE_CAPTURE){
            Bundle extras = data.getExtras();
            postPicture = (Bitmap) extras.get("data");

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
