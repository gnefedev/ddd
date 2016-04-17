package com.gnefedev.gg.shop;

import com.gnefedev.gg.infrostructure.repository.EntityId;
import com.gnefedev.gg.infrostructure.repository.RootEntity;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * Created by gerakln on 17.04.16.
 */
public class OrderList extends RootEntity<OrderList, EntityId<OrderList>> {
    @QuerySqlField
    private String name;
    @QuerySqlField
    private String familyName;
}
