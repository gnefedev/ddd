package com.gnefedev.gg.user.model

import com.gnefedev.gg.infrostructure.repository.EntityId
import com.gnefedev.gg.infrostructure.repository.RootEntity
import org.apache.ignite.cache.query.annotations.QuerySqlField

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
class User(
        @QuerySqlField var name: String = "",
        @QuerySqlField var familyName: String = ""
) : RootEntity<User, EntityId<User>>() {
    val addresses = mutableListOf<Address>()
}
