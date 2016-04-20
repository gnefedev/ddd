package com.gnefedev.gg.shop;

import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.RootEntity;
import com.gnefedev.gg.user.User;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gerakln on 17.04.16.
 */
public class OrderList extends RootEntity<OrderList, EntityId<OrderList>> {
    public void fix() {
        status = Status.FIXED;
    }

    public void recalculate(EntityId<Item> itemId, double price) {
        if (orderLines.containsKey(itemId)) {
            OrderLine oldOrderLine = orderLines.get(itemId);
            buy(oldOrderLine.getItemId(), price, oldOrderLine.getQuantity());
        }
    }

    enum Status {
        NEW, FIXED
    }

    public boolean isFixed() {
        return status.equals(Status.FIXED);
    }
    @QuerySqlField(index = true)
    private Status status = Status.NEW;
    private double sum = 0.0;
    private Map<EntityId<Item>, OrderLine> orderLines = new HashMap<>();
    @QuerySqlField(index = true)
    private final Long userId;

    public OrderList(EntityId<User> userId) {
        this.userId = userId.getId();
    }

    public double getSum() {
        return sum;
    }

    public void buy(Item item, int quantity){
        buy(item.getId(), item.getPrice(), quantity);
    }

    private void buy(EntityId<Item> itemId, double price, int quantity) {
        if (orderLines.containsKey(itemId)) {
            OrderLine oldOrderLine = orderLines.get(itemId);
            sum-= oldOrderLine.getPrice() * oldOrderLine.getQuantity();
        }
        sum+= price * quantity;
        orderLines.put(itemId, new OrderLine(itemId, quantity, price));
    }
}
