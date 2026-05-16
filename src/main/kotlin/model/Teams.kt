package model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.math.BigDecimal
import java.time.LocalDate

object Teams : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val gameId = integer("game_id")
    val budget = decimal("budget", 15, 2).default(BigDecimal(100000))
    val captainId = integer("captain_id").nullable()
    val createdAt = date("created_at")

    override val primaryKey = PrimaryKey(id)
}

data class Team(
    val id: Int,
    val name: String,
    val gameId: Int,
    val budget: Double,
    val captainId: Int?,
    val createdAt: LocalDate
)