package com.gnefedev.gg.shop;

import com.gnefedev.gg.infrostructure.event.EventRegister;
import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.RootEntity;

/**
 * Created by SBT-Nefedev-GV on 18.04.2016.
 */
public class Item extends RootEntity<Item, EntityId<Item>> {
    private String name;
    private double price;

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Item setPrice(double price) {
        this.price = price;
        EventRegister.fire(new PriceChanged(getId(), this.price, price));
        return this;
    }
}
