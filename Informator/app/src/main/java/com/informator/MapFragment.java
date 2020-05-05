package com.informator;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.*;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.informator.data.User;
import com.informator.data.UserWithPicture;
import com.informator.data.VirtualObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapFragment extends Fragment implements OnMapReadyCallback {



    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location current_user_location;

    private float current_zoom=12;
    private ArrayList<VirtualObject> virtualObjects;
    private HashMap<Marker,String> markerPlaceIdMap;

    static final int PERMISSION_ACCESS_FINE_LOCATION=1;
    static final int  CAMERA_PERMISSION = 2;
    static final int  REQUEST_IMAGE_CAPTURE = 3;
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
    Bitmap bitmap;



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

        firebaseStorage=FirebaseStorage.getInstance(StartActivity.url);
        storageReference=firebaseStorage.getReference();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        virtualObjects=new ArrayList<VirtualObject>();

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

        locationManager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                centreMapOnLocation(location);
                current_user_location=new Location(location);
                //addVirtualObjectMarkers();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



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


                double lat=current_user_location.getLatitude();
                double lon=current_user_location.getLongitude();
                final VirtualObject virtualObject=new VirtualObject(editTextTitle.getText().toString(),editTextDesc.getText().toString(),lat,lon);

                String key=databaseReference.child("users").push().getKey();
                virtualObject.setId(key);

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this.getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ACCESS_FINE_LOCATION);
        }
        else
        {
            mMap.setMyLocationEnabled(true);

            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location currentLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(currentLocation);
                showVirtualObjectsOnMap();
            }
            else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
                Location currentLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                centreMapOnLocation(currentLocation);
                showVirtualObjectsOnMap();
            }
            else
            {
                Toast.makeText(getActivity(),"Nije ukljucen ni gps provider ni network provider nemoguce je pronaci lokaciju",Toast.LENGTH_LONG).show();
                return;
            }


            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    current_zoom=mMap.getCameraPosition().zoom;
                }
            });

        }
    }

    private void centreMapOnLocation(Location location){
        LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
        //mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Current user location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,current_zoom));
    }

    //treba da pinuje na mapi sve virtuelne objekte i svoje i svojih prijatelja
    private void showVirtualObjectsOnMap(){
        databaseReference.child("users").child("").orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(StoredData.getInstance().getUser().getUsername()).child("virtual_objects").getValue()==null)
                    return;

                for(DataSnapshot dataSnapshot1: dataSnapshot.child((StoredData.getInstance().getUser().getUsername()))
                        .child("virtual_objects").getChildren()){
                    String id=dataSnapshot1.child("id").getValue().toString();
                    String description=dataSnapshot1.child("description").getValue().toString();

                    double lat=(Double) dataSnapshot1.child("latitude").getValue();
                    double lon=(Double)dataSnapshot1.child("longitude").getValue();
                    float rating=Float.parseFloat(dataSnapshot1.child("rating").getValue().toString());
                    String title=dataSnapshot1.child("title").getValue().toString();
                    VirtualObject virtualObject=new VirtualObject(title,description,lat,lon,rating);
                    virtualObject.setId(id);
                    StorageReference virtualObjectImage=storageReference.child(virtualObject.getId()+".jpg");
                    bitmap=null;
                    if(virtualObjectImage!=null){
                        virtualObjectImage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            }


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(),"Neuspelo skidanje slike",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        //bitmap=null;
                    }
                    virtualObject.setVirtual_object_image(bitmap);


                    virtualObjects.add(virtualObject);
                }

                addVirtualObjectMarkers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addVirtualObjectMarkers(){
        markerPlaceIdMap=new HashMap<Marker, String>();

        for(int i=0;i<virtualObjects.size();i++){
            LatLng location=new LatLng(virtualObjects.get(i).getLatitude(),virtualObjects.get(i).getLongitude());
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(location);
            if(virtualObjects.get(i).getVirtual_object_image()!=null){
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(virtualObjects.get(i).getVirtual_object_image()));
            }
            else
            {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
            }
            markerOptions.title(virtualObjects.get(i).getTitle());
            Marker marker=mMap.addMarker(markerOptions);
            markerPlaceIdMap.put(marker,virtualObjects.get(i).getId());
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //logika za klik na marker
                Toast.makeText(getActivity(),"kliknuto na marker",Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    private Bitmap getResizedBitmap(Bitmap bitmap,int newWidth,int newHeight){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        float scaleWidth=((float)newWidth)/width;
        float scaleHeight=((float)newHeight)/height;

        Matrix matrix=new Matrix();
        Bitmap resizedBitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,false);
        bitmap.recycle();

        return resizedBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mMap.setMyLocationEnabled(true);
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

    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();
    }


}
