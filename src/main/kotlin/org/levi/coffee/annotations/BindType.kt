package org.levi.coffee.annotations

/**
 * Specify a class to convert to a typescript type on the frontend.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class BindType(
    /**
     * Specifies which fields to pick for the typescript type.
     * If empty of not provided at all, it will be ignored.
     * If provided, will override the exclude tag.
     * @return the exclusively included fields' names.
     */
    val only: Array<String> = [],
    /**
     * Specifies which fields to ignore when building the typescript type.
     * If empty or not provided, the type will include all fields.
     * @return the ignored fields' names
     */
    val ignore: Array<String> = [], // TODO: optionally, ignore some fields.
) 