package com.example.projectjava.UI;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import com.example.projectjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URI;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth auth;
    private Uri profilePicUri;
    private ImageView imageViewProfilePic;
    private EditText editTextUserName;
    private TextView textViewEmail;
    private Button btnUploadPic;
    private Button btnSaveChanges;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);
        editTextUserName = findViewById(R.id.editTextUserName);
        textViewEmail = findViewById(R.id.textViewEmail);
        btnUploadPic = findViewById(R.id.btnUploadPic);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        bottomNavigationView = new BottomNavigationView(this, false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null){
            textViewEmail.setText(user.getEmail());

            if(user.getPhotoUrl() != null){
                Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.icon_profile).into(imageViewProfilePic);
            }

            if(user.getDisplayName() != null && !user.getDisplayName().isEmpty()){
                editTextUserName.setHint(user.getDisplayName());
            }
        }

        btnUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPicture();
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.updateProfile(new UserProfileChangeRequest.Builder()
                                .setPhotoUri(profilePicUri)
                                .setDisplayName(editTextUserName.getText().toString())
                                .build()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileActivity.this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to update photo URL
                                        Toast.makeText(ProfileActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                                    }
                                });
            }
        });
    }

    public void uploadPicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profilePicUri = data.getData();
            imageViewProfilePic.setImageURI(profilePicUri);
        }
    }
}
