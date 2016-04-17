package com.gnefedev.gg.infrostructure.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@Component
public class RepositorytRegister {
    private static Map<Class<? extends RootEntity>, Repository<? extends RootEntity>> register = new HashMap<>();

    @Autowired
    public void registerRepositories(List<Repository<? extends RootEntity>> repositories) {
        for(Repository<? extends RootEntity> repository: repositories) {
            register.put(repository.entityClass(), repository);
        }
    }
    public static <T extends RootEntity> Repository<T> repository(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Repository<T> repository = (Repository<T>) register.get(clazz);
        return repository;
    }
}
