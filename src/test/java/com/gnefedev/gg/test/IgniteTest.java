package com.gnefedev.gg.test;

import com.gnefedev.gg.config.GGConfig;
import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.RepositoryRegister;
import com.gnefedev.gg.infrostructure.repository.exception.NoSuchObject;
import com.gnefedev.gg.infrostructure.repository.exception.NoTransactionInActive;
import com.gnefedev.gg.user.UserRepository;
import com.gnefedev.gg.user.model.User;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@ContextConfiguration(classes = GGConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IgniteTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private static EntityId<User> userId;

    @Transactional
    @Test
    public void crud() {
        User user = new User();
        user.setName("Ivan Ivanov");
        userRepository.save(user);
        assertNotEquals(-1, user.getId());

        User fetched = userRepository.get(user.getId());
        assertEquals("Ivan Ivanov", fetched.getName());

        fetched.setName("Petr Petrov");
        userRepository.save(fetched);

        fetched = userRepository.get(user.getId());
        assertEquals("Petr Petrov", fetched.getName());

        userRepository.remove(fetched);

        try {
            userRepository.get(user.getId());
            assertTrue(false);
        } catch (NoSuchObject ignored) {
        }
    }

    @Test
    public void workWithoutTransaction() {
        try {
            User user = new User();
            user.setName("Ivan Ivanov");
            userRepository.save(user);
            assertTrue(false);
        } catch (NoTransactionInActive ignored) {
        }
    }

    @Transactional
    @Test
    public void repositoryRegister() {
        User user = new User();
        user.setName("Ivan Ivanov");
        userRepository.save(user);
        assertEquals(
                "Ivan Ivanov",
                RepositoryRegister
                        .repository(User.class)
                        .get(user.getId())
                        .getName()
        );
    }

    @Test
    public void transaction() {
        TransactionStatus transaction = transactionManager.getTransaction(null);
        User user = new User();
        user.setName("Ivan Ivanov");
        userRepository.save(user);
        EntityId<User> userId = user.getId();
        assertNotEquals(-1, userId);

        transactionManager.rollback(transaction);

        transaction = transactionManager.getTransaction(null);
        try {
            userRepository.get(userId);
            assertTrue(false);
        } catch (NoSuchObject ignored) {
        }
        transactionManager.rollback(transaction);
    }

    @Transactional
    @Rollback
    @Test
    public void transactionalAnnotation1() {
        User user = new User();
        user.setName("Ivan Ivanov");
        userRepository.save(user);
        userId = user.getId();
        assertNotEquals(-1, userId);
    }

    @Transactional
    @Test
    public void transactionalAnnotation2() {
        try {
            userRepository.get(userId);
            assertTrue(false);
        } catch (NoSuchObject ignored) {
        }
    }
}
