package com.informator.event_fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
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
import com.informator.data.Constants;
import com.informator.data.Event;
import com.informator.data.InviteFriendsListViewItem;
import com.informator.data.MapPicturesWithName;
import com.informator.data.SearchFriendsListViewItem;
import com.informator.data.StoredData;

import java.util.ArrayList;

public class AddEventFragment extends Fragment {

    Toolbar toolbar;
    EditText editTitle;
    DatePicker datePicker;
    TimePicker timePicker;
    Switch notifications;
    LinearLayout inviteFrinedsClck;
    LinearLayout addLocationClick;
    Button btnAdd;
    Event event;
    Dialog addLocationDialog;
    Dialog inviteFriendsDialog;
    GoogleMap mMap;
    Location location;
    InviteFriendsListViewItem adapter = null;
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;
    ListView listFriends;
    ArrayList<String> friendsUsernamesForView;
    ArrayList<String> friendsOfPersonFullNames;
    MapPicturesWithName friendsOfPersonPictures;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event,container,false);

        Initialize(view);

        return view;
    }

    private void Initialize(View view){

        event = new Event();

        toolbar = view.findViewById(R.id.fragment_add_event_toolbar);
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        editTitle = view.findViewById(R.id.editTextTitle);
        datePicker = view.findViewById(R.id.event_date_pick);
        timePicker = view.findViewById(R.id.event_time_pick);
        notifications = view.findViewById(R.id.switch_notifications);

        inviteFrinedsClck = view.findViewById(R.id.invite_friend_click);
        addLocationClick = view.findViewById(R.id.add_location_click);

        btnAdd = view.findViewById(R.id.button_add_event);

        addLocationDialog = new Dialog(this.getActivity());
        inviteFriendsDialog = new Dialog(this.getActivity());

        notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setNotifications(b);
            }
        });

        inviteFrinedsClck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteFriends();
            }
        });

        addLocationClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocation();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                addEvent();
            }
        });
    }

    private void dispatchAddLocationDialog(){
        addLocationDialog.setContentView(R.layout.popup_add_location);
        Button add = addLocationDialog.findViewById(R.id.btnAdd);
        Button cancel = addLocationDialog.findViewById(R.id.btnCancel);

        MapView mapView = addLocationDialog.findViewById(R.id.mapFragAddLocation);
        mapView.onCreate(addLocationDialog.onSaveInstanceState());
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if(location != null ){
                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),10));
                }
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.clear();
                        location = new Location("locEvent");
                        location.setLongitude(latLng.longitude);
                        location.setLatitude(latLng.latitude);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Location"));
                        if(mMap.getCameraPosition().zoom < 10){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),10));
                        }
                    }
                });
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location != null) {
                    event.setLatitude(location.getLatitude());
                    event.setLongitude(location.getLongitude());
                    addLocationDialog.cancel();
                    Toast.makeText(getActivity(),"Location added !",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(),"Location have to be selected !!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocationDialog.cancel();
            }
        });

        addLocationDialog.show();
    }


    private void dispatchInviteFriendsDialog() {
        inviteFriendsDialog.setContentView(R.layout.popup_invite_friends);

        try {
            database = FirebaseDatabase.getInstance();
            mDatabase = database.getReference();
            storage = FirebaseStorage.getInstance(Constants.URL_STORAGE);
            storageRef = storage.getReference();
        }
        catch (Exception e){
            Toast.makeText(getActivity(),"Database error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        inviteFriendsDialog.findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Friends invited !",Toast.LENGTH_SHORT).show();
                inviteFriendsDialog.cancel();
            }
        });


        listFriends = inviteFriendsDialog.findViewById(R.id.listViewInvFriends);

        friendsOfPersonFullNames = new ArrayList<>();
        friendsOfPersonPictures = new MapPicturesWithName();
        friendsUsernamesForView = new ArrayList<>();


        listFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                event.getInvitedFriends().add(StoredData.getInstance().user.getFriend(i));
                adapter.notifyDataSetChanged();
            }
        });


        adapter = new InviteFriendsListViewItem(getActivity(),friendsOfPersonFullNames,friendsUsernamesForView,event.getInvitedFriends(),friendsOfPersonPictures);

        listFriends.setAdapter(adapter);
        for(String friend : StoredData.getInstance().user.getFriends()) {

            final String fUser = friend;

            mDatabase.child("users").child(friend).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        friendsOfPersonFullNames.add(String.valueOf(dataSnapshot.child("fullName").getValue()));
                        friendsUsernamesForView.add(fUser);
                        adapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            StorageReference profilePicture = storageRef.child(fUser + ".jpg");
            profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {


                    Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    friendsOfPersonPictures.add(picture, fUser);
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    friendsOfPersonPictures.add(Bitmap.createScaledBitmap(
                            ProfileFragment.drawableToBitmap(getResources().getDrawable(R.drawable.ic_person_outline_black_24dp)),
                            3000,
                            3000,
                            false), fUser
                    );
                    adapter.notifyDataSetChanged();
                }
            });
        }

        inviteFriendsDialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addEvent(){
        String title = editTitle.getText().toString();

        if(title.isEmpty())
            title = "untitled";
        event.setTitle(title);

        event.setOwner(StoredData.getInstance().user.getUsername());

        String dateTime = (datePicker.getMonth() + 1) + "/"+
                datePicker.getDayOfMonth()+"/"+datePicker.getYear()+" "+
                timePicker.getHour()+":"+timePicker.getMinute()+":00";
        event.setDatetime(dateTime);
        String key=FirebaseDatabase.getInstance().getReference().child("users").push().getKey();
        event.setId(key);
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(StoredData.getInstance().user.getUsername())
                .child("events").child(key).setValue(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        succeedPushingEvent();
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                failedPushingEvent();
            }
        });


    }

    private void succeedPushingEvent(){
        Toast.makeText(getContext(),"Event has been added !",Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    private void failedPushingEvent(){
        Toast.makeText(getContext(),"Error has been occurred while adding event !",Toast.LENGTH_SHORT).show();

    }

    private void addLocation(){
        // TODO add logic for getting location from map, and storing in event as his location
        // should be as dialog with map, just pin location and get it from map
        dispatchAddLocationDialog();
    }

    private void inviteFriends(){
        // TODO add logic for inviting friends on this event
        // should be as dialog, list of my friends, and allow me to select multiple friends
        dispatchInviteFriendsDialog();
    }

    private void setNotifications(boolean state){
        if(state)
            event.setAlert("on");
        else
            event.setAlert("off");
    }
}
