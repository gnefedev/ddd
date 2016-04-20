package com.gnefedev.gg.shop;

import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.EntityTransformer;
import com.gnefedev.gg.infrostructure.repository.RepositoryForJava;
import com.gnefedev.gg.user.User;
import org.springframework.stereotype.Component;

/**
 * Created by gerakln on 17.04.16.
 */
@Component
public class OrderRepository extends RepositoryForJava<OrderList> {
    @Override
    protected Class<OrderList> entityClass() {
        return OrderList.class;
    }

    public OrderList findByUser(EntityId<User> userId) {
        //FIXME userId.getId() -> userId OR userId = ? -> userId.id = ?
        return getListByQuery("Select * from OrderList where userId = ?", userId.getId()).get(0);
    }

    public void transformNotFixed(final EntityTransformer<OrderList> entityTransformer) {
        //FIXME * -> _key
        invokeOnQuery(entityTransformer, "Select * from OrderList where status = ?", OrderList.Status.NEW.name());
    }
}