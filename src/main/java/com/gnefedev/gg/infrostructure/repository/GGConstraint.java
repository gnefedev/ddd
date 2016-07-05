package com.gnefedev.gg.infrostructure.repository;

/**
 * Created by gerakln on 05.07.16.
 */
public @interface GGConstraint {
    String name() default "";
    String[] value();
}
