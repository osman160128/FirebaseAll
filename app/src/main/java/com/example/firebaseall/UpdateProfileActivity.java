package com.example.firebaseall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText editTextUpdateFullName,editTextUpdateDoB,editTextUpdateMobile;
    ProgressBar progressBarRegistration;
    RadioGroup radioGroupUpdateGender;
    RadioButton radioButtonUpdateGenderSelect;
    private String textFullname,textEmail,textdob,textGender,textMobile;
    FirebaseAuth authProfile;
    Button buttonUpdateProfilePhoto;
    Button updateEmail;
    Button updateProfileButton;

    DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editTextUpdateFullName = findViewById(R.id.edittext_update_full_name);
        editTextUpdateDoB=findViewById(R.id.edittext_update_dob);
        editTextUpdateMobile=findViewById(R.id.edittext_update_mobile);

        buttonUpdateProfilePhoto = findViewById(R.id.updatephotoButton);
        updateEmail = findViewById(R.id.updateEmailButton);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        //update email activity
        updateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //update profile photo
        buttonUpdateProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this,UploadProfilePhoto.class);
                startActivity(intent);
                finish();
            }
        });
        //update date
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textSADoB[]=textdob.split("/");
                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1])-1;
                int year = Integer.parseInt(textSADoB[2]);

                picker= new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);

                    }
                },year,month,day);
                picker.show();

            }
        });

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_gender);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =authProfile.getCurrentUser();

        showProfile(firebaseUser);

        //updateButton
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateProfile(firebaseUser);
            }
        });

    }

    private void updateProfile(FirebaseUser firebaseUser) {

        int selectedGenderId = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelect = findViewById(selectedGenderId);


        textGender = radioButtonUpdateGenderSelect.getText().toString();
        textFullname=editTextUpdateFullName.getText().toString();
        textdob = editTextUpdateDoB.getText().toString();
        textMobile = editTextUpdateMobile.getText().toString();

        ReasWriteUserDetails writeUserDetails = new ReasWriteUserDetails(textFullname,textdob,textGender,textMobile);
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
        String userID = authProfile.getUid();

        referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(textFullname).build();

                    firebaseUser.updateProfile(profileUpdate);
                    Toast.makeText(UpdateProfileActivity.this, "update successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UpdateProfileActivity.this,UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }else {
                    try {
                        throw task.getException();
                    }
                    catch (Exception e) {
                        Log.e("errod",e.getMessage());
                        Toast.makeText(UpdateProfileActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });
    }

    private void showProfile(FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");

        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ReasWriteUserDetails readUserDetails = snapshot.getValue(ReasWriteUserDetails.class);

                if(readUserDetails!=null){
                    textFullname = readUserDetails.name;
                    textdob = readUserDetails.doB;
                    textGender=readUserDetails.gender;
                    textMobile = readUserDetails.mobile;


                    editTextUpdateFullName.setText(textFullname);
                    editTextUpdateDoB.setText(textdob);
                    editTextUpdateMobile.setText(textMobile);

                    if(textGender.equals("Male")){
                        radioButtonUpdateGenderSelect = findViewById(R.id.radio_update_male);
                    }
                    else {
                        radioButtonUpdateGenderSelect = findViewById(R.id.radio_upadte_female);
                    }
                    radioButtonUpdateGenderSelect.setChecked(true);

                }else{
                    Toast.makeText(UpdateProfileActivity.this, "someting is wrong", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}