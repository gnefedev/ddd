package com.gnefedev.gg.infrostructure.repository

import com.gnefedev.gg.infrostructure.repository.exception.NoSuchObject
import com.gnefedev.gg.infrostructure.repository.exception.NoTransactionInActive
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteAtomicSequence
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.configuration.CacheConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import sun.org.mozilla.javascript.tools.idswitch.FileBody

import javax.annotation.PostConstruct

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
abstract class Repository<T : RootEntity<*, *>> {
    @Autowired
    private lateinit var ignite: Ignite
    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager
    private lateinit var cache: IgniteCache<Long, T>
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

    private inline fun <T> inTransaction(body: () -> T): T {
        val transaction = transactionManager.getTransaction(null)
        if (transaction.isNewTransaction) {
            transactionManager.rollback(transaction)
            throw NoTransactionInActive();
        }
        return body();
    }

    fun save(entity: T): T {
        inTransaction {
            if (!entity.saved) {
                entity.id.id = sequence.andIncrement
            }
            cache.put(entity.id.id, entity)
            return entity
        }
    }

    fun get(id: EntityId<T>): T {
        inTransaction {
            return cache.get(id.id) ?: throw NoSuchObject()
        }
    }

    fun remove(entity: T) {
        inTransaction {
            cache.remove(entity.id.id)
        }
    }
}
