package com.gnefedev.gg.shop;

import com.gnefedev.gg.infrostructure.event.Event;
import com.gnefedev.gg.infrostructure.repository.EntityId;

/**
 * Created by SBT-Nefedev-GV on 18.04.2016.
 */
public class PriceChanged extends Event {
    private final EntityId<Item> itemId;
    private final double oldPrice;
    private final double newPrice;

    public PriceChanged(EntityId<Item> itemId, double oldPrice, double newPrice) {
        this.itemId = itemId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    public EntityId<Item> getItemId() {
        return itemId;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }
}
