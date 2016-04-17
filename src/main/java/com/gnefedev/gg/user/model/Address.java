package com.gnefedev.gg.user.model;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
public class Address {
    private final String city;
    private final String district;

    public Address(String city, String district) {
        this.city = city;
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }
}
