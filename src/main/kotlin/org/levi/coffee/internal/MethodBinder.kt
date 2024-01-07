package org.levi.coffee.internal

import org.levi.coffee.annotations.BindMethod
import com.google.gson.Gson
import com.google.gson.JsonParser
import dev.webview.Webview
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

internal object MethodBinder {
    private val gson = Gson()
    private val log = LoggerFactory.getLogger(this::class.java)

    fun bind(wv: Webview, vararg objects: Any) {
        for (obj in objects) {
            val handlers = createHandlers(obj)
            for (h in handlers) {
                wv.bind(h.name, WebviewCallbackWrapper.wrap(h))
            }
        }
    }

    private fun createHandlers(obj: Any): List<Handler> {
        return obj::class.java.declaredMethods
            .filter { it.isAnnotationPresent(BindMethod::class.java) }
            .map { createHandler(obj, it) }
    }

    private fun createHandler(obj: Any, method: Method): Handler {
        val name = obj.javaClass.simpleName + "_" + method.name
        return Handler(
            name
        ) { jsonArgs: String ->
            val jsonElements = splitArrayToJsonElements(jsonArgs)
            val params = method.parameters
            val properParams: MutableList<Any> = ArrayList()
            var i = 0
            while (i < params.size) {
                val currentJsonElement = jsonElements[i]
                val currentParam = params[i]
                properParams.add(gson.fromJson(currentJsonElement, currentParam.type))
                i++
            }
            try {
                method.invoke(obj, *properParams.toTypedArray())
            } catch (e: Exception) {
                log.error("Failed to execute handler for $name", e)
                null
            }
        }
    }

    private fun splitArrayToJsonElements(jsonString: String): List<String> {
        val parsedElement = JsonParser.parseString(jsonString)
        if (!parsedElement.isJsonArray) {
            log.error("$jsonString is not a json array.")
            return ArrayList()
        }
        return parsedElement.asJsonArray.map { it.toString() }
    }
}