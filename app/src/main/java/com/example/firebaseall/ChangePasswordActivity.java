package com.example.firebaseall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText  editTextOldPwd,editTextNewPwd,editTextComPwd;
    TextView textViewEuthontication;
    String userOldPassword,userNewPassword,userComPassword;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    Button buttonUpdatePwd,buttonAuthonmpwd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        editTextNewPwd = findViewById(R.id.edittext_new_pwd);
        editTextOldPwd = findViewById(R.id.edittext_old_pwd);
        editTextComPwd = findViewById(R.id.edittext_conform_pwd);
        buttonAuthonmpwd = findViewById(R.id.auth_change_pwd_Button);
        buttonUpdatePwd = findViewById(R.id.set_change_pwd_Button);

        editTextNewPwd.setEnabled(false);
        editTextComPwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        if(firebaseUser.equals("")){
            Toast.makeText(this, "there is no user now ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            reAuthonticate(firebaseUser);
        }

    }

    private void reAuthonticate(FirebaseUser firebaseUser) {

        buttonAuthonmpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOldPassword = editTextOldPwd.getText().toString();
                if(TextUtils.isEmpty(userOldPassword)){
                    Toast.makeText(ChangePasswordActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextOldPwd.setError("Password is Required");
                    editTextOldPwd.requestFocus();
                }
                else {
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userOldPassword);


                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(ChangePasswordActivity.this, "your account is authonticate now", Toast.LENGTH_SHORT).show();

                            editTextOldPwd.setEnabled(false);
                            buttonAuthonmpwd.setEnabled(false);
                            editTextComPwd.setEnabled(true);
                            editTextNewPwd.setEnabled(true);

                            buttonUpdatePwd.setBackgroundTintList(ContextCompat.getColorStateList(
                                    ChangePasswordActivity.this,R.color.purple_200));

                            buttonUpdatePwd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (task.isSuccessful()) {
                                        userNewPassword = editTextNewPwd.getText().toString();
                                        if (TextUtils.isEmpty(userNewPassword)) {
                                            Toast.makeText(ChangePasswordActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                                            editTextNewPwd.setError("Password Name is Required");
                                            editTextNewPwd.requestFocus();
                                        } else {
                                            updatePassword(firebaseUser);
                                        }

                                    }else {
                                        try {
                                            throw task.getException();
                                        } catch (Exception e) {
                                            Log.e("errod", e.getMessage());
                                            Toast.makeText(ChangePasswordActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void updatePassword(FirebaseUser firebaseUser) {
        userNewPassword = editTextNewPwd.getText().toString();
        userComPassword = editTextComPwd.getText().toString();

        if (TextUtils.isEmpty(userNewPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            editTextNewPwd.setError("Password is Required");
            editTextNewPwd.requestFocus();
        } else if (TextUtils.isEmpty(userComPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Please comform password", Toast.LENGTH_SHORT).show();
            editTextComPwd.setError("password is Required again");
            editTextComPwd.requestFocus();
        } else if (!userNewPassword.equals(userComPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Please enter same password", Toast.LENGTH_SHORT).show();
            editTextComPwd.setError("Password doesn't match");
            editTextComPwd.requestFocus();
            editTextNewPwd.clearComposingText();
            editTextComPwd.clearComposingText();
        } else{

            firebaseUser.updatePassword(userNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Log.e("errod", e.getMessage());
                            Toast.makeText(ChangePasswordActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });
    }
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
            Intent intent = new Intent(ChangePasswordActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(ChangePasswordActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }
        /*
        else if (id==R.id.menu_setting) {
            Intent intent = new Intent(UserProfileActivity.this,SettingActivity.class);
            startActivity(intent);
        }
        */
        else if (id==R.id.menu_update_password) {
            Intent intent = new Intent(ChangePasswordActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
        }
         /*
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

            Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);


    }
}