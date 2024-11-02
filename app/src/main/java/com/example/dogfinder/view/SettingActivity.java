package com.example.dogfinder.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dogfinder.R;
import com.example.dogfinder.Util.Loader;
import com.example.dogfinder.Util.Messenger;
import com.example.dogfinder.Util.SessionManager;
import com.example.dogfinder.api.PostCallback;
import com.example.dogfinder.api.PostTask;
import com.example.dogfinder.model.Officer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity {

    private CardView cvEditName;
    private CardView cvChangePassword;
    private CardView cvChangeAge;
    private Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        cvEditName = findViewById(R.id.cvEditName);
        cvChangePassword = findViewById(R.id.cvEditPassword);
        cvChangeAge = findViewById(R.id.cvEditAge);



        cvEditName.setOnClickListener(this::changeName);
        cvChangePassword.setOnClickListener(this::changePassword);
        cvChangeAge.setOnClickListener(this::changeAge);
        loader = new Loader();


    }

    private void changeAge(View view) {

        // implement change age
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_age, null);

        TextInputEditText etAge = dialogView.findViewById(R.id.etAge);
        Officer officer = SessionManager.getInstance(this).getOfficer();
        etAge.setText(String.valueOf(officer.getOfficerAge()));


        builder.setTitle("Change Age")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    try {
                        String age = etAge.getText().toString();
                        loader.showLoader(this);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("officerAge", age);
                        jsonObject.put("officerId", officer.getOfficerId());

                        new PostTask(SettingActivity.this, new PostCallback() {
                            @Override
                            public void onPostSuccess(String responseData) {
                                hideLoader();
                                officer.setOfficerAge(Integer.parseInt(age));
                                SessionManager.getInstance(SettingActivity.this).saveLoggedInEmployee(officer);
                                Messenger.showAlertDialog(SettingActivity.this, "Success", "Age changed successfully", "Ok").show();

                            }

                            @Override
                            public void onPostError(String errorMessage) {
                                hideLoader();
                                Messenger.showAlertDialog(SettingActivity.this, "Error", errorMessage, "Ok").show();
                            }
                        }, "error", "change-age.php").execute(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.create().show();


    }

    private void changePassword(View view) {

        // implement change password
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

        TextInputEditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);
        Officer officer = SessionManager.getInstance(this).getOfficer();

        builder.setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    String oldPassword = etOldPassword.getText().toString();
                    String newPassword = etNewPassword.getText().toString();
                    String confirmPassword = etConfirmPassword.getText().toString();

                    if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                        Messenger.showAlertDialog(this, "Error", "All fields are required", "Ok").show();
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        Messenger.showAlertDialog(this, "Error", "Passwords do not match", "Ok").show();
                        return;
                    }

                    try {
                        loader.showLoader(this);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("oldPassword", oldPassword);
                        jsonObject.put("newPassword", newPassword);
                        jsonObject.put("officerId", officer.getOfficerId());

                        new PostTask(SettingActivity.this, new PostCallback() {
                            @Override
                            public void onPostSuccess(String responseData) {
                                hideLoader();
                                Messenger.showAlertDialog(SettingActivity.this, "Success", "Password changed successfully", "Ok").show();
                            }

                            @Override
                            public void onPostError(String errorMessage) {
                                hideLoader();
                                Messenger.showAlertDialog(SettingActivity.this, "Error", errorMessage, "Ok").show();
                            }
                        }, "error", "change-password.php").execute(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.create().show();
    }

    private void hideLoader() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loader.dismissLoader();
            }
        }, 1500);
    }

    private void changeName(View view) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_name, null);

        TextInputEditText etFirstName = dialogView.findViewById(R.id.etFirstName);
        TextInputEditText etLastName = dialogView.findViewById(R.id.etLastName);
        TextInputEditText etMiddleName = dialogView.findViewById(R.id.etMiddleName);
        TextInputEditText etSuffix = dialogView.findViewById(R.id.etSuffix);

        Officer officer = SessionManager.getInstance(this).getOfficer();

        etFirstName.setText(officer.getOfficerName());
        etLastName.setText(officer.getOfficerLName());
        etMiddleName.setText(officer.getOfficerMName());
        etSuffix.setText(officer.getOfficerSuffix());


        builder.setTitle("Change Name")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {


                    String firstName = etFirstName.getText().toString();
                    String lastName = etLastName.getText().toString();
                    String middleName = etMiddleName.getText().toString();
                    String suffix = etSuffix.getText().toString();

                    if (firstName.isEmpty() || lastName.isEmpty()) {
                        Messenger.showAlertDialog(this, "Error", "First name and last name are required", "Ok").show();

                        return;
                    }

                    try {
                        loader.showLoader(this);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("officerName", firstName);
                        jsonObject.put("officerLName", lastName);
                        jsonObject.put("officerMName", middleName);
                        jsonObject.put("officerSuffix", suffix);
                        jsonObject.put("officerId", officer.getOfficerId());

                        new PostTask(SettingActivity.this, new PostCallback() {
                            @Override
                            public void onPostSuccess(String responseData) {
                                hideLoader();
                                officer.setOfficerName(firstName);
                                officer.setOfficerLName(lastName);
                                officer.setOfficerMName(middleName);
                                officer.setOfficerSuffix(suffix);


                                SessionManager.getInstance(SettingActivity.this).saveLoggedInEmployee(officer);
                                Messenger.showAlertDialog(SettingActivity.this, "Success", "Name changed successfully", "Ok").show();
                            }

                            @Override
                            public void onPostError(String errorMessage) {
                                hideLoader();
                                Messenger.showAlertDialog(SettingActivity.this, "Error", errorMessage, "Ok").show();
                            }
                        }, "error", "change-name.php").execute(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.create().show();

    }
}