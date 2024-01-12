import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            staticResources("/", "dist") {
                default("index.html")
                preCompressed(CompressedFileType.GZIP)
            }
        }
    }.start(wait = true)
}