package com.gnefedev.gg.test;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by gerakln on 04.07.16.
 */
@Component
public class TransactionalBean {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requireNew() {

    }
}
