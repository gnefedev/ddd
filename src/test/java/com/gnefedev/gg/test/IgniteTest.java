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

import java.util.List;

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
        User user = new User("Ivan", "Ivanov");
        userRepository.save(user);
        assertNotEquals(-1, user.getId());

        User fetched = userRepository.get(user.getId());
        assertEquals("Ivanov", fetched.getFamilyName());

        fetched.setFamilyName("Petrov");
        userRepository.save(fetched);

        fetched = userRepository.get(user.getId());
        assertEquals("Petrov", fetched.getFamilyName());

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
            userRepository.save(user);
            assertTrue(false);
        } catch (NoTransactionInActive ignored) {
        }
    }

    @Transactional
    @Test
    public void repositoryRegister() {
        User user = new User("Ivan", "Ivanov");
        userRepository.save(user);
        assertEquals(
                "Ivanov",
                RepositoryRegister
                        .repository(User.class)
                        .get(user.getId())
                        .getFamilyName()
        );
    }

    @Test
    public void transaction() {
        TransactionStatus transaction = transactionManager.getTransaction(null);
        User user = new User();
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

    @Test
    public void referenceStore() {
        TransactionStatus transaction = transactionManager.getTransaction(null);
        User user = new User();
        userRepository.save(user);
        EntityId<User> userId = user.getId();

        assertTrue(user == userRepository.get(userId));
        transactionManager.commit(transaction);

        transaction = transactionManager.getTransaction(null);

        User fetched = userRepository.get(userId);
        assertFalse(user == fetched);
        userRepository.remove(fetched);

        try {
            userRepository.get(userId);
            assertTrue(false);
        } catch (NoSuchObject ignored) {
        }

        transactionManager.commit(transaction);
    }

    @Test
    public void sqlQuery() {
        TransactionStatus transaction = transactionManager.getTransaction(null);
        userRepository.save(new User("Ivan", "Ivanov"));
        userRepository.save(new User("Petr", "Ivanov"));
        transactionManager.commit(transaction);

        transaction = transactionManager.getTransaction(null);
        List<User> users = userRepository.findByFamilyName("Ivanov");
        assertEquals(2, users.size());
        transactionManager.commit(transaction);
    }
}
