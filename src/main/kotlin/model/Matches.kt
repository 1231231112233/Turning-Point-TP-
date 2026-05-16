package model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object Matches : Table() {
    val id = integer("id").autoIncrement()
    val tournamentId = integer("tournament_id")
    val round = integer("round")
    val team1Id = integer("team1_id").nullable()
    val team2Id = integer("team2_id").nullable()
    val winnerId = integer("winner_id").nullable()
    val scoreTeam1 = integer("score_team1").default(0)
    val scoreTeam2 = integer("score_team2").default(0)
    val scheduledDate = date("scheduled_date")
    val status = varchar("status", 20).default("scheduled")

    override val primaryKey = PrimaryKey(id)
}

data class Match(
    val id: Int,
    val tournamentId: Int,
    val round: Int,
    val team1Id: Int?,
    val team2Id: Int?,
    val winnerId: Int?,
    val scoreTeam1: Int,
    val scoreTeam2: Int,
    val scheduledDate: LocalDate,
    val status: String
)