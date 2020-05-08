package com.informator.profile_fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.informator.MainActivity;
import com.informator.ProfileFragment;
import com.informator.R;
import com.informator.RegisterActivity;
import com.informator.StartActivity;
import com.informator.data.Constants;
import com.informator.data.PhotosListViewItem;
import com.informator.data.SearchFriendsListViewItem;
import com.informator.data.StoredData;
import com.informator.data.UserWithPicture;

import java.util.ArrayList;

public class PhotosFragment extends Fragment {
    TextView tvWelcome;
    Boolean profile;
    ImageButton addPhotoCamera;
    ImageButton addPhotoGallery;
    ListView listViewPhoytos;
    ArrayList<Bitmap> images;
    PhotosListViewItem adapter = null;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profile = false;
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        Bundle bundle = getArguments();

        listViewPhoytos = view.findViewById(R.id.list_photos);
        images = new ArrayList<>();

        adapter = new PhotosListViewItem(getActivity(),images);
        listViewPhoytos.setAdapter(adapter);

        try {
            storage = FirebaseStorage.getInstance(Constants.URL_STORAGE);
            storageRef = storage.getReference();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if(bundle != null) {
            profile = bundle.getBoolean("profile");
        }

        addPhotoCamera = view.findViewById(R.id.fragment_photos_add_photo_camera_button);
        addPhotoGallery = view.findViewById(R.id.fragment_photos_add_photo_gallery_button);
        if(!profile) {
            addPhotoCamera.setVisibility(View.VISIBLE);
            addPhotoGallery.setVisibility(View.VISIBLE);
            addPhotoCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                Constants.CAMERA_PERMISSION);
                    }
                    else{
                        ((StartActivity)getActivity()).dispatchTakePictureIntent();
                    }
                }
            });
            addPhotoGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                Constants.STORAGE_PERMISSION);
                    }
                    else {
                        ((StartActivity)getActivity()).openImagePicker();
                    }
                }
            });
        }
        else {
            addPhotoCamera.setVisibility(View.GONE);
            addPhotoGallery.setVisibility(View.GONE);
        }
        tvWelcome = (TextView)view.findViewById(R.id.tv_welcome_text_photos_fragment);

        StorageReference imagesOfUser = storageRef.child("images").child(StoredData.getInstance().getUser().getUsername());
        imagesOfUser.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if(listResult.getItems().size() <= 0){
                    tvWelcome.setText(R.string.no_photos);
                }
                else{
                    tvWelcome.setVisibility(View.GONE);
                    for(int i =0;i<listResult.getItems().size()-1;i++) {
                        listResult.getItems().get(i).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                images.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                adapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Error while fetching photos",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        //TODO provera da li mu prosledjujeo slike
        tvWelcome.setText(R.string.no_photos);

        return  view;
    }
}
