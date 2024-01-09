package org.levi.coffee.annotations

/**
 * Use on a class instead of @BindMethod on all functions separately.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class BindAllMethods
