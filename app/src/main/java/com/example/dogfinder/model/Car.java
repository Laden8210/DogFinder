package com.example.dogfinder.model;

import com.google.gson.annotations.SerializedName;

public class Car {

//       "car_id": "1",
//               "car_name": "a",
//               "car_plate_no": "1234"

    @SerializedName("car_id")
    private String carId;

    @SerializedName("car_name")
    private String carName;

    @SerializedName("car_plate_no")
    private String carPlateNo;


    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarPlateNo() {
        return carPlateNo;
    }

    public void setCarPlateNo(String carPlateNo) {
        this.carPlateNo = carPlateNo;
    }

    @Override
    public String toString() {
        return carName + " (" + carPlateNo + ")";
    }
}
