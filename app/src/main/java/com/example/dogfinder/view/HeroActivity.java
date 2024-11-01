package com.example.dogfinder.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.dogfinder.R;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HeroActivity extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 2;
    private CardView cvCamera, cvGallery, cvSetting;
    private ImageView ivCamera, ivGallery, ivSetting;
    private Bitmap capturedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero);

        ivCamera = findViewById(R.id.camera);
        ivGallery = findViewById(R.id.gallery);
        ivSetting = findViewById(R.id.setting);


        cvCamera = findViewById(R.id.cvCamera);
        cvGallery = findViewById(R.id.cvGallery);
        cvSetting = findViewById(R.id.cvSetting);

        loadGif(ivCamera, R.drawable.camera);
        loadGif(ivSetting, R.drawable.setting);
        loadGif(ivGallery, R.drawable.gallery);


        cvCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        });

        cvGallery.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            if (imageUri != null) {

            }
        }
    }


    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                capturedImage = (Bitmap) data.getExtras().get("data");


                String captureTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            }
        }
    });



    public void loadGif(ImageView iv, int gifID) {
        Glide.with(this).load(gifID).into(iv);
    }
}