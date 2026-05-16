package console

import config.tournamentService
import config.gameService
import config.transferService
import model.Tournament
import service.RegistrationResult
import service.PrizeResult
import service.TournamentResultData
import java.time.LocalDate

fun handleTournamentsCommand(input: String) {
    val parts = input.split(" ")
    when (parts.getOrNull(1)) {
        "list" -> {
            val tournaments = tournamentService.getAllTournaments()
            if (tournaments.isEmpty()) {
                println("📭 Турниров нет")
            } else {
                println("\n🏆 Список турниров:")
                for (tournament in tournaments) {
                    val game = gameService.getGameById(tournament.gameId)
                    println("  ${tournament.id}. ${tournament.name} (${game?.name}) - призовой: ${tournament.prizePool}, статус: ${tournament.status}")
                }
            }
        }
        "get" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            if (id == null) {
                println("❌ Укажите ID: tournaments get <id>")
                return
            }
            val tournament = tournamentService.getTournamentById(id)
            if (tournament != null) {
                val game = gameService.getGameById(tournament.gameId)
                println("\n🏆 Турнир #${tournament.id}:")
                println("  Название: ${tournament.name}")
                println("  Дисциплина: ${game?.name ?: "неизвестно"}")
                println("  Призовой фонд: ${tournament.prizePool}")
                println("  Макс. команд: ${tournament.maxTeams}")
                println("  Дата начала: ${tournament.startDate}")
                println("  Статус: ${tournament.status}")
            } else {
                println("❌ Турнир с ID $id не найден")
            }
        }
        "add" -> addTournamentInteractive()
        "status" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            val status = parts.getOrNull(3)
            if (id == null || status == null) {
                println("❌ Используйте: tournaments status <id> <статус>")
                return
            }
            val success = tournamentService.updateStatus(id, status)
            if (success) {
                println("✅ Статус турнира #$id изменён на '$status'")
            } else {
                println("❌ Турнир с ID $id не найден")
            }
        }
        "register" -> {
            val tournamentId = parts.getOrNull(2)?.toIntOrNull()
            val teamId = parts.getOrNull(3)?.toIntOrNull()
            if (tournamentId == null || teamId == null) {
                println("❌ Используйте: tournaments register <tournamentId> <teamId>")
                return
            }
            val result = transferService.registerTeamForTournament(teamId, tournamentId)
            when (result) {
                is RegistrationResult.Success -> println("✅ ${result.message}")
                is RegistrationResult.Error -> println("❌ ${result.message}")
            }
        }
        "prizes" -> {
            val tournamentId = parts.getOrNull(2)?.toIntOrNull()
            if (tournamentId == null) {
                println("❌ Используйте: tournaments prizes <tournamentId> <teamId> <place> <prize> [<teamId> <place> <prize>...]")
                return
            }

            val args = parts.drop(3)
            if (args.size % 3 != 0) {
                println("❌ Каждая команда должна иметь: teamId place prize")
                return
            }

            val results = mutableListOf<TournamentResultData>()
            for (i in args.indices step 3) {
                val teamId = args[i].toIntOrNull()
                val place = args[i + 1].toIntOrNull()
                val prize = args[i + 2].toDoubleOrNull()
                if (teamId == null || place == null || prize == null) {
                    println("❌ Ошибка в параметрах")
                    return
                }
                results.add(TournamentResultData(teamId, place, prize))
            }

            val result = transferService.distributePrizeMoney(tournamentId, results)
            when (result) {
                is PrizeResult.Success -> println("✅ ${result.message}")
                is PrizeResult.Error -> println("❌ ${result.message}")
            }
        }
        "delete" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            if (id == null) {
                println("❌ Укажите ID: tournaments delete <id>")
                return
            }
            val deleted = tournamentService.deleteTournament(id)
            if (deleted) {
                println("✅ Турнир #$id удалён")
            } else {
                println("❌ Турнир с ID $id не найден")
            }
        }
        else -> println("❌ Неизвестная подкоманда")
    }
}

fun addTournamentInteractive() {
    println("\n➕ Добавление нового турнира:")

    print("  Название турнира: ")
    val name = readln()
    if (name.isBlank()) {
        println("❌ Название не может быть пустым")
        return
    }

    println("  Доступные дисциплины:")
    val games = gameService.getAllGames()
    for (game in games) {
        println("    ${game.id}. ${game.name}")
    }
    print("  ID дисциплины: ")
    val gameId = readln().toIntOrNull()
    if (gameId == null || gameService.getGameById(gameId) == null) {
        println("❌ Дисциплина не найдена")
        return
    }

    print("  Призовой фонд: ")
    val prizePool = readln().toDoubleOrNull()
    if (prizePool == null || prizePool < 0) {
        println("❌ Некорректная сумма")
        return
    }

    print("  Максимум команд: ")
    val maxTeams = readln().toIntOrNull()
    if (maxTeams == null || maxTeams <= 0) {
        println("❌ Некорректное количество")
        return
    }

    print("  Дата начала (ГГГГ-ММ-ДД): ")
    val startDateInput = readln()
    val startDate = LocalDate.parse(startDateInput)

    val tournament = Tournament(
        id = 0,
        name = name,
        gameId = gameId,
        prizePool = prizePool,
        maxTeams = maxTeams,
        startDate = startDate,
        status = "registration"
    )

    val created = tournamentService.createTournament(tournament)
    println("✅ Турнир добавлен с ID: ${created.id}")
}