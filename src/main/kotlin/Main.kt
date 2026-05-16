import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import config.configureDatabase
import config.configureSerialization
import config.configureDI
import console.startConsole

fun main() {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    rootLogger.level = Level.WARN

    val exposedLogger = LoggerFactory.getLogger("org.jetbrains.exposed") as Logger
    exposedLogger.level = Level.ERROR

    val hikariLogger = LoggerFactory.getLogger("com.zaxxer.hikari") as Logger
    hikariLogger.level = Level.ERROR

    val server = embeddedServer(Netty, port = 8080) {
        configureDatabase()
        configureSerialization()
        configureDI()
    }

    server.start(wait = false)
    println("🎮 Turning Point (TP) Server Started!")
    println("📍 Server running at http://localhost:8080")
    println("📚 Swagger UI: http://localhost:8080/swagger")
    println("📝 Введите 'help' для списка команд")

    startConsole()
}