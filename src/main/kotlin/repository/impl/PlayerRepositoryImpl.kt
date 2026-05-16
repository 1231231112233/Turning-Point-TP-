package repository.impl

import model.Player
import model.Players
import repository.PlayerRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PlayerRepositoryImpl : PlayerRepository {

    override fun getAll(): List<Player> = transaction {
        Players.selectAll().map { rowToPlayer(it) }
    }

    override fun getById(id: Int): Player? = transaction {
        Players.select { Players.id eq id }
            .singleOrNull()
            ?.let { rowToPlayer(it) }
    }

    override fun getByTeam(teamId: Int): List<Player> = transaction {
        Players.select { Players.teamId eq teamId }
            .map { rowToPlayer(it) }
    }

    override fun getByGame(gameId: Int): List<Player> = transaction {
        Players.select { Players.gameId eq gameId }
            .map { rowToPlayer(it) }
    }

    override fun create(player: Player): Player = transaction {
        val insertId = Players.insert {
            it[nickname] = player.nickname
            it[realName] = player.realName
            it[gameId] = player.gameId
            it[teamId] = player.teamId
            it[rating] = player.rating
            it[role] = player.role
        }[Players.id]
        player.copy(id = insertId)
    }

    override fun updateTeam(playerId: Int, newTeamId: Int?): Boolean = transaction {
        val updatedRows = Players.update({ Players.id eq playerId }) {
            it[teamId] = newTeamId
        }
        updatedRows > 0
    }

    override fun updateRating(playerId: Int, newRating: Int): Boolean = transaction {
        val updatedRows = Players.update({ Players.id eq playerId }) {
            it[rating] = newRating
        }
        updatedRows > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        Players.deleteWhere { Players.id eq id } > 0
    }

    private fun rowToPlayer(row: ResultRow): Player = Player(
        id = row[Players.id],
        nickname = row[Players.nickname],
        realName = row[Players.realName],
        gameId = row[Players.gameId],
        teamId = row[Players.teamId],
        rating = row[Players.rating],
        role = row[Players.role]
    )
}