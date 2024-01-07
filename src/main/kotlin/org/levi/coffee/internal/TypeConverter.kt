package org.levi.coffee.internal

import org.slf4j.LoggerFactory
import java.lang.reflect.Type
import java.util.*
import java.util.regex.Pattern

internal object TypeConverter {
    val boundTypes: MutableSet<String> = HashSet()

    private val log = LoggerFactory.getLogger(this::class.java)

    private val jsTypes: Map<String, String> = mapOf(
        // primitives
        "byte" to "number",
        "byte" to "number",
        "char" to "number",
        "short" to "number",
        "int" to "number",
        "long" to "number",
        "float" to "number",
        "double" to "number",
        "boolean" to "boolean",
        "String" to "string",
        "void" to "void",

        // classes
        "Byte" to "number",
        "Character" to "number",
        "Short" to "number",
        "Integer" to "number",
        "Long" to "number",
        "Float" to "number",
        "Double" to "number",
        "Boolean" to "boolean",
        "List" to "Array",
        "Map" to "Map",
        "Set" to "Set",
    )

    /**
     *
     * @param t the java type that needs conversion to javascript.
     * @param addTypePrefix in types.ts, no need to that prefix, but in the methods, we need it because of
     * "import * as jt from ../types"
     * @return the converted type name in javascript
     */
    fun convert(t: Type, addTypePrefix: Boolean): String {
        var type = t.typeName
            // Remove java packages prefix
            .replace("\\b[a-z]+\\.".toRegex(), "")
            // Remove some kotlin extension stuff, if there is any
            .replace("\\? extends ".toRegex(), "")
            // Remove part of classes that are a specific implementation of a generic class (List, etc.)
            .replace("Tree|Hash|Linked|Array".toRegex(), "")

        val pattern = Pattern.compile("[a-zA-Z0-9]+")
        val matcher = pattern.matcher(type)

        // Each type found, swap for the corresponding type in Typescript
        while (matcher.find()) {
            val javaType = matcher.group()
            if (!jsTypes.containsKey(javaType) && !boundTypes.contains(javaType)) {
                log.error("java type $javaType is not recognized. Did you forget to @BindType ?\n" +
                    "Used 'any' instead, just in case.")
                type = type.replace(javaType, jsTypes.getOrDefault(javaType, "any"))
            } else if (!jsTypes.containsKey(javaType) && addTypePrefix) {
                type = type.replace(javaType, "t.$javaType")
            } else if (jsTypes.containsKey(javaType)) {
                type = type.replace(javaType, jsTypes[javaType]!!)
            }
        }
        return type
    }
}