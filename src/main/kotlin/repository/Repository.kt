package repository

import model.*
import java.time.LocalDate

interface GameRepository {
    fun getAll(): List<Game>
    fun getById(id: Int): Game?
}

interface TeamRepository {
    fun getAll(): List<Team>
    fun getById(id: Int): Team?
    fun getByGame(gameId: Int): List<Team>
    fun create(team: Team): Team
    fun updateBudget(id: Int, newBudget: Double): Boolean
    fun updateCaptain(teamId: Int, captainId: Int): Boolean
    fun delete(id: Int): Boolean
}

interface PlayerRepository {
    fun getAll(): List<Player>
    fun getById(id: Int): Player?
    fun getByTeam(teamId: Int): List<Player>
    fun getByGame(gameId: Int): List<Player>
    fun create(player: Player): Player
    fun updateTeam(playerId: Int, newTeamId: Int?): Boolean
    fun updateRating(playerId: Int, newRating: Int): Boolean
    fun delete(id: Int): Boolean
}

interface TournamentRepository {
    fun getAll(): List<Tournament>
    fun getById(id: Int): Tournament?
    fun getByGame(gameId: Int): List<Tournament>
    fun create(tournament: Tournament): Tournament
    fun updateStatus(id: Int, status: String): Boolean
    fun delete(id: Int): Boolean
}

interface TournamentRegistrationRepository {
    fun getAll(): List<TournamentRegistration>
    fun getByTournament(tournamentId: Int): List<TournamentRegistration>
    fun getByTeam(teamId: Int): List<TournamentRegistration>
    fun getByTeamAndTournament(teamId: Int, tournamentId: Int): TournamentRegistration?
    fun getCountByTournament(tournamentId: Int): Int
    fun create(registration: TournamentRegistration): TournamentRegistration
    fun updatePlace(teamId: Int, tournamentId: Int, place: Int): Boolean
    fun delete(teamId: Int, tournamentId: Int): Boolean
}

interface MatchRepository {
    fun getAll(): List<Match>
    fun getById(id: Int): Match?
    fun getByTournament(tournamentId: Int): List<Match>
    fun create(match: Match): Match
    fun updateWinner(matchId: Int, winnerId: Int, score1: Int, score2: Int): Boolean
    fun delete(id: Int): Boolean
}

interface TransferRepository {
    fun getAll(): List<Transfer>
    fun getById(id: Int): Transfer?
    fun getByPlayer(playerId: Int): List<Transfer>
    fun create(transfer: Transfer): Transfer
    fun updateStatus(id: Int, status: String): Boolean
    fun delete(id: Int): Boolean
}

interface PrizeLogRepository {
    fun getAll(): List<PrizeLog>
    fun getById(id: Int): PrizeLog?
    fun getByTournament(tournamentId: Int): List<PrizeLog>
    fun create(log: PrizeLog): PrizeLog
    fun delete(id: Int): Boolean
}