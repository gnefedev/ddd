package com.gnefedev.gg.test;

import com.gnefedev.gg.config.GGConfig;
import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.RepositoryRegister;
import com.gnefedev.gg.infrostructure.repository.exception.NoSuchObject;
import com.gnefedev.gg.infrostructure.repository.exception.NoTransactionInActive;
import com.gnefedev.gg.user.User;
import com.gnefedev.gg.user.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@ContextConfiguration(classes = GGConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class IgniteTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private TransactionalBean transactionalBean;

    @Before
    @After
    public void clear() {
        TransactionStatus transaction = transactionManager.getTransaction(null);
        for (User user : userRepository.findByFamilyName("Ivanov")) {
            userRepository.remove(user);
        }
        transactionManager.commit(transaction);
    }

    @Transactional
    @Test
    public void crud() {
        User user = new User("Ivan", "Ivanov");
        userRepository.save(user);
        assertNotEquals(-1, user.getId());

        User fetched = userRepository.get(user.getId());
        assertEquals("Ivanov", fetched.getFamilyName());

        fetched.setAge(25);
        userRepository.save(fetched);

        fetched = userRepository.get(user.getId());
        assertEquals(25, fetched.getAge());

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
            User user = new User("Ivan", "Ivanov");
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
        User user = new User("Ivan", "Ivanov");
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

    @Test
    public void referenceStore() {
        TransactionStatus transaction = transactionManager.getTransaction(null);
        User user = new User("Ivan", "Ivanov");
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
        User ivan = new User("Ivan", "Ivanov");
        userRepository.save(ivan);
        userRepository.save(new User("Petr", "Ivanov"));
        transactionManager.commit(transaction);

        transaction = transactionManager.getTransaction(null);
        ivan = userRepository.get(ivan.getId());

        List<User> users = userRepository.findByFamilyName("Ivanov");
        assertEquals(2, users.size());
        assertEquals("Ivan", users.get(0).getName());
        assertTrue(ivan == users.get(0));

        User petr = userRepository.findByNameAndFamily("Petr", "Ivanov");
        assertNotNull(petr);
        transactionManager.commit(transaction);
    }

    @Test(expected = TransactionSuspensionNotSupportedException.class)
    @Transactional
    public void transactionalRequireNew() {
        transactionalBean.requireNew();
    }
}
