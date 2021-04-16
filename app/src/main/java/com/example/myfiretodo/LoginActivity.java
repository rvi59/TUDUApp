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
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText mTextEmail, mTextPass;
    private Button mButtonLogin, mButtongotoRegister;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Objects.requireNonNull(getSupportActionBar()).hide();

        //Finding Ids
        mTextEmail = findViewById(R.id.LEmail);
        mTextPass = findViewById(R.id.LPass);
        mButtonLogin = findViewById(R.id.btnLogin);
        mButtongotoRegister = findViewById(R.id.btngotoRegister);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mProgressDialog = new ProgressDialog(this);


        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mTextEmail.getText().toString();
                String pass = mTextPass.getText().toString().trim();

                String patternEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (email.isEmpty()) {
                    mTextEmail.setError("Email cannot be empty");
                } else if (!email.matches(patternEmail)) {
                    mTextEmail.setError("Enter valid Email Id");
                } else if (pass.isEmpty()) {
                    mTextPass.setError("Password cannot be empty");
                } else {
                    mProgressDialog.setMessage("Login");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                                mProgressDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Login Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    });
                }
            }
        });


        mButtongotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


    }
}