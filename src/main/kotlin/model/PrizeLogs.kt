package model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object PrizeLogs : Table() {
    val id = integer("id").autoIncrement()
    val tournamentId = integer("tournament_id")
    val distribution = text("distribution")
    val distributedAt = date("distributed_at").default(LocalDate.now())

    override val primaryKey = PrimaryKey(id)
}

data class PrizeLog(
    val id: Int,
    val tournamentId: Int,
    val distribution: String,
    val distributedAt: LocalDate
)