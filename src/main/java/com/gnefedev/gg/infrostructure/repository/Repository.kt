package com.gnefedev.gg.infrostructure.repository

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteAtomicSequence
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.configuration.CacheConfiguration
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
abstract class Repository<T : RootEntity> {
    @Autowired
    private lateinit var ignite: Ignite
    protected lateinit var cache: IgniteCache<Long, T>
    private lateinit var sequence: IgniteAtomicSequence

    @PostConstruct
    fun registerCache() {
        val cacheConfiguration = CacheConfiguration<Long, T>()
        cacheConfiguration.name = entityClass().simpleName
        cacheConfiguration.setIndexedTypes(
                Long::class.java, entityClass())
        cacheConfiguration.atomicityMode = CacheAtomicityMode.TRANSACTIONAL

        cache = ignite.getOrCreateCache(cacheConfiguration)
        sequence = ignite.atomicSequence(entityClass().simpleName, 1, true)
    }

    internal abstract fun entityClass(): Class<T>

    fun save(entity: T): T {
        if (entity.id == -1L) {
            entity.id = sequence.andIncrement
        }
        cache.put(entity.id, entity)
        return entity
    }

    fun get(id: Long): T {
        return cache.get(id)?:throw NoSuchObject()
    }

    fun remove(entity: T) {
        cache.remove(entity.id)
    }
}
