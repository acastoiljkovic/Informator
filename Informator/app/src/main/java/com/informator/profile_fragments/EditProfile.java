package com.informator.profile_fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informator.R;
import com.informator.RegisterActivity;
import com.informator.StartActivity;
import com.informator.data.Constants;
import com.informator.data.StoredData;
import com.informator.data.UserWithPicture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class EditProfile extends Fragment {

    EditText etPassword;
    EditText etConfirmPassword;
    EditText etFullName;
    EditText etPhone;

    Button btnChangePassword;
    Button btnChangeFullNameAndPhone;
    Button btnChangePicture;

    ImageButton btnTakePhoto;
    ImageButton btnAddPhoto;
    Bitmap image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        Initialize(view);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        btnChangeFullNameAndPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFullNameAndPhone();
            }
        });

        btnChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePicture();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            Constants.CAMERA_PERMISSION);
                }
                else{
                    dispatchTakePictureIntent();
                }
            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.STORAGE_PERMISSION);
                }
                else {
                    openImagePicker();
                }
            }
        });

        return  view;
    }

    private void Initialize(View view){
        etPassword = (EditText) view.findViewById(R.id.editPassword);
        etConfirmPassword = (EditText) view.findViewById(R.id.editConfirmPassword);
        etFullName = (EditText) view.findViewById(R.id.editFullName);
        etPhone = (EditText) view.findViewById(R.id.editPhone);

        btnChangePassword = (Button)view.findViewById(R.id.editBtnChangePassword);
        btnChangeFullNameAndPhone = (Button)view.findViewById(R.id.editBtnChangeFNameAndPhone);
        btnChangePicture = (Button)view.findViewById(R.id.editBtnChangePicture);

        btnTakePhoto = (ImageButton)view.findViewById(R.id.editAddPhoto);
        btnAddPhoto = (ImageButton)view.findViewById(R.id.editAddFromGallery);

        etFullName.setText(StoredData.getInstance().user.getFullName());
        etPhone.setText(StoredData.getInstance().user.getPhone());

    }

    private void changePassword(){
        String Password = etPassword.getText().toString();
        String ConfPassword = etConfirmPassword.getText().toString();


        if(TextUtils.isEmpty(Password)){
            etPassword.setError("Password is required");
            return;
        }
        else if(etPassword.length()<6){
            etPassword.setError("Password must be >= 6 characters");
            return;
        }
        else if(!Password.equals(ConfPassword)){
            etConfirmPassword.setError("Passwords does not match !");
            etPassword.setError("Passwords does not match !");
            return;
        }
        else{
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(Password);
            Toast.makeText(getContext(), "Updated password !", Toast.LENGTH_SHORT).show();
        }

    }

    private void changeFullNameAndPhone(){

        String fullName = etFullName.getText().toString();
        String phone = etPhone.getText().toString();

        if(fullName.isEmpty()) {
            etFullName.setError("Cant be empty !");
            return;
        }
        else if(phone.isEmpty()){
            etPhone.setError("Cant bet empty !");
            return;
        }
        else {
            StoredData.getInstance().user.setFullName(fullName);
            StoredData.getInstance().user.setPhone(phone);
            FirebaseDatabase.getInstance().getReference().child("users").child(StoredData.getInstance().user.getUsername()).child("fullName").setValue(fullName).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Updated full name successfully !", Toast.LENGTH_SHORT).show();
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(getContext(), "Error while changing full name!", Toast.LENGTH_SHORT).show();

                }
            });
            FirebaseDatabase.getInstance().getReference().child("user").child(StoredData.getInstance().user.getUsername()).child("phone").setValue(phone).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Updated phone successfully !", Toast.LENGTH_SHORT).show();
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(getContext(), "Error while changing phone!", Toast.LENGTH_SHORT).show();

                }
            });

        }

    }

    private void changePicture(){
        if(image == null){
            btnAddPhoto.setBackgroundResource(R.drawable.button_red_border );
            btnTakePhoto.setBackgroundResource(R.drawable.button_red_border );
            return;
        }
        else{
            StorageReference profilePicture = FirebaseStorage.getInstance(Constants.URL_STORAGE).getReference().child(StoredData.getInstance().user.getUsername()+".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(image != null) {
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = profilePicture.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error while uploading picture to server : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Picture changed", Toast.LENGTH_SHORT).show();
                        StoredData.getInstance().user.setProfilePhoto(image);
                    }
                });
            }
            else{
                Toast.makeText(getContext(), "Error while uploading picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {

                    Toast.makeText(getContext(), "Permission denied to access to your Camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 4: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker();
                } else {

                    Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openImagePicker(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.REQUEST_PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            image = (Bitmap) extras.get("data");
            Toast.makeText(getContext(), "Image Added!", Toast.LENGTH_SHORT).show();
            btnTakePhoto.setBackgroundResource(R.drawable.button_green_border );
            btnAddPhoto.setBackgroundResource(R.drawable.button_black_border );
        }
        else if(requestCode == Constants.REQUEST_PICK_IMAGE && resultCode == RESULT_OK){
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    btnAddPhoto.setBackgroundResource(R.drawable.button_green_border);
                    btnTakePhoto.setBackgroundResource(R.drawable.button_black_border);
                    Toast.makeText(getContext(), "Image Added!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    btnAddPhoto.setBackgroundResource(R.drawable.button_red_border);
                    btnTakePhoto.setBackgroundResource(R.drawable.button_black_border);
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
