package mobi.sevenwinds.app.author

import com.fasterxml.jackson.annotation.JsonFormat
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import java.time.LocalDateTime

fun NormalOpenAPIRoute.author() {
    route("/author") {
        route("/add").post<AuthorCreateParam, AuthorRecord, Any>(info("Добавить автора")) { param, body ->
            respond(AuthorService.addRecord(param))
        }
    }
}

data class AuthorRecord(
    val id: Int,
    val fio: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    val createTimestamp: LocalDateTime
)

data class AuthorCreateParam(
    @QueryParam("ФИО автора") val fio: String

)