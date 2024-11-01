package com.example.dogfinder.api;

public interface DeleteCallback {
    void onDeleteSuccess(String response);
    void onDeleteFail(String error);
}
