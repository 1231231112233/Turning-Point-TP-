package service

import model.Player
import repository.PlayerRepository

class PlayerService(private val repository: PlayerRepository) {

    fun getAllPlayers(): List<Player> = repository.getAll()

    fun getPlayerById(id: Int): Player? = repository.getById(id)

    fun getPlayersByTeam(teamId: Int): List<Player> = repository.getByTeam(teamId)

    fun getPlayersByGame(gameId: Int): List<Player> = repository.getByGame(gameId)

    fun createPlayer(player: Player): Player = repository.create(player)

    fun updateTeam(playerId: Int, newTeamId: Int?): Boolean = repository.updateTeam(playerId, newTeamId)

    fun updateRating(playerId: Int, newRating: Int): Boolean = repository.updateRating(playerId, newRating)

    fun deletePlayer(id: Int): Boolean = repository.delete(id)
}