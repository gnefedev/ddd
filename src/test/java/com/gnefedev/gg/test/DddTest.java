package com.gnefedev.gg.test;

import com.gnefedev.gg.config.GGConfig;
import com.gnefedev.gg.delivery.Address;
import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.shop.OrderList;
import com.gnefedev.gg.shop.OrderRepository;
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

import static org.junit.Assert.assertNotNull;

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
    private static EntityId<User> ivanId;

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
    public void _02createOrder() {
        User ivan = userRepository.get(ivanId);
        EntityId<OrderList> orderId = ivan.newOrder();
        assertNotNull(orderRepository.get(orderId));
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
