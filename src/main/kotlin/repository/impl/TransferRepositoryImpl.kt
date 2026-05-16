package repository.impl

import model.Transfer
import model.Transfers
import repository.TransferRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDate

class TransferRepositoryImpl : TransferRepository {

    override fun getAll(): List<Transfer> = transaction {
        Transfers.selectAll().map { rowToTransfer(it) }
    }

    override fun getById(id: Int): Transfer? = transaction {
        Transfers.select { Transfers.id eq id }
            .singleOrNull()
            ?.let { rowToTransfer(it) }
    }

    override fun getByPlayer(playerId: Int): List<Transfer> = transaction {
        Transfers.select { Transfers.playerId eq playerId }
            .map { rowToTransfer(it) }
    }

    override fun create(transfer: Transfer): Transfer = transaction {
        val insertId = Transfers.insert {
            it[playerId] = transfer.playerId
            it[fromTeamId] = transfer.fromTeamId
            it[toTeamId] = transfer.toTeamId
            it[fee] = BigDecimal(transfer.fee)
            it[status] = transfer.status
            it[transferDate] = transfer.transferDate
        }[Transfers.id]
        transfer.copy(id = insertId)
    }

    override fun updateStatus(id: Int, status: String): Boolean = transaction {
        val updatedRows = Transfers.update({ Transfers.id eq id }) {
            it[this.status] = status
        }
        updatedRows > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        Transfers.deleteWhere { Transfers.id eq id } > 0
    }

    private fun rowToTransfer(row: ResultRow): Transfer = Transfer(
        id = row[Transfers.id],
        playerId = row[Transfers.playerId],
        fromTeamId = row[Transfers.fromTeamId],
        toTeamId = row[Transfers.toTeamId],
        fee = row[Transfers.fee].toDouble(),
        status = row[Transfers.status],
        transferDate = row[Transfers.transferDate]
    )
}