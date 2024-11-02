package com.example.dogfinder.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dogfinder.model.Officer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SessionManager {
    private static SessionManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private static final String PREF_NAME = "EmployeePrefs";
    private static final String KEY_LOGGED_IN_EMPLOYEE = "loggedInEmployee";

    private SessionManager(Context context) {

        this.gson = new GsonBuilder().create();
        // Initialize SharedPreferences
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }


    public void saveLoggedInEmployee(Officer employee) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(employee);
        editor.putString(KEY_LOGGED_IN_EMPLOYEE, json);
        editor.apply();
    }


    public Officer getOfficer() {
        String json = sharedPreferences.getString(KEY_LOGGED_IN_EMPLOYEE, null);
        return gson.fromJson(json, Officer.class);
    }


    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGGED_IN_EMPLOYEE);
        editor.apply();
    }


    public boolean login(Officer employee) {

        saveLoggedInEmployee(employee);
        return true;
    }
}
