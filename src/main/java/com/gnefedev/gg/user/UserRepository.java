package com.gnefedev.gg.user;

import com.gnefedev.gg.infrostructure.repository.GGLock;
import com.gnefedev.gg.infrostructure.repository.RepositoryForJava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by gerakln on 17.04.16.
 */
@Component
public class UserRepository extends RepositoryForJava<User> {
    @Autowired
    private GGLock ggLock;
    @Override
    protected Class<User> entityClass() {
        return User.class;
    }

    public List<User> findByFamilyName(String family) {
        return getListByQuery("select * from User where familyName = ?", family);
    }

    public User findByNameAndFamily(String name, String family) {
        List<User> users = getListByQuery("select * from User where name = ? and familyName = ?", name, family);
        if (users.isEmpty()) {
            return null;
        } else if (users.size() == 1) {
            return users.get(0);
        } else {
            throw new RuntimeException("Нарушение уникальности");
        }
    }

    public User findOrCreateUser(String name, String family) {
        if (ggLock.lock("User-" + name + "-" + family)) {
            User user = findByNameAndFamily(name, family);
            if (user == null) {
                user = new User(name, family);
                save(user);
            }
            return user;
        } else {
            throw new RuntimeException("Невозможно получить лок");
        }
    }
}
