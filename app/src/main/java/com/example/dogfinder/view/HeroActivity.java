package com.example.dogfinder.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.dogfinder.MainActivity;
import com.example.dogfinder.R;
import com.example.dogfinder.Util.Messenger;
import com.example.dogfinder.Util.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HeroActivity extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 2;
    private CardView cvCamera, cvGallery, cvSetting, cvLogout;
    private ImageView ivCamera, ivGallery, ivSetting, ivLogout;
    private Bitmap capturedImage;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero);

        ivCamera = findViewById(R.id.camera);
        ivGallery = findViewById(R.id.gallery);
        ivSetting = findViewById(R.id.setting);
        ivLogout = findViewById(R.id.logout);


        cvCamera = findViewById(R.id.cvCamera);
        cvGallery = findViewById(R.id.cvGallery);
        cvSetting = findViewById(R.id.cvSetting);
        cvLogout = findViewById(R.id.cvLogout);

        loadGif(ivCamera, R.drawable.camera);
        loadGif(ivSetting, R.drawable.setting);
        loadGif(ivGallery, R.drawable.gallery);
        loadGif(ivLogout, R.drawable.logout);

        checkLocationPermission();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        cvSetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });

        cvLogout.setOnClickListener(v -> {
            Messenger.showAlertDialog(this, "Logout", "Are you sure you want to logout?", "Yes", "No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            SessionManager.getInstance(HeroActivity.this).logout();
                            Intent intent = new Intent(HeroActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            if (imageUri != null) {

                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("capturedImageUri", imageUri.toString());
                startActivity(intent);

            }
        }
    }


    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                capturedImage = (Bitmap) data.getExtras().get("data");


                getLastKnownLocation((location) -> {
                    double latitude = location != null ? location.getLatitude() : 0;
                    double longitude = location != null ? location.getLongitude() : 0;

                    Intent intent = new Intent(this, ResultActivity.class);
                    intent.putExtra("capturedImage", capturedImage);
                    intent.putExtra("captureTime", capturedImage);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);
                });

            }
        }
    });

    private void getLastKnownLocation(OnLocationReceivedListener listener) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        listener.onLocationReceived(task.getResult());
                    } else {
                        listener.onLocationReceived(null);
                    }
                }
            });
        } else {
            listener.onLocationReceived(null);
        }
    }

    interface OnLocationReceivedListener {
        void onLocationReceived(Location location);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    public void loadGif(ImageView iv, int gifID) {
        Glide.with(this).load(gifID).into(iv);
    }
}