package service

import model.Match
import repository.MatchRepository

class MatchService(private val repository: MatchRepository) {

    fun getAllMatches(): List<Match> = repository.getAll()

    fun getMatchById(id: Int): Match? = repository.getById(id)

    fun getMatchesByTournament(tournamentId: Int): List<Match> = repository.getByTournament(tournamentId)

    fun createMatch(match: Match): Match = repository.create(match)

    fun finishMatch(matchId: Int, winnerId: Int, score1: Int, score2: Int): Boolean =
        repository.updateWinner(matchId, winnerId, score1, score2)

    fun deleteMatch(id: Int): Boolean = repository.delete(id)
}