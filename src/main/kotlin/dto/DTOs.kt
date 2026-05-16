package dto

import model.*

// Team DTOs
data class TeamRequest(
    val name: String,
    val gameId: Int,
    val budget: Double,
    val createdAt: String
)

data class TeamResponse(
    val id: Int,
    val name: String,
    val gameId: Int,
    val gameName: String?,
    val budget: Double,
    val createdAt: String
)

// Player DTOs
data class PlayerRequest(
    val nickname: String,
    val realName: String,
    val gameId: Int,
    val teamId: Int?,
    val rating: Int,
    val role: String
)

data class PlayerResponse(
    val id: Int,
    val nickname: String,
    val realName: String,
    val gameId: Int,
    val gameName: String?,
    val teamId: Int?,
    val teamName: String?,
    val rating: Int,
    val role: String
)

// Tournament DTOs
data class TournamentRequest(
    val name: String,
    val gameId: Int,
    val prizePool: Double,
    val maxTeams: Int,
    val startDate: String,
    val status: String
)

data class TournamentResponse(
    val id: Int,
    val name: String,
    val gameId: Int,
    val gameName: String?,
    val prizePool: Double,
    val maxTeams: Int,
    val startDate: String,
    val status: String
)

// Match DTOs
data class MatchRequest(
    val tournamentId: Int,
    val round: Int,
    val team1Id: Int?,
    val team2Id: Int?,
    val scheduledDate: String
)

data class MatchResponse(
    val id: Int,
    val tournamentId: Int,
    val tournamentName: String?,
    val round: Int,
    val team1Id: Int?,
    val team1Name: String?,
    val team2Id: Int?,
    val team2Name: String?,
    val winnerId: Int?,
    val winnerName: String?,
    val scoreTeam1: Int,
    val scoreTeam2: Int,
    val scheduledDate: String,
    val status: String
)

// Transfer DTOs
data class TransferRequest(
    val playerId: Int,
    val toTeamId: Int,
    val fee: Double
)

data class TransferResponse(
    val id: Int,
    val playerId: Int,
    val playerNickname: String?,
    val fromTeamId: Int?,
    val fromTeamName: String?,
    val toTeamId: Int?,
    val toTeamName: String?,
    val fee: Double,
    val status: String,
    val transferDate: String
)

// Finish Match DTO
data class FinishMatchRequest(
    val winnerId: Int,
    val scoreTeam1: Int,
    val scoreTeam2: Int
)

// Update DTOs
data class TeamBudgetUpdateRequest(val newBudget: Double)
data class PlayerTeamUpdateRequest(val newTeamId: Int?)
data class PlayerRatingUpdateRequest(val newRating: Int)
data class TournamentStatusUpdateRequest(val status: String)