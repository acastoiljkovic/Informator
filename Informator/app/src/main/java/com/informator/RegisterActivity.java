package com.informator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.informator.data.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = null;
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    User user;
    EditText etEmail;
    EditText etPassword;
    EditText etConfPassword;
    EditText etUsername;
    EditText etPhone;
    Button btnSignUp;
    EditText etFullName;
    ImageButton btnTakePhoto;
    ImageButton btnAddPhoto;
    Bitmap image;
    FirebaseStorage storage;
    StorageReference storageRef;
    String Password = "";
    ProgressBar progressBar;


    static final int  REQUEST_IMAGE_CAPTURE = 1;
    static final int  REQUEST_PICK_IMAGE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = new User();
        storage = FirebaseStorage.getInstance("gs://informator-b509e.appspot.com");
        storageRef = storage.getReference();

        try {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        catch (Exception e){
            Toast.makeText(this,"Login error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        try{
            database = FirebaseDatabase.getInstance();
        }
        catch (Exception e){
            Toast.makeText(this,"Database error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        mDatabase = database.getReference();

        btnTakePhoto = (ImageButton)findViewById(R.id.registerAddPhoto);
        btnAddPhoto = (ImageButton)findViewById(R.id.registerAddFromGallery);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        etEmail = (EditText)findViewById(R.id.registerEmail);
        etPassword = (EditText)findViewById(R.id.registerPassword);
        etConfPassword = (EditText)findViewById(R.id.registerConfirmPassword);
        etUsername = (EditText)findViewById(R.id.registerUsername);
        etPhone = (EditText)findViewById(R.id.registerPhone);
        btnSignUp = (Button) findViewById(R.id.registerBtnSignUp);
        etFullName = (EditText)findViewById(R.id.registerFullName);
        progressBar=(ProgressBar)findViewById(R.id.id_progress_bar);
        progressBar.setVisibility(View.GONE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setEmail(etEmail.getText().toString());
                user.setFullName(etFullName.getText().toString());
                user.setPhone(etPhone.getText().toString());
                user.setUsername(etUsername.getText().toString());
                progressBar.setVisibility(View.VISIBLE);

                Password=etPassword.getText().toString();
                String ConfPassword=etConfPassword.getText().toString();

                if(TextUtils.isEmpty(user.getUsername())){
                    etUsername.setError("Username is required");
                    return;
                }

                if(TextUtils.isEmpty(user.getEmail())){
                    etEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(Password)){
                    etPassword.setError("Password is required");
                }

                if(etPassword.length()<6){
                    etPassword.setError("Password must be >= 6 characters");
                    return;
                }

                if(image == null){
                    btnAddPhoto.setBackgroundResource(R.drawable.button_red_border );
                    btnTakePhoto.setBackgroundResource(R.drawable.button_red_border );
                    return;
                }

                if(!Password.equals(ConfPassword)){
                    etConfPassword.setError("Passwords does not match !");
                    etPassword.setError("Passwords does not match !");
                    return;
                }

                mDatabase.child("users").child(user.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            etUsername.setError("Username is taken !");
                            return;
                        }
                        else{
                            firebaseAuth.createUserWithEmailAndPassword(user.getEmail(),Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        user.setId(FirebaseAuth.getInstance().getUid());
                                        mDatabase.child("users").child(user.getUsername()).setValue(user);
                                        StorageReference profilePicture = storageRef.child(user.getUsername()+".jpg");
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        if(image != null) {
                                            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] data = baos.toByteArray();

                                            UploadTask uploadTask = profilePicture.putBytes(data);
                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterActivity.this, "Error while uploading picture to server : " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Toast.makeText(RegisterActivity.this, "User created.", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(),StartActivity.class));

                                                }
                                            });
                                        }
                                        else{
                                            Toast.makeText(RegisterActivity.this, "Error while uploading picture", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this,
                                                "Error "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
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
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openImagePicker(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            image = (Bitmap) extras.get("data");
            Toast.makeText(RegisterActivity.this, "Image Added!", Toast.LENGTH_SHORT).show();
            btnTakePhoto.setBackgroundResource(R.drawable.button_green_border );
            btnAddPhoto.setBackgroundResource(R.drawable.button_black_border );
        }
        else if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK){
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    btnAddPhoto.setBackgroundResource(R.drawable.button_green_border);
                    btnTakePhoto.setBackgroundResource(R.drawable.button_black_border);
                    Toast.makeText(RegisterActivity.this, "Image Added!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    btnAddPhoto.setBackgroundResource(R.drawable.button_red_border);
                    btnTakePhoto.setBackgroundResource(R.drawable.button_black_border);
                    Toast.makeText(RegisterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
