package com.gnefedev.gg.infrostructure.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@Component
public class EventRegister {
    private static ApplicationEventPublisher publisher;

    @Autowired
    public void setPublisher(ApplicationEventPublisher injected) {
        publisher = injected;
    }

    public static <T extends Event> void fire(T event) {
        publisher.publishEvent(event);
    }
}
