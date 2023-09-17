package mobi.sevenwinds.app.budget

import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object BudgetTable : IntIdTable("budget") {
    val year = integer("year")
    val month = integer("month")
    val amount = integer("amount")
    val type = enumerationByName("type", 100, BudgetType::class)
    val author = reference("author_id", AuthorTable).nullable()
}

class BudgetEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BudgetEntity>(BudgetTable)

    var year by BudgetTable.year
    var month by BudgetTable.month
    var amount by BudgetTable.amount
    var type by BudgetTable.type
    var author by AuthorEntity optionalReferencedOn BudgetTable.author

    fun toBudgetRecord(): BudgetRecord {
        return BudgetRecord(year, month, amount, type, author?.id?.value)
    }

    fun toBudgetAuthorRecord(): BudgetAuthorRecord {
        val date = author?.createDatetime
        var datetime: LocalDateTime? = null
        if (date != null) {
            datetime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.millis), ZoneId.of(date.zone.id))
        }
        return BudgetAuthorRecord(
            year, month, amount, type, author?.fio, datetime
        );
    }


}