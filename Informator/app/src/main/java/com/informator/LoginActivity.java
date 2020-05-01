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

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        final EditText etEmail = (EditText) findViewById(R.id.loginEmail);
        final EditText etPassword = (EditText) findViewById(R.id.loginPassword);
        Button btnLogin = (Button) findViewById(R.id.loginBtnLogin);

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
                    firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Successfully...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}