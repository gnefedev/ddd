package com.gnefedev.gg.infrostructure.repository;

/**
 * Created by SBT-Nefedev-GV on 18.04.2016.
 */
public interface EntityTransformer<T extends RootEntity<T, EntityId<T>>> {
    T transform(T entity);
}
