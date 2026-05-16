package service

import model.Tournament
import repository.TournamentRepository

class TournamentService(private val repository: TournamentRepository) {

    fun getAllTournaments(): List<Tournament> = repository.getAll()

    fun getTournamentById(id: Int): Tournament? = repository.getById(id)

    fun getTournamentsByGame(gameId: Int): List<Tournament> = repository.getByGame(gameId)

    fun createTournament(tournament: Tournament): Tournament = repository.create(tournament)

    fun updateStatus(tournamentId: Int, status: String): Boolean = repository.updateStatus(tournamentId, status)

    fun deleteTournament(id: Int): Boolean = repository.delete(id)
}