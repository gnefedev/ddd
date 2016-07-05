package com.gnefedev.gg.infrostructure.repository

import com.gnefedev.gg.infrostructure.repository.exception.ConstraintError
import com.gnefedev.gg.user.User
import org.apache.ignite.IgniteCache

/**
 * Created by gerakln on 05.07.16.
 */
class GGConstraintCoordinator(val cache: IgniteCache<String, EntityId<*>>) {
    fun checkConstraint(user: User) {
        val id = cache.getAndPut(getKey(user), user.id)
        if (id != null && id != user.id) {
            val oldUser = RepositoryRegister
                    .repository(user.javaClass)
                    .getOrNull(id as EntityId<User>)
            if (oldUser != null) {
                throw ConstraintError()
            }
        }
    }

    private fun getKey(user: User) = user.name + user.familyName
}