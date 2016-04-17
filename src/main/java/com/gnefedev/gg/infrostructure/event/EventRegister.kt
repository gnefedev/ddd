package com.gnefedev.gg.infrostructure.event

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Created by SBT-Nefedev-GV on 15.04.2016.
 */
@Component
object EventRegister {
    @Autowired
    private lateinit var publisher: ApplicationEventPublisher;

    @JvmStatic
    fun <T : Event> fire(event: T) {
        publisher.publishEvent(event)
    }
}
