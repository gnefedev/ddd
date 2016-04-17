package com.gnefedev.gg.user;

import com.gnefedev.gg.infrostructure.repository.RepositoryForJava;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by gerakln on 17.04.16.
 */
@Component
public class UserRepository extends RepositoryForJava<User> {
    @Override
    protected Class<User> entityClass() {
        return User.class;
    }

    public List<User> findByFamilyName(String family) {
        return getListByQuery("select * from User where familyName = ?", family);
    }
}
