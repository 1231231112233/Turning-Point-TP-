package console

import config.matchService
import config.tournamentService
import config.teamService
import model.Match
import java.time.LocalDate

fun handleMatchesCommand(input: String) {
    val parts = input.split(" ")
    when (parts.getOrNull(1)) {
        "list" -> {
            val matches = matchService.getAllMatches()
            if (matches.isEmpty()) {
                println("📭 Матчей нет")
            } else {
                println("\n⚔️ Список матчей:")
                for (match in matches) {
                    val tournament = tournamentService.getTournamentById(match.tournamentId)
                    val team1 = match.team1Id?.let { teamService.getTeamById(it) }
                    val team2 = match.team2Id?.let { teamService.getTeamById(it) }
                    println("  ${match.id}. ${team1?.name ?: "TBD"} vs ${team2?.name ?: "TBD"} - ${tournament?.name ?: "?"} (${match.status})")
                }
            }
        }
        "get" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            if (id == null) {
                println("❌ Укажите ID: matches get <id>")
                return
            }
            val match = matchService.getMatchById(id)
            if (match != null) {
                val tournament = tournamentService.getTournamentById(match.tournamentId)
                val team1 = match.team1Id?.let { teamService.getTeamById(it) }
                val team2 = match.team2Id?.let { teamService.getTeamById(it) }
                val winner = match.winnerId?.let { teamService.getTeamById(it) }
                println("\n⚔️ Матч #${match.id}:")
                println("  Турнир: ${tournament?.name ?: "неизвестен"}")
                println("  Раунд: ${match.round}")
                println("  Команда 1: ${team1?.name ?: "TBD"}")
                println("  Команда 2: ${team2?.name ?: "TBD"}")
                println("  Счёт: ${match.scoreTeam1} : ${match.scoreTeam2}")
                println("  Победитель: ${winner?.name ?: "не определён"}")
                println("  Статус: ${match.status}")
            } else {
                println("❌ Матч с ID $id не найден")
            }
        }
        "add" -> addMatchInteractive()
        "finish" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            val winnerId = parts.getOrNull(3)?.toIntOrNull()
            val score1 = parts.getOrNull(4)?.toIntOrNull()
            val score2 = parts.getOrNull(5)?.toIntOrNull()
            if (id == null || winnerId == null || score1 == null || score2 == null) {
                println("❌ Используйте: matches finish <id> <winnerId> <score1> <score2>")
                return
            }
            val success = matchService.finishMatch(id, winnerId, score1, score2)
            if (success) {
                println("✅ Матч #$id завершён! Победитель - команда #$winnerId со счётом $score1:$score2")
            } else {
                println("❌ Матч с ID $id не найден или уже завершён")
            }
        }
        "delete" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            if (id == null) {
                println("❌ Укажите ID: matches delete <id>")
                return
            }
            val deleted = matchService.deleteMatch(id)
            if (deleted) {
                println("✅ Матч #$id удалён")
            } else {
                println("❌ Матч с ID $id не найден")
            }
        }
        else -> println("❌ Неизвестная подкоманда")
    }
}

fun addMatchInteractive() {
    println("\n➕ Добавление нового матча:")

    println("  Доступные турниры:")
    val tournaments = tournamentService.getAllTournaments()
    for (tournament in tournaments) {
        println("    ${tournament.id}. ${tournament.name}")
    }
    print("  ID турнира: ")
    val tournamentId = readln().toIntOrNull()
    if (tournamentId == null || tournamentService.getTournamentById(tournamentId) == null) {
        println("❌ Турнир не найден")
        return
    }

    print("  Раунд: ")
    val round = readln().toIntOrNull()
    if (round == null || round <= 0) {
        println("❌ Некорректный номер раунда")
        return
    }

    println("  Доступные команды (Enter - TBD):")
    val teams = teamService.getAllTeams()
    for (team in teams) {
        println("    ${team.id}. ${team.name}")
    }
    print("  ID команды 1: ")
    val team1IdInput = readln()
    val team1Id = if (team1IdInput.isBlank()) null else team1IdInput.toIntOrNull()

    print("  ID команды 2: ")
    val team2IdInput = readln()
    val team2Id = if (team2IdInput.isBlank()) null else team2IdInput.toIntOrNull()

    print("  Дата матча (ГГГГ-ММ-ДД): ")
    val scheduledDateInput = readln()
    val scheduledDate = LocalDate.parse(scheduledDateInput)

    val match = Match(
        id = 0,
        tournamentId = tournamentId,
        round = round,
        team1Id = team1Id,
        team2Id = team2Id,
        winnerId = null,
        scoreTeam1 = 0,
        scoreTeam2 = 0,
        scheduledDate = scheduledDate,
        status = "scheduled"
    )

    val created = matchService.createMatch(match)
    println("✅ Матч добавлен с ID: ${created.id}")
}