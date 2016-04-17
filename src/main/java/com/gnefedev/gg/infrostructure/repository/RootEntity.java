package com.gnefedev.gg.infrostructure.repository;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
public class RootEntity {
    private long id = -1;

    public long getId() {
        return id;
    }

    RootEntity setId(long id) {
        this.id = id;
        return this;
    }
}
