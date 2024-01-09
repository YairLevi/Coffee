package org.levi.coffee.internal

import org.levi.coffee.annotations.BindAllMethods
import org.levi.coffee.annotations.BindMethod
import org.levi.coffee.annotations.BindType
import org.levi.coffee.annotations.IgnoreMethod
import java.lang.reflect.Field
import java.lang.reflect.Method

object BindFilter {
    fun methodsOf(c: Class<*>): List<Method> {
        if (c.isAnnotationPresent(BindAllMethods::class.java)) {
            return c.declaredMethods.filter { !it.isAnnotationPresent(IgnoreMethod::class.java) }
        }
        return c.declaredMethods.filter { it.isAnnotationPresent(BindMethod::class.java) }
    }

    fun fieldsOf(c: Class<*>): List<Field> {
        // Assuming "@BindType()" present on class "c"

        val annotation = c.getAnnotation(BindType::class.java)
        if (annotation.only.isNotEmpty()) {
            return c.declaredFields.filter { annotation.only.contains(it.name) }
        }
        return c.declaredFields.filter { !annotation.ignore.contains(it.name) }
    }
}