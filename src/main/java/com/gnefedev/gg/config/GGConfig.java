package com.gnefedev.gg.config;

import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.GGConstraintCoordinator;
import com.gnefedev.gg.infrostructure.repository.GGLock;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.spring.SpringTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@Configuration
@ComponentScan(basePackages = "com.gnefedev.gg")
@EnableTransactionManagement
public class GGConfig {
    @Bean(destroyMethod = "close")
    public Ignite ignite() {
        Ignition.setClientMode(true);
        IgniteConfiguration cfg = new IgniteConfiguration();
        TcpDiscoverySpi discoSpi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        discoSpi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(discoSpi);
        return Ignition.start(cfg);
    }

    @Bean
    public GGLock lock() {
        CacheConfiguration<String, Boolean> cacheConfiguration = new CacheConfiguration<>();
        cacheConfiguration.setName("lock");
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        cacheConfiguration.setCacheMode(CacheMode.REPLICATED);
        return new GGLock(ignite().getOrCreateCache(cacheConfiguration));
    }

    @Bean
    public GGConstraintCoordinator ggConstraint() {
        CacheConfiguration<String, EntityId<?>> cacheConfiguration = new CacheConfiguration<>();
        cacheConfiguration.setName("constraint");
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cacheConfiguration.setCacheMode(CacheMode.REPLICATED);
        return new GGConstraintCoordinator(ignite().getOrCreateCache(cacheConfiguration));
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        SpringTransactionManager transactionManager = new SpringTransactionManager();
        transactionManager.setTransactionConcurrency(TransactionConcurrency.PESSIMISTIC);
        return transactionManager;
    }
}
