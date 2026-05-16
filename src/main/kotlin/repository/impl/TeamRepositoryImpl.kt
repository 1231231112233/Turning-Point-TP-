package repository.impl

import model.Team
import model.Teams
import repository.TeamRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDate

class TeamRepositoryImpl : TeamRepository {

    override fun getAll(): List<Team> = transaction {
        Teams.selectAll().map { rowToTeam(it) }
    }

    override fun getById(id: Int): Team? = transaction {
        Teams.select { Teams.id eq id }
            .singleOrNull()
            ?.let { rowToTeam(it) }
    }

    override fun getByGame(gameId: Int): List<Team> = transaction {
        Teams.select { Teams.gameId eq gameId }
            .map { rowToTeam(it) }
    }

    override fun create(team: Team): Team = transaction {
        val insertId = Teams.insert {
            it[name] = team.name
            it[gameId] = team.gameId
            it[budget] = BigDecimal(team.budget)
            it[captainId] = team.captainId
            it[createdAt] = team.createdAt
        }[Teams.id]
        team.copy(id = insertId)
    }

    override fun updateBudget(id: Int, newBudget: Double): Boolean = transaction {
        val updatedRows = Teams.update({ Teams.id eq id }) {
            it[budget] = BigDecimal(newBudget)
        }
        updatedRows > 0
    }

    override fun updateCaptain(teamId: Int, captainId: Int): Boolean = transaction {
        val updatedRows = Teams.update({ Teams.id eq teamId }) {
            it[this.captainId] = captainId
        }
        updatedRows > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        Teams.deleteWhere { Teams.id eq id } > 0
    }

    private fun rowToTeam(row: ResultRow): Team = Team(
        id = row[Teams.id],
        name = row[Teams.name],
        gameId = row[Teams.gameId],
        budget = row[Teams.budget].toDouble(),
        captainId = row[Teams.captainId],
        createdAt = row[Teams.createdAt]
    )
}