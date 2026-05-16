package console

import config.teamService
import config.playerService
import config.tournamentService
import config.matchService
import config.transferService

fun showStats() {
    try {
        val teams = teamService.getAllTeams()
        val players = playerService.getAllPlayers()
        val tournaments = tournamentService.getAllTournaments()
        val matches = matchService.getAllMatches()
        val transfers = transferService.getAllTransfers()

        println("\n📊 СТАТИСТИКА TURNING POINT:")
        println("  🏆 Команд: ${teams.size}")
        println("  👤 Игроков: ${players.size}")
        println("  🎮 Турниров: ${tournaments.size}")
        println("  ⚔️ Матчей: ${matches.size}")
        println("  🔄 Трансферов: ${transfers.size}")

        var avgRating = 0.0
        if (players.isNotEmpty()) {
            var sum = 0.0
            for (player in players) {
                sum += player.rating
            }
            avgRating = sum / players.size
        }
        println("  ⭐ Средний рейтинг: ${String.format("%.1f", avgRating)}")

        var totalBudget = 0.0
        for (team in teams) {
            totalBudget += team.budget
        }
        println("  💰 Общий бюджет: ${String.format("%.0f", totalBudget)}")
    } catch (e: Exception) {
        println("❌ Ошибка при получении статистики: ${e.message}")
    }
}