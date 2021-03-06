package com.informator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.informator.data.VirtualObject;
import com.informator.event_fragments.AddEventFragment;
import com.informator.event_fragments.SingleEventFragment;
import com.informator.map_fragments.CommentsFragment;
import com.informator.map_fragments.ListVirtualObjectsFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informator.data.Constants;
import com.informator.data.StoredData;
import com.informator.map_fragments.VirtualObjectFragment;
import com.informator.profile_fragments.AddFriendsBluetoothFragment;
import com.informator.profile_fragments.EditProfile;
import com.informator.profile_fragments.SearchFriendsFragment;
import com.informator.profile_fragments.SendMessageFragment;
import com.informator.services.LocationTracker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StartActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    FirebaseStorage storage;
    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        fromNotifications();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        try {

            storage = FirebaseStorage.getInstance(Constants.URL_STORAGE);
            storageRef = storage.getReference();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new GroupsFragment()).addToBackStack(null).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    private void fromNotifications() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // TODO uvek daje poslednju notifikaciju, da se vidi zasto
            String username = bundle.getString("username");
            String title = bundle.getString("title");
            String user = bundle.getString("user");
            if (username != null && !username.isEmpty()) {
                bundle = new Bundle();
                bundle.putString("username", username);
//                setFragment(R.id.profile, bundle);
            }
            if (title != null && !title.isEmpty() && user != null && !user.isEmpty()) {
                FirebaseDatabase.getInstance().getReference().child("users").child(user).child("virtual_objects")
                        .child(title).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        StoredData.getInstance().setVirtualObject(dataSnapshot.getValue(VirtualObject.class));
                        Bundle bundle = new Bundle();
                        bundle.putString("idVirtualObject", StoredData.getInstance().getVirtualObject().getId());
                        setFragment(R.string.virtualObjectId, bundle);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(StartActivity.class.getSimpleName(),"Canceled fetching data for virtual object !");
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public static Fragment getSelectedFragment(int itemId, Bundle bundle){

        Fragment retFragment = null;

        if(itemId==R.id.groups){
            retFragment=new GroupsFragment();
        }
        else if(itemId==R.id.map){
            retFragment=new MapFragment();
        }
        else if(itemId==R.id.events)
        {
            retFragment=new EventsFragment();
        }
        else if(itemId==R.id.profile){
            retFragment=new ProfileFragment();
        }
        else if(itemId==R.id.add_group){
//            retFragment=new AddGroupFragment();
        }
        else if(itemId==R.id.search_on_map){
//            retFragment=new SearchMapFragment();
        }
        else if(itemId==R.id.add_event){
            retFragment=new AddEventFragment();
        }
        else if(itemId==R.string.single_event_id){
            retFragment=new SingleEventFragment();
        }
        else if(itemId==R.id.find_event){
//            retFragment=new FindEventFragment();
        }
        else if(itemId==R.id.search_friends){
            retFragment=new SearchFriendsFragment();
        }
        else if(itemId==R.id.send_message){
            retFragment=new SendMessageFragment();
        }
        else if(itemId==R.id.add_friends_bluetooth){
            retFragment=new AddFriendsBluetoothFragment();
        }
        else if(itemId==R.string.virtualObjectId){
            retFragment=new VirtualObjectFragment();
            retFragment.setArguments(bundle);
        }
        else if(itemId==R.string.open_comments){
            retFragment=new CommentsFragment();
            retFragment.setArguments(bundle);
        }
        else if(itemId==R.string.open_listVO){
            retFragment=new ListVirtualObjectsFragment();
            retFragment.setArguments(bundle);
        }
        else if(itemId==R.string.edit_profile_id){
            retFragment=new EditProfile();
        }
        else if(itemId==R.string.single_group){
            retFragment=new GroupFragment();
            retFragment.setArguments(bundle);
        }

        retFragment.setArguments(bundle);
        return retFragment;
    }

    public void setFragment(int fragmentId,Bundle bundle){
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, getSelectedFragment(fragmentId,bundle)).addToBackStack(null).commit();
        }
        catch (Exception e){
            Log.e("Error :",e.getMessage());
        }
    }

    public void setBottomNavItemSelected(int item){
        if(bottomNavigationView.getSelectedItemId() != item )
            bottomNavigationView.setSelectedItemId(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        setFragment(menuItem.getItemId(),null);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {

                    Toast.makeText(StartActivity.this, "Permission denied, can't access your Camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 4: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker();
                } else {

                    Toast.makeText(StartActivity.this, "Permission denied, can't read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
        }
    }

    public void openImagePicker(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.REQUEST_PICK_IMAGE);
    }

    private void uploadImage(final Bitmap image){
        final StorageReference imagesOfUser = storageRef.child("images").child(StoredData.getInstance().getUser().getUsername());
        imagesOfUser.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                uploadImageStoreageRef(image,imagesOfUser,listResult.getItems().size()+1+".jpg");

            }
        });
    }

    private void uploadImageStoreageRef(Bitmap image, StorageReference ref,String name){
        StorageReference picture = ref.child(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(image != null) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = picture.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(StartActivity.this,"Error while uploading image : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StoredData.getInstance().user.getImageAddedListener().onImageAdded();
//                    Toast.makeText(StartActivity.this,"Image uploaded successfully ",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this, "Error while uploading picture", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap image;
        if(requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            image = (Bitmap) extras.get("data");
            uploadImage(image);
            Toast.makeText(StartActivity.this, "Image Added!", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == Constants.REQUEST_PICK_IMAGE && resultCode == RESULT_OK){
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    uploadImage(image);
                    Toast.makeText(StartActivity.this, "Image Added!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(StartActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if(isMyServiceRunning(LocationTracker.class))
            stopService(new Intent(this, LocationTracker.class));
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
