package org.levi.coffee.annotations

/**
 * When using @BindAllMethods on a class, use @IgnoreMethod to mark methods
 * to be ignored and not bind to the frontend.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class IgnoreMethod
