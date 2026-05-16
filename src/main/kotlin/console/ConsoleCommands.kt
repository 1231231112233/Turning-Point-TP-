package console

import config.gameService
import config.teamService
import config.playerService
import config.tournamentService
import config.matchService
import config.transferService

fun startConsole() {
    val reader = System.`in`.bufferedReader()

    while (true) {
        print("\n> ")
        val input = reader.readLine() ?: break
        val command = input.trim().lowercase()

        when {
            command == "help" -> showHelp()
            command == "exit" -> {
                println("👋 Остановка сервера...")
                System.exit(0)
            }
            command.startsWith("teams") -> handleTeamsCommand(input)
            command.startsWith("players") -> handlePlayersCommand(input)
            command.startsWith("tournaments") -> handleTournamentsCommand(input)
            command.startsWith("matches") -> handleMatchesCommand(input)
            command.startsWith("transfers") -> handleTransfersCommand(input)
            command == "stats" -> showStats()
            else -> println("❌ Неизвестная команда. Введите 'help' для списка команд")
        }
    }
}

fun showHelp() {
    println("""
        |📋 Доступные команды:
        |
        |  teams list                    - показать все команды
        |  teams get <id>               - показать команду по ID
        |  teams add                    - добавить команду (интерактивно)
        |  teams budget <id> <сумма>    - изменить бюджет команды
        |  teams delete <id>            - удалить команду
        |
        |  players list                  - показать всех игроков
        |  players get <id>              - показать игрока по ID
        |  players add                   - добавить игрока (интерактивно)
        |  players team <id> <teamId>    - перевести игрока в команду
        |  players rating <id> <рейтинг> - изменить рейтинг
        |  players delete <id>           - удалить игрока
        |
        |  tournaments list              - показать все турниры
        |  tournaments get <id>          - показать турнир по ID
        |  tournaments add               - добавить турнир (интерактивно)
        |  tournaments status <id> <статус> - изменить статус
        |  tournaments register <tournamentId> <teamId> - зарегистрировать команду
        |  tournaments prizes <tournamentId> <teamId> <place> <prize> - распределить призовые
        |  tournaments delete <id>       - удалить турнир
        |
        |  matches list                  - показать все матчи
        |  matches get <id>              - показать матч по ID
        |  matches add                   - добавить матч (интерактивно)
        |  matches finish <id> <winner> <score1> <score2> - завершить матч
        |  matches delete <id>           - удалить матч
        |
        |  transfers list                - показать все трансферы
        |  transfers add <playerId> <toTeamId> <fee> - трансфер игрока
        |  transfers player <playerId>   - трансферы игрока
        |
        |  stats                         - статистика по базе
        |  help                          - показать эту справку
        |  exit                          - остановить сервер
        |
    """.trimMargin())
}