package com.informator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.informator.data.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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


        etEmail = (EditText)findViewById(R.id.registerEmail);
        etPassword = (EditText)findViewById(R.id.registerPassword);
        etConfPassword = (EditText)findViewById(R.id.registerConfirmPassword);
        etUsername = (EditText)findViewById(R.id.registerUsername);
        etPhone = (EditText)findViewById(R.id.registerPhone);
        btnSignUp = (Button) findViewById(R.id.registerBtnSignUp);
        etFullName = (EditText)findViewById(R.id.registerFullName);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User();
                user.setEmail(etEmail.getText().toString());
                user.setFullName(etFullName.getText().toString());
                user.setPhone(etPhone.getText().toString());
                user.setUsername(etUsername.getText().toString());

                String Password=etPassword.getText().toString();
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

                Toast.makeText(RegisterActivity.this,Password + ConfPassword,Toast.LENGTH_SHORT).show();
                if(!Password.equals(ConfPassword)){
                    etConfPassword.setError("Passwords does not match !");
                    etPassword.setError("Passwords does not match !");
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(user.getEmail(),Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDatabase.child("users").child(FirebaseAuth.getInstance().getUid()).setValue(user);
                            Toast.makeText(RegisterActivity.this,"User created.",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,
                                    "Error "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }
}
