package com.gnefedev.gg.infrostructure.repository

import com.gnefedev.gg.infrostructure.repository.exception.NoSuchObject
import com.gnefedev.gg.infrostructure.repository.exception.NoTransactionInActive
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteAtomicSequence
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.cache.query.SqlQuery
import org.apache.ignite.configuration.CacheConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.*
import javax.annotation.PostConstruct
import javax.cache.processor.EntryProcessor

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
abstract class Repository<T : RootEntity<T, EntityId<T>>> {
    @Autowired
    private lateinit var ignite: Ignite
    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager
    private lateinit var cache: CacheProxy<T>
    private lateinit var sequence: IgniteAtomicSequence

    private class CacheProxy<T>(val cache: IgniteCache<Long, T>) {
        var register: MutableMap<Long, T> = HashMap()
        fun put(id: Long, entity: T) {
            if (!register.containsKey(id)) {
                register[id] = entity
            }
            cache.put(id, entity)
        }

        fun get(id: Long): T? {
            return register.getOrPut(id) {
                cache.get(id)
            }
        }

        fun remove(id: Long): Boolean {
            register.remove(id)
            return cache.remove(id);
        }

        fun getList(query: SqlQuery<Long, T>): List<T> {
            val cursor = cache.query(query)
            try {
                return cursor
                        .map {
                            register.getOrPut(it.key) { it.value }
                        }
            } finally {
                cursor.close()
            }
        }

        fun invokeOnQuery(query: SqlQuery<Long, T>, entryProcessor: EntryProcessor<Long, T, T>) {
            val cursor = cache.query(query)
            try {
                cache.invokeAll<T>(cursor.map { it.key }.toSet(), entryProcessor)
            } finally {
                cursor.close()
            }
        }
    }

    @PostConstruct
    fun registerCache() {
        val cacheConfiguration = CacheConfiguration<Long, T>()
        cacheConfiguration.name = entityClass().simpleName
        cacheConfiguration.setIndexedTypes(Long::class.java, entityClass())
        cacheConfiguration.atomicityMode = CacheAtomicityMode.TRANSACTIONAL

        cache = CacheProxy(ignite.getOrCreateCache(cacheConfiguration))
        sequence = ignite.atomicSequence(entityClass().simpleName, 1, true)
    }

    internal abstract fun entityClass(): Class<T>

    private inline fun <T> inTransaction(body: () -> T): T {
        val transaction = transactionManager.getTransaction(null)
        if (transaction.isNewTransaction) {
            transactionManager.rollback(transaction)
            throw NoTransactionInActive();
        }
        TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronizationAdapter() {
                    override fun afterCompletion(status: Int) {
                        cache.register = HashMap()
                    }
                }
        )
        return body();
    }

    fun save(entity: T): EntityId<T> {
        inTransaction {
            if (!entity.saved) {
                entity.id.id = sequence.andIncrement
            }
            cache.put(entity.id.id, entity)
            return entity.id;
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
    fun remove(entityId: EntityId<T>) {
        inTransaction {
            cache.remove(entityId.id)
        }
    }

    protected fun getListByQuery(sqlQuery: String, vararg args: Any): List<T> {
        inTransaction {
            val sql: SqlQuery<Long, T> = SqlQuery(entityClass(), sqlQuery);
            return cache.getList(sql.setArgs(args))
        }
    }

    protected fun invokeOnQuery(entityTransformer: EntityTransformer<T>, sqlQuery: String, vararg args: Any) {
        val sql: SqlQuery<Long, T> = SqlQuery(entityClass(), sqlQuery);
        cache.invokeOnQuery(sql.setArgs(args), EntryProcessor { mutableEntry, args ->
            mutableEntry.value = entityTransformer.transform(mutableEntry.value)
            null
        })
    }
}
