package com.gnefedev.gg.infrostructure.repository;

import org.jetbrains.annotations.NotNull;

/**
 * Created by gerakln on 17.04.16.
 */
public abstract class RepositoryForJava<T extends RootEntity<T, EntityId<T>>> extends Repository<T> {
    @NotNull
    public Class<T> entityClass$ddd() {
        return entityClass();
    }
    @NotNull
    public Class<T> entityClass$production_sources_for_module_ddd() {
        return entityClass();
    }


    protected abstract Class<T> entityClass();

}
