package com.example.dogfinder.view;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dogfinder.R;
import com.example.dogfinder.Util.Loader;
import com.example.dogfinder.Util.Messenger;
import com.example.dogfinder.Util.SessionManager;
import com.example.dogfinder.api.ApiAddress;
import com.example.dogfinder.api.ApiService;
import com.example.dogfinder.api.PostCallback;
import com.example.dogfinder.api.PostTask;
import com.example.dogfinder.model.Car;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Part;

public class ResultActivity extends AppCompatActivity {

    private Loader loader;
    private Location location;
    private ImageView ivResult;

    private TextInputEditText etDogColor;
    private TextInputEditText etDogBreed;

    private Spinner spCar;

    private TextInputEditText etLocation;

    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        etLocation = findViewById(R.id.etLocation);
        etDogColor = findViewById(R.id.etDogColor);
        etDogBreed = findViewById(R.id.etDogBreed);
        spCar = findViewById(R.id.sp_car);



        new PostTask(this, new PostCallback() {
            @Override
            public void onPostSuccess(String responseData) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Car>>() {}.getType();
                List<Car> cars = gson.fromJson(responseData, listType);
                ArrayAdapter<Car> adapter = new ArrayAdapter<>(ResultActivity.this, android.R.layout.simple_spinner_item, cars);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCar.setAdapter(adapter);
                for (Car car : cars) {
                    Log.d("Car", car.toString());
                }
            }

            @Override
            public void onPostError(String errorMessage) {

            }
        }, "error", "get-car.php").execute(new JSONObject());


        loader = new Loader();


        ivResult = findViewById(R.id.result);
        if (getIntent().hasExtra("capturedImage")) {
            ivResult.setImageBitmap((Bitmap) getIntent().getParcelableExtra("capturedImage"));

            if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
                double latitude = getIntent().getDoubleExtra("latitude", 0);
                double longitude = getIntent().getDoubleExtra("longitude", 0);

                location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                getAddress(latitude, longitude);
            }

        } else if (getIntent().hasExtra("capturedImageUri")) {
            Uri imageUri = Uri.parse(getIntent().getStringExtra("capturedImageUri"));
            ivResult.setImageURI(imageUri);

        }

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this::saveAction);
    }

    private void saveAction(View view) {


        String dogColor = etDogColor.getText().toString();
        String dogBreed = etDogBreed.getText().toString();
        String location = etLocation.getText().toString();
        String carId = ((Car) spCar.getSelectedItem()).getCarId();


        if (location.isEmpty()) {
            Messenger.showAlertDialog(this, "Location", "Location is required", "Ok").show();
            return;
        }

        if (dogColor.isEmpty()) {
            Messenger.showAlertDialog(this, "Dog color", "Dog color is required", "Ok").show();
            return;
        }

        if (dogBreed.isEmpty()) {
            Messenger.showAlertDialog(this, "Dog breed", "Dog breed is required", "Ok").show();
            return;
        }


        Bitmap bitmap = ((BitmapDrawable) ivResult.getDrawable()).getBitmap();
        loader.showLoader(this);
        if (bitmap != null) {
            byte[] imageUri = imageToBase64(bitmap);

            uploadImage(imageUri, dogColor, dogBreed, location, carId);
        } else {
            Messenger.showAlertDialog(this, "Error", "Image is required", "Ok").show();
        }
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String addressLine = address.getAddressLine(0);

                this.etLocation.setText(addressLine);
            } else {
                Log.d("Address", "No address found for the given coordinates.");
            }
        } catch (IOException e) {
            Log.e("Address", "Geocoder failed", e);
        }
    }

    private void uploadImage(byte[] image, String dogColor, String dogBreed, String location, String carId) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiAddress.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.d("Upload", "Dog color: " + dogColor);
        Log.d("Upload", "Dog breed: " + dogBreed);
        Log.d("Upload", "Location: " + location);


        Log.d("Url", retrofit.baseUrl().toString());

        ApiService apiService = retrofit.create(ApiService.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), image);

        String filename = System.currentTimeMillis() + ".jpg";
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", filename, requestFile);

        RequestBody dogColorPart = RequestBody.create(MediaType.parse("text/plain"), dogColor);
        RequestBody dogBreedPart = RequestBody.create(MediaType.parse("text/plain"), dogBreed);
        RequestBody locationPart = RequestBody.create(MediaType.parse("text/plain"), location);



        RequestBody officerIdPart = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(SessionManager.getInstance(this).getOfficer().getOfficerId()));

        RequestBody carIdPart = RequestBody.create(MediaType.parse("text/plain"), carId);
        Call<ResponseBody> call = apiService.uploadImage(body, dogColorPart, dogBreedPart, locationPart, officerIdPart, carIdPart);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loader.dismissLoader();
                        if (response.isSuccessful()) {

                            try {
                                String jsonResponse = response.body().string();
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                String message = jsonObject.getString("message");

                                Messenger.showAlertDialog(ResultActivity.this, "Success", message, "Ok").show();
                                etDogColor.setText("");
                                etDogBreed.setText("");
                                etLocation.setText("");
                                ivResult.setImageResource(0);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Messenger.showAlertDialog(ResultActivity.this, "Error", "Failed to upload image", "Ok").show();
                        }
                    }
                }, 1000);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loader.dismissLoader(); // Dismiss the loader after a delay
                        Log.d("Upload", "Error: " + t.getMessage());
                        Messenger.showAlertDialog(ResultActivity.this, "Error", t.getMessage(), "Ok").show();
                    }
                }, 1000);
            }
        });
    }

    private byte[] imageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}