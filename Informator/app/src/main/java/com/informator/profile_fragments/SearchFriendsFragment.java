package com.informator.profile_fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.informator.data.MapPicturesWithName;
import com.informator.data.Constants;
import com.informator.data.SearchFriendsListViewItem;
import com.informator.data.User;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchFriendsFragment extends Fragment {

    EditText etSearchFriends;
    ImageButton btnSearchFrineds;
    ListView listViewSearchFriends;
    ArrayList<String> fullname = null;
    ArrayList<String> usernames = null;
    MapPicturesWithName profileImages = null;
    SearchFriendsListViewItem adapter = null;
    ProgressDialog dialog;
    Timer timer;
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_friends,container,false);

        Initialize(view);

        listViewSearchFriends.setAdapter(adapter);

        listViewSearchFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(),"Username : "+usernames.get(position) ,Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("username",usernames.get(position));
                ((StartActivity)getActivity()).setFragment(R.id.profile,bundle);
            }
        });

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
                dialogShow();
                hideDialogAfter5sec();
            }
        });

        return view;

    }

    private void Initialize(View view){
        etSearchFriends = (EditText) view.findViewById(R.id.etSearchFriends);
        btnSearchFrineds = (ImageButton) view.findViewById(R.id.btnSearchFriends);
        listViewSearchFriends = (ListView) view.findViewById(R.id.listViewSearchFriends);

        fullname = new ArrayList<>();
        profileImages = new MapPicturesWithName();
        usernames = new ArrayList<>();
        adapter = new SearchFriendsListViewItem(getActivity(), fullname, usernames, profileImages);

        timer = new Timer();

        initDialog();

        try {
            database = FirebaseDatabase.getInstance();
            mDatabase = database.getReference();
            storage = FirebaseStorage.getInstance(Constants.URL_STORAGE);
            storageRef = storage.getReference();
        }
        catch (Exception e){
            Toast.makeText(getActivity(),"Database error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void initDialog(){
        dialog =new ProgressDialog(getContext());
        dialog.setTitle("Please Wait");
        dialog.setMessage("Searching...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
    }

    public void hideDialogAfter5sec(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialogDismiss();
            }
        }, 5*1000);
        return;
    }

    private void fetchDataFromDatabase(final String text){
        fullname.clear();
        usernames.clear();
        profileImages.clear();
        user = new User();
        mDatabase.child("users").child("").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    user.setUsername(String.valueOf(snapshot.child("username").getValue()));
                    user.setEmail(String.valueOf(snapshot.child("email").getValue()));
                    user.setFullName(String.valueOf(snapshot.child("fullName").getValue()));
                    if(user.getEmail().contains(text)){
                        fullname.add(user.getFullName());
                        usernames.add(user.getUsername());
                        final String fn = user.getUsername();
                        dialogHide();
                        StorageReference profilePicture = storageRef.child(user.getUsername()+".jpg");
                        profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                profileImages.add(picture,fn);
                                if(getActivity() != null) {
                                    adapter = new SearchFriendsListViewItem(getActivity(), fullname, usernames, profileImages);
                                    listViewSearchFriends.setAdapter(adapter);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                profileImages.add(Bitmap.createScaledBitmap(
                                        ProfileFragment.drawableToBitmap(getResources().getDrawable(R.drawable.ic_person_outline_black_24dp)),
                                        3000,
                                        3000,
                                        false), fn
                                );
                                if(getActivity() != null) {
                                    adapter = new SearchFriendsListViewItem(getActivity(), fullname, usernames, profileImages);
                                    listViewSearchFriends.setAdapter(adapter);
                                }
                            }
                        });
                    }
                    else if(user.getFullName().contains(text)){
                        fullname.add(user.getFullName());
                        usernames.add(user.getUsername());
                        final String fn = user.getUsername();
                        dialogHide();
                        StorageReference profilePicture = storageRef.child(user.getUsername()+".jpg");
                        profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                profileImages.add(picture,fn);
                                if(getActivity() != null) {
                                    adapter = new SearchFriendsListViewItem(getActivity(), fullname, usernames, profileImages);
                                    listViewSearchFriends.setAdapter(adapter);
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                profileImages.add(Bitmap.createScaledBitmap(
                                        ProfileFragment.drawableToBitmap(getResources().getDrawable(R.drawable.ic_person_outline_black_24dp)),
                                        3000,
                                        3000,
                                        false), fn
                                );
                                if(getActivity() != null) {
                                    adapter = new SearchFriendsListViewItem(getActivity(), fullname, usernames, profileImages);
                                    listViewSearchFriends.setAdapter(adapter);

                                }
                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialogHide();
            }
        });
    }


    public void dialogShow(){
        try {
            if (!dialog.isShowing())
                dialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dialogHide(){
        try {
            if (dialog.isShowing())
                dialog.hide();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dialogDismiss(){
        try{
            dialog.dismiss();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
