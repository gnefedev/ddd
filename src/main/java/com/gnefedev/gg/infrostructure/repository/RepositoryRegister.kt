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
    fun registerRepositories(repositories: List<Repository<out RootEntity<*,*>>>) {
        for (repository in repositories) {
            register.put(repository.entityClass(), repository)
        }
    }

    private val register = HashMap<Class<out RootEntity<*,*>>, Repository<out RootEntity<*,*>>>()
    @JvmStatic
    fun <T : RootEntity<T,EntityId<T>>> repository(clazz: Class<T>): Repository<T> {
        @Suppress("UNCHECKED_CAST")
        val repository = register[clazz] as Repository<T>
        return repository
    }
}
