package repository.impl

import model.PrizeLog
import model.PrizeLogs
import repository.PrizeLogRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class PrizeLogRepositoryImpl : PrizeLogRepository {

    override fun getAll(): List<PrizeLog> = transaction {
        PrizeLogs.selectAll().map { rowToLog(it) }
    }

    override fun getById(id: Int): PrizeLog? = transaction {
        PrizeLogs.select { PrizeLogs.id eq id }
            .singleOrNull()
            ?.let { rowToLog(it) }
    }

    override fun getByTournament(tournamentId: Int): List<PrizeLog> = transaction {
        PrizeLogs.select { PrizeLogs.tournamentId eq tournamentId }
            .map { rowToLog(it) }
    }

    override fun create(log: PrizeLog): PrizeLog = transaction {
        val insertId = PrizeLogs.insert {
            it[tournamentId] = log.tournamentId
            it[distribution] = log.distribution
            it[distributedAt] = log.distributedAt
        }[PrizeLogs.id]
        log.copy(id = insertId)
    }

    override fun delete(id: Int): Boolean = transaction {
        PrizeLogs.deleteWhere { PrizeLogs.id eq id } > 0
    }

    private fun rowToLog(row: ResultRow): PrizeLog = PrizeLog(
        id = row[PrizeLogs.id],
        tournamentId = row[PrizeLogs.tournamentId],
        distribution = row[PrizeLogs.distribution],
        distributedAt = row[PrizeLogs.distributedAt]
    )
}