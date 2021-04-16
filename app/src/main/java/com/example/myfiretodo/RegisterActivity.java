package com.example.myfiretodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText mTextEmail, mTextPass, mTextCPass;
    private Button mButtonRegister, mButtongotoLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).hide();


        //Finding Ids
        mTextEmail = findViewById(R.id.rEmail);
        mTextPass = findViewById(R.id.rPass);
        mTextCPass = findViewById(R.id.rCPass);
        mButtonRegister = findViewById(R.id.btnSignup);
        mButtongotoLogin = findViewById(R.id.btngotoLogin);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgressDialog = new ProgressDialog(this);


        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();
            }
        });


        mButtongotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    //For Validation
    private void Validate() {
        String email = mTextEmail.getText().toString();
        String pass = mTextPass.getText().toString().trim();
        String confirmpass = mTextCPass.getText().toString();

        String patternEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.isEmpty()) {
            mTextEmail.setError("Email cannot be empty");
        } else if (!email.matches(patternEmail)) {
            mTextEmail.setError("Enter valid Email Id");
        } else if (pass.isEmpty()) {
            mTextPass.setError("Please enter valid email");
        } else if (confirmpass.isEmpty()) {
            mTextCPass.setError("Password cannot be empty");
        } else if (!confirmpass.equals(pass)) {
            mTextCPass.setError("Password and confirm password should be same");
        } else {
            registerUser();
        }

    }


    private void registerUser() {
        String email = mTextEmail.getText().toString();
        String pass = mTextPass.getText().toString().trim();
        mProgressDialog.setMessage("Registering");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    HashMap<String, String> map = new HashMap<>();
                    map.put("email", email);
                    map.put("pass", pass);
                    mDatabaseReference.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                            mProgressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}