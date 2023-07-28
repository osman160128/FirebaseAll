package com.example.firebaseall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {

    EditText editTextNewEmail,editTextPassword;
    TextView textViewEuthontication,textOldEmail;
    String userOldEmail,userNewMail,userPwd;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    Button buttonUpdateEmail,buttonAuthonmEmail;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        textOldEmail = findViewById(R.id.edittext_update_email);
        editTextNewEmail = findViewById(R.id.edittext_new_email);
        editTextPassword= findViewById(R.id.edittext_update_email_pwd);

        buttonUpdateEmail =findViewById(R.id.set_new_email_Button);



        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        userOldEmail = firebaseUser.getEmail();
        textOldEmail.setText(userOldEmail);

        if(firebaseUser.equals("")){
            Toast.makeText(this, "there is no user now ", Toast.LENGTH_SHORT).show();
        }
        else {
            reAuthonticate(firebaseUser);
        }

    }

    private void reAuthonticate(FirebaseUser firebaseUser) {

        buttonAuthonmEmail = findViewById(R.id.auth_email_Button);

        buttonAuthonmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd = editTextPassword.getText().toString();
                 if(TextUtils.isEmpty(userPwd)){
                    Toast.makeText(UpdateEmailActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password is Required");
                    editTextPassword.requestFocus();
                }
                 else {
                     AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail,userPwd);
                     firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             Toast.makeText(UpdateEmailActivity.this, "your account is authonticate now", Toast.LENGTH_SHORT).show();

                             textOldEmail.setEnabled(false);
                             editTextPassword.setEnabled(false);
                             buttonAuthonmEmail.setEnabled(false);


                             buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {

                                     if (task.isSuccessful()) {
                                         userNewMail = editTextNewEmail.getText().toString();
                                         if (TextUtils.isEmpty(userNewMail)) {
                                             Toast.makeText(UpdateEmailActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                                             editTextNewEmail.setError("Email Name is Required");
                                             editTextNewEmail.requestFocus();
                                         } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewMail).matches()) {
                                             Toast.makeText(UpdateEmailActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                                             editTextNewEmail.setError("Valid email Name is Required");
                                             editTextNewEmail.requestFocus();
                                         } else {
                                             updateEmail(firebaseUser);
                                         }

                                     }else {
                                         try {
                                             throw task.getException();
                                         } catch (Exception e) {
                                             Log.e("errod", e.getMessage());
                                             Toast.makeText(UpdateEmailActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                         }

                                     }

                                 }
                             });



                         }


                     });
                 }
            }
        });

    }

    private void updateEmail(FirebaseUser firebaseUser) {

        firebaseUser.updateEmail(userNewMail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(UpdateEmailActivity.this, "password is update", Toast.LENGTH_SHORT).show();
                    firebaseUser.sendEmailVerification();
                    Intent intent = new Intent(UpdateEmailActivity.this, UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Log.e("errod", e.getMessage());
                        Toast.makeText(UpdateEmailActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);

        } else if (id==R.id.menu_update_profile) {
            Intent intent = new Intent(UpdateEmailActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(UpdateEmailActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }
        /*
        else if (id==R.id.menu_setting) {
            Intent intent = new Intent(UserProfileActivity.this,SettingActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.menu_update_password) {
            Intent intent = new Intent(UserProfileActivity.this,UpdatePasswordActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.menu_delete) {
            Intent intent = new Intent(UserProfileActivity.this,ProfileDeleteActivity.class);
            startActivity(intent);
        }

        else if (id==R.id.menu_log_out) {
           authProfile.signOut();
            Toast.makeText(this, "Loggout", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        */
        if (id==R.id.menu_log_out) {
            authProfile.signOut();
            Toast.makeText(this, "Loggout", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UpdateEmailActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);


    }
}