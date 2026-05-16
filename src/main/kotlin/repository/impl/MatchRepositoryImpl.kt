package repository.impl

import model.Match
import model.Matches
import repository.MatchRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class MatchRepositoryImpl : MatchRepository {

    override fun getAll(): List<Match> = transaction {
        Matches.selectAll().map { rowToMatch(it) }
    }

    override fun getById(id: Int): Match? = transaction {
        Matches.select { Matches.id eq id }
            .singleOrNull()
            ?.let { rowToMatch(it) }
    }

    override fun getByTournament(tournamentId: Int): List<Match> = transaction {
        Matches.select { Matches.tournamentId eq tournamentId }
            .map { rowToMatch(it) }
    }

    override fun create(match: Match): Match = transaction {
        val insertId = Matches.insert {
            it[tournamentId] = match.tournamentId
            it[round] = match.round
            it[team1Id] = match.team1Id
            it[team2Id] = match.team2Id
            it[winnerId] = match.winnerId
            it[scoreTeam1] = match.scoreTeam1
            it[scoreTeam2] = match.scoreTeam2
            it[scheduledDate] = match.scheduledDate
            it[status] = match.status
        }[Matches.id]
        match.copy(id = insertId)
    }

    override fun updateWinner(matchId: Int, winnerId: Int, score1: Int, score2: Int): Boolean = transaction {
        val updatedRows = Matches.update({ Matches.id eq matchId }) {
            it[this.winnerId] = winnerId
            it[scoreTeam1] = score1
            it[scoreTeam2] = score2
            it[status] = "finished"
        }
        updatedRows > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        Matches.deleteWhere { Matches.id eq id } > 0
    }

    private fun rowToMatch(row: ResultRow): Match = Match(
        id = row[Matches.id],
        tournamentId = row[Matches.tournamentId],
        round = row[Matches.round],
        team1Id = row[Matches.team1Id],
        team2Id = row[Matches.team2Id],
        winnerId = row[Matches.winnerId],
        scoreTeam1 = row[Matches.scoreTeam1],
        scoreTeam2 = row[Matches.scoreTeam2],
        scheduledDate = row[Matches.scheduledDate],
        status = row[Matches.status]
    )
}