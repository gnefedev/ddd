package com.gnefedev.gg.user.model

import com.gnefedev.gg.infrostructure.repository.EntityId
import com.gnefedev.gg.infrostructure.repository.RootEntity

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
class User(var name: String = "") : RootEntity<User, EntityId<User>>() {
    var address: Address? = null
}
