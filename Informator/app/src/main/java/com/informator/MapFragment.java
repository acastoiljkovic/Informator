package com.informator;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
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
import com.informator.data.Constants;
import com.informator.data.Post;
import com.informator.data.StoredData;
import com.informator.data.VirtualObject;
import com.informator.map_fragments.VirtualObjectFragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {



    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location current_user_location;

    private HashMap<String,Integer> virtualObjectIdMapPosition;

    private float current_zoom=12;
    public static ArrayList<VirtualObject> virtualObjects;
    private HashMap<Marker,String> markerPlaceIdMap;

    static final int PERMISSION_ACCESS_FINE_LOCATION=1;
    static final int  CAMERA_PERMISSION = 2;
    static final int  REQUEST_IMAGE_CAPTURE = 3;
    Toolbar toolbar;
    Dialog popup_add_virtual_object;


    Bitmap virtual_object_image;
    ImageButton imgButton;
    Button createButton;
    ProgressDialog dialog;

    FirebaseAuth firebaseAuth = null;
    FirebaseStorage firebaseStorage;  //za slike
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase; //za podatke
    DatabaseReference databaseReference;

    EditText editTextDesc;
    EditText editTextTitle;
    Bitmap bitmap;
    ArrayList<VirtualObject> listPinnedVirtualObjects;

    private float currentZoom;
    private float radius;


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

        firebaseStorage=FirebaseStorage.getInstance(Constants.URL_STORAGE);
        storageReference=firebaseStorage.getReference();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        virtualObjects=new ArrayList<VirtualObject>();

        dialog =new ProgressDialog(getActivity());
        dialog.setTitle("Please Wait");
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        popup_add_virtual_object=new Dialog(this.getActivity());
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_map_toolbar);
        currentZoom = 12;

        toolbar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.add_virtual_object_on_map){
                    showPopup();

                }else if(item.getItemId()==R.id.search_on_map){
                    
                }
                else if(item.getItemId()==R.id.search_in_radius){
                    Bundle bundle=new Bundle();
                    bundle.putFloat("radius",radius);
                    ((StartActivity)getActivity()).setFragment(R.string.open_listVO,bundle);
                }
                else if(item.getItemId()==R.id.id_show_virtual_objects){
                    getVirtualObjects();
                    for(VirtualObject virtualObject:listPinnedVirtualObjects){
                        addVirtualObjectMarker(virtualObject);
                    }
                }
                else if(item.getItemId()==R.id.id_show_online_friends){
                    //prikazuje u realnom vremenu trenutne lokacije svih prijatelja
                }
                else if(item.getItemId()==R.id.id_show_default){
                    mMap.clear();
                    //defaultni prikaz eventa i groupa na mapi ili prazno videcemo
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
                StoredData.getInstance().getUser().addCurrentLocaation(current_user_location);
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
                current_user_location=currentLocation;
                StoredData.getInstance().getUser().addCurrentLocaation(current_user_location);
                centreMapOnLocation(currentLocation);
                calculateVisibleRegionRadius();
                //showVirtualObjectsOnMap();
            }
            else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
                Location currentLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                current_user_location=currentLocation;
                StoredData.getInstance().getUser().addCurrentLocaation(current_user_location);
                centreMapOnLocation(currentLocation);
                calculateVisibleRegionRadius();
                //showVirtualObjectsOnMap();
            }
            else
            {
                Toast.makeText(getActivity(),"Nije ukljucen ni gps provider ni network provider nemoguce je pronaci lokaciju",Toast.LENGTH_LONG).show();
                return;
            }


            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override

