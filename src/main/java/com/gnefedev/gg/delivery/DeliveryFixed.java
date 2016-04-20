package com.gnefedev.gg.delivery;

import com.gnefedev.gg.infrostructure.event.Event;
import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.shop.OrderList;

/**
 * Created by SBT-Nefedev-GV on 18.04.2016.
 */
public class DeliveryFixed extends Event {
    private final EntityId<Delivery> deliveryId;
    private final EntityId<OrderList> orderId;

    public DeliveryFixed(EntityId<Delivery> deliveryId, EntityId<OrderList> orderId) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
    }

    public EntityId<Delivery> getDeliveryId() {
        return deliveryId;
    }

    public EntityId<OrderList> getOrderId() {
        return orderId;
    }
}
