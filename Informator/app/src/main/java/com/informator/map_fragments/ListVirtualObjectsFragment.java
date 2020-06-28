package com.informator.map_fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
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
import com.informator.R;
import com.informator.StartActivity;
import com.informator.data.Constants;
import com.informator.data.ListVirtualObjectsAdapter;
import com.informator.data.MapPicturesWithName;
import com.informator.data.Post;
import com.informator.data.SearchFriendsListViewItem;
import com.informator.data.StoredData;
import com.informator.data.VirtualObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListVirtualObjectsFragment extends Fragment {

    ListView listView;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private HashMap<String,Integer> virtualObjectIdMapPosition;
    MapPicturesWithName virtualObjectPictures;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_virual_objects,container,false);

        listView=view.findViewById(R.id.list_virtual_objects);
        firebaseStorage= FirebaseStorage.getInstance(Constants.URL_STORAGE);
        firebaseDatabase=FirebaseDatabase.getInstance();
        storageReference=firebaseStorage.getReference();
        databaseReference=firebaseDatabase.getReference();
        virtualObjectIdMapPosition=new HashMap<>();
        virtualObjectPictures = new MapPicturesWithName();
        final int[] positionCount = {0};

        final ArrayList<String> titles=new ArrayList<>();
        final ArrayList<Bitmap> images=new ArrayList<>();
        final ArrayList<String> virtualObjectsIds=new ArrayList<>();
        final ArrayList<VirtualObject> virtualObjects=new ArrayList<>();

        Bundle bundle=this.getArguments();
        final float radius=bundle.getFloat("radius");
        Toast.makeText(getActivity(),String.valueOf(radius),Toast.LENGTH_LONG).show();
        final ListVirtualObjectsAdapter listVirtualObjectsAdapter=new ListVirtualObjectsAdapter(getActivity(),titles,images,virtualObjectPictures);


        //preuzima moje virtuelne objekte i proverva da li su u radiusu
        for(final VirtualObject virtualObject:StoredData.getInstance().getUser().getListVO()){
            if(checkIfInRadius(new LatLng(virtualObject.getLatitude(),virtualObject.getLongitude()),radius))
            {
                titles.add(virtualObject.getTitle());
                virtualObjectsIds.add(virtualObject.getId());
                virtualObjects.add(virtualObject);
                virtualObjectIdMapPosition.put(virtualObject.getId(), positionCount[0]);
                positionCount[0]++;

                if(virtualObject.getVirtual_object_image()!=null){
                    images.add(virtualObject.getVirtual_object_image());
                }
                else
                {

                    StorageReference virtualObjectImage=storageReference.child(virtualObject.getId()+".jpg");

                    if(virtualObjectImage!=null){
                        virtualObjectImage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                images.add(bitmap);
                                listVirtualObjectsAdapter.notifyDataSetChanged();
                                StoredData.getInstance().getUser().setVirtualObjectWithId(virtualObject.getId(),bitmap);
                                int pos=virtualObjectIdMapPosition.get(virtualObject.getId());
                                virtualObjects.get(pos).setVirtual_object_image(bitmap);
                                virtualObjectPictures.add(bitmap,virtualObject.getTitle());

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

        //preuzima virtuelne objekte prijatelja i proverava da li su u radijusu
        for(final String friendUsername:StoredData.getInstance().getUser().getFriends()){
            databaseReference.child("users").child(friendUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("virtual_objects")!=null){
                        DataSnapshot ds1=dataSnapshot.child("virtual_objects");
                        for(DataSnapshot ds:ds1.getChildren()){
                            if(checkIfInRadius(new LatLng(Double.valueOf(ds.child("latitude").getValue().toString())
                                    ,Double.valueOf(ds.child("longitude").getValue().toString())),radius)){
                                titles.add(ds.child("title").getValue().toString());
                                virtualObjectsIds.add(ds.child("id").getValue().toString());


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

                                virtualObjects.add(vo);
                                virtualObjectIdMapPosition.put(vo.getId(), positionCount[0]);
                                positionCount[0]++;

                                StorageReference virtualObjectImage=storageReference.child(ds.child("id").getValue()+".jpg");

                                if(virtualObjectImage!=null){
                                    virtualObjectImage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                            images.add(bitmap);
                                            listVirtualObjectsAdapter.notifyDataSetChanged();
                                            vo.setVirtual_object_image(bitmap);
                                            int pos=virtualObjectIdMapPosition.get(vo.getId());
                                            virtualObjects.get(pos).setVirtual_object_image(bitmap);
                                            virtualObjectPictures.add(bitmap,vo.getTitle());

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

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle=new Bundle();
                bundle.putString("idVirtualObject",virtualObjectsIds.get(position));
                StoredData.getInstance().setVirtualObject(virtualObjects.get(position));
                ((StartActivity)getActivity()).setFragment(R.string.virtualObjectId,bundle);
            }
        });



        listView.setAdapter(listVirtualObjectsAdapter);



        return view;
    }

    private boolean checkIfInRadius(LatLng position,float radius){
        float distanceFromCurrentLocation[]=new float[1];

        Location.distanceBetween(
                StoredData.getInstance().getUser().getCurrentLocation().getLatitude(),
                StoredData.getInstance().getUser().getCurrentLocation().getLongitude(),
                position.latitude,
                position.longitude,
                distanceFromCurrentLocation
        );

        if(distanceFromCurrentLocation[0] > radius)
            return false;
        else
            return true;
    }


}
