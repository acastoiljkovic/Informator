package com.informator;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.storage.UploadTask;
import com.informator.data.StoredData;
import com.informator.data.VirtualObject;

import java.io.ByteArrayOutputStream;


public class MapFragment extends Fragment implements OnMapReadyCallback {



    private GoogleMap mMap;
    static final int PERMISSION_ACCESS_FINE_LOCATION=1;
    static final int  CAMERA_PERMISSION = 2;
    static final int  REQUEST_IMAGE_CAPTURE = 3;
    LocationManager locationManager;
    Toolbar toolbar;
    Dialog popup_add_virtual_object;


    Bitmap virtual_object_image;
    ImageButton imgButton;
    Button createButton;

    FirebaseAuth firebaseAuth = null;
    FirebaseStorage firebaseStorage;  //za slike
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase; //za podatke
    DatabaseReference databaseReference;

    EditText editTextDesc;
    EditText editTextTitle;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map,container,false);

        popup_add_virtual_object=new Dialog(this.getActivity());
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_map_toolbar);


        toolbar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.add_virtual_object_on_map){
                    showPopup();

                }else if(item.getItemId()==R.id.search_on_map){
                    
                }
                return false;
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(MapFragment.this);

        return view;
    }

    private void showPopup(){
        popup_add_virtual_object.setContentView(R.layout.popup_add_virtual_object);
        imgButton=(ImageButton) popup_add_virtual_object.findViewById(R.id.add_photo_forVO);
        createButton=(Button) popup_add_virtual_object.findViewById(R.id.btn_create_VO);
        editTextTitle=(EditText) popup_add_virtual_object.findViewById(R.id.title_edit_text);
        editTextDesc=(EditText) popup_add_virtual_object.findViewById(R.id.edit_text_desc);

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION);
                }
                else{
                    takePicture();
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(virtual_object_image==null){
                    imgButton.setBackgroundResource(R.drawable.button_red_border);
                    return;
                }

                firebaseStorage=FirebaseStorage.getInstance(StartActivity.url);
                storageReference=firebaseStorage.getReference();
                firebaseDatabase=FirebaseDatabase.getInstance();
                databaseReference=firebaseDatabase.getReference();
                firebaseAuth = FirebaseAuth.getInstance();
                final VirtualObject virtualObject=new VirtualObject(editTextTitle.getText().toString(),editTextDesc.getText().toString());
                virtualObject.setId(firebaseAuth.getUid());

                final Bitmap image=virtual_object_image;

                databaseReference.child("users").child(StoredData.getInstance().user.getUsername())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        databaseReference.child("users").child(StoredData.getInstance().user.getUsername())
                                .child("virtual_objects").child(editTextTitle.getText().toString())
                                .setValue(virtualObject);

                        StorageReference virtual_object_image_reference = storageReference.child(virtualObject.getId()+".jpg");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        if(virtual_object_image!=null){
                            virtual_object_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = virtual_object_image_reference.putBytes(data);
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
            }
        });
        popup_add_virtual_object.show();
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
            virtual_object_image = (Bitmap) extras.get("data");
            Toast.makeText(getActivity(), "Image Added!", Toast.LENGTH_SHORT).show();
            imgButton.setBackgroundResource(R.drawable.button_green_border );

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this.getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ACCESS_FINE_LOCATION);
        }
        else
        {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if(location!=null){
                        //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("blblblb"));
                        CameraPosition cp = CameraPosition.builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).bearing(0).build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                    }
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mMap.setMyLocationEnabled(true);
                    mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            if(location!=null){
                                //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("blblblb"));
                                CameraPosition cp = CameraPosition.builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(10).bearing(0).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                            }
                        }
                    });

                }
                return;
            }
            case CAMERA_PERMISSION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {

                    Toast.makeText(getActivity(), "Permission denied to access to your Camera", Toast.LENGTH_SHORT).show();
                }
                return;

            }
        }
    }

}
