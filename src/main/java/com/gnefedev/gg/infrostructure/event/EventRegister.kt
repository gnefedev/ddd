package com.gnefedev.gg.infrostructure.event

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.stereotype.Component

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@Component
object EventRegister : ApplicationEventPublisherAware {
    override fun setApplicationEventPublisher(publisher: ApplicationEventPublisher) {
        this.publisher = publisher;
    }
    private lateinit var publisher: ApplicationEventPublisher;

    @JvmStatic
    fun <T : Event> fire(event: T) {
        publisher.publishEvent(event)
    }
}
