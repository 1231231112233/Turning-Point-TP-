package console

import config.transferService
import config.teamService
import config.playerService
import service.TransferResult

fun handleTransfersCommand(input: String) {
    val parts = input.split(" ")
    when (parts.getOrNull(1)) {
        "list" -> {
            val transfers = transferService.getAllTransfers()
            if (transfers.isEmpty()) {
                println("📭 Трансферов нет")
            } else {
                println("\n🔄 Список трансферов:")
                for (transfer in transfers) {
                    val player = playerService.getPlayerById(transfer.playerId)
                    val fromTeam = transfer.fromTeamId?.let { teamService.getTeamById(it) }
                    val toTeam = transfer.toTeamId?.let { teamService.getTeamById(it) }
                    println("  ${transfer.id}. ${player?.nickname ?: "?"} - ${fromTeam?.name ?: "?"} → ${toTeam?.name ?: "?"} за ${transfer.fee} (${transfer.status})")
                }
            }
        }
        "add" -> {
            val playerId = parts.getOrNull(2)?.toIntOrNull()
            val toTeamId = parts.getOrNull(3)?.toIntOrNull()
            val fee = parts.getOrNull(4)?.toDoubleOrNull()
            if (playerId == null || toTeamId == null || fee == null) {
                println("❌ Используйте: transfers add <playerId> <toTeamId> <fee>")
                return
            }

            val result = transferService.transferPlayer(playerId, toTeamId, fee)
            when (result) {
                is TransferResult.Success -> println("✅ ${result.message}")
                is TransferResult.Error -> println("❌ ${result.message}")
            }
        }
        "player" -> {
            val playerId = parts.getOrNull(2)?.toIntOrNull()
            if (playerId == null) {
                println("❌ Используйте: transfers player <playerId>")
                return
            }
            val transfers = transferService.getTransfersByPlayer(playerId)
            if (transfers.isEmpty()) {
                println("📭 У игрока #$playerId нет трансферов")
            } else {
                println("\n🔄 Трансферы игрока #$playerId:")
                for (transfer in transfers) {
                    val fromTeam = transfer.fromTeamId?.let { teamService.getTeamById(it) }
                    val toTeam = transfer.toTeamId?.let { teamService.getTeamById(it) }
                    println("  ${transfer.id}. ${fromTeam?.name ?: "?"} → ${toTeam?.name ?: "?"} за ${transfer.fee} (${transfer.status})")
                }
            }
        }
        else -> println("❌ Неизвестная подкоманда")
    }
}