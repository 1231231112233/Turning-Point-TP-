package model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.math.BigDecimal
import java.time.LocalDate

object Transfers : Table() {
    val id = integer("id").autoIncrement()
    val playerId = integer("player_id")
    val fromTeamId = integer("from_team_id").nullable()
    val toTeamId = integer("to_team_id").nullable()
    val fee = decimal("fee", 12, 2)
    val status = varchar("status", 20).default("pending")
    val transferDate = date("transfer_date")

    override val primaryKey = PrimaryKey(id)
}

data class Transfer(
    val id: Int,
    val playerId: Int,
    val fromTeamId: Int?,
    val toTeamId: Int?,
    val fee: Double,
    val status: String,
    val transferDate: LocalDate
)