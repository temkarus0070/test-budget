package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upperCase

object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            var author: AuthorEntity? = null
            if (body.authorId != null) {
                author = AuthorEntity[body.authorId]
            }
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.author = author
            }

            return@transaction entity.toBudgetRecord()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {

            var totalQuery = (BudgetTable leftJoin AuthorTable).select { BudgetTable.year eq param.year }
            var query = (BudgetTable leftJoin AuthorTable).select { BudgetTable.year eq param.year }
                .orderBy(BudgetTable.month to SortOrder.ASC, BudgetTable.amount to SortOrder.DESC)
                .limit(param.limit, param.offset)
            if (param.fio != null) {
                query = query
                    .adjustWhere { AuthorTable.fio.upperCase().like("${param.fio.toUpperCase()}%") }
                totalQuery = totalQuery.adjustWhere { AuthorTable.fio.upperCase().like("${param.fio.toUpperCase()}%") }

            }
            val data = BudgetEntity.wrapRows(query).map { it.toBudgetAuthorRecord() }

            val sumByType = data.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }.toMutableMap()

            val total = totalQuery.count()
            for (value in BudgetType.values()) {
                if (sumByType.get(value.name) == null) {
                    sumByType[value.name] = 0
                }
            }

            return@transaction BudgetYearStatsResponse(
                total = total, totalByType = sumByType, items = data
            )
        }
    }
}