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


public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = null;
    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;
    ProgressDialog dialog;
    Bitmap picture;
    User user;
    SharedPreferences sharedPreferences;

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

        final EditText etEmail = (EditText) findViewById(R.id.loginEmail);
        final EditText etPassword = (EditText) findViewById(R.id.loginPassword);
        Button btnLogin = (Button) findViewById(R.id.loginBtnLogin);

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
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mDatabase.child("users").child("").orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int i = 0;
                                        for(DataSnapshot data : dataSnapshot.getChildren()){
                                            if(i == 0)
                                                user = data.getValue(User.class);
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
                                                StoredData.getInstance().setUser(new UserWithPicture(user,picture));
//                                                Toast.makeText(LoginActivity.this, "Login Successfully...", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(getApplicationContext(),StartActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                                dialogHide();
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(LoginActivity.this, "Error while fetching data...", Toast.LENGTH_SHORT).show();
                                        dialogHide();
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                dialogHide();
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
}
