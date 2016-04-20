package com.gnefedev.gg.shop;

import com.gnefedev.gg.delivery.DeliveryFixed;
import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.EntityTransformer;
import com.gnefedev.gg.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Created by SBT-Nefedev-GV on 18.04.2016.
 */
@Component
public class Shop {
    @Autowired
    private OrderRepository orderRepository;

    public EntityId<OrderList> createNewOrder(EntityId<User> userId) {
        OrderList orderList = new OrderList(userId);
        orderRepository.save(orderList);
        return orderList.getId();
    }
    @TransactionalEventListener
    public void fixOrder(DeliveryFixed deliveryFixed) {
        OrderList order = orderRepository.get(deliveryFixed.getOrderId());
        order.fix();
        orderRepository.save(order);
    }
    @TransactionalEventListener
    public void recalculateOrders(final PriceChanged priceChanged) {
        orderRepository.transformNotFixed(new EntityTransformer<OrderList>() {
            @Override
            public OrderList transform(OrderList order) {
                order.recalculate(priceChanged.getItemId(), priceChanged.getNewPrice());
                return order;
            }
        });
    }
}
