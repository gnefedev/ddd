package com.gnefedev.gg.test;

import com.gnefedev.gg.config.GGConfig;
import com.gnefedev.gg.infrostructure.repository.GGLock;
import com.gnefedev.gg.infrostructure.repository.exception.ConstraintError;
import com.gnefedev.gg.user.User;
import com.gnefedev.gg.user.UserRepository;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
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
    public static final int FIVE_SECONDS = 5000;
    @Autowired
    private GGLock ggLock;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
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
        try {
            boolean acquired = ggLock.lock("key");
            assertTrue(acquired);
            assertFalse(ggLock.lock("key"));
            runMain("tryCustomLock");
        } finally {
            ggLock.unlock("key");
        }
    }

    @Transactional
    public void tryCustomLock() {
        boolean acquired = ggLock.lock("key");
        assertFalse(acquired);
    }

    @Test
    public void _00uniqueUser() throws InterruptedException {
        TransactionStatus transaction = transactionManager.getTransaction(null);
        assertNull(userRepository.findByNameAndFamily(name, family));
        userRepository.save(new User(name, family));
        Process process = getProcess("tryCreateIvan");
        Thread.sleep(FIVE_SECONDS);
        assertTrue(process.isAlive());
        transactionManager.commit(transaction);
        process.waitFor();
        assertEquals(0, process.exitValue());
    }

    @Test
    @Transactional
    @Rollback(false)
    public void _01checkUniqueUser() {
        User ivan = userRepository.findByNameAndFamily(name, family);
        assertNotNull(ivan);
        userRepository.remove(ivan);
    }

    public void tryCreateIvan() {
        TransactionStatus transaction = transactionManager.getTransaction(null);
        try {
            assertNull(userRepository.findByNameAndFamily(name, family));
            userRepository.save(new User(name, family));
            fail();
        } catch (ConstraintError ignored) {
            transactionManager.rollback(transaction);
        }
    }

    public static void main(String[] args) {
        try (AbstractApplicationContext context = new AnnotationConfigApplicationContext(GGConfig.class, ConcurrentIgniteTest.class)) {
            ConcurrentIgniteTest concurrentIgniteTest = context.getBean(ConcurrentIgniteTest.class);
            System.out.println("Context was initialized");
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
        Process process = getProcess(methodToRun);
        try {
            process.waitFor();
            assertEquals(0, process.exitValue());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Process getProcess(String methodToRun) {
        ProcessBuilder builder = getProcessBuilder(methodToRun);
        try {
            return builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private ProcessBuilder getProcessBuilder(String methodToRun) {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = ConcurrentIgniteTest.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className, methodToRun);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        return builder;
    }
}
