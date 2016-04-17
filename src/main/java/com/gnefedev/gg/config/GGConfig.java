package com.gnefedev.gg.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.spring.SpringTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@Configuration
@ComponentScan(basePackages = "com.gnefedev.gg")
@EnableTransactionManagement
public class GGConfig {
    @Bean(destroyMethod = "close")
    public Ignite ignite() {
        return Ignition.start();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        SpringTransactionManager transactionManager = new SpringTransactionManager();
        transactionManager.setTransactionConcurrency(TransactionConcurrency.OPTIMISTIC);
        return transactionManager;
    }
}
