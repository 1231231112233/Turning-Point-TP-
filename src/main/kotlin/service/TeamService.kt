package service

import model.Team
import repository.TeamRepository

class TeamService(private val repository: TeamRepository) {

    fun getAllTeams(): List<Team> = repository.getAll()

    fun getTeamById(id: Int): Team? = repository.getById(id)

    fun getTeamsByGame(gameId: Int): List<Team> = repository.getByGame(gameId)

    fun createTeam(team: Team): Team = repository.create(team)

    fun updateBudget(teamId: Int, newBudget: Double): Boolean = repository.updateBudget(teamId, newBudget)

    fun updateCaptain(teamId: Int, captainId: Int): Boolean = repository.updateCaptain(teamId, captainId)

    fun deleteTeam(id: Int): Boolean = repository.delete(id)
}