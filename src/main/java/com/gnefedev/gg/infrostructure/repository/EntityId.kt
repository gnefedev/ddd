package com.gnefedev.gg.infrostructure.repository

/**
 * Created by gerakln on 17.04.16.
 */
data class EntityId<E>(var id: Long) : Comparable<EntityId<E>> {
    override fun compareTo(other: EntityId<E>): Int {
        return id.compareTo(other.id)
    }
}