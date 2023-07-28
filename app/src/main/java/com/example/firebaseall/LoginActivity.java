
package com.example.firebaseall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText editTextLoginEmail,editTextLoginActivityPwd;
    ProgressBar loginProgressBar;
    Button loginButton,forgetPassword;

    FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editTextLoginEmail = findViewById(R.id.edittext_Login_Email);
        loginButton = findViewById(R.id.loginButton);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        forgetPassword = findViewById(R.id.foregetPasswordButton);

        editTextLoginActivityPwd = findViewById(R.id.edittext_LoginActivity_Password);

        authProfile = FirebaseAuth.getInstance();


        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,PasswordResetActivity.class);

                startActivity(intent);

            }
        });



        //password show and hide icon

        ImageView imageView = findViewById(R.id.image_view_show_hide_pwd);
        imageView.setImageResource(R.drawable.img_1);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(editTextLoginActivityPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                   editTextLoginActivityPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                   imageView.setImageResource(R.drawable.img_2);

               }else {
                   editTextLoginActivityPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                   imageView.setImageResource(R.drawable.img_1);
               }
            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPassword = editTextLoginActivityPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(LoginActivity.this, "PLease Enter Email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is requaird");
                    editTextLoginEmail.requestFocus();
                }
                 else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "PLease Enter Valid Email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid Email is requaird");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    editTextLoginActivityPwd.setError("Password is required");
                    editTextLoginActivityPwd.requestFocus();
                }
                else{
                    loginProgressBar.setVisibility(View.GONE);
                    loginUSer(textEmail,textPassword);
                }
            }
        });

    }

    private void loginUSer(String textEmail, String textPassword) {

        authProfile.signInWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){


                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();
                        //open user profile

                        Intent intent = new Intent(LoginActivity.this,UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlartDialog();
                    }


                }
                else {
                    try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            editTextLoginEmail.setError("Email is inavlid,please registrtion first");
                            editTextLoginEmail.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            editTextLoginEmail.setError("Email is inavlid,please login again");
                            editTextLoginEmail.requestFocus();
                        } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                }
                loginProgressBar.setVisibility(View.GONE);

            }
        });

    }

    private void showAlartDialog() {
        //setip alart dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not Verified ");
        builder.setMessage("Please Varified your your email now.you can not login without varifiaction");
        builder.setPositiveButton("Contnue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}