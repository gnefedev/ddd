package com.gnefedev.gg.user;

import com.gnefedev.gg.infrostructure.repository.Repository;
import com.gnefedev.gg.user.model.User;
import org.springframework.stereotype.Component;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@Component
public class UserRepository extends Repository<User> {
    @Override
    protected Class<User> entityClass() {
        return User.class;
    }
}
