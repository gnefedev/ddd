package com.gnefedev.gg.delivery;

import com.gnefedev.gg.infrostructure.event.EventRegister;
import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.shop.OrderList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by SBT-Nefedev-GV on 18.04.2016.
 */
@Component
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    public EntityId<Delivery> newDelivery(EntityId<OrderList> order, Address address) {
        EntityId<Delivery> deliveryId = deliveryRepository.save(new Delivery(order, address));
        EventRegister.fire(new DeliveryFixed(deliveryId, order));
        return deliveryId;
    }
}
