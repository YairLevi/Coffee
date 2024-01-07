package org.levi.coffee.internal

import co.casterlabs.rakurai.json.Rson
import co.casterlabs.rakurai.json.element.JsonArray
import dev.webview.ConsumingProducer
import org.slf4j.LoggerFactory

/**
 * Represents a handler that is called from the frontend.
 */
internal data class Handler(var name: String, var callback: (String) -> Any?)

/**
 * The current implementation of the `webview` package requires that methods passed to
 * `webview.bind()` receive a JsonArray and return JsonElement of the casterlabs package.
 * So this is a wrapper to make things more intuitive.
 */
internal object WebviewCallbackWrapper {
    private val r: Rson = Rson.Builder().build()
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     *
     * @param handler a method that receives a json string of the arguments from the frontend,
     * and returns some java object in return.
     * @return a valid method for `webview.bind()` that essentially does what `handler()` does.
     */
    fun wrap(handler: Handler) =
        ConsumingProducer { jsonArray: JsonArray? ->
            try {
                r.toJson<Any>(handler.callback(jsonArray.toString()))
            } catch (e: Exception) {
                log.error("Failed to execute ${handler.name}. Returned `null` instead.", e)
                null
            }
        }
}