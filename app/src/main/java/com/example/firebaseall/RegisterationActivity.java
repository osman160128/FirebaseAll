package com.example.firebaseall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterationActivity extends AppCompatActivity {

    EditText editTextRegistrationFullName,editTextRegistrationEmail,editTextRegistrationDoB,
            editTextRegistrationMobile,editTextRegistrationPwd,editTextRegistrationComformPwd;
    ProgressBar progressBarRegistration;
    RadioGroup radioGroupRegistargetGender;
    RadioButton radioButtonRegistrartiongetGenderSelect;
    Button registerButton;

    DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        getSupportActionBar().setTitle("Registration");

        Toast.makeText(this, "You can resgitration now", Toast.LENGTH_SHORT).show();

        editTextRegistrationFullName = findViewById(R.id.edittext_register_full_name);
        editTextRegistrationEmail = findViewById(R.id.edittext_register_email);
        editTextRegistrationDoB = findViewById(R.id.edittext_register_dob);
        editTextRegistrationMobile = findViewById(R.id.edittext_register_mobile);
        editTextRegistrationPwd = findViewById(R.id.edittext_register_password);

        editTextRegistrationComformPwd = findViewById(R.id.edittext_register_comform_password);
        progressBarRegistration = findViewById(R.id.registrationProgressBar);
        registerButton = findViewById(R.id.registerationButton);


        radioGroupRegistargetGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegistargetGender.clearCheck();
        //date picker
        editTextRegistrationDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker= new DatePickerDialog(RegisterationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegistrationDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);

                    }
                },year,month,day);
                picker.show();

            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(RegisterationActivity.this, "registration Button is clicked", Toast.LENGTH_SHORT).show();
                int selectGenderId = radioGroupRegistargetGender.getCheckedRadioButtonId();
                radioButtonRegistrartiongetGenderSelect = findViewById(selectGenderId);

                String txtFullName = editTextRegistrationFullName.getText().toString();
                String txtEmail= editTextRegistrationEmail.getText().toString();
                String txtDoB = editTextRegistrationDoB.getText().toString();
                String txtMobile = editTextRegistrationMobile.getText().toString();
                String txtPwd = editTextRegistrationPwd.getText().toString();
                String txtComformPwd = editTextRegistrationComformPwd.getText().toString();
                String txtGender;

                //mobile number validity cheack
                String mobileRegex = "[0][1,3][6-9]";
                Matcher mobileMatcher;
                Pattern mobilePatter = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePatter.matcher(txtMobile);


                if(TextUtils.isEmpty(txtFullName)){
                    Toast.makeText(RegisterationActivity.this, "Please enter your FullName", Toast.LENGTH_SHORT).show();
                    editTextRegistrationFullName.setError("Full Name is Required");
                    editTextRegistrationFullName.requestFocus();
                }
                else if(TextUtils.isEmpty(txtEmail)){
                    Toast.makeText(RegisterationActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    editTextRegistrationEmail.setError("Email Name is Required");
                    editTextRegistrationEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
                    Toast.makeText(RegisterationActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    editTextRegistrationEmail.setError("Valid email Name is Required");
                    editTextRegistrationEmail.requestFocus();
                }

                else if(TextUtils.isEmpty(txtDoB)){
                    Toast.makeText(RegisterationActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    editTextRegistrationDoB.setError("Date of Birth is Required");
                    editTextRegistrationDoB.requestFocus();
                }
                else if(radioGroupRegistargetGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(RegisterationActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(txtMobile)){
                    Toast.makeText(RegisterationActivity.this, "Please enter your Mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegistrationMobile.setError("Mobile number is Required");
                    editTextRegistrationMobile.requestFocus();
                }
                else if(!mobileMatcher.find()){
                    Toast.makeText(RegisterationActivity.this, "Please enter Valid Mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegistrationMobile.setError("Valid Mobile number is Required");
                    editTextRegistrationMobile.requestFocus();
                }
                else if(txtMobile.length()!=11){
                    Toast.makeText(RegisterationActivity.this, "Please enter your Mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegistrationFullName.setError("Valid number is Required");
                    editTextRegistrationFullName.requestFocus();
                }
                else if(TextUtils.isEmpty(txtPwd)){
                    Toast.makeText(RegisterationActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextRegistrationPwd.setError("Password is Required");
                    editTextRegistrationPwd.requestFocus();
                }
                else if(txtPwd.length()<6){
                    Toast.makeText(RegisterationActivity.this, "Password is weeked", Toast.LENGTH_SHORT).show();
                    editTextRegistrationPwd.setError("Password should be at least 6 digit");
                    editTextRegistrationPwd.requestFocus();
                }
                else if(TextUtils.isEmpty(txtComformPwd)){
                    Toast.makeText(RegisterationActivity.this, "Please comform password", Toast.LENGTH_SHORT).show();
                    editTextRegistrationComformPwd.setError("password is Required again");
                    editTextRegistrationComformPwd.requestFocus();
                }
                else if(!txtPwd.equals(txtComformPwd)){
                    Toast.makeText(RegisterationActivity.this, "Please enter same password", Toast.LENGTH_SHORT).show();
                    editTextRegistrationComformPwd.setError("Password doesn't match");
                    editTextRegistrationComformPwd.requestFocus();
                    editTextRegistrationPwd.clearComposingText();
                    editTextRegistrationComformPwd.clearComposingText();
                }
                else {
                    txtGender = radioButtonRegistrartiongetGenderSelect.getText().toString();
                    progressBarRegistration.setVisibility(View.VISIBLE);
                    registerUser(txtFullName,txtEmail,txtDoB,txtGender,txtMobile,txtPwd,txtComformPwd);

                }

            }
        });
    }

    private void registerUser(String txtFullName, String txtEmail, String txtDoB, String txtGender, String txtMobile, String txtPwd, String txtComformPwd) {


        Toast.makeText(RegisterationActivity.this, "registration Button Button is clicked", Toast.LENGTH_SHORT).show();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(txtEmail,txtPwd).addOnCompleteListener(RegisterationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBarRegistration.setVisibility(View.GONE);

                if(task.isSuccessful()){
                    Toast.makeText(RegisterationActivity.this, "User Register SuccessFul", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(txtFullName).build();
                    firebaseUser.updateProfile(userProfileChangeRequest);

                    firebaseUser.sendEmailVerification();

                    //store data in real time database start from here
                    ReasWriteUserDetails writeUserDetails = new ReasWriteUserDetails(txtFullName,txtDoB,txtGender,txtMobile);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered User");

                    reference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> taskk) {

                            if(taskk.isSuccessful()) {

                                //Open User Profile Using Successful Registration
                                Intent intent = new Intent(RegisterationActivity.this,UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterationActivity.this, "Something is wrong,plese try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    //store data in real time database end from here
                }else {
                        try {
                            throw task.getException();
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            editTextRegistrationPwd.setError("This email is already used use another one");
                        }
                        catch (Exception e) {
                            Log.e("errod",e.getMessage());
                            Toast.makeText(RegisterationActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBarRegistration.setVisibility(View.GONE);
                        }

                }


            }
        });
    }
}