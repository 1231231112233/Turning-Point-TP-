package model

import org.jetbrains.exposed.sql.Table

object Players : Table() {
    val id = integer("id").autoIncrement()
    val nickname = varchar("nickname", 50)
    val realName = varchar("real_name", 100)
    val gameId = integer("game_id")
    val teamId = integer("team_id").nullable()
    val rating = integer("rating").default(1000)
    val role = varchar("role", 50)

    override val primaryKey = PrimaryKey(id)
}

data class Player(
    val id: Int,
    val nickname: String,
    val realName: String,
    val gameId: Int,
    val teamId: Int?,
    val rating: Int,
    val role: String
)