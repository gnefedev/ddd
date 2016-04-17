package com.gnefedev.gg.infrostructure.repository;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
public abstract class Repository<T extends RootEntity> {
    @Autowired
    private Ignite ignite;
    private IgniteCache<Long, T> cache;
    private IgniteAtomicSequence sequence;

    @PostConstruct
    public void registerCache() {
        CacheConfiguration<Long, T> cacheConfiguration = new CacheConfiguration<>();
        cacheConfiguration.setName(entityClass().getSimpleName());
        cacheConfiguration.setIndexedTypes(
                Long.class, entityClass()
        );
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

        cache = ignite.getOrCreateCache(cacheConfiguration);
        sequence = ignite.atomicSequence(entityClass().getSimpleName(), 1, true);
    }

    protected IgniteCache<Long, T> getCache() {
        return cache;
    }

    protected abstract Class<T> entityClass();

    public T save(T entity) {
        if (entity.getId() == -1) {
            entity.setId(sequence.getAndIncrement());
        }
        cache.put(entity.getId(), entity);
        return entity;
    }

    public T load(long id) {
        return cache.get(id);
    }

    public void remove(T entity) {
        cache.remove(entity.getId());
    }
}
