package com.gnefedev.gg.user;

import com.gnefedev.gg.delivery.Address;
import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.RootEntity;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gerakln on 17.04.16.
 */
public class User extends RootEntity<User, EntityId<User>> {
    @QuerySqlField
    private String name;
    @QuerySqlField
    private String familyName;

    private List<Address> addresses = new ArrayList<>();

    public void registerAddress(Address address) {
        addresses.add(address);
    }

    public Address getDefaultAddress() {
        return addresses.get(0);
    }

    public User() {
    }

    public User(String name, String familyName) {
        this.name = name;
        this.familyName = familyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
