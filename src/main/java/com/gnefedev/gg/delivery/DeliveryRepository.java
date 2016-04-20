package com.gnefedev.gg.delivery;

import com.gnefedev.gg.infrostructure.repository.RepositoryForJava;
import org.springframework.stereotype.Component;

/**
 * Created by gerakln on 17.04.16.
 */
@Component
public class DeliveryRepository extends RepositoryForJava<Delivery> {
    @Override
    protected Class<Delivery> entityClass() {
        return Delivery.class;
    }

}
