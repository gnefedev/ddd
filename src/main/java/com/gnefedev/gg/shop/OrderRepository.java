package com.gnefedev.gg.shop;

import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.RepositoryForJava;
import com.gnefedev.gg.infrostructure.repository.RepositoryRegister;
import org.springframework.stereotype.Component;

/**
 * Created by gerakln on 17.04.16.
 */
@Component
public class OrderRepository extends RepositoryForJava<OrderList> {
    @Override
    protected Class<OrderList> entityClass() {
        return OrderList.class;
    }

    public static EntityId<OrderList> createNewOrder() {
        OrderList orderList = new OrderList();
        RepositoryRegister.repository(OrderList.class).save(orderList);
        return orderList.getId();
    }
}
