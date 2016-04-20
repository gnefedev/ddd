package com.gnefedev.gg.delivery;

import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.RootEntity;
import com.gnefedev.gg.shop.OrderList;

/**
 * Created by SBT-Nefedev-GV on 18.04.2016.
 */
public class Delivery extends RootEntity<Delivery, EntityId<Delivery>> {
    private final EntityId<OrderList> orderId;
    private Address address;

    public Delivery(EntityId<OrderList> orderId, Address address) {
        this.orderId = orderId;
        this.address = address;
    }

    public EntityId<OrderList> getOrderId() {
        return orderId;
    }

    public Address getAddress() {
        return address;
    }

    public Delivery setAddress(Address address) {
        this.address = address;
        return this;
    }
}
