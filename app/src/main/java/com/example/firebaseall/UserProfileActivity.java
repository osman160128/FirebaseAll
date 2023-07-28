package com.example.firebaseall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewWelcom,textViewFullName,textViewEmail,textViewDOB,textViewGender,textViewMobile;
    private ProgressBar progressBar;
    private String welcome,name,email,dob,gender,mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        textViewWelcom = findViewById(R.id.show_welcome);
        textViewFullName= findViewById(R.id.text_view_show_full_name);
        textViewEmail=findViewById(R.id.text_view_show_email);
        textViewDOB=findViewById(R.id.text_view_show_dob);
        textViewGender=findViewById(R.id.text_view_show_gender);
        textViewMobile=findViewById(R.id.text_view_show_mobile);
        progressBar=findViewById(R.id.profileProgressBar);

        imageView = findViewById(R.id.imageview_profile_dp);

        //GO TO IMage upload profile image activity
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this,UploadProfilePhoto.class);
                startActivity(intent);
            }
        });


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(this, "your details is not vaild at the moment", Toast.LENGTH_SHORT).show();
        }else{
            cheakIfEmailVarified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

    }

    private void cheakIfEmailVarified(FirebaseUser firebaseUser){
        if(!firebaseUser.isEmailVerified()){
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
            builder.setTitle("Email not Verified ");
            builder.setMessage("Please Varified your your email now.you can not login without varifiaction next time");
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



    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID= firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered User");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReasWriteUserDetails readUserDeatials = snapshot.getValue(ReasWriteUserDetails.class);
                if(readUserDeatials!=null){
                    name = readUserDeatials.name;
                    email=firebaseUser.getEmail();
                    dob = readUserDeatials.doB;
                    gender= readUserDeatials.gender;
                    mobile=readUserDeatials.mobile;


                    textViewFullName.setText(name);
                    textViewEmail.setText(email);
                    textViewDOB.setText(dob);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                    //get url for add photo
                    Uri uri = firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).into(imageView);

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
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
            Intent intent = new Intent(UserProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(UserProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }
        /*
        else if (id==R.id.menu_setting) {
            Intent intent = new Intent(UserProfileActivity.this,SettingActivity.class);
            startActivity(intent);
        }
        */
        else if (id==R.id.menu_update_password) {
            Intent intent = new Intent(UserProfileActivity.this,ChangePasswordActivity.class);
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

            Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);


    }


}