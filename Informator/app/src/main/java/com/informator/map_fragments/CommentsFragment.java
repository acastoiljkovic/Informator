package com.informator.map_fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.informator.R;
import com.informator.StartActivity;
import com.informator.data.Constants;
import com.informator.data.ListVirtualObjectsAdapter;
import com.informator.data.MapPicturesWithName;
import com.informator.data.Post;
import com.informator.data.StoredData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CommentsFragment extends Fragment {

    ListView listViewComments;
    Button btnPost;
    EditText editTextWriteComment;

    int index;
    ArrayList<String> listUsernames;
    ArrayList<Bitmap> listImages;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    MapPicturesWithName commentMapToUserPicture;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_comments,container,false);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseStorage=FirebaseStorage.getInstance(Constants.URL_STORAGE);
        storageReference=firebaseStorage.getReference();

        listUsernames=new ArrayList<String>();
        listImages=new ArrayList<Bitmap>();
        commentMapToUserPicture=new MapPicturesWithName();

        Bundle bundle=this.getArguments();
        final String userRecommended=bundle.getString("userRecommendedName");
        final String virtualObjectName=bundle.getString("virtualObjectName");

        listViewComments=view.findViewById(R.id.list_view_comments);
        btnPost=view.findViewById(R.id.btn_add_comment);
        editTextWriteComment=view.findViewById(R.id.id_write_comment);

        final ListVirtualObjectsAdapter listVirtualObjectsAdapter=new ListVirtualObjectsAdapter(getActivity(),listUsernames,listImages,commentMapToUserPicture);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("users").child(userRecommended).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String key=databaseReference.child("users").push().getKey();
                                listUsernames.add(editTextWriteComment.getText().toString());
                                listImages.add(StoredData.getInstance().getUser().getProfilePhoto());
                                commentMapToUserPicture.add(StoredData.getInstance().getUser().getProfilePhoto(),editTextWriteComment.getText().toString());
                                listVirtualObjectsAdapter.notifyDataSetChanged();
                                Post post=new Post(StoredData.getInstance().getUser().getUsername(),editTextWriteComment.getText().toString());
                                databaseReference.child("users").child(userRecommended)
                                        .child("virtual_objects").child(virtualObjectName)
                                        .child("comments").child(key)
                                        .setValue(post);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
                //editTextWriteComment.setText("");
            }
        });


        for(final Post post: StoredData.getInstance().getVirtualObject().getPosts()){
            listUsernames.add(post.getPost());

            StorageReference user_comment_image_reference=storageReference.child(post.getUsername()+".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if(user_comment_image_reference!=null){
                user_comment_image_reference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        listImages.add(bitmap);
                        listVirtualObjectsAdapter.notifyDataSetChanged();
                        commentMapToUserPicture.add(bitmap,post.getPost());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }

        listViewComments.setAdapter(listVirtualObjectsAdapter);



        return view;
    }
}


