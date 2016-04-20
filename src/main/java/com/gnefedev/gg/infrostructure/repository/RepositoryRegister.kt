package com.gnefedev.gg.infrostructure.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.HashMap

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@Component
object RepositoryRegister {

    @Autowired
    fun registerRepositories(repositories: List<Repository<*>>) {
        repositories.forEach { register.put(it.entityClass(), it) }
    }

    private val register: MutableMap<Class<out RootEntity<*, *>>, Repository<*>> = HashMap()
    @JvmStatic
    fun <T : RootEntity<T,EntityId<T>>> repository(clazz: Class<T>): Repository<T> {
        @Suppress("UNCHECKED_CAST")
        return register[clazz] as Repository<T>
    }
}
