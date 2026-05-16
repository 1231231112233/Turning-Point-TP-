package service

import model.Game
import repository.GameRepository

class GameService(private val repository: GameRepository) {

    fun getAllGames(): List<Game> = repository.getAll()

    fun getGameById(id: Int): Game? = repository.getById(id)
}