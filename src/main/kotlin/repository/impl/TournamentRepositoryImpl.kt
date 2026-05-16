package repository.impl

import model.Tournament
import model.Tournaments
import repository.TournamentRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDate

class TournamentRepositoryImpl : TournamentRepository {

    override fun getAll(): List<Tournament> = transaction {
        Tournaments.selectAll().map { rowToTournament(it) }
    }

    override fun getById(id: Int): Tournament? = transaction {
        Tournaments.select { Tournaments.id eq id }
            .singleOrNull()
            ?.let { rowToTournament(it) }
    }

    override fun getByGame(gameId: Int): List<Tournament> = transaction {
        Tournaments.select { Tournaments.gameId eq gameId }
            .map { rowToTournament(it) }
    }

    override fun create(tournament: Tournament): Tournament = transaction {
        val insertId = Tournaments.insert {
            it[name] = tournament.name
            it[gameId] = tournament.gameId
            it[prizePool] = BigDecimal(tournament.prizePool)
            it[maxTeams] = tournament.maxTeams
            it[startDate] = tournament.startDate
            it[status] = tournament.status
        }[Tournaments.id]
        tournament.copy(id = insertId)
    }

    override fun updateStatus(id: Int, status: String): Boolean = transaction {
        val updatedRows = Tournaments.update({ Tournaments.id eq id }) {
            it[this.status] = status
        }
        updatedRows > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        Tournaments.deleteWhere { Tournaments.id eq id } > 0
    }

    private fun rowToTournament(row: ResultRow): Tournament = Tournament(
        id = row[Tournaments.id],
        name = row[Tournaments.name],
        gameId = row[Tournaments.gameId],
        prizePool = row[Tournaments.prizePool].toDouble(),
        maxTeams = row[Tournaments.maxTeams],
        startDate = row[Tournaments.startDate],
        status = row[Tournaments.status]
    )
}