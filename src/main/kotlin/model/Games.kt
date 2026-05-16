package model

import org.jetbrains.exposed.sql.Table

object Games : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}

data class Game(
    val id: Int,
    val name: String
)