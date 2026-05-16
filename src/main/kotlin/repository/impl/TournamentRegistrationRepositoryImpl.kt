package repository.impl

import model.TournamentRegistration
import model.TournamentRegistrations
import repository.TournamentRegistrationRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class TournamentRegistrationRepositoryImpl : TournamentRegistrationRepository {

    override fun getAll(): List<TournamentRegistration> = transaction {
        TournamentRegistrations.selectAll().map { rowToRegistration(it) }
    }

    override fun getByTournament(tournamentId: Int): List<TournamentRegistration> = transaction {
        TournamentRegistrations.select { TournamentRegistrations.tournamentId eq tournamentId }
            .map { rowToRegistration(it) }
    }

    override fun getByTeam(teamId: Int): List<TournamentRegistration> = transaction {
        TournamentRegistrations.select { TournamentRegistrations.teamId eq teamId }
            .map { rowToRegistration(it) }
    }

    override fun getByTeamAndTournament(teamId: Int, tournamentId: Int): TournamentRegistration? = transaction {
        TournamentRegistrations.select {
            (TournamentRegistrations.teamId eq teamId) and
                    (TournamentRegistrations.tournamentId eq tournamentId)
        }.singleOrNull()?.let { rowToRegistration(it) }
    }

    override fun getCountByTournament(tournamentId: Int): Int = transaction {
        TournamentRegistrations.select { TournamentRegistrations.tournamentId eq tournamentId }
            .count().toInt()
    }

    override fun create(registration: TournamentRegistration): TournamentRegistration = transaction {
        TournamentRegistrations.insert {
            it[teamId] = registration.teamId
            it[tournamentId] = registration.tournamentId
            it[registeredAt] = registration.registeredAt
            it[place] = registration.place
        }
        registration
    }

    override fun updatePlace(teamId: Int, tournamentId: Int, place: Int): Boolean = transaction {
        val updatedRows = TournamentRegistrations.update({
            (TournamentRegistrations.teamId eq teamId) and
                    (TournamentRegistrations.tournamentId eq tournamentId)
        }) {
            it[this.place] = place
        }
        updatedRows > 0
    }

    override fun delete(teamId: Int, tournamentId: Int): Boolean = transaction {
        TournamentRegistrations.deleteWhere {
            (TournamentRegistrations.teamId eq teamId) and
                    (TournamentRegistrations.tournamentId eq tournamentId)
        } > 0
    }

    private fun rowToRegistration(row: ResultRow): TournamentRegistration = TournamentRegistration(
        teamId = row[TournamentRegistrations.teamId],
        tournamentId = row[TournamentRegistrations.tournamentId],
        registeredAt = row[TournamentRegistrations.registeredAt],
        place = row[TournamentRegistrations.place]
    )
}