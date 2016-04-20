package com.gnefedev.gg.shop;

import com.gnefedev.gg.infrostructure.repository.EntityId;

/**
 * Created by SBT-Nefedev-GV on 18.04.2016.
 */
public class OrderLine {
    private final EntityId<Item> itemId;
    private final int quantity;
    private final double price;

    public OrderLine(EntityId<Item> itemId, int quantity, double price) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public EntityId<Item> getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }
}
