package com.gnefedev.gg.infrostructure.repository

import org.apache.ignite.IgniteCache

/**
 * Created by gerakln on 04.07.16.
 */
class GGLock(val cache: IgniteCache<String, Boolean>) {
    fun igniteLock(key: String) = cache.lock(key)

    fun lock(key: String): Boolean {
        return cache.putIfAbsent(key, true)
    }

    fun unlock(key: String) {
        cache.remove(key)
    }
}