package console

import config.playerService
import config.teamService
import config.gameService
import model.Player

fun handlePlayersCommand(input: String) {
    val parts = input.split(" ")
    when (parts.getOrNull(1)) {
        "list" -> {
            val players = playerService.getAllPlayers()
            if (players.isEmpty()) {
                println("📭 Игроков нет")
            } else {
                println("\n👤 Список игроков:")
                for (player in players) {
                    val team = player.teamId?.let { teamService.getTeamById(it) }
                    val game = gameService.getGameById(player.gameId)
                    println("  ${player.id}. ${player.nickname} (${player.realName}) - ${game?.name ?: "?"} / ${team?.name ?: "нет команды"} - рейтинг: ${player.rating}")
                }
            }
        }
        "get" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            if (id == null) {
                println("❌ Укажите ID: players get <id>")
                return
            }
            val player = playerService.getPlayerById(id)
            if (player != null) {
                val team = player.teamId?.let { teamService.getTeamById(it) }
                val game = gameService.getGameById(player.gameId)
                println("\n👤 Игрок #${player.id}:")
                println("  Никнейм: ${player.nickname}")
                println("  Имя: ${player.realName}")
                println("  Дисциплина: ${game?.name ?: "неизвестно"}")
                println("  Команда: ${team?.name ?: "свободный агент"}")
                println("  Рейтинг: ${player.rating}")
                println("  Роль: ${player.role}")
            } else {
                println("❌ Игрок с ID $id не найден")
            }
        }
        "add" -> addPlayerInteractive()
        "team" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            val teamId = parts.getOrNull(3)?.toIntOrNull()
            if (id == null) {
                println("❌ Используйте: players team <id> <teamId>")
                return
            }
            val success = playerService.updateTeam(id, teamId)
            if (success) {
                println("✅ Игрок #$id переведён в команду ${teamId ?: "свободные агенты"}")
            } else {
                println("❌ Игрок с ID $id не найден")
            }
        }
        "rating" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            val rating = parts.getOrNull(3)?.toIntOrNull()
            if (id == null || rating == null) {
                println("❌ Используйте: players rating <id> <рейтинг>")
                return
            }
            val success = playerService.updateRating(id, rating)
            if (success) {
                println("✅ Рейтинг игрока #$id изменён на $rating")
            } else {
                println("❌ Игрок с ID $id не найден")
            }
        }
        "delete" -> {
            val id = parts.getOrNull(2)?.toIntOrNull()
            if (id == null) {
                println("❌ Укажите ID: players delete <id>")
                return
            }
            val deleted = playerService.deletePlayer(id)
            if (deleted) {
                println("✅ Игрок #$id удалён")
            } else {
                println("❌ Игрок с ID $id не найден")
            }
        }
        else -> println("❌ Неизвестная подкоманда")
    }
}

fun addPlayerInteractive() {
    println("\n➕ Добавление нового игрока:")

    print("  Никнейм: ")
    val nickname = readln()
    if (nickname.isBlank()) {
        println("❌ Никнейм не может быть пустым")
        return
    }

    print("  Настоящее имя: ")
    val realName = readln()

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

    println("  Доступные команды (Enter - свободный агент):")
    val teams = teamService.getTeamsByGame(gameId)
    for (team in teams) {
        println("    ${team.id}. ${team.name}")
    }
    print("  ID команды: ")
    val teamIdInput = readln()
    val teamId = if (teamIdInput.isBlank()) null else teamIdInput.toIntOrNull()

    print("  Рейтинг (100-3000): ")
    val rating = readln().toIntOrNull()
    if (rating == null || rating !in 100..3000) {
        println("❌ Некорректный рейтинг")
        return
    }

    print("  Роль (Carry/Mid/Offline/Support/AWPer/Rifler/IGL): ")
    val role = readln()

    val player = Player(
        id = 0,
        nickname = nickname,
        realName = realName,
        gameId = gameId,
        teamId = teamId,
        rating = rating,
        role = role
    )

    val created = playerService.createPlayer(player)
    println("✅ Игрок добавлен с ID: ${created.id}")
}