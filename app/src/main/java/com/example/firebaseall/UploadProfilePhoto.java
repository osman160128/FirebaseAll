package com.example.firebaseall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.FocusFinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePhoto extends AppCompatActivity {

    Button choseImgButton,uploadImgButton;
    ImageView imageView;

    FirebaseAuth authProfile;

    StorageReference storageReference;
    FirebaseUser firebaseUser;
    int PICK_IMAGE_REQ = 1;
    private  Uri imgUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_photo);

        imageView = findViewById(R.id.uploadImage);
        choseImgButton = findViewById(R.id.choseImage);
        uploadImgButton = findViewById(R.id.sendImgtoFireBase);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("DisplayPic");
        //if already chosen shoe it
        Uri uri = firebaseUser.getPhotoUrl();
        Picasso.get().load(uri).into(imageView);
        //else chose the picture
        choseImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFIleChoser();
            }
        });
        uploadImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadPic();
                Toast.makeText(UploadProfilePhoto.this, "upload file", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //uplod image to the storage
    private void UploadPic() {
        Toast.makeText(UploadProfilePhoto.this, "upload file 2", Toast.LENGTH_SHORT).show();
        if(imgUri!=null){

            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid()+"."
                    +getFIleExtention(imgUri));
            fileReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = authProfile.getCurrentUser();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri).build();
                            firebaseUser.updateProfile(profileUpdate);
                            Toast.makeText(UploadProfilePhoto.this, "upload file 3", Toast.LENGTH_SHORT).show();

                        }
                    });
                    Intent intent = new Intent(UploadProfilePhoto.this,UserProfileActivity.class);
                    startActivity(intent);
                }

            });
        }
        else {
            Toast.makeText(UploadProfilePhoto.this, ""+imgUri, Toast.LENGTH_SHORT).show();
        }



    }

    private String getFIleExtention(Uri imgUri) {

        ContentResolver cR = getContentResolver();
        MimeTypeMap mime =MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(imgUri));

    }

    private void openFIleChoser() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQ);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_REQ && resultCode ==RESULT_OK && data!=null && data.getData()!=null)){
            imgUri = data.getData();
            imageView.setImageURI(imgUri);
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
       /*
        if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);

        } else if (id==R.id.menu_update_profile) {
            Intent intent = new Intent(UserProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(UserProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }
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

            Intent intent = new Intent(UploadProfilePhoto.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);


    }
}