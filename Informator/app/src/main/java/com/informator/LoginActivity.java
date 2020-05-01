package com.informator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.informator.data.StoredData;
import com.informator.data.User;
import com.informator.data.UserWithPicture;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = null;
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;
    ProgressBar progressBar;
    Bitmap picture;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = new User();
        try {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        try{
            database = FirebaseDatabase.getInstance();
            storage = FirebaseStorage.getInstance("gs://informator-b509e.appspot.com");
            storageRef = storage.getReference();
        }
        catch (Exception e){
            Toast.makeText(this,"Database error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        mDatabase = database.getReference();

        final EditText etEmail = (EditText) findViewById(R.id.loginEmail);
        final EditText etPassword = (EditText) findViewById(R.id.loginPassword);
        Button btnLogin = (Button) findViewById(R.id.loginBtnLogin);
        progressBar=(ProgressBar)findViewById(R.id.id_progress_bar_login);
        progressBar.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=etEmail.getText().toString();
                String Password=etPassword.getText().toString();


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
                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mDatabase.child("users").child("").orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        user.setPhone(((Map<String,String>)((Map<String,User>)dataSnapshot.getValue()).values().toArray()[0]).get("phone"));
                                        user.setUsername(((Map<String,String>)((Map<String,User>)dataSnapshot.getValue()).values().toArray()[0]).get("username"));
                                        user.setFullName(((Map<String,String>)((Map<String,User>)dataSnapshot.getValue()).values().toArray()[0]).get("fullName"));
                                        user.setEmail(((Map<String,String>)((Map<String,User>)dataSnapshot.getValue()).values().toArray()[0]).get("email"));
                                        user.setId(FirebaseAuth.getInstance().getUid());
                                        StorageReference profilePicture = storageRef.child(user.getUsername()+".jpg");

                                        picture = null;
                                        profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                picture = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                StoredData.getInstance().setUser(new UserWithPicture(user,picture));
                                                Toast.makeText(LoginActivity.this, "Login Successfully...", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(getApplicationContext(),StartActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                                progressBar.setVisibility(View.GONE);
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(LoginActivity.this, "Error while fetching data...", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                etEmail.setText("");
                                etPassword.setText("");
                            }
                        }
                    });
                }
            }
        });
    }
}