//                public void onMyLocationChange(Location location) {
//                    if(location!=null){
//                        //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("blblblb"));
//                        CameraPosition cp = CameraPosition.builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(currentZoom).bearing(0).build();
//                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
//                    }

                public void onCameraMove() {
                    current_zoom=mMap.getCameraPosition().zoom;
                    radius=calculateVisibleRegionRadius();
                    //Toast.makeText(getActivity(),String.valueOf(radius),Toast.LENGTH_LONG).show();

                }
            });

        }
    }

    private float calculateVisibleRegionRadius(){
        VisibleRegion visibleRegion=mMap.getProjection().getVisibleRegion();

        LatLng topLeft=visibleRegion.farLeft;
        LatLng bottomRight=visibleRegion.nearRight;

        float[] diagonalDistance=new float[1];

        Location.distanceBetween(
                topLeft.latitude,
                topLeft.longitude,
                bottomRight.latitude,
                bottomRight.longitude,
                diagonalDistance
        );

        this.radius=diagonalDistance[0]/2;

        return diagonalDistance[0]/2;

    }

    private void centreMapOnLocation(Location location){

        if(location!=null){
            LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
            //mMap.clear();
            //mMap.addMarker(new MarkerOptions().position(userLocation).title("Current user location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,current_zoom));
        }

        return;
    }


    private void getVirtualObjects() {
        virtualObjectIdMapPosition=new HashMap<>();
        final int[] positionCount = {0};
        listPinnedVirtualObjects=new ArrayList<>();
        markerPlaceIdMap=new HashMap<Marker, String>();
        for(final VirtualObject virtualObject:StoredData.getInstance().getUser().getListVO()){


            if(virtualObject.getVirtual_object_image()!=null){
                listPinnedVirtualObjects.add(virtualObject);
                virtualObjectIdMapPosition.put(virtualObject.getId(), positionCount[0]);
                positionCount[0]++;
            }
            else
            {
                listPinnedVirtualObjects.add(virtualObject);
                virtualObjectIdMapPosition.put(virtualObject.getId(), positionCount[0]);
                positionCount[0]++;
                StorageReference virtualObjectImage=storageReference.child(virtualObject.getId()+".jpg");

                if(virtualObjectImage!=null){
                    virtualObjectImage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            StoredData.getInstance().getUser().setVirtualObjectWithId(virtualObject.getId(),bitmap);
                            int pos=virtualObjectIdMapPosition.get(virtualObject.getId());
                            listPinnedVirtualObjects.get(pos).setVirtual_object_image(bitmap);
                            notifyDownloadPictureForVirtualObject();

                        }


                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }

        }

        for(final String friendUsername:StoredData.getInstance().getUser().getFriends()){
            databaseReference.child("users").child(friendUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("virtual_objects")!=null){
                        DataSnapshot ds1=dataSnapshot.child("virtual_objects");
                        for(DataSnapshot ds:ds1.getChildren()){

                            final VirtualObject vo=new VirtualObject();
                            vo.setDescription(ds.child("description").getValue().toString());
                            vo.setId(ds.child("id").getValue().toString());
                            vo.setLatitude(Double.valueOf(ds.child("latitude").getValue().toString()));
                            vo.setLongitude(Double.valueOf(ds.child("longitude").getValue().toString()));
                            vo.setNumberOfRates(Integer.valueOf(ds.child("numberOfRates").getValue().toString()));
                            vo.setRating(Float.valueOf(ds.child("rating").getValue().toString()));
                            vo.setTitle(ds.child("title").getValue().toString());
                            vo.setUserRecommended(friendUsername);

                            for(DataSnapshot dataSnapshot1:ds.child("comments").getChildren()){
                                vo.getPosts().add(new Post(dataSnapshot1.child("username").getValue().toString(),dataSnapshot1.child("post").getValue().toString()));
                            }

                            listPinnedVirtualObjects.add(vo);
                            virtualObjectIdMapPosition.put(vo.getId(), positionCount[0]);


                            StorageReference virtualObjectImage=storageReference.child(ds.child("id").getValue()+".jpg");

                            if(virtualObjectImage!=null){
                                virtualObjectImage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        vo.setVirtual_object_image(bitmap);
                                        int pos=virtualObjectIdMapPosition.get(vo.getId());
                                        listPinnedVirtualObjects.get(pos).setVirtual_object_image(bitmap);
                                        notifyDownloadPictureForVirtualObject();

                                    }


                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            }
                        }


                        }

                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    //posto treba vremena da se skinu slike objekata u trenutku kada se skinu poziva se ova funkcija i postavlja na markere sliku inace je neka defaultna slika
    public void notifyDownloadPictureForVirtualObject(){
        mMap.clear();
        for(VirtualObject virtualObject:listPinnedVirtualObjects){
            addVirtualObjectMarker(virtualObject);
        }
    }


    private void addVirtualObjectMarker(VirtualObject virtualObject){
        LatLng location=new LatLng(virtualObject.getLatitude(),virtualObject.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(location);
        if(virtualObject.getVirtual_object_image()!=null){
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(virtualObject.getVirtual_object_image(),100,100,false)));
        }
        else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
        }
        markerOptions.title(virtualObject.getTitle());
        Marker marker=mMap.addMarker(markerOptions);
        markerPlaceIdMap.put(marker,virtualObject.getId());


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                String idVirtualObject=markerPlaceIdMap.get(marker);
                int pos=0;

                while(listPinnedVirtualObjects.get(pos).getId().compareTo(idVirtualObject)!=0){
                    pos++;
                }
                StoredData.getInstance().setVirtualObject(listPinnedVirtualObjects.get(pos));
                //logika za klik na marker
                Bundle result=new Bundle();
                result.putString("idVirtualObject",markerPlaceIdMap.get(marker));

                ((StartActivity) getActivity()).setFragment(R.string.virtualObjectId,result);

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
