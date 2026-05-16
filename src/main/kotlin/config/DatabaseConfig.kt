package config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import org.jetbrains.exposed.sql.Database
import repository.impl.*
import service.*
import LocalDateAdapter
import configureRoutes
import java.time.LocalDate

// Глобальные сервисы
lateinit var gameService: GameService
lateinit var teamService: TeamService
lateinit var playerService: PlayerService
lateinit var tournamentService: TournamentService
lateinit var matchService: MatchService
lateinit var transferService: TransferService

fun Application.configureDatabase() {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/turning_point"
        username = "postgres"
        password = "0000"
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }

    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        }
    }
}

fun Application.configureDI() {
    val gameRepository = GameRepositoryImpl()
    val teamRepository = TeamRepositoryImpl()
    val playerRepository = PlayerRepositoryImpl()
    val tournamentRepository = TournamentRepositoryImpl()
    val tournamentRegistrationRepository = TournamentRegistrationRepositoryImpl()
    val matchRepository = MatchRepositoryImpl()
    val transferRepository = TransferRepositoryImpl()
    val prizeLogRepository = PrizeLogRepositoryImpl()

    gameService = GameService(gameRepository)
    teamService = TeamService(teamRepository)
    playerService = PlayerService(playerRepository)
    tournamentService = TournamentService(tournamentRepository)
    matchService = MatchService(matchRepository)
    transferService = TransferService(
        transferRepository,
        playerRepository,
        teamRepository,
        tournamentRegistrationRepository,
        tournamentRepository,
        prizeLogRepository
    )

    configureRoutes(
        gameService,
        teamService,
        playerService,
        tournamentService,
        matchService,
        transferService
    )
}