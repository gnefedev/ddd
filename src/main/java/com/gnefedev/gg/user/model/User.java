package com.gnefedev.gg.user.model;

import com.gnefedev.gg.infrostructure.repository.RootEntity;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
public class User extends RootEntity {
    private String name;
    private Address address;

    public Address getAddress() {
        return address;
    }

    public User setAddress(Address address) {
        this.address = address;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }
}
