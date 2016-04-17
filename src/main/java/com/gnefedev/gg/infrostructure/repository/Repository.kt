package com.gnefedev.gg.infrostructure.repository

import com.gnefedev.gg.infrostructure.repository.exception.NoSuchObject
import com.gnefedev.gg.infrostructure.repository.exception.NoTransactionInActive
import com.gnefedev.gg.user.model.User
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteAtomicSequence
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.cache.query.QueryCursor
import org.apache.ignite.cache.query.SqlQuery
import org.apache.ignite.configuration.CacheConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager
import sun.org.mozilla.javascript.tools.idswitch.FileBody
import java.util.*

import javax.annotation.PostConstruct
import javax.cache.Cache

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
abstract class Repository<T : RootEntity<*, *>> {
    @Autowired
    private lateinit var ignite: Ignite
    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager
    private lateinit var cache: CacheProxy<T>
    private lateinit var sequence: IgniteAtomicSequence

    private class CacheProxy<T>(val cache: IgniteCache<Long, T>) {
        private val register: MutableMap<Long, T> = HashMap()
        fun put(id: Long, entity: T) {
            if (!register.containsKey(id)) {
                register[id] = entity
                TransactionSynchronizationManager.registerSynchronization(
                        object : TransactionSynchronizationAdapter() {
                            override fun afterCompletion(status: Int) {
                                register.remove(id)
                            }
                        }
                )
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

        fun query(args: SqlQuery<Long, T>): QueryCursor<Cache.Entry<Long, T>> {
            val query = cache.query(args)
            return query;
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

    protected fun getListByQuery(sqlQuery: String, vararg args: String): List<T> {
        inTransaction {
            val sql: SqlQuery<Long, T> = SqlQuery(entityClass(), sqlQuery);
            val cursor = cache.query(sql.setArgs(args))
            try {
                return cursor.map { it.value }
            } finally {
                cursor.close()
            }
        }
    }
}
