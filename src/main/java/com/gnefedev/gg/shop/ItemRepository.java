package com.gnefedev.gg.shop;

import com.gnefedev.gg.infrostructure.repository.RepositoryForJava;
import org.springframework.stereotype.Component;

/**
 * Created by gerakln on 17.04.16.
 */
@Component
public class ItemRepository extends RepositoryForJava<Item> {
    @Override
    protected Class<Item> entityClass() {
        return Item.class;
    }

}
