package console

import config.teamService
import config.gameService
import model.Team
import java.time.LocalDate

fun handleTeamsCommand(input: String) {
    val parts = input.split(" ")
    when (parts.getOrNull(1)) {
        "list" -> {
            val teams = teamService.getAllTeams()
            if (teams.isEmpty()) {
                println("📭 Команд нет")
            } else {
                println("\n🏆 Список команд:")
                for (team in teams) {
                    val game = gameService.getGameById(team.gameId)
                    println("  ${team.id}. ${team.name} (${game?.name ?: "неизвестно"}) - бюджет: ${team.budget}")
                }
            }
        }
        "get" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            if (id == null) {
                println("❌ Укажите ID: teams get <id>")
                return
            }
            val team = teamService.getTeamById(id)
            if (team != null) {
                val game = gameService.getGameById(team.gameId)
                println("\n🏆 Команда #${team.id}:")
                println("  Название: ${team.name}")
                println("  Дисциплина: ${game?.name ?: "неизвестно"}")
                println("  Бюджет: ${team.budget}")
                println("  Создана: ${team.createdAt}")
            } else {
                println("❌ Команда с ID $id не найдена")
            }
        }
        "add" -> addTeamInteractive()
        "budget" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            val budget = parts.getOrNull(3)?.toDoubleOrNull()
            if (id == null || budget == null) {
                println("❌ Используйте: teams budget <id> <сумма>")
                return
            }
            val success = teamService.updateBudget(id, budget)
            if (success) {
                println("✅ Бюджет команды #$id обновлён до $budget")
            } else {
                println("❌ Команда с ID $id не найдена")
            }
        }
        "delete" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            if (id == null) {
                println("❌ Укажите ID: teams delete <id>")
                return
            }
            val deleted = teamService.deleteTeam(id)
            if (deleted) {
                println("✅ Команда #$id удалена")
            } else {
                println("❌ Команда с ID $id не найдена")
            }
        }
        else -> println("❌ Неизвестная подкоманда")
    }
}

fun addTeamInteractive() {
    println("\n➕ Добавление новой команды:")

    print("  Название команды: ")
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

    print("  Бюджет: ")
    val budget = readln().toDoubleOrNull()
    if (budget == null || budget < 0) {
        println("❌ Некорректный бюджет")
        return
    }

    val team = Team(
        id = 0,
        name = name,
        gameId = gameId,
        budget = budget,
        captainId = null,
        createdAt = LocalDate.now()
    )

    val created = teamService.createTeam(team)
    println("✅ Команда добавлена с ID: ${created.id}")
}