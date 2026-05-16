package service

import model.*
import repository.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class TransferService(
    private val transferRepository: TransferRepository,
    private val playerRepository: PlayerRepository,
    private val teamRepository: TeamRepository,
    private val tournamentRegistrationRepository: TournamentRegistrationRepository,
    private val tournamentRepository: TournamentRepository,
    private val prizeLogRepository: PrizeLogRepository
) {

    // СЛОЖНАЯ ОПЕРАЦИЯ №1: Трансфер игрока (аналог перевода денег)
    fun transferPlayer(playerId: Int, toTeamId: Int, fee: Double): TransferResult {

        if (fee <= 0) {
            return TransferResult.Error("Сумма трансфера должна быть положительной")
        }

        return transaction {
            val player = playerRepository.getById(playerId)
                ?: return@transaction TransferResult.Error("Игрок не найден")

            val fromTeamId = player.teamId
                ?: return@transaction TransferResult.Error("Игрок не состоит в команде")

            val fromTeam = teamRepository.getById(fromTeamId)
                ?: return@transaction TransferResult.Error("Команда-продавец не найдена")

            val toTeam = teamRepository.getById(toTeamId)
                ?: return@transaction TransferResult.Error("Команда-покупатель не найдена")

            if (toTeam.budget < fee) {
                return@transaction TransferResult.Error("У команды ${toTeam.name} недостаточно средств")
            }

            if (fromTeamId == toTeamId) {
                return@transaction TransferResult.Error("Нельзя перевести игрока в ту же команду")
            }

            // Списание и зачисление
            teamRepository.updateBudget(toTeamId, toTeam.budget - fee)
            teamRepository.updateBudget(fromTeamId, fromTeam.budget + fee)
            playerRepository.updateTeam(playerId, toTeamId)

            // Логирование
            val transfer = Transfer(
                id = 0,
                playerId = playerId,
                fromTeamId = fromTeamId,
                toTeamId = toTeamId,
                fee = fee,
                status = "completed",
                transferDate = LocalDate.now()
            )
            val createdTransfer = transferRepository.create(transfer)

            TransferResult.Success(
                message = "${player.nickname} перешёл из ${fromTeam.name} в ${toTeam.name} за $fee!",
                transfer = createdTransfer
            )
        }
    }

    // СЛОЖНАЯ ОПЕРАЦИЯ №2: Завершение матча (в MatchService - уже есть)

    // СЛОЖНАЯ ОПЕРАЦИЯ №3: Регистрация команды на турнир (аналог регистрации на мероприятие)
    fun registerTeamForTournament(teamId: Int, tournamentId: Int): RegistrationResult {
        return transaction {
            val tournament = tournamentRepository.getById(tournamentId)
                ?: return@transaction RegistrationResult.Error("Турнир не найден")

            val team = teamRepository.getById(teamId)
                ?: return@transaction RegistrationResult.Error("Команда не найдена")

            if (team.gameId != tournament.gameId) {
                return@transaction RegistrationResult.Error("Команда и турнир из разных дисциплин")
            }

            val existingRegistration = tournamentRegistrationRepository.getByTeamAndTournament(teamId, tournamentId)
            if (existingRegistration != null) {
                return@transaction RegistrationResult.Error("Команда уже зарегистрирована на этот турнир")
            }

            val registeredCount = tournamentRegistrationRepository.getCountByTournament(tournamentId)
            if (registeredCount >= tournament.maxTeams) {
                return@transaction RegistrationResult.Error("Нет свободных мест в турнире. Максимум: ${tournament.maxTeams}")
            }

            val registration = TournamentRegistration(
                teamId = teamId,
                tournamentId = tournamentId,
                registeredAt = LocalDate.now(),
                place = null
            )
            tournamentRegistrationRepository.create(registration)

            RegistrationResult.Success(
                message = "Команда ${team.name} успешно зарегистрирована на турнир ${tournament.name}!",
                registration = registration
            )
        }
    }

    // СЛОЖНАЯ ОПЕРАЦИЯ №4: Распределение призовых (аналог возврата товара)
    fun distributePrizeMoney(tournamentId: Int, results: List<TournamentResultData>): PrizeResult {
        return transaction {
            val tournament = tournamentRepository.getById(tournamentId)
                ?: return@transaction PrizeResult.Error("Турнир не найден")

            if (tournament.status != "finished") {
                return@transaction PrizeResult.Error("Турнир ещё не завершён")
            }

            if (tournament.prizePool <= 0.0) {
                return@transaction PrizeResult.Error("Призовой фонд отсутствует")
            }

            if (results.isEmpty()) {
                return@transaction PrizeResult.Error("Результаты турнира не указаны")
            }

            val prizeDistribution = mutableListOf<Pair<Int, Double>>()
            var totalDistributed = 0.0

            for (result in results) {
                val team = teamRepository.getById(result.teamId)
                    ?: return@transaction PrizeResult.Error("Команда с ID ${result.teamId} не найдена")

                if (result.prizeAmount < 0) {
                    return@transaction PrizeResult.Error("Сумма призовых не может быть отрицательной")
                }

                teamRepository.updateBudget(team.id, team.budget + result.prizeAmount)
                prizeDistribution.add(Pair(team.id, result.prizeAmount))
                totalDistributed += result.prizeAmount

                tournamentRegistrationRepository.updatePlace(result.teamId, tournamentId, result.place)
            }

            if (totalDistributed > tournament.prizePool) {
                return@transaction PrizeResult.Error("Сумма призовых превышает призовой фонд")
            }

            val prizeLog = PrizeLog(
                id = 0,
                tournamentId = tournamentId,
                distribution = prizeDistribution.toString(),
                distributedAt = LocalDate.now()
            )
            prizeLogRepository.create(prizeLog)

            PrizeResult.Success(
                message = "Призовые распределены! Всего выплачено: $totalDistributed из ${tournament.prizePool}",
                distribution = prizeDistribution
            )
        }
    }

    fun getAllTransfers(): List<Transfer> = transferRepository.getAll()
    fun getTransferById(id: Int): Transfer? = transferRepository.getById(id)
    fun getTransfersByPlayer(playerId: Int): List<Transfer> = transferRepository.getByPlayer(playerId)
}

data class TournamentResultData(
    val teamId: Int,
    val place: Int,
    val prizeAmount: Double
)

sealed class TransferResult {
    data class Success(val message: String, val transfer: Transfer) : TransferResult()
    data class Error(val message: String) : TransferResult()
}

sealed class RegistrationResult {
    data class Success(val message: String, val registration: TournamentRegistration) : RegistrationResult()
    data class Error(val message: String) : RegistrationResult()
}

sealed class PrizeResult {
    data class Success(val message: String, val distribution: List<Pair<Int, Double>>) : PrizeResult()
    data class Error(val message: String) : PrizeResult()
}