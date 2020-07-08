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
    ListVirtualObjectsAdapter listVirtualObjectsAdapterMy;
    private ArrayList<VirtualObject> listVirtualObjects;

    TextView textViewListVirtualObjectFragment;

    ArrayList<String> titles1;
    ArrayList<Bitmap> images1;
    ArrayList<String> virtualObjectIds1;
    private int count;

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
        listVirtualObjects=new ArrayList<>();
        textViewListVirtualObjectFragment=view.findViewById(R.id.textViewListVirtualObjectFragment);
        final int[] positionCount = {0};

        titles1=new ArrayList<>();
        images1=new ArrayList<>();
        virtualObjectIds1=new ArrayList<>();
        count=0;

        final ArrayList<String> titles=new ArrayList<>();
        final ArrayList<Bitmap> images=new ArrayList<>();
        final ArrayList<String> virtualObjectsIds=new ArrayList<>();
        final ArrayList<VirtualObject> virtualObjects=new ArrayList<>();

        Bundle bundle=this.getArguments();
        final float radius=bundle.getFloat("radius");
        String searchInRadius=bundle.getString("search_in_radius");
        String sort=bundle.getString("Sort");
        String filter=bundle.getString("Filter");

        if(searchInRadius!=null){
            float del=radius/1000;
            textViewListVirtualObjectFragment.setText("Virtual objects in radius of "+del+" km");
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
        }

        if(sort!=null && filter!=null){
            textViewListVirtualObjectFragment.setText("Sort by "+sort+" and filter by "+filter);
            getAllVirtualObjects();
            if(filter.compareTo("Rate Over 4.5")==0){
                filterRateOver();
            }
            else if(filter.compareTo("Food and drink places")==0){
                filterFoodAndDrinkPlaces();
            }
            else if(filter.compareTo("Other")==0){
                filterOther();
            }

            if(sort.compareTo("Rating")==0){
                sortByRating();
            }
            else if(sort.compareTo("Alphabetically")==0){
                sortByAlphabetically();
            }
            else if(sort.compareTo("Due date created")==0){
                sortByDate();
            }

            setListViewObjects();


        }
        else if(sort!=null){
            textViewListVirtualObjectFragment.setText("Sort by "+sort);
            getAllVirtualObjects();

            if(sort.compareTo("Rating")==0){
                sortByRating();
            }
            else if(sort.compareTo("Alphabetically")==0){
                sortByAlphabetically();
            }
            else if(sort.compareTo("Due date created")==0){
                sortByDate();
            }
            setListViewObjects();

        }
        else if(filter!=null){
            textViewListVirtualObjectFragment.setText("Filter by "+filter);

            getAllVirtualObjects();
            if(filter.compareTo("Rate Over 4.5")==0){
                filterRateOver();
            }
            else if(filter.compareTo("Food and drink places")==0){
                filterFoodAndDrinkPlaces();
            }
            else if(filter.compareTo("Other")==0){
                filterOther();
            }
            setListViewObjects();
        }





        return view;
    }

    private void setListViewObjects() {
        virtualObjectPictures = new MapPicturesWithName();
        virtualObjectIdMapPosition = new HashMap<>();
        listVirtualObjectsAdapterMy = new ListVirtualObjectsAdapter(getActivity(), titles1, images1, virtualObjectPictures);
        for (final VirtualObject virtualObject : listVirtualObjects) {
            titles1.add(virtualObject.getTitle());
            virtualObjectIds1.add(virtualObject.getId());
            virtualObjectIdMapPosition.put(virtualObject.getId(), count);
            count++;

            StorageReference virtualObjectImage = storageReference.child(virtualObject.getId() + ".jpg");

            if (virtualObjectImage != null) {
                virtualObjectImage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        images1.add(bitmap);
                        listVirtualObjectsAdapterMy.notifyDataSetChanged();
                        int pos = virtualObjectIdMapPosition.get(virtualObject.getId());
                        listVirtualObjects.get(pos).setVirtual_object_image(bitmap);
                        virtualObjectPictures.add(bitmap, virtualObject.getTitle());

                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        }

        listView.setAdapter(listVirtualObjectsAdapterMy);
    }

    private void sortByRating(){
        for(int i=0;i<listVirtualObjects.size()-1;i++){
            for(int j=i+1;j<listVirtualObjects.size();j++)
            {
                if(listVirtualObjects.get(i).getRating()<listVirtualObjects.get(j).getRating()){
                    VirtualObject pom=listVirtualObjects.get(i);
                    listVirtualObjects.set(i,listVirtualObjects.get(j));
                    listVirtualObjects.set(j,pom);
                }
            }
        }
    }

    private void sortByAlphabetically(){
        for(int i=0;i<listVirtualObjects.size()-1;i++){
            for(int j=i+1;j<listVirtualObjects.size();j++)
            {
                if(listVirtualObjects.get(i).getTitle().compareTo(listVirtualObjects.get(j).getTitle())>0){
                    VirtualObject pom=listVirtualObjects.get(i);
                    listVirtualObjects.set(i,listVirtualObjects.get(j));
                    listVirtualObjects.set(j,pom);
                }
            }
        }
    }

    private void sortByDate(){

    }

    private void getAllVirtualObjects(){
        for(VirtualObject virtualObject:StoredData.getInstance().getUser().getListVO()){
            listVirtualObjects.add(virtualObject);
        }

        for(String friendUsername:StoredData.getInstance().getUser().getFriends()){
            databaseReference.child("users").child(friendUsername).child("virtual_objects").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        VirtualObject virtualObject=dataSnapshot1.getValue(VirtualObject.class);
                        listVirtualObjects.add(virtualObject);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void filterRateOver(){
        for(VirtualObject virtualObject:listVirtualObjects){
            if(virtualObject.getRating()<4.5){
                listVirtualObjects.remove(virtualObject);
            }
        }
    }

    private void filterFoodAndDrinkPlaces(){
        for(VirtualObject virtualObject:listVirtualObjects){
            if(virtualObject.getTypeOfVirtualObject().compareTo("Food and drink places")!=0){
                listVirtualObjects.remove(virtualObject);
            }
        }
    }

    private void filterOther(){
        for(VirtualObject virtualObject:listVirtualObjects){
            if(virtualObject.getTypeOfVirtualObject().compareTo("Other")!=0){
                listVirtualObjects.remove(virtualObject);
            }
        }
    }

    private boolean checkIfInRadius(LatLng position,float radius){
        float[] distanceFromCurrentLocation =new float[1];

        Location.distanceBetween(
                StoredData.getInstance().getUser().getCurrentLocation().getLatitude(),
                StoredData.getInstance().getUser().getCurrentLocation().getLongitude(),
                position.latitude,
                position.longitude,
                distanceFromCurrentLocation
        );

        return !(distanceFromCurrentLocation[0] > radius);
    }


}
