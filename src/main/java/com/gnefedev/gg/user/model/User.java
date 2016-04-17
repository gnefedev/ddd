package com.gnefedev.gg.user.model;

import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.RootEntity;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * Created by gerakln on 17.04.16.
 */
public class User extends RootEntity<User, EntityId<User>> {
    @QuerySqlField
    private String name;
    @QuerySqlField
    private String familyName;

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
