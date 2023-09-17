package mobi.sevenwinds.app.author

import io.restassured.RestAssured
import mobi.sevenwinds.app.budget.BudgetRecord
import mobi.sevenwinds.app.budget.BudgetTable
import mobi.sevenwinds.app.budget.BudgetType
import mobi.sevenwinds.app.budget.BudgetYearStatsResponse
import mobi.sevenwinds.common.ServerTest
import mobi.sevenwinds.common.addAuthor
import mobi.sevenwinds.common.addRecord
import mobi.sevenwinds.common.toResponse
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthorApiKtTest : ServerTest() {
    private var author: AuthorRecord? = null;

    @BeforeEach
    internal fun setUp() {
        transaction {
            BudgetTable.deleteAll()
            AuthorTable.deleteAll()

            author = addAuthor("PUPKIN");
            addRecord(BudgetRecord(2020, 5, 100, BudgetType.Приход))
            addRecord(BudgetRecord(2020, 5, 500, BudgetType.Приход))
        }
    }

    @Test
    fun testAuthorRecords() {
        RestAssured.given()
            .queryParam("limit", 5)
            .queryParam("offset", 1)
            .queryParam("fio", "pupkin")
            .get("/budget/year/2020/stats")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                println("${response.total} / ${response.items} / ${response.totalByType}")

                Assert.assertEquals(0, response.total)
                Assert.assertEquals(0, response.items.size)
                Assert.assertEquals(0, response.totalByType[BudgetType.Приход.name])
                Assert.assertEquals(0, response.totalByType[BudgetType.Расход.name])
            }

        addAuthorRecords()


        RestAssured.given()
            .queryParam("limit", 5)
            .queryParam("offset", 0)
            .queryParam("fio", "pupkin")
            .get("/budget/year/2020/stats")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                println("${response.total} / ${response.items} / ${response.totalByType}")

                Assert.assertEquals(2, response.total)
                Assert.assertEquals(2, response.items.size)
                Assert.assertEquals(700, response.totalByType[BudgetType.Приход.name])
                Assert.assertEquals(0, response.totalByType[BudgetType.Расход.name])
                Assert.assertTrue(response.items.all {
                    it.authorFio.equals(author?.fio) and (it.authorCreateDateTime?.equals
                        (author?.createTimestamp) == true)
                })
            }

        RestAssured.given()
            .queryParam("limit", 5)
            .queryParam("offset", 0)
            .queryParam("fio", "PUPKIN")
            .get("/budget/year/2020/stats")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                println("${response.total} / ${response.items} / ${response.totalByType}")

                Assert.assertEquals(2, response.total)
                Assert.assertEquals(2, response.items.size)
                Assert.assertEquals(700, response.totalByType[BudgetType.Приход.name])
                Assert.assertEquals(0, response.totalByType[BudgetType.Расход.name])
                Assert.assertTrue(response.items.all {
                    it.authorFio.equals(author?.fio) and (it.authorCreateDateTime?.equals
                        (author?.createTimestamp) == true)
                })
            }
    }

    @Test
    fun testAuthorRecordsPagination() {
        addAuthorRecords()


        RestAssured.given()
            .queryParam("limit", 1)
            .queryParam("offset", 0)
            .queryParam("fio", "pupkin")
            .get("/budget/year/2020/stats")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                println("${response.total} / ${response.items} / ${response.totalByType}")

                Assert.assertEquals(2, response.total)
                Assert.assertEquals(1, response.items.size)
                Assert.assertEquals(200, response.totalByType[BudgetType.Приход.name])
                Assert.assertEquals(0, response.totalByType[BudgetType.Расход.name])
                Assert.assertTrue(response.items.all {
                    it.authorFio.equals(author?.fio) and (it.authorCreateDateTime?.equals
                        (author?.createTimestamp) == true)
                })
            }

        RestAssured.given()
            .queryParam("limit", 1)
            .queryParam("offset", 0)
            .queryParam("fio", "PUPKIN")
            .get("/budget/year/2020/stats")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                println("${response.total} / ${response.items} / ${response.totalByType}")

                Assert.assertEquals(2, response.total)
                Assert.assertEquals(1, response.items.size)
                Assert.assertEquals(200, response.totalByType[BudgetType.Приход.name])
                Assert.assertEquals(0, response.totalByType[BudgetType.Расход.name])
                Assert.assertTrue(response.items.all {
                    it.authorFio.equals(author?.fio) and (it.authorCreateDateTime?.equals
                        (author?.createTimestamp) == true)
                })
            }
    }

    @Test
    fun testRecordsWithoutAuthor() {
        addAuthorRecords()
        RestAssured.given()
            .queryParam("limit", 5)
            .queryParam("offset", 0)
            .get("/budget/year/2020/stats")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                println("${response.total} / ${response.items} / ${response.totalByType}")

                Assert.assertEquals(4, response.total)
                Assert.assertEquals(4, response.items.size)
                Assert.assertEquals(1300, response.totalByType[BudgetType.Приход.name])
                Assert.assertEquals(0, response.totalByType[BudgetType.Расход.name])
            }
    }

    private fun addAuthorRecords() {

        addRecord(BudgetRecord(2020, 5, 200, BudgetType.Приход, author?.id))
        addRecord(BudgetRecord(2020, 7, 500, BudgetType.Приход, author?.id))
    }

}