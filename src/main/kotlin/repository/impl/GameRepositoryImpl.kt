package repository.impl

import model.Game
import model.Games
import repository.GameRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class GameRepositoryImpl : GameRepository {

    override fun getAll(): List<Game> = transaction {
        Games.selectAll().map { rowToGame(it) }
    }

    override fun getById(id: Int): Game? = transaction {
        Games.select { Games.id eq id }
            .singleOrNull()
            ?.let { rowToGame(it) }
    }

    private fun rowToGame(row: ResultRow): Game = Game(
        id = row[Games.id],
        name = row[Games.name]
    )
}