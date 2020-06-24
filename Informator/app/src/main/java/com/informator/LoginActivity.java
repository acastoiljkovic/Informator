package com.informator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.informator.data.Constants;
import com.informator.data.StoredData;
import com.informator.data.User;
import com.informator.data.UserWithPicture;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = null;
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;
    ProgressDialog dialog;
    Bitmap picture;
    UserWithPicture user;
    SharedPreferences sharedPreferences;
    EditText etEmail;
    EditText etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Initialize();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=etEmail.getText().toString();
                final String Password=etPassword.getText().toString();


                if(TextUtils.isEmpty(Email)){
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

                if(firebaseAuth != null) {
                    dialogShow();
                    firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mDatabase.child("users").child("").orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int i = 0;
                                        for(DataSnapshot data : dataSnapshot.getChildren()){
                                            if(i == 0) {
                                                user.setUsername(String.valueOf(data.child("username").getValue()));
                                                user.setEmail(String.valueOf(data.child("email").getValue()));
                                                user.setFullName(String.valueOf(data.child("fullName").getValue()));
                                                user.setPhone(String.valueOf(data.child("phone").getValue()));
                                                user.setId(String.valueOf(data.child("id").getValue()));
                                                user.setPoints(String.valueOf(data.child("points")));
                                                user.setStatus(String.valueOf(data.child("status")));
                                            }
                                            i++;
                                        }

                                        SharedPreferences.Editor edit = sharedPreferences.edit();
                                        edit.putBoolean(Constants.SHARED_PREFERENCES_LOGGED,true);
                                        edit.putString(Constants.SHARED_PREFERENCES_EMAIL,user.getEmail());
                                        edit.putString(Constants.SHARED_PREFERENCES_USERNAME,user.getUsername());
                                        edit.putString(Constants.SHARED_PREFERENCES_PASSWORD,Password);
                                        edit.commit();

                                        StorageReference profilePicture = storageRef.child(user.getUsername()+".jpg");

                                        picture = null;
                                        profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                user.setProfilePhoto(picture);
                                                StoredData.getInstance().setUser(user);
                                                successLogin();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                user.setProfilePhoto(Bitmap.createScaledBitmap(
                                                        ProfileFragment.drawableToBitmap(getResources().getDrawable(R.drawable.ic_person_outline_black_24dp)),
                                                        3000,
                                                        3000,
                                                        false));
                                                StoredData.getInstance().setUser(user);
                                                failedLoginPicture();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        failedLoginUser();
                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                dialogHide();
                                etEmail.setText("");
                                etPassword.setText("");
                            }
                        }
                    });
                }
            }
        });
    }

    private void Initialize(){
        user = new UserWithPicture();
        try {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        try{
            database = FirebaseDatabase.getInstance();
            storage = FirebaseStorage.getInstance(Constants.URL_STORAGE);
            storageRef = storage.getReference();
            mDatabase = database.getReference();
        }
        catch (Exception e){
            Toast.makeText(this,"Database error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        initDialog();

        sharedPreferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        etEmail = (EditText) findViewById(R.id.loginEmail);
        etPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.loginBtnLogin);
    }


    private void successLogin(){
        dialogDismiss();
        Intent i = new Intent(getApplicationContext(), StartActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void failedLoginPicture(){
        dialogDismiss();
        Toast.makeText(LoginActivity.this, "Error while fetching data...", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(),StartActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void failedLoginUser(){
        Toast.makeText(LoginActivity.this, "Error while fetching data...", Toast.LENGTH_SHORT).show();
        dialogHide();
    }

    private void initDialog(){
        dialog =new ProgressDialog(this);
        dialog.setTitle("Please Wait");
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
    }

    public void dialogShow(){
        try {
            if (!dialog.isShowing()) {
                dialog.show();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dialogHide(){
        try {
            if (dialog.isShowing())
                dialog.hide();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dialogDismiss(){
        try{
            dialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
