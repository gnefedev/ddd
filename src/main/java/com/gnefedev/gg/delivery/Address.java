package com.gnefedev.gg.delivery;

/**
 * Created by gerakln on 17.04.16.
 */
public class Address {
    private final String city;
    private final String district;

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public Address(String city, String district) {
        this.city = city;
        this.district = district;
    }
}
