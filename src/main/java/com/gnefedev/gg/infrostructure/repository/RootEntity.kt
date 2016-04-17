package com.gnefedev.gg.infrostructure.repository

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
open class RootEntity<E, ID : EntityId<E>> {
    var id: EntityId<E> = EntityId(-1)
        internal set
}
