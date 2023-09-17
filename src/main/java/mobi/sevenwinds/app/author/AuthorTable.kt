package mobi.sevenwinds.app.author

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


object AuthorTable : IntIdTable("author") {

    val fio = varchar("fio", 255)
    val createDatetime = datetime("create_datetime")
}

class AuthorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthorEntity>(AuthorTable)

    var fio by AuthorTable.fio
    var createDatetime by AuthorTable.createDatetime


    fun toResponse(): AuthorRecord {
        return AuthorRecord(
            id.value,
            fio,
            LocalDateTime.ofInstant(Instant.ofEpochMilli(createDatetime.millis), ZoneId.of(createDatetime.zone.id))
        )
    }

}