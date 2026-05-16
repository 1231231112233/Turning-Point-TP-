package model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object TournamentRegistrations : Table() {
    val teamId = integer("team_id")
    val tournamentId = integer("tournament_id")
    val registeredAt = date("registered_at").default(LocalDate.now())
    val place = integer("place").nullable()

    override val primaryKey = PrimaryKey(teamId, tournamentId)
}

data class TournamentRegistration(
    val teamId: Int,
    val tournamentId: Int,
    val registeredAt: LocalDate,
    val place: Int?
)