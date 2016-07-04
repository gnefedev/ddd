package com.gnefedev.gg.test;

import com.gnefedev.gg.config.GGConfig;
import com.gnefedev.gg.infrostructure.repository.GGLock;
import com.gnefedev.gg.user.User;
import com.gnefedev.gg.user.UserRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.cache.CacheException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by gerakln on 04.07.16.
 */
@ContextConfiguration(classes = GGConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConcurrentIgniteTest {
    @Autowired
    private GGLock ggLock;
    @Autowired
    private UserRepository userRepository;
    private String name = "Ivan";
    private String family = "Ivanov";

    @Test
    @Transactional
    public void testIgniteLock() {
        try {
            ggLock.igniteLock("key").lock();
            fail();
        } catch (CacheException e) {
            assertEquals("Explicit lock can't be acquired within a transaction.", e.getMessage());
        }
    }

    @Test
    @Transactional
    public void customLock() {
        boolean acquired = ggLock.lock("key");
        assertTrue(acquired);
        assertFalse(ggLock.lock("key"));
        runMain("tryCustomLock");
        ggLock.unlock("key");
    }

    @Transactional
    public void tryCustomLock() {
        boolean acquired = ggLock.lock("key");
        assertFalse(acquired);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void _00uniqueUser() {
        assertNull(userRepository.findByNameAndFamily(name, family));
        assertNotNull(userRepository.findOrCreateUser(name, family));
        runMain("tryCreateIvan");
        assertNull(userRepository.findByNameAndFamily(name, family));
    }

    @Test
    @Transactional
    @Rollback(false)
    public void _01checkUniqueUser() {
        User ivan = userRepository.findByNameAndFamily(name, family);
        assertNotNull(ivan);
        userRepository.remove(ivan);
    }

    @Transactional
    public void tryCreateIvan() {
        try {
            assertNull(userRepository.findByNameAndFamily(name, family));
            userRepository.findOrCreateUser(name, family);
            fail();
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        try (AbstractApplicationContext context = new AnnotationConfigApplicationContext(GGConfig.class, ConcurrentIgniteTest.class)) {
            ConcurrentIgniteTest concurrentIgniteTest = context.getBean(ConcurrentIgniteTest.class);
            switch (args[0]) {
                case "tryCustomLock":
                    concurrentIgniteTest.tryCustomLock();
                    break;
                case "tryCreateIvan":
                    concurrentIgniteTest.tryCreateIvan();
                    break;
                default:
                    fail();
            }
        }
    }

    private void runMain(String methodToRun) {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = ConcurrentIgniteTest.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className, methodToRun);

        try {
            Process process = builder.start();
            process.waitFor();
            assertEquals(0, process.exitValue());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
