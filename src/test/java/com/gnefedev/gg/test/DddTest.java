package com.gnefedev.gg.test;

import com.gnefedev.gg.config.GGConfig;
import com.gnefedev.gg.delivery.Address;
import com.gnefedev.gg.delivery.DeliveryService;
import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.shop.*;
import com.gnefedev.gg.user.User;
import com.gnefedev.gg.user.UserRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by gerakln on 17.04.16.
 */
@ContextConfiguration(classes = GGConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DddTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private Shop shop;
    private static EntityId<User> ivanId;
    private static EntityId<Item> pencilId;
    private static EntityId<Item> monitorId;

    @Test
    @Transactional
    @Rollback(false)
    public void _01registerUsers() {
        User ivan = new User("Ivan", "Ivanov");
        ivan.registerAddress(new Address("Moscow", "SVAO"));
        userRepository.save(ivan);
        ivanId = ivan.getId();

        User petr = new User("Petr", "Ivanov");
        petr.registerAddress(new Address("Moscow", "VAO"));
        userRepository.save(petr);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void _02createItems() {
        Item pencil = new Item("pencil", 10.0);
        itemRepository.save(pencil);
        pencilId = pencil.getId();

        Item monitor = new Item("monitor", 1000.0);
        itemRepository.save(monitor);
        monitorId = monitor.getId();
    }

    @Test
    @Transactional
    @Rollback(false)
    public void _03createOrder() {
        EntityId<OrderList> orderId = shop.createNewOrder(ivanId);
        OrderList order = orderRepository.get(orderId);
        assertNotNull(order);

        order.buy(itemRepository.get(pencilId), 10);
        assertEquals(100.0, order.getSum(), 0.0);

        order.buy(itemRepository.get(monitorId), 1);
        assertEquals(1100.0, order.getSum(), 0.0);

        order.buy(itemRepository.get(pencilId), 20);
        assertEquals(1200.0, order.getSum(), 0.0);

        orderRepository.save(order);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void _04changePrice() {
        Item monitor = itemRepository.get(monitorId);
        monitor.setPrice(2000.0);
        itemRepository.save(monitor);
    }

    @Test
    @Transactional
    public void _05checkOrder() {
        OrderList order = orderRepository.findByUser(ivanId);
        assertEquals(2200.0, order.getSum(), 0.0);
    }


    @Test
    @Transactional
    @Rollback(false)
    public void _06createDelivery() {
        User ivan = userRepository.get(ivanId);
        OrderList order = orderRepository.findByUser(ivanId);
        deliveryService.newDelivery(order.getId(), ivan.getDefaultAddress());
        assertFalse(order.isFixed());
    }

    @Test
    @Transactional
    public void _07checkOrder() {
        OrderList order = orderRepository.findByUser(ivanId);
        assertTrue(order.isFixed());
    }

    @Test
    @Transactional
    @Rollback(false)
    public void _99clear() {
        for (User user : userRepository.findByFamilyName("Ivanov")) {
            userRepository.remove(user);
        }
    }

}
