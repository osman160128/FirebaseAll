package com.example.firebaseall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class PasswordResetActivity extends AppCompatActivity {

    EditText editTextForgetPwdEmail;
    Button passwordRest;
    ProgressBar resetPwdProgressBar;
    FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        getActionBar().setTitle("Pasword Reset");

        editTextForgetPwdEmail = findViewById(R.id.edittext_password_reset_Email);
        resetPwdProgressBar = findViewById(R.id.pwdResetProgressBar);
        passwordRest = findViewById(R.id.passwordResetButton);
        passwordRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextForgetPwdEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(PasswordResetActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    editTextForgetPwdEmail.setError("Email Name is Required");
                    editTextForgetPwdEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(PasswordResetActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    editTextForgetPwdEmail.setError("Valid email Name is Required");
                    editTextForgetPwdEmail.requestFocus();
                }

                else {
                    resetPwdProgressBar.setVisibility(View.VISIBLE);
                    resetPasword(email);
                }


            }
        });
    }

    private void resetPasword(String email) {
        authProfile= FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {


                    Toast.makeText(PasswordResetActivity.this, "pleas cheack your email one link is send ", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PasswordResetActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editTextForgetPwdEmail.setError("Email is inavlid,please registrtion first");
                        editTextForgetPwdEmail.requestFocus();
                    } catch (Exception e) {
                        Toast.makeText(PasswordResetActivity .this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }
}