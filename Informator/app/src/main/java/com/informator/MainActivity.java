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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth = null;
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;
    ProgressDialog dialog;
    Bitmap picture;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            mDatabase = database.getReference();
        }
        catch (Exception e){
            Toast.makeText(this,"Database error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        dialog =new ProgressDialog(this);
        dialog.setTitle("Please Wait");
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        sharedPreferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        checkuserLogged();
        Button btnLogin = (Button)findViewById(R.id.mainActivity_btnLogin);
        Button btnRegister = (Button)findViewById(R.id.mainActivity_btnReister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    public void checkuserLogged(){
        if(sharedPreferences.getBoolean(Constants.SHARED_PREFERENCES_LOGGED,false)) {
            String email = sharedPreferences.getString(Constants.SHARED_PREFERENCES_EMAIL,"");
            String password = sharedPreferences.getString(Constants.SHARED_PREFERENCES_PASSWORD,"");
            dialogShow();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                        ArrayList<String> friends = new ArrayList<>();
                                        for(DataSnapshot data1 : data.child("friends").getChildren()){
                                            friends.add(String.valueOf(data1.getValue()));
                                        }
                                        user.setFriends(friends);
                                    }
                                    i++;
                                }
                                StorageReference profilePicture = storageRef.child(user.getUsername() + ".jpg");

                                picture = null;
                                profilePicture.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        StoredData.getInstance().setUser(new UserWithPicture(user, picture));
                                        dialogDismiss();
                                        Intent i = new Intent(getApplicationContext(), StartActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        startActivity(i);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        StoredData.getInstance().setUser(new UserWithPicture(user,
                                                Bitmap.createScaledBitmap(
                                                        ProfileFragment.drawableToBitmap(getResources().getDrawable(R.drawable.ic_person_outline_black_24dp)),
                                                        3000,
                                                        3000,
                                                        false)
                                                )
                                        );
                                        dialogDismiss();
                                        Toast.makeText(MainActivity.this, "Error while fetching data...", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), StartActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        startActivity(i);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MainActivity.this, "Error while fetching data...", Toast.LENGTH_SHORT).show();
                                dialogHide();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            }
                        });

                    } else {
                        Toast.makeText(MainActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        dialogHide();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            });
        }
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
            if (dialog.isShowing()) {
                dialog.hide();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dialogDismiss(){
        try{
            dialog.dismiss();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

