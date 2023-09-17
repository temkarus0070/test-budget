package mobi.sevenwinds.common

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification
import mobi.sevenwinds.app.author.AuthorRecord
import mobi.sevenwinds.app.budget.BudgetRecord
import org.junit.Assert

fun RequestSpecification.auth(token: String): RequestSpecification = this
    .header("Authorization", "Bearer $token")

fun <T> RequestSpecification.jsonBody(body: T): RequestSpecification = this
    .body(body)
    .contentType(ContentType.JSON)

inline fun <reified T> ResponseBodyExtractionOptions.toResponse(): T {
    return this.`as`(T::class.java)
}

fun RequestSpecification.When(): RequestSpecification {
    return this.`when`()
}


fun addRecord(record: BudgetRecord) {
    RestAssured
        .given()
        .jsonBody(record)
        .post("/budget/add")
        .toResponse<BudgetRecord>().let { response ->
            Assert.assertEquals(record, response)
        }
}

fun addAuthor(fio: String): AuthorRecord {
    return RestAssured.given()
        .contentType(ContentType.JSON)
        .jsonBody("{}")
        .queryParam("fio", fio)
        .post("/author/add")
        .toResponse<AuthorRecord>()
}