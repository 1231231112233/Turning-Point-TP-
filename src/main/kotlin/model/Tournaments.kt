package model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.math.BigDecimal
import java.time.LocalDate

object Tournaments : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 200)
    val gameId = integer("game_id")
    val prizePool = decimal("prize_pool", 15, 2).default(BigDecimal.ZERO)
    val maxTeams = integer("max_teams")
    val startDate = date("start_date")
    val status = varchar("status", 20).default("registration")

    override val primaryKey = PrimaryKey(id)
}

data class Tournament(
    val id: Int,
    val name: String,
    val gameId: Int,
    val prizePool: Double,
    val maxTeams: Int,
    val startDate: LocalDate,
    val status: String
)