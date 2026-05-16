import dto.*
import model.*
import service.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

fun Application.configureRoutes(
    gameService: GameService,
    teamService: TeamService,
    playerService: PlayerService,
    tournamentService: TournamentService,
    matchService: MatchService,
    transferService: TransferService
) {
    routing {
        // Swagger
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")

        // ==================== GAMES ====================
        route("/games") {
            get {
                val games = gameService.getAllGames()
                call.respond(games)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }
                val game = gameService.getGameById(id)
                if (game != null) {
                    call.respond(game)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Game not found")
                }
            }
        }

        // ==================== TEAMS ====================
        route("/teams") {
            get {
                val gameId = call.parameters["gameId"]?.toIntOrNull()
                val teams = if (gameId != null) teamService.getTeamsByGame(gameId) else teamService.getAllTeams()
                call.respond(teams)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }
                val team = teamService.getTeamById(id)
                if (team != null) {
                    call.respond(team)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Team not found")
                }
            }
            post {
                try {
                    val request = call.receive<TeamRequest>()
                    val team = Team(
                        id = 0,
                        name = request.name,
                        gameId = request.gameId,
                        budget = request.budget,
                        captainId = request.captainId,
                        createdAt = LocalDate.parse(request.createdAt)
                    )
                    val created = teamService.createTeam(team)
                    call.respond(HttpStatusCode.Created, created)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }
            put("/{id}/budget") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@put
                }
                try {
                    val request = call.receive<TeamBudgetUpdateRequest>()
                    val success = teamService.updateBudget(id, request.newBudget)
                    if (success) {
                        call.respond(HttpStatusCode.OK, "Budget updated")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Team not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@delete
                }
                val deleted = teamService.deleteTeam(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Team deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Team not found")
                }
            }
        }

        // ==================== PLAYERS ====================
        route("/players") {
            get {
                val teamId = call.parameters["teamId"]?.toIntOrNull()
                val gameId = call.parameters["gameId"]?.toIntOrNull()
                val players = when {
                    teamId != null -> playerService.getPlayersByTeam(teamId)
                    gameId != null -> playerService.getPlayersByGame(gameId)
                    else -> playerService.getAllPlayers()
                }
                call.respond(players)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }
                val player = playerService.getPlayerById(id)
                if (player != null) {
                    call.respond(player)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Player not found")
                }
            }
            post {
                try {
                    val request = call.receive<PlayerRequest>()
                    val player = Player(
                        id = 0,
                        nickname = request.nickname,
                        realName = request.realName,
                        gameId = request.gameId,
                        teamId = request.teamId,
                        rating = request.rating,
                        role = request.role
                    )
                    val created = playerService.createPlayer(player)
                    call.respond(HttpStatusCode.Created, created)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }
            put("/{id}/team") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@put
                }
                try {
                    val request = call.receive<PlayerTeamUpdateRequest>()
                    val success = playerService.updateTeam(id, request.newTeamId)
                    if (success) {
                        call.respond(HttpStatusCode.OK, "Team updated")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Player not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }
            put("/{id}/rating") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@put
                }
                try {
                    val request = call.receive<PlayerRatingUpdateRequest>()
                    val success = playerService.updateRating(id, request.newRating)
                    if (success) {
                        call.respond(HttpStatusCode.OK, "Rating updated")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Player not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@delete
                }
                val deleted = playerService.deletePlayer(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Player deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Player not found")
                }
            }
        }

        // ==================== TOURNAMENTS ====================
        route("/tournaments") {
            get {
                val gameId = call.parameters["gameId"]?.toIntOrNull()
                val tournaments = if (gameId != null) tournamentService.getTournamentsByGame(gameId) else tournamentService.getAllTournaments()
                call.respond(tournaments)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }
                val tournament = tournamentService.getTournamentById(id)
                if (tournament != null) {
                    call.respond(tournament)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Tournament not found")
                }
            }
            post {
                try {
                    val request = call.receive<TournamentRequest>()
                    val tournament = Tournament(
                        id = 0,
                        name = request.name,
                        gameId = request.gameId,
                        prizePool = request.prizePool,
                        maxTeams = request.maxTeams,
                        startDate = LocalDate.parse(request.startDate),
                        status = request.status
                    )
                    val created = tournamentService.createTournament(tournament)
                    call.respond(HttpStatusCode.Created, created)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }
            put("/{id}/status") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@put
                }
                try {
                    val request = call.receive<TournamentStatusUpdateRequest>()
                    val success = tournamentService.updateStatus(id, request.status)
                    if (success) {
                        call.respond(HttpStatusCode.OK, "Status updated")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Tournament not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@delete
                }
                val deleted = tournamentService.deleteTournament(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Tournament deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Tournament not found")
                }
            }

            // СЛОЖНАЯ ОПЕРАЦИЯ №3: Регистрация команды на турнир
            post("/{tournamentId}/register/{teamId}") {
                val tournamentId = call.parameters["tournamentId"]?.toIntOrNull()
                val teamId = call.parameters["teamId"]?.toIntOrNull()
                if (tournamentId == null || teamId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid IDs")
                    return@post
                }
                val result = transferService.registerTeamForTournament(teamId, tournamentId)
                when (result) {
                    is RegistrationResult.Success -> call.respond(HttpStatusCode.OK, mapOf("message" to result.message))
                    is RegistrationResult.Error -> call.respond(HttpStatusCode.BadRequest, mapOf("error" to result.message))
                }
            }

            // СЛОЖНАЯ ОПЕРАЦИЯ №4: Распределение призовых
            post("/{tournamentId}/distribute-prizes") {
                val tournamentId = call.parameters["tournamentId"]?.toIntOrNull()
                if (tournamentId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid tournament ID")
                    return@post
                }
                try {
                    val results = call.receive<List<TournamentResultData>>()
                    val result = transferService.distributePrizeMoney(tournamentId, results)
                    when (result) {
                        is PrizeResult.Success -> call.respond(HttpStatusCode.OK, mapOf("message" to result.message, "distribution" to result.distribution))
                        is PrizeResult.Error -> call.respond(HttpStatusCode.BadRequest, mapOf("error" to result.message))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
                }
            }
        }

        // ==================== MATCHES ====================
        route("/matches") {
            get {
                val tournamentId = call.parameters["tournamentId"]?.toIntOrNull()
                val matches = if (tournamentId != null) matchService.getMatchesByTournament(tournamentId) else matchService.getAllMatches()
                call.respond(matches)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }
                val match = matchService.getMatchById(id)
                if (match != null) {
                    call.respond(match)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Match not found")
                }
            }
            post {
                try {
                    val request = call.receive<MatchRequest>()
                    val match = Match(
                        id = 0,
                        tournamentId = request.tournamentId,
                        round = request.round,
                        team1Id = request.team1Id,
                        team2Id = request.team2Id,
                        winnerId = null,
                        scoreTeam1 = 0,
                        scoreTeam2 = 0,
                        scheduledDate = LocalDate.parse(request.scheduledDate),
                        status = "scheduled"
                    )
                    val created = matchService.createMatch(match)
                    call.respond(HttpStatusCode.Created, created)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                }
            }

            // СЛОЖНАЯ ОПЕРАЦИЯ №2: Завершение матча
            put("/{id}/finish") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid match ID")
                    return@put
                }
                try {
                    val request = call.receive<FinishMatchRequest>()
                    val success = matchService.finishMatch(id, request.winnerId, request.scoreTeam1, request.scoreTeam2)
                    if (success) {
                        call.respond(HttpStatusCode.OK, "Match finished")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Match not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
                }
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@delete
                }
                val deleted = matchService.deleteMatch(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Match deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Match not found")
                }
            }
        }

        // ==================== TRANSFERS ====================
        route("/transfers") {
            // СЛОЖНАЯ ОПЕРАЦИЯ №1: Трансфер игрока
            post {
                try {
                    val request = call.receive<TransferRequest>()
                    val result = transferService.transferPlayer(request.playerId, request.toTeamId, request.fee)
                    when (result) {
                        is TransferResult.Success -> call.respond(HttpStatusCode.OK, mapOf("message" to result.message, "transfer" to result.transfer))
                        is TransferResult.Error -> call.respond(HttpStatusCode.BadRequest, mapOf("error" to result.message))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
                }
            }
            get {
                val transfers = transferService.getAllTransfers()
                call.respond(transfers)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }
                val transfer = transferService.getTransferById(id)
                if (transfer != null) {
                    call.respond(transfer)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Transfer not found")
                }
            }
            get("/player/{playerId}") {
                val playerId = call.parameters["playerId"]?.toIntOrNull()
                if (playerId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid player ID")
                    return@get
                }
                val transfers = transferService.getTransfersByPlayer(playerId)
                call.respond(transfers)
            }
        }
    }
}

// DTO для запросов
data class TeamRequest(val name: String, val gameId: Int, val budget: Double, val captainId: Int?, val createdAt: String)
data class TeamBudgetUpdateRequest(val newBudget: Double)
data class PlayerRequest(val nickname: String, val realName: String, val gameId: Int, val teamId: Int?, val rating: Int, val role: String)
data class PlayerTeamUpdateRequest(val newTeamId: Int?)
data class PlayerRatingUpdateRequest(val newRating: Int)
data class TournamentRequest(val name: String, val gameId: Int, val prizePool: Double, val maxTeams: Int, val startDate: String, val status: String)
data class TournamentStatusUpdateRequest(val status: String)
data class MatchRequest(val tournamentId: Int, val round: Int, val team1Id: Int?, val team2Id: Int?, val scheduledDate: String)
data class FinishMatchRequest(val winnerId: Int, val scoreTeam1: Int, val scoreTeam2: Int)
data class TransferRequest(val playerId: Int, val toTeamId: Int, val fee: Double)