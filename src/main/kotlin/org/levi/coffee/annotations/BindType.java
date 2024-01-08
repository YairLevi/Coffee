package org.levi.coffee.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BindType {
    /**
     * Specifies which fields to pick for the typescript type.
     * If empty of not provided at all, it will be ignored.
     * If provided, will override the exclude tag.
     * @return the exclusively included fields' names.
     */
    String[] only() default {};

    /**
     * Specifies which fields to ignore when building the typescript type.
     * If empty or not provided, the type will include all fields.
     * @return the ignored fields' names
     */
    String[] ignore() default {}; // TODO: optionally, ignore some fields.
}